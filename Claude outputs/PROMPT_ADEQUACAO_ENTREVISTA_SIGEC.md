# PROMPT — Adequação do módulo Entrevista (SIGEC) ao schema real do banco

> **Como usar:** cole o conteúdo deste arquivo inteiro como instrução/contexto para a IA do seu IDE (IntelliJ AI Assistant, Copilot, Cursor, etc.), no projeto `SIGEC`. Ele foi escrito para ser autossuficiente — não depende de nenhuma outra conversa. Revise o diff proposto pela IA antes de aceitar, especialmente as regras de negócio das seções 4 e 5.

---

## 1. Contexto (por que esta mudança é necessária)

O banco de dados SQL Server `SIGEC` já foi alterado e hoje a tabela `entrevista` tem uma estrutura diferente da que o código Java atual (`Entrevista.java`, `EntrevistaDAO.java`, `EntrevistaService.java`) espera. O código ainda foi escrito para uma versão antiga da tabela, que tinha uma coluna `data_entrevista` (hoje inexistente) e não tinha `data_agendamento`, `data_realizacao`, `tipo_atendimento` nem `status`.

**Resultado prático:** hoje, `EntrevistaDAO.inserir()`, `.atualizar()`, `.buscarPorId()` e `.listarTodos()` não funcionam contra o banco real — eles fazem referência a uma coluna (`data_entrevista`) que não existe mais, e não preenchem duas colunas obrigatórias (`tipo_atendimento`, `status`).

**Esta tarefa é só de código Java.** O banco **já está correto e não deve ser alterado** — nenhum `CREATE TABLE`, `ALTER TABLE` ou script de migração deve ser executado. O objetivo é fazer o código Java refletir o banco que já existe.

**Não faça parte desta tarefa** (deixe para depois, são tarefas separadas):
- Não crie nem altere nenhuma tela FXML nem nenhum Controller (`telaExamesPendentes.fxml`, botão "ATENDER", `telaResumo.fxml`, etc.). Esta tarefa é só das camadas `model`, `dao`, `service` (e as classes de teste manuais que dependem delas).
- Não altere `banco/scripts/01_criacao_banco.sql` nem nenhum outro script SQL do projeto.
- Não altere `PedidoExameService.java` além do estritamente necessário para continuar compilando (a assinatura de `EntrevistaDAO.foiAtendidoPorAmbos(int)`, usada por `concluirPedido()`, deve continuar existindo com o mesmo nome e mesmo tipo de retorno).
- Não faça commit. Deixe as mudanças no working tree para o usuário revisar e commitar manualmente.
- Não existem dados reais na tabela `entrevista` hoje que precisem ser preservados — trate como se a tabela estivesse vazia. Não é necessário escrever nenhum script de migração/backfill de dados.

---

## 2. Estrutura REAL e atual da tabela `entrevista` (fonte da verdade)

```sql
CREATE TABLE [dbo].[entrevista](
    [id]                  [int]      IDENTITY(1,1) NOT NULL,   -- PK
    [id_pedido_exame]     [int]      NOT NULL,                  -- FK -> pedido_exame(id)
    [id_profissional]     [int]      NULL,                      -- FK -> profissional(id)  -- AGORA OPCIONAL
    [data_agendamento]    [date]     NULL,
    [id_usuario]          [int]      NOT NULL,                  -- FK -> usuario(id)
    [data_entrega_laudo]  [date]     NULL,
    [data_cadastro]       [date]     NOT NULL,
    [data_realizacao]     [date]     NULL,
    [tipo_atendimento]    [varchar](30) NOT NULL,                -- CHECK: 'PSICOLOGO' ou 'ASSISTENTE_SOCIAL'
    [status]              [varchar](30) NOT NULL                 -- CHECK: 'PENDENTE_AGENDAMENTO','AGENDADA','REALIZADA','CANCELADA'
)
```

Constraints de banco confirmadas (não recriar, já existem):
- `CK_entrevista_tipo_atendimento`: `tipo_atendimento IN ('ASSISTENTE_SOCIAL','PSICOLOGO')`
- `CK_entrevista_status`: `status IN ('CANCELADA','REALIZADA','AGENDADA','PENDENTE_AGENDAMENTO')`
- FK `id_pedido_exame` → `pedido_exame(id)`, FK `id_profissional` → `profissional(id)`, FK `id_usuario` → `usuario(id)`

Note que `id_profissional` e `data_agendamento` são `NULL`-áveis — isso é intencional: uma entrevista pode existir "na fila", ainda sem profissional/data definidos.

---

## 3. Modelo de domínio alvo

### 3.1 Reaproveitar o enum existente para `tipo_atendimento`

O enum `br.com.sigec.model.TipoProfissional` já existe e já tem exatamente os dois valores do `CHECK` de `tipo_atendimento`:
```java
public enum TipoProfissional {
    PSICOLOGO,
    ASSISTENTE_SOCIAL;
}
```
**Reaproveite este enum** para o campo `tipoAtendimento` de `Entrevista` — não crie um enum novo e duplicado. (`profissional.tipo` já usa este mesmo enum, então isso mantém consistência semântica: o profissional atende segundo seu `tipo`, e a entrevista pede um atendimento de um determinado `tipoAtendimento`.)

### 3.2 Criar um novo enum para `status`

Criar `br.com.sigec.model.StatusEntrevista`, no mesmo estilo de `StatusPedidoExame`:
```java
package br.com.sigec.model;

public enum StatusEntrevista {
    PENDENTE_AGENDAMENTO,
    AGENDADA,
    REALIZADA,
    CANCELADA
}
```

### 3.3 Reescrever `Entrevista.java`

Campos alvo (getters/setters no mesmo padrão do resto do projeto):
- `id` (int)
- `pedidoExame` (`PedidoExame`)
- `profissional` (`Profissional`, **agora pode ser null**)
- `tipoAtendimento` (`TipoProfissional`, obrigatório)
- `status` (`StatusEntrevista`, obrigatório)
- `dataAgendamento` (`LocalDate`, nullable)
- `dataRealizacao` (`LocalDate`, nullable)
- `dataEntregaLaudo` (`LocalDate`, nullable)
- `usuario` (`Usuario`, obrigatório — quem registrou/está conduzindo o processo)
- `dataCadastro` (`LocalDate`, obrigatório)

Remover completamente o campo `dataEntrevista` (a coluna correspondente não existe mais no banco).

Construtores sugeridos (seguindo o padrão do projeto, ex. `PedidoExame`, que já preenche `usuario`/`dataCadastro` automaticamente a partir da sessão):
```java
public Entrevista() { }

// Cria uma entrevista "na fila", ainda sem profissional nem data de agendamento definidos
public Entrevista(PedidoExame pedidoExame, TipoProfissional tipoAtendimento) {
    this.pedidoExame = pedidoExame;
    this.tipoAtendimento = tipoAtendimento;
    this.status = StatusEntrevista.PENDENTE_AGENDAMENTO;
    this.usuario = SessaoUsuario.getUsuarioLogado();
    this.dataCadastro = LocalDate.now();
}
```

Atualize o `toString()` para os novos campos (mantendo o estilo atual do projeto).

### 3.4 Reescrever `EntrevistaDAO.java`

Os métodos abaixo substituem os atuais `inserir`/`atualizar`/`buscarPorId`/`listarTodos` (que ficam obsoletos porque dependiam de `data_entrevista`):

- **`inserir(Entrevista entrevista)`** — `INSERT INTO entrevista (id_pedido_exame, tipo_atendimento, status, id_usuario, data_cadastro) VALUES (?,?,?,?,?)`, recuperando a chave gerada (`Statement.RETURN_GENERATED_KEYS`), no mesmo padrão de `try-with-resources` usado em todos os outros DAOs do projeto. `id_profissional`, `data_agendamento`, `data_realizacao` e `data_entrega_laudo` ficam `NULL` neste momento (colunas nulas por padrão — não é preciso incluí-las na lista de colunas do INSERT).

- **`agendar(int idEntrevista, int idProfissional, LocalDate dataAgendamento)`** — `UPDATE entrevista SET id_profissional = ?, data_agendamento = ?, status = 'AGENDADA' WHERE id = ?`.

- **`registrarRealizacao(int idEntrevista, LocalDate dataRealizacao, LocalDate dataEntregaLaudo)`** — `UPDATE entrevista SET data_realizacao = ?, data_entrega_laudo = ?, status = 'REALIZADA' WHERE id = ?` (`dataEntregaLaudo` pode ser `null` → usar `Types.DATE` como já é feito em outros pontos do projeto, ex. `PedidoExameDAO`/`EntrevistaDAO` atuais).

- **`cancelar(int idEntrevista)`** — `UPDATE entrevista SET status = 'CANCELADA' WHERE id = ?`.

- **`buscarPorId(int id)`** e **`listarTodos()`** — reescrever o SELECT para os novos campos. **Ponto de atenção importante:** como `id_profissional` agora pode ser `NULL`, o JOIN com `profissional` precisa virar **`LEFT JOIN`** (no código atual é `INNER JOIN`, o que excluiria silenciosamente qualquer entrevista sem profissional ainda atribuído). O método que monta o objeto (`montarEntrevista`) precisa tratar o caso de `id_profissional`/colunas de profissional virem `NULL` no `ResultSet` (usar `resultado.getObject("profissional_id")` ou checar `resultado.getInt(...)` + `resultado.wasNull()` antes de montar o objeto `Profissional`, deixando `entrevista.setProfissional(null)` quando não houver).

- **`listarPorPedidoExame(int idPedidoExame)`** — método novo, útil para reunir todas as entrevistas (fila/agendadas/realizadas) de um mesmo pedido; mesmo SELECT de `listarTodos()` com `WHERE e.id_pedido_exame = ?`.

- **`foiAtendidoPorAmbos(int idPedido)`** — **mantenha o nome e a assinatura** (é chamado por `PedidoExameService.concluirPedido()`), mas **corrija/simplifique a consulta** para usar a nova coluna `tipo_atendimento` diretamente (não é mais necessário fazer JOIN com `profissional` para descobrir o tipo) e para só considerar entrevistas efetivamente **realizadas**:
  ```sql
  SELECT COUNT(DISTINCT tipo_atendimento)
  FROM entrevista
  WHERE id_pedido_exame = ?
    AND status = 'REALIZADA'
  ```
  (retornar `true` se o resultado for `>= 2`). Isso corrige uma imprecisão do código antigo, que considerava "atendido" qualquer registro de entrevista existente, mesmo que ainda não tivesse sido realizado.

- **`excluir(Entrevista entrevista)`** — pode manter como está hoje (`DELETE FROM entrevista WHERE id = ?`), não depende das colunas alteradas.

### 3.5 Reescrever `EntrevistaService.java`

Substituir `inserir`/`atualizar` (que hoje fazem uma única validação genérica) por métodos alinhados ao novo ciclo de vida `PENDENTE_AGENDAMENTO → AGENDADA → REALIZADA`, com `CANCELADA` alcançável a partir de `PENDENTE_AGENDAMENTO` ou `AGENDADA`:

- **`criarPendente(PedidoExame pedidoExame, TipoProfissional tipoAtendimento)`**
  - valida `pedidoExame` não nulo;
  - valida que o status do pedido não é `CANCELADO`, `CONCLUIDO` nem `TRANSFERIDO` (mesma regra já usada em `ReiteracaoService`/`EntrevistaService` atual);
  - valida `tipoAtendimento` não nulo;
  - valida usuário logado (`SessaoUsuario.getUsuarioLogado()`) não nulo e ativo;
  - monta `new Entrevista(pedidoExame, tipoAtendimento)` e chama `entrevistaDAO.inserir(...)`.

- **`agendar(int idEntrevista, Profissional profissional, LocalDate dataAgendamento)`**
  - busca a entrevista (`entrevistaDAO.buscarPorId`), valida que existe;
  - valida `status` atual é `PENDENTE_AGENDAMENTO` ou `AGENDADA` (permite reagendar antes de realizar); se `REALIZADA` ou `CANCELADA`, lança `IllegalArgumentException`;
  - valida `profissional` não nulo, ativo, e **`profissional.getTipo() == entrevista.getTipoAtendimento()`** (não deixar agendar um psicólogo para um atendimento do tipo `ASSISTENTE_SOCIAL`, e vice-versa);
  - valida `dataAgendamento` não nula e não anterior à `dataSolicitacao` do pedido de exame (mesma regra de "não pode ser anterior ao pedido" que já existia);
  - chama `entrevistaDAO.agendar(idEntrevista, profissional.getId(), dataAgendamento)`.

- **`registrarRealizacao(int idEntrevista, LocalDate dataRealizacao, LocalDate dataEntregaLaudo)`**
  - busca a entrevista, valida que existe;
  - valida `status` atual é `AGENDADA` (não é possível registrar realização sem antes agendar);
  - valida `dataRealizacao` não nula, não futura (`isAfter(LocalDate.now())`), e não anterior à `dataAgendamento`;
  - se `dataEntregaLaudo` informada, valida que não é anterior a `dataRealizacao`;
  - chama `entrevistaDAO.registrarRealizacao(...)`.

- **`cancelar(int idEntrevista)`**
  - busca a entrevista, valida que existe;
  - valida `status` atual é `PENDENTE_AGENDAMENTO` ou `AGENDADA` (não é possível cancelar uma entrevista já `REALIZADA`);
  - chama `entrevistaDAO.cancelar(idEntrevista)`.

Mantenha os métodos privados de validação no mesmo estilo do restante do projeto (`validarX(...)`, lançando `IllegalArgumentException` com mensagens em português, no padrão já usado em `PedidoExameService`/`ReiteracaoService`).

---

## 4. Classes de teste manuais que dependem da API atual (precisam ser atualizadas para o projeto continuar compilando)

Estas classes usam a API antiga de `Entrevista`/`EntrevistaDAO` e **vão quebrar a compilação** se não forem atualizadas junto:

- **`src/br/com/sigec/dao/TesteEntrevistaDAO.java`** — reescrever o `main()` para exercitar o novo fluxo manualmente, no mesmo estilo de `System.out.println` com "✅"/"❌" já usado nessa classe: criar uma entrevista pendente (`criarPendente`), agendar (`agendar`), registrar realização (`registrarRealizacao`), e opcionalmente testar `cancelar` em outro registro. Buscar e imprimir o resultado depois de cada etapa (`buscarPorId`).
- **`src/br/com/sigec/service/EntrevistaServiceTeste.java`** — se existir, atualizar da mesma forma (só existe um `TesteBcrypt.java` e outras classes `*ServiceTeste`; confirme se há uma classe de teste manual para `EntrevistaService` especificamente e, se houver, atualize-a; caso não exista, não é necessário criá-la).

Não precisa se preocupar em preservar o comportamento exato desses testes manuais — o objetivo é só garantir que compilem e sirvam para verificar manualmente (rodando o `main`) que o novo fluxo funciona contra o banco real.

---

## 5. Checklist final (peça para a IA do IDE confirmar/executar)

1. O projeto compila sem erros após as mudanças (nenhuma referência remanescente a `Entrevista.getDataEntrevista()`/`setDataEntrevista()` em nenhum arquivo).
2. Nenhum arquivo fora de `model/`, `dao/`, `service/` (e as classes de teste manuais citadas na seção 4) foi alterado — em particular, nenhum arquivo em `controller/`, `view/` (FXML), `banco/scripts/`, nem `.idea/`/`.git/`.
3. `PedidoExameService.concluirPedido()` continua compilando e chamando `entrevistaDAO.foiAtendidoPorAmbos(idPedido)` sem mudança de assinatura.
4. Rodar manualmente `TesteEntrevistaDAO.main()` (com o SQL Server local rodando e o banco `SIGEC` acessível) e confirmar visualmente, pelo `System.out.println`, que: a entrevista é criada com status `PENDENTE_AGENDAMENTO`; após `agendar`, o status muda para `AGENDADA` e profissional/data aparecem preenchidos; após `registrarRealizacao`, o status muda para `REALIZADA`.
5. Nenhum `ALTER TABLE`/`CREATE TABLE`/script de migração foi executado contra o banco — a estrutura da tabela `entrevista` não muda, só o código Java que a acessa.
