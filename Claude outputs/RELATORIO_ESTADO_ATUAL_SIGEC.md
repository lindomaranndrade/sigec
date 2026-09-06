# RELATÓRIO DE ESTADO ATUAL — SIGEC

**Data da análise:** 06/09/2026 (revisado no mesmo dia após confirmação do schema real do banco)
**Projeto analisado:** `C:\Users\Lindomar\Documents\SIGEC\SIGEC`
**Método:** leitura direta dos arquivos do projeto (código-fonte, FXML, arquivos de configuração do IntelliJ e do Git). Nenhum arquivo do projeto foi alterado, criado ou excluído durante esta análise — apenas cópias de leitura foram utilizadas para exame. O único arquivo criado foi este relatório. Adicionalmente, o usuário forneceu um script `.sql` gerado pelo SSMS ("Generate Scripts") com a estrutura **real e atual** do banco `SIGEC` (tabelas, chaves e constraints), que foi usado para confirmar ou corrigir os pontos que a versão inicial deste relatório havia marcado como "Não confirmado" a partir apenas do script `banco/scripts/01_criacao_banco.sql` versionado no projeto (esse script se mostrou desatualizado — ver seção 7).

Este relatório reflete **exclusivamente** o que foi encontrado no código do projeto e nesse script de schema real. Onde ainda não foi possível confirmar algo, isso está marcado explicitamente como **"Não confirmado"**. Trechos marcados como *inferência* são conclusões do analista a partir de evidências indiretas, não fatos diretamente lidos no código/schema.

---

## 1. VISÃO GERAL DO SIGEC

**Objetivo do sistema** (conforme descrito no `README.md` do próprio projeto): o SIGEC — Sistema Integrado de Gestão de Execução Criminal — é um sistema desktop em Java para apoiar o gerenciamento de processos da execução penal, especificamente o fluxo de **pedidos de exame criminológico** de sentenciados (pessoas cumprindo pena), incluindo o vínculo desses pedidos a benefícios da execução penal (ex.: progressão de regime), o agendamento/registro de atendimentos por profissionais (psicólogo e assistente social) e o registro de reiterações (cobranças/insistências sobre pedidos pendentes).

**Problema que resolve** (inferência a partir do domínio modelado): controlar, de forma centralizada, quais sentenciados têm pedidos de exame criminológico em aberto, quais benefícios estão vinculados a cada pedido, se os atendimentos com os profissionais responsáveis já ocorreram, e permitir reiterar pedidos que estão demorando — substituindo um controle manual (ex.: planilhas), tal como mencionado no próprio código (há referência a "coluna de Psiquiatra" da "planilha da penitenciária" em conhecimento de contexto do usuário, mas isso não está documentado no código-fonte em si).

**Contexto de utilização:** o README declara explicitamente que o projeto foi concebido como um **projeto de aprendizado prático** ("com o objetivo de aprofundar conhecimentos em Java, Programação Orientada a Objetos, JDBC e SQL Server"), simulando um cenário próximo de aplicações corporativas reais, e evitando deliberadamente frameworks de persistência (Hibernate/JPA) para entender a camada JDBC manualmente. Portanto, o "usuário final" declarado do sistema é o próprio autor, no papel de administrador/usuário do órgão prisional.

**Como o sistema funciona, de forma geral (fluxo real confirmado no código):**
1. O usuário abre a aplicação e vê uma tela de login.
2. Após autenticação, é levado a uma tela principal com menu lateral (Início, Usuários, Sair) e uma área de conteúdo central que troca de tela sem abrir novas janelas.
3. Por padrão, a tela inicial mostra os **pedidos de exame pendentes** (status `CADASTRADO` ou `SOLICITADO`).
4. A partir dali é possível cadastrar um novo pedido de exame, vinculando um sentenciado (localizado por matrícula, ou cadastrado na hora se não existir) e um ou mais benefícios.
5. Funcionalidades de atendimento (entrevista com profissional), reiteração e conclusão de pedido **existem na camada de regras de negócio (Service/DAO)**, mas — como detalhado nas seções 6 e 10 — **não possuem tela funcional que as acione** no estado atual do código.

---

## 2. TECNOLOGIAS

Tecnologias efetivamente identificadas nos arquivos de configuração e no código:

- **Java 21** — confirmado em `.idea/misc.xml` (`languageLevel="JDK_21"`, `project-jdk-name="ms-21"`, sugerindo o Microsoft Build of OpenJDK 21). O README também declara "Java 21".
- **JavaFX 17.0.12 (build "win")** — confirmado em `.idea/libraries/javafx.xml`, que referencia os jars `javafx-base`, `javafx-graphics`, `javafx-controls` e `javafx-fxml`, todos na versão `17.0.12`, localizados no repositório Maven local do usuário (`$USER_HOME$/.m2/repository/org/openjfx/...`). **Observação:** os arquivos FXML declaram `xmlns="http://javafx.com/javafx/26"` (namespace de versão 26), o que diverge da versão 17.0.12 efetivamente usada em tempo de execução — ver item 10 (inconsistência de metadados, provavelmente por causa da versão do Scene Builder usada para editar os FXML).
- **FXML + Scene Builder** — todas as telas são definidas em arquivos `.fxml` carregados via `FXMLLoader`.
- **JavaFX CSS** — um único arquivo `style.css` compartilhado entre as telas, aplicando um tema de dashboard administrativo (paleta azul-marinho `#082B63`/`#0b2a5b`, cantos arredondados, sombras).
- **JDBC puro** (sem ORM) — confirmado em toda a camada `dao/`, que usa `java.sql.Connection`, `PreparedStatement`, `ResultSet` diretamente, com blocos `try-with-resources`.
- **Driver JDBC:** `mssql-jdbc-13.4.0.jre11.jar` (Microsoft JDBC Driver para SQL Server), incluído diretamente na pasta `lib/` do projeto (não via gerenciador de dependências).
- **Banco de dados:** SQL Server (confirmado pela URL de conexão `jdbc:sqlserver://localhost:1433;databaseName=SIGEC;...` em `Conexao.java` e pela sintaxe T-SQL do script de criação, ex.: `IDENTITY(1,1)`, tipo `BIT`).
- **jBCrypt 0.4** (`org.mindrot.jbcrypt`) — usado para hash de senha de usuário (`UsuarioService`).
- **Git** — repositório Git presente (pasta `.git` com histórico de commits, `FETCH_HEAD`, `ORIG_HEAD`, etc.), confirmando uso de controle de versão. **Não confirmado** o estado do repositório remoto (branches, se está sincronizado) nem o conteúdo detalhado do histórico de commits — essa análise não examinou o log de commits em profundidade.
- **IntelliJ IDEA** como IDE — projeto estruturado como módulo IntelliJ (`SIGEC.iml`, pasta `.idea/`), **sem** Maven (`pom.xml`) nem Gradle (`build.gradle`) no projeto. As dependências do JavaFX e do jBCrypt são resolvidas como bibliotecas de projeto do IntelliJ apontando para jars já baixados no repositório Maven local do usuário — ou seja, o projeto não tem um mecanismo de build automatizado e portátil (ver seção 11).

Não foram encontradas nesta análise: frameworks de injeção de dependência, frameworks de teste (JUnit/TestNG/Mockito — nenhuma dependência ou import desse tipo foi localizado), logging estruturado (todo o log é via `System.out.println`/`e.printStackTrace()`), nem qualquer camada REST/web.

---

## 3. ESTRUTURA DO PROJETO

```
SIGEC/
├── README.md
├── SIGEC.iml                          (módulo IntelliJ)
├── .idea/                             (config. do IntelliJ: SDK, libraries, run configuration)
├── .gitignore
├── lib/
│   └── mssql-jdbc-13.4.0.jre11.jar    (driver JDBC versionado no projeto)
├── out/                               (build output do IntelliJ — artefato de compilação, ignorado no Git)
├── banco/
│   ├── modelos/
│   │   ├── modelo_conceitual.drawio + .jpg
│   │   └── modelo_logico.drawio + .jpg
│   └── scripts/
│       └── 01_criacao_banco.sql       (único script SQL encontrado)
└── src/br/com/sigec/
    ├── Main.java                      (ponto de entrada JavaFX)
    ├── controller/                    (8 classes) — controllers JavaFX ligados aos FXML
    ├── dao/                           (8 DAOs + 8 classes "Teste*DAO")
    ├── service/                       (8 Services + 8 classes "*ServiceTeste" + TesteBcrypt)
    ├── model/                         (8 classes de entidade + 2 enums)
    ├── session/                       (SessaoUsuario — sessão do usuário logado)
    ├── util/                          (Conexao — fábrica de conexão JDBC; TesteConexao)
    ├── view/                          (9 arquivos .fxml + style.css)
    └── images/                        (7 imagens PNG usadas nas telas)
```

**Responsabilidade de cada pacote:**

- **`controller`** — classes JavaFX (`@FXML`) que respondem a eventos de UI (cliques, submissão de formulário) e chamam a camada `service`. Contém: `LoginController`, `TelaPrincipalController`, `TelaExamesPendentesController`, `TelaNovoPedidoController`, `TelaCadastroSentenciadoController`, `CadastroUsuarioController`, `TelaAlterarSenhaController`, `ResumoPedidoController`.
- **`dao`** — acesso direto ao banco via JDBC, um DAO por entidade principal (`UsuarioDAO`, `SentenciadoDAO`, `BeneficioDAO`, `PedidoExameDAO`, `PedidoBeneficioDAO`, `ReiteracaoDAO`, `EntrevistaDAO`, `ProfissionalDAO`), cada um com métodos `inserir`, `buscarPorId`, `listarTodos`, `atualizar`, `excluir` (nem todos implementam todos esses métodos — variação caso a caso). Também contém 8 classes `Teste<Entidade>DAO` com método `main`, usadas manualmente para validar cada DAO durante o desenvolvimento (não são testes automatizados).
- **`service`** — regras de negócio e validações (obrigatoriedade de campos, tamanhos mínimos/máximos, duplicidade, transições de status permitidas), intermediando `controller` e `dao`. Um Service por entidade principal, espelhando os DAOs. Também contém 8 classes `*ServiceTeste` com método `main` (testes manuais dos Services, incluindo fluxos de erro) e `TesteBcrypt` (teste manual de hashing de senha).
- **`model`** — classes de domínio simples (POJOs), sem anotações de framework, com getters/setters e `toString()`. Entidades: `Usuario`, `Sentenciado`, `Beneficio`, `Profissional`, `PedidoExame`, `PedidoBeneficio` (associação N:N), `Reiteracao`, `Entrevista`. Enums: `StatusPedidoExame` (`CADASTRADO`, `SOLICITADO`, `CONCLUIDO`, `CANCELADO`, `TRANSFERIDO`) e `TipoProfissional` (`PSICOLOGO`, `ASSISTENTE_SOCIAL`).
- **`session`** — classe estática única `SessaoUsuario`, guarda o `Usuario` atualmente logado em memória (não há gerenciamento de sessão multiusuário — é uma aplicação desktop single-user por instância).
- **`util`** — `Conexao` (única fábrica de `java.sql.Connection`, com URL/usuário/senha do SQL Server fixos no código) e `TesteConexao` (classe manual para testar a conectividade).
- **`view`** — os arquivos `.fxml` de cada tela e o `style.css` compartilhado.
- **`images`** — imagens estáticas (logo, ícones de usuário/senha, planos de fundo) usadas nos FXML.

---

## 4. ARQUITETURA

O README declara a seguinte arquitetura em camadas:

```
View (FXML)
 ↓
Controller
 ↓
Service
 ↓
DAO
 ↓
SQL Server
```

**Essa arquitetura é, no geral, confirmada pelo código**: todos os controllers instanciam e chamam classes `Service` (nunca `DAO` diretamente para operações de escrita/validação), e todos os `Service` instanciam e chamam classes `DAO`, que por sua vez usam `util.Conexao` para obter a conexão JDBC.

**Exceções/desvios encontrados em relação a essa arquitetura declarada:**

- `TelaNovoPedidoController.pesquisarSentenciado()` instancia `SentenciadoDAO` **diretamente** (`new SentenciadoDAO()`) para buscar um sentenciado por matrícula, **pulando a camada `SentenciadoService`**. É o único ponto do código onde um `Controller` acessa um `DAO` sem passar por um `Service`.
- `session.SessaoUsuario` também instancia `UsuarioDAO` diretamente (fora do padrão Controller→Service→DAO, mas isso é esperado, já que `SessaoUsuario` não é um Controller).
- Não existe uma camada de "Model" no sentido MVC (o pacote `model` é apenas o conjunto de entidades/DTOs, não contém lógica de apresentação).
- Não há injeção de dependência: cada `Controller`/`Service` cria suas próprias instâncias de `Service`/`DAO` via `new`.
- Não há transações explícitas entre múltiplas operações DAO (ex.: `PedidoExameDAO.inserir()` insere o pedido e os benefícios vinculados em um mesmo método, mas usando duas `PreparedStatement`s sem controle explícito de transação/commit-rollback conjunto — cada `Connection` obtida por `Conexao.conectar()` está, por padrão, em modo autocommit).

**Padrão de navegação de telas:** a aplicação usa dois mecanismos de composição de UI:
1. **Troca de `Scene`** inteira (usada em `LoginController` → `TelaPrincipal` e em `TelaPrincipalController.sair()` → `telaLoginMaior`).
2. **Substituição de conteúdo dentro de um container** (`VBox formContainer` dentro de um `StackPane`/`BorderPane`) — usada pelo menu lateral da `TelaPrincipal` para trocar entre "Exames Pendentes" e "Usuários" sem abrir nova janela.
3. **Janelas modais** (`new Stage()` com `Modality.APPLICATION_MODAL` ou `showAndWait()`) — usadas para: Novo Pedido, Cadastro de Sentenciado, Alterar Senha.

---

## 5. TELAS DO SISTEMA

Inventário de todos os arquivos `.fxml` encontrados em `src/br/com/sigec/view/`:

### 5.1 telaLoginMaior.fxml
- **Finalidade:** tela de login/autenticação.
- **Controller:** `LoginController`.
- **Principais componentes:** `TextField txtUsuario`, `PasswordField txtSenha`, `Button btnEntrar`, `Label lblErro` (inicialmente invisível).
- **Funcionalidades:** autentica usuário via `UsuarioService.autenticar()`; se sucesso, seta `SessaoUsuario` e troca a `Scene` para `TelaPrincipal.fxml`; se falha, exibe `lblErro`.
- **Como é acessada:** é a **primeira tela** carregada pela aplicação (`Main.java`).
- **Para onde navega:** `TelaPrincipal.fxml` (login bem-sucedido).

### 5.2 TelaPrincipal.fxml
- **Finalidade:** shell/casca principal do sistema após login (cabeçalho, menu lateral, área de conteúdo).
- **Controller:** `TelaPrincipalController`.
- **Principais componentes:** `BorderPane`; header com `Label lblUsuario` e `Hyperlink lnkAlterarSenha`; menu lateral (`VBox menuLateral`) com botões `btnInicio`, um botão "Sentenciados" **sem `fx:id` e sem `onAction`**, `btnUsuarios`, `btnSair`; área central `StackPane contentArea` contendo `VBox formContainer` onde as sub-telas são injetadas.
- **Funcionalidades:** ao inicializar, já chama automaticamente `abrirExamesPendentes()`; permite trocar para a tela de Usuários; permite abrir "Alterar Senha" (modal); permite sair (volta ao login).
- **Como é acessada:** carregada pelo `LoginController` após autenticação bem-sucedida.
- **Para onde navega:** `telaExamesPendentes.fxml` (padrão/Início), `cadastroUsuario.fxml` (Usuários), `telaAlterarSenha.fxml` (modal), `telaLoginMaior.fxml` (Sair).

### 5.3 telaExamesPendentes.fxml
- **Finalidade:** listar pedidos de exame pendentes (status `CADASTRADO`/`SOLICITADO`) e servir de ponto de partida para criar novos pedidos.
- **Controller:** `TelaExamesPendentesController`.
- **Principais componentes:** `TextField txtMatricula`, `Button btnPesquisar` (sem `onAction` no FXML), `TableView<PedidoExame> tabelaExames` com colunas Matrícula, Nome, Benefício (`colTipoBeneficio` — **declarada mas sem `setCellValueFactory` no controller**, portanto sempre vazia) e Data da Solicitação; botões `btnNovo`, `btnAtender` (sem `onAction`), `btnReiterar` (sem `onAction`).
- **Funcionalidades:** ao inicializar, carrega a lista de pedidos pendentes via `PedidoExameService.listarPendentes()` e popula a tabela (apenas colunas Matrícula, Nome e Data — a coluna Benefício fica sem dados); botão "NOVO" abre a tela de Novo Pedido em janela modal.
- **Como é acessada:** carregada por padrão dentro da `TelaPrincipal` (Início) e ao clicar em "Inicio" no menu lateral.
- **Para onde navega:** `telaNovoPedido.fxml` (modal, via botão NOVO).

### 5.4 telaNovoPedido.fxml
- **Finalidade:** cadastrar um novo Pedido de Exame vinculando um Sentenciado e um ou mais Benefícios.
- **Controller:** `TelaNovoPedidoController`.
- **Principais componentes:** `TextField txtSentenciado` (matrícula) + `Button btnPesquisar`; `Label lblSentenciado` (feedback do sentenciado encontrado); `DatePicker dpDataSolicitacao`; `TextField txtNumeroProcesso`; `ComboBox<Beneficio> cbBeneficio` + `Button btnAdicionar`; `ListView<Beneficio> listaBeneficios` + `Button btnRemover`; `Button btnCancelar`, `btnSalvar`.
- **Funcionalidades:** pesquisa sentenciado por matrícula (se não encontrado, oferece cadastro imediato via `telaCadastroSentenciado.fxml`); adiciona/remove benefícios da lista local (impede duplicados na própria lista); ao salvar, cria o `PedidoExame` com os benefícios selecionados via `PedidoExameService.inserir()` (que valida se já existe pedido ativo para o mesmo benefício/sentenciado).
- **Como é acessada:** modal aberta pelo botão "NOVO" da tela de Exames Pendentes.
- **Para onde navega:** `telaCadastroSentenciado.fxml` (modal, se sentenciado não encontrado).

### 5.5 telaCadastroSentenciado.fxml
- **Finalidade:** cadastro rápido de um novo Sentenciado.
- **Controller:** `TelaCadastroSentenciadoController`.
- **Principais componentes:** `TextField txtMatricula`, `TextField txtNome`, `Button btnCancelar`, `Button btnSalvar`.
- **Funcionalidades:** valida e insere o sentenciado via `SentenciadoService.inserir()`; fecha a janela ao salvar com sucesso; exibe alerta em caso de erro de validação.
- **Como é acessada:** modal aberta a partir da pesquisa de sentenciado em `telaNovoPedido.fxml`, quando a matrícula não é encontrada.
- **Para onde navega:** nenhuma (janela fecha após salvar/cancelar).

### 5.6 cadastroUsuario.fxml
- **Finalidade:** cadastro de novos usuários do sistema.
- **Controller:** `CadastroUsuarioController`.
- **Principais componentes:** `TextField txtNome`, `txtLogin`; `PasswordField txtSenha`, `txtConfirmarSenha`; `Button btnCadastrar`, `btnLimpar`.
- **Funcionalidades:** valida se senha e confirmação conferem; cadastra usuário via `UsuarioService.cadastrar()` (login único, senha com hash BCrypt); botão "Limpar" reseta os campos.
- **Como é acessada:** exibida dentro do `formContainer` da `TelaPrincipal` ao clicar em "Usuarios" no menu lateral.
- **Para onde navega:** nenhuma (fica embutida na tela principal).

### 5.7 telaAlterarSenha.fxml
- **Finalidade:** o usuário logado alterar a própria senha.
- **Controller:** `TelaAlterarSenhaController`.
- **Principais componentes:** `PasswordField txtSenhaAtual`, `txtNovaSenha`, `txtConfirmarSenha`; `Label lblMensagem` (feedback de sucesso/erro); `Button btnCancelar`, `btnSalvar`.
- **Funcionalidades:** valida senha atual (reautentica via `UsuarioService`), confere nova senha com confirmação, e persiste a nova senha (com novo hash) via `UsuarioService.alterarSenha()`.
- **Como é acessada:** modal aberta pelo link "Alterar Senha" no cabeçalho da `TelaPrincipal`.
- **Para onde navega:** nenhuma (janela modal, fecha ao cancelar; permanece aberta após salvar, mostrando mensagem de sucesso).

### 5.8 telaResumo.fxml *(não integrada ao fluxo atual — ver seção 6 e 10)*
- **Finalidade (pelo desenho da tela):** exibir o resumo detalhado de um Pedido de Exame: dados do sentenciado, dados da solicitação (data, processo, benefícios, situação, SEI, status, data de conclusão), exame criminológico (atendimento psicólogo e assistente social — profissional, data, laudo, status) e reiterações (data, observação); botões "Reiterar", "Concluir", "Fechar".
- **Controller:** `ResumoPedidoController`.
- **Principais componentes:** dezenas de `Label`s de rótulo/valor organizados em `VBox`/`HBox` dentro de um `ScrollPane`; três botões no rodapé (`btnReiterar`, `btnConcluir`, `btnFechar`).
- **Funcionalidades reais:** **nenhuma.** O FXML contém apenas valores estáticos de exemplo (ex.: `text="15/05/2026"`, `text="Nome do sentenciado"`, `text="Progressão de Regime"`), os três botões do rodapé **não têm `onAction`**, e o `ResumoPedidoController.initialize()` apenas imprime `"ResumoPedidoController carregado!"` no console — não busca nem popula nenhum dado real.
- **Como é acessada:** **não é acessada por nenhum ponto do código atual** — não há nenhuma chamada `FXMLLoader` referenciando `telaResumo.fxml` em nenhum controller do projeto (confirmado por busca textual em todo o código-fonte).
- **Para onde navega:** N/A (tela não está ligada ao fluxo).

### 5.9 telaBase.fxml *(sem controller — protótipo não referenciado)*
- **Finalidade (pelo desenho):** parece ser um protótipo anterior de layout de menu lateral (botões "Exames", "Sentenciados", "Profissionais", "Relatórios", "Usuários", "Sair"), com imagem de fundo (`bkpBase.png`) e um painel de conteúdo (`contentPane`) vazio.
- **Controller:** **nenhum** — este é o único FXML do projeto sem atributo `fx:controller`.
- **Principais componentes:** `Pane` raiz, `ImageView` de fundo, `Pane` de conteúdo (`contentPane`), 6 botões de menu (nenhum com `onAction`).
- **Funcionalidades:** nenhuma (nenhum botão está ligado a qualquer ação).
- **Como é acessada:** **não é referenciada por nenhum `FXMLLoader` em todo o código** (confirmado por busca textual).
- **Para onde navega:** N/A.

---

## 6. FUNCIONALIDADES

### Implementadas
(fluxo completo View → Controller → Service → DAO → banco, com tela funcional acionando a regra de negócio)

- **Login/autenticação** de usuário com verificação de senha via hash BCrypt (`LoginController` → `UsuarioService.autenticar()` → `UsuarioDAO.buscarPorLogin()`).
- **Cadastro de usuário** com validação de login/senha e geração de hash BCrypt (`CadastroUsuarioController` → `UsuarioService.cadastrar()`).
- **Alteração de senha** do usuário logado, exigindo confirmação da senha atual (`TelaAlterarSenhaController` → `UsuarioService.alterarSenha()`).
- **Cadastro de Sentenciado**, com validação de nome, matrícula (normalização de pontuação, apenas números, tamanho mínimo/máximo) e checagem de duplicidade de matrícula (`TelaCadastroSentenciadoController` → `SentenciadoService.inserir()`).
- **Pesquisa de Sentenciado por matrícula** (`TelaNovoPedidoController.pesquisarSentenciado()`, acessando `SentenciadoDAO` diretamente).
- **Cadastro de Pedido de Exame** vinculado a um Sentenciado e a um ou mais Benefícios, com validação para impedir pedido ativo duplicado para o mesmo sentenciado/benefício (`TelaNovoPedidoController.salvar()` → `PedidoExameService.inserir()`).
- **Listagem de pedidos de exame pendentes** (status `CADASTRADO` ou `SOLICITADO`), ocultando os demais status (`TelaExamesPendentesController.initialize()` → `PedidoExameService.listarPendentes()`).
- **Navegação principal** entre "Início" (Exames Pendentes) e "Usuários", além de logout.
- **Persistência JDBC (CRUD)** funcional e completa nos DAOs de: `Usuario`, `Sentenciado`, `Beneficio`, `PedidoExame` (+ `pedido_beneficio`), `Reiteracao`, `Entrevista`, `Profissional` — todos exercitados com sucesso (segundo as mensagens de log `"✅"`) pelas classes de teste manual correspondentes.

### Parcialmente implementadas
(regra de negócio e persistência prontas na camada Service/DAO, mas **sem tela/controller que as acione** na aplicação em execução)

- **Conclusão de Pedido de Exame** — `PedidoExameService.concluirPedido()` está totalmente implementado (exige número SEI preenchido e confirmação de que o pedido foi atendido tanto por psicólogo quanto por assistente social, via `EntrevistaDAO.foiAtendidoPorAmbos()`), mas **nenhum botão/controller do sistema o chama**. É exercitado apenas por `PedidoExameServiceTeste` (classe de teste manual).
- **Reiteração de Pedido** — `ReiteracaoService`/`ReiteracaoDAO` completos (inserir, atualizar, validações de status do pedido e de data), mas **sem tela/controller que os utilize**. O botão "REITERAR" existe em `telaExamesPendentes.fxml`, porém sem `onAction` definido.
- **Atendimento/Entrevista com profissional** — `EntrevistaService`/`EntrevistaDAO` completos e com regras de negócio (incluindo "atendido por ambos os tipos de profissional"), **sem tela/controller que os utilize** (botão "ATENDER" em `telaExamesPendentes.fxml` sem `onAction`). **Achado adicional (seção 7.2, confirmado a partir do schema real do banco):** além de faltar UI, o `EntrevistaDAO` está hoje **desalinhado com a estrutura real da tabela `entrevista`** — ele lê/grava uma coluna `data_entrevista` que não existe mais no banco, e não preenche as colunas `tipo_atendimento`/`status`, hoje obrigatórias. Ou seja, `inserir`/`atualizar`/`buscarPorId`/`listarTodos` de `EntrevistaDAO` não são apenas "sem tela" — são código que precisaria ser adaptado ao schema atual antes de voltar a funcionar. Apenas `foiAtendidoPorAmbos()` permanece compatível com o banco real.
- **Gestão de Benefícios** — `BeneficioService.inserir()`/`BeneficioDAO` completos (CRUD), mas **não existe nenhuma tela de cadastro de Benefício** no projeto. A única forma encontrada de inserir um benefício é via classe de teste manual `TesteBeneficioDAO` ou diretamente no banco.
- **Gestão de Profissionais** — `ProfissionalService.inserir()`/`ProfissionalDAO` completos (CRUD), mas **não existe nenhuma tela de cadastro/gestão de Profissional** no projeto.
- **Tela de Resumo do Pedido** (`telaResumo.fxml` + `ResumoPedidoController`) — layout visual completo, mas sem nenhuma lógica de carregamento de dados reais e sem estar conectada a nenhum ponto de navegação do sistema (ver item 5.8).

### Não utilizadas ou aparentemente abandonadas
- **`telaBase.fxml`** — sem `fx:controller`, sem nenhuma referência via `FXMLLoader` em todo o projeto; aparenta ser um protótipo de layout anterior, substituído pelo conjunto `TelaPrincipal.fxml` + `style.css`.
- **`UsuarioDAO.autenticar(login, senha)`** — método que compara a senha em texto puro diretamente via SQL (`WHERE senha = ?`). **Não é chamado em nenhum lugar do código** (o fluxo real de login usa `UsuarioService.autenticar()`, que compara com BCrypt). É código morto — e, se algum dia voltasse a ser usado, comprometeria a segurança da autenticação (ver seção 10).
- **Botão "Sentenciados"** no menu lateral de `TelaPrincipal.fxml` — presente na interface, mas sem `fx:id` e sem `onAction`; clicar nele não tem efeito.
- **Botão "Pesquisar" (`btnPesquisar`)** em `telaExamesPendentes.fxml` — presente, mas sem `onAction`; sem efeito ao clicar.
- **Coluna "Benefício" (`colTipoBeneficio`)** na tabela de `telaExamesPendentes.fxml` — existe no FXML e é referenciada no controller, mas nunca recebe `setCellValueFactory`; portanto sempre aparece vazia.
- **16 classes `Teste*`** (`Teste<Entidade>DAO` em `dao/` e `<Entidade>ServiceTeste` em `service/`, mais `TesteBcrypt` e `TesteConexao`) — não fazem parte do fluxo de produção da aplicação (não são chamadas pelos controllers); são classes com método `main()` usadas manualmente pelo autor para validar DAOs e Services durante o desenvolvimento. Não há evidência de que estejam "abandonadas" no sentido de obsoletas — parecem ser mantidas como scripts de verificação manual —, mas tecnicamente não são testes automatizados (não há JUnit no projeto).

### Planejadas/não implementadas
Não foram encontrados comentários `TODO`/`FIXME` explícitos no código apontando funcionalidades futuras específicas. A única evidência indireta de funcionalidades cogitadas é o próprio `telaBase.fxml` (protótipo abandonado), cujos botões sugerem que telas de "Sentenciados", "Profissionais" e "Relatórios" foram cogitadas em algum momento — mas isso é **inferência**, não uma funcionalidade documentada como planejada no código atual.

---

## 7. BANCO DE DADOS

**Fontes analisadas:** (1) `banco/scripts/01_criacao_banco.sql`, o script versionado dentro do projeto; (2) `banco/modelos/modelo_logico.drawio`, o diagrama lógico; e (3) um script gerado pelo SSMS ("Generate Scripts") **fornecido diretamente pelo usuário**, contendo a estrutura **real e atual** do banco `SIGEC` em 06/09/2026 (tabelas, chaves primárias, chaves estrangeiras e constraints de todos os objetos). A fonte (3) é tratada como a **verdade atual do banco**; onde ela diverge das fontes (1)/(2), isso é reportado explicitamente como divergência do projeto em relação ao banco real, e não mais como "não confirmado". Não houve conexão direta desta análise a um SQL Server — a fonte (3) foi extraída e fornecida pelo usuário a partir do seu próprio ambiente.

**Banco:** `SIGEC` (SQL Server, `CREATE DATABASE SIGEC`; compatibility level 170).

**Tabelas — estrutura REAL e ATUAL do banco (confirmada pelo script fornecido pelo usuário):**

| Tabela | Colunas | PK | FKs |
|---|---|---|---|
| `usuario` | id, nome VARCHAR(100) NOT NULL, login VARCHAR(100) NOT NULL UNIQUE, senha VARCHAR(255) NOT NULL, ativo BIT NOT NULL | id | — |
| `sentenciado` | id, matricula VARCHAR(**8**) NOT NULL UNIQUE (`UQ_SENTENCIADO_MATRICULA`), nome VARCHAR(100) NOT NULL | id | — |
| `beneficio` | id, descricao VARCHAR(100) NOT NULL UNIQUE, **sigla VARCHAR(20) NULL** | id | — |
| `profissional` | id, nome VARCHAR(100) NOT NULL, tipo VARCHAR(30) NOT NULL, ativo BIT NOT NULL | id | — |
| `pedido_exame` | id, id_sentenciado NOT NULL, data_cadastro NOT NULL, data_solicitacao NOT NULL, status VARCHAR(30) NOT NULL, id_usuario NOT NULL, numero_processo VARCHAR(100) NULL, numero_sei VARCHAR(100) NULL, **data_conclusao NULL** | id | id_sentenciado→sentenciado, id_usuario→usuario |
| `pedido_beneficio` | id_beneficio NOT NULL, id_pedido_exame NOT NULL | (id_beneficio, id_pedido_exame) composta | id_beneficio→beneficio, id_pedido_exame→pedido_exame |
| `reiteracao` | id, id_pedido_exame NOT NULL, data_reiteracao NOT NULL, id_usuario NOT NULL, observacoes VARCHAR(200) NULL, despacho VARCHAR(100) NULL, data_cadastro NOT NULL | id | id_pedido_exame→pedido_exame, id_usuario→usuario |
| `entrevista` | **id, id_pedido_exame NOT NULL, id_profissional NULL, data_agendamento DATE NULL, id_usuario NOT NULL, data_entrega_laudo DATE NULL, data_cadastro NOT NULL, data_realizacao DATE NULL, tipo_atendimento VARCHAR(30) NOT NULL, status VARCHAR(30) NOT NULL** | id | id_pedido_exame→pedido_exame, id_profissional→profissional, id_usuario→usuario |

**Constraints de checagem (`CHECK`) confirmadas — apenas na tabela `entrevista`:**
- `CK_entrevista_status`: `status` só aceita `'CANCELADA'`, `'REALIZADA'`, `'AGENDADA'` ou `'PENDENTE_AGENDAMENTO'`.
- `CK_entrevista_tipo_atendimento`: `tipo_atendimento` só aceita `'ASSISTENTE_SOCIAL'` ou `'PSICOLOGO'`.
(Nenhuma outra tabela do banco possui `CHECK` — por exemplo, `profissional.tipo`, que também deveria só aceitar `PSICOLOGO`/`ASSISTENTE_SOCIAL`, não tem essa restrição a nível de banco, dependendo inteiramente da validação em `ProfissionalService`.)

**Relacionamentos:** `pedido_exame` N:1 com `sentenciado` e `usuario`; `pedido_exame` N:N com `beneficio` via a tabela associativa `pedido_beneficio`; `reiteracao` e `entrevista` N:1 com `pedido_exame`; `entrevista` também N:1 com `profissional` (relação agora **opcional**, já que `id_profissional` é `NULL`-ável) e N:1 com `usuario`.

**Procedures, views, triggers:** **confirmado que não existem** — o script de schema completo fornecido pelo usuário cobre todos os objetos do banco (tabelas, chaves, constraints) e não contém nenhuma `PROCEDURE`, `VIEW` ou `TRIGGER`.

**Como o Java acessa o banco:** exclusivamente via JDBC puro (driver `mssql-jdbc`), através da classe única `util.Conexao`, que abre uma nova `Connection` por chamada (`DriverManager.getConnection`) com URL, usuário e senha fixos no código-fonte (ver seção 10, item de segurança). Cada método de cada DAO abre e fecha sua própria conexão via `try-with-resources`; não há *connection pool*.

### 7.1 O script `01_criacao_banco.sql` do projeto está desatualizado (agora confirmado)

Comparando o script versionado no projeto com a estrutura real do banco:

1. **`beneficio.sigla` existe de fato no banco real** (VARCHAR(20), NULL) — o script do projeto não a tem. **Confirmado**: o script está desatualizado; o código (`Beneficio.java`, `BeneficioDAO`, `BeneficioService`) está alinhado com o banco real, não com o script.
2. **`pedido_exame.data_conclusao` é `NULL`-ável no banco real** — o script do projeto a declara `NOT NULL`. **Confirmado**: o script está desatualizado nesse ponto; o código (`PedidoExameDAO.inserir()`, que insere `NULL` nesse campo) está correto em relação ao banco real.
3. **`sentenciado.matricula` é `VARCHAR(8)` no banco real**, não `VARCHAR(11)` como no script do projeto. Não é um problema prático — `SentenciadoService` limita a matrícula normalizada a no máximo 7 caracteres — mas é mais uma evidência de que o script está desatualizado.
4. **Coluna `entrevista.laudo_entregue` não existe no banco real** (o script do projeto a tem). Ou seja, o ponto anteriormente levantado ("coluna existe mas não é usada pelo código") está superado: a coluna aparentemente foi removida do banco em algum momento, junto com a reformulação mais ampla da tabela `entrevista` descrita a seguir.

### 7.2 Achado crítico: a tabela `entrevista` foi reformulada no banco, e o código Java NÃO foi atualizado

Esta é a divergência mais importante encontrada nesta revisão. A tabela `entrevista` no banco real tem uma estrutura **significativamente diferente** da que `Entrevista.java`, `EntrevistaDAO.java` e `EntrevistaService.java` esperam:

| No banco real (hoje) | No código Java (`Entrevista`/`EntrevistaDAO`) |
|---|---|
| `data_agendamento` (DATE, NULL) | **não existe no model nem é lida/gravada pelo DAO** |
| `data_realizacao` (DATE, NULL) | **não existe no model nem é lida/gravada pelo DAO** |
| `tipo_atendimento` (VARCHAR(30), **NOT NULL**, restrita por CHECK a `PSICOLOGO`/`ASSISTENTE_SOCIAL`) | **não existe no model nem é gravada pelo DAO** |
| `status` (VARCHAR(30), **NOT NULL**, restrita por CHECK a `AGENDADA`/`REALIZADA`/`CANCELADA`/`PENDENTE_AGENDAMENTO`) | **não existe no model nem é gravada pelo DAO** |
| `id_profissional` agora **NULL**-ável | model/DAO tratam `Profissional` como sempre presente (`entrevista.getProfissional().getId()` sem checagem de nulo) |
| **não existe mais uma coluna `data_entrevista`** | `EntrevistaDAO` lê/grava `data_entrevista` em **todos** os seus métodos (`inserir`, `atualizar`, `buscarPorId`, `listarTodos`) |

**Consequência concreta:** todo SQL de `EntrevistaDAO` que referencia `data_entrevista` (`inserir`, `atualizar`, `buscarPorId`, `listarTodos`) tentaria acessar uma coluna que **não existe mais** no banco real — isso faria o SQL Server rejeitar a instrução com um erro de "nome de coluna inválido" antes mesmo de considerar que as colunas `tipo_atendimento` e `status` (obrigatórias, sem valor padrão) também não são preenchidas por essas instruções. Ou seja, com o banco no estado atual, **`EntrevistaDAO.inserir()`, `.atualizar()`, `.buscarPorId()` e `.listarTodos()` não funcionariam** se fossem executados agora. Isso é uma conclusão baseada em comparação direta entre o SQL escrito no DAO e a estrutura real da tabela — **não foi executado nenhum código Java contra o banco nesta análise**, mas a incompatibilidade de nomes de coluna é direta e verificável nos dois arquivos-fonte.

O único método de `EntrevistaDAO` que **continua compatível** com o banco real é `foiAtendidoPorAmbos(idPedido)`, pois sua consulta (`SELECT COUNT(DISTINCT p.tipo) FROM entrevista e INNER JOIN profissional p ON e.id_profissional = p.id WHERE e.id_pedido_exame = ?`) não referencia nenhuma das colunas novas/removidas — por isso `PedidoExameService.concluirPedido()` continua utilizável no que depende dessa consulta específica.

**Interpretação (inferência, coerente com os achados acima):** o banco foi evoluído para representar um fluxo de "agendamento → realização" do atendimento (com `status` controlando esse ciclo e `tipo_atendimento` guardando explicitamente se é psicólogo ou assistente social, em vez de depender apenas do `profissional.tipo` via join), mas essa evolução do schema **ainda não foi acompanhada por uma atualização de `Entrevista.java`, `EntrevistaDAO.java` e `EntrevistaService.java`**. Isso é consistente com o fato de a funcionalidade de "Atender" não ter nenhuma tela funcional na interface (seção 6): o código de apoio a essa funcionalidade não está apenas sem UI — ele está, no estado atual, desalinhado com o schema do banco que passaria a usar.

---

## 8. PRINCIPAIS CLASSES

*(getters/setters triviais omitidos)*

- **`Main`** — ponto de entrada JavaFX (`extends Application`); carrega `telaLoginMaior.fxml` na inicialização e define o ícone/título da janela. Depende de `FXMLLoader`.

- **`util.Conexao`** — responsabilidade única: criar uma `java.sql.Connection` para o SQL Server (`conectar()`). Concentra URL, usuário e senha. É dependência direta de **todos** os DAOs.

- **`session.SessaoUsuario`** — mantém, em um campo `static`, o `Usuario` atualmente logado (`getUsuarioLogado`/`setUsuarioLogado`). **Ponto de atenção:** o campo é inicializado estaticamente com `usuarioDAO.buscarPorId(1)` — ou seja, ao carregar a classe pela primeira vez (antes de qualquer login), o sistema já tenta buscar o usuário de id 1 no banco (ver seção 10). Usada por `PedidoExame`, `Reiteracao`, `Entrevista` (para preencher o usuário responsável automaticamente) e pelos controllers de tela principal/logout.

- **`UsuarioService`** — responsável por cadastro, autenticação (`autenticar`, com `BCrypt.checkpw`) e troca de senha (`alterarSenha`) de usuários; aplica validações de tamanho mínimo de login/senha e unicidade de login. Depende de `UsuarioDAO` e `SessaoUsuario`.

- **`PedidoExameService`** — a classe de regra de negócio mais rica do sistema: `inserir()` (valida sentenciado/usuário/data, impede pedido ativo duplicado por benefício), `concluirPedido()` (valida SEI e atendimento por ambos profissionais antes de mudar status para `CONCLUIDO`), `listarPendentes()`. Depende de `PedidoExameDAO` e `EntrevistaDAO`.

- **`SentenciadoService`** — valida e normaliza nome e matrícula do sentenciado (remove pontuação, exige apenas números, checa duplicidade). Depende de `SentenciadoDAO`.

- **`EntrevistaService`** — valida uma entrevista/atendimento antes de inserir/atualizar: pedido não pode estar `CANCELADO`/`CONCLUIDO`/`TRANSFERIDO`, profissional obrigatório, usuário ativo, data da entrevista não pode ser anterior ao pedido nem futura. Depende de `EntrevistaDAO`.

- **`ReiteracaoService`** — valida uma reiteração de forma análoga (status do pedido, datas, usuário ativo, tamanho da observação). Depende de `ReiteracaoDAO`.

- **`BeneficioService`** / **`ProfissionalService`** — validações de cadastro (descrição/sigla do benefício; nome/tipo do profissional, incluindo checagem de que o nome não contém números). Dependem de `BeneficioDAO`/`ProfissionalDAO` respectivamente.

- **`PedidoExameDAO`** — o DAO mais complexo: monta `PedidoExame` a partir de *joins* com `usuario` e `sentenciado`; possui `listarPendendes()` (nome com erro de digitação — ver seção 10), `existePedidoAtivoParaSentenciadoEBeneficio()` (usada pela regra de negócio de duplicidade).

- **`EntrevistaDAO`** — inclui o método `foiAtendidoPorAmbos(idPedido)`, que conta tipos distintos de profissional que já atenderam o pedido — peça-chave da regra de conclusão de pedido, e o único método da classe compatível com o schema real da tabela `entrevista`. Os demais métodos (`inserir`, `atualizar`, `buscarPorId`, `listarTodos`) referenciam a coluna `data_entrevista`, que não existe mais na tabela real (ver seção 7.2) — estão desatualizados em relação ao banco atual.

- **`LoginController`** — controla a tela de login; depende de `UsuarioService` e `SessaoUsuario`; ao autenticar com sucesso, troca a `Scene` inteira para `TelaPrincipal.fxml`.

- **`TelaPrincipalController`** — controla o "shell" da aplicação pós-login; gerencia a troca de conteúdo no `formContainer` e a navegação para Usuários/Exames/Alterar Senha/Sair. Depende de `SessaoUsuario`.

- **`TelaNovoPedidoController`** — a lógica de UI mais complexa do projeto: gerencia pesquisa de sentenciado (com fluxo de cadastro inline), seleção de múltiplos benefícios em uma `ListView` local, e a montagem final do objeto `PedidoExame` com sua lista de `PedidoBeneficio` antes de delegar ao `PedidoExameService`.

---

## 9. PRINCIPAIS FLUXOS

Apenas fluxos **confirmados no código** estão documentados abaixo.

**Login:**
`telaLoginMaior.fxml` → `LoginController.entrar()` → `UsuarioService.autenticar(login, senha)` → `UsuarioDAO.buscarPorLogin(login)` (SQL filtra `ativo = 1`) → `BCrypt.checkpw(senha, hash)` → se válido, `SessaoUsuario.setUsuarioLogado(usuario)` e troca de `Scene` para `TelaPrincipal.fxml`; se inválido, exibe `lblErro`.

**Navegação principal (pós-login):**
`TelaPrincipalController.initialize()` chama automaticamente `abrirExamesPendentes()`, que carrega `telaExamesPendentes.fxml` dentro do `formContainer`. O menu lateral permite alternar para `abrirUsuarios()` (carrega `cadastroUsuario.fxml` no mesmo container) ou `sair()` (troca a `Scene` de volta para o login, limpando a sessão).

**Cadastro de usuário:**
`cadastroUsuario.fxml` → `CadastroUsuarioController.cadastrar()` (confere senha == confirmação) → `UsuarioService.cadastrar()` (valida login/senha, checa login duplicado, gera hash BCrypt) → `UsuarioDAO.inserir()`.

**Consulta de pedidos pendentes:**
`TelaExamesPendentesController.initialize()` → `PedidoExameService.listarPendentes()` → `PedidoExameDAO.listarPendendes()` (SQL com `status IN ('CADASTRADO','SOLICITADO')` e *joins* com `usuario`/`sentenciado`) → popula `TableView`.

**Cadastro de Pedido de Exame (com Sentenciado e Benefícios):**
Botão "NOVO" em Exames Pendentes → abre `telaNovoPedido.fxml` (modal) →
1. `pesquisarSentenciado()`: busca por matrícula direto via `SentenciadoDAO`. Se não encontrado, oferece cadastro imediato (`Alert` de confirmação) → abre `telaCadastroSentenciado.fxml` (modal) → `SentenciadoService.inserir()`.
2. Usuário adiciona benefícios (`ComboBox` populado por `BeneficioService.listarTodos()`) a uma `ListView` local, com checagem de duplicidade **na lista local** (não consulta o banco neste ponto).
3. `salvar()`: monta `PedidoExame` + lista de `PedidoBeneficio`, chama `PedidoExameService.inserir()`, que valida sentenciado/usuário/data e a ausência de pedido ativo duplicado por benefício (`PedidoExameDAO.existePedidoAtivoParaSentenciadoEBeneficio`), define status inicial `CADASTRADO`, e persiste via `PedidoExameDAO.inserir()` (que insere o pedido e, em sequência, cada vínculo em `pedido_beneficio`).

**Cadastro de Sentenciado (fluxo isolado):**
`telaCadastroSentenciado.fxml` → `TelaCadastroSentenciadoController.salvar()` → `SentenciadoService.inserir()` (normaliza e valida nome/matrícula, checa duplicidade) → `SentenciadoDAO.inserir()`.

**Alteração de senha:**
`telaAlterarSenha.fxml` → `TelaAlterarSenhaController.salvar()` → `UsuarioService.alterarSenha(senhaAtual, novaSenha)` (reautentica o usuário logado com a senha atual, valida a nova senha, gera novo hash) → `UsuarioDAO.atualizar()`.

**Fluxos existentes apenas na camada de regra de negócio (sem UI que os acione — ver seções 6 e 10):** reiteração (`ReiteracaoService`/`ReiteracaoDAO`), conclusão de pedido (`PedidoExameService.concluirPedido`), resumo de pedido (`ResumoPedidoController`, sem carregamento de dados). O atendimento/entrevista (`EntrevistaService`/`EntrevistaDAO`) também está nessa situação, mas com uma ressalva a mais: além de não ter UI, a maior parte do `EntrevistaDAO` está desalinhada com o schema real da tabela `entrevista` (seção 7.2) e não funcionaria se fosse chamada hoje, exceto o método `foiAtendidoPorAmbos()`, usado por `concluirPedido()`.

---

## 10. ESTADO ATUAL

### O que está funcionando/implementado
- Login com autenticação segura (hash BCrypt).
- CRUD completo (a nível de DAO) para as 8 entidades principais.
- Cadastro de usuário e alteração de senha, ponta a ponta (tela → banco).
- Cadastro de sentenciado, ponta a ponta, com validações de negócio.
- Cadastro de pedido de exame vinculado a sentenciado e benefício(s), ponta a ponta, com regra de não duplicidade.
- Listagem de pedidos pendentes na tela inicial.
- Layout visual consistente e "moderno" (dashboard style), aplicado via `style.css` compartilhado.
- Persistência 100% JDBC manual, sem ORM, conforme proposta pedagógica do README.

### O que está parcialmente implementado
- Backend completo (Service + DAO, com regras de negócio inclusive) para **reiteração** e **conclusão de pedido** — mas nenhuma das duas tem uma tela funcional que as acione. Os botões correspondentes existem na UI (`REITERAR` em Exames Pendentes; `Reiterar`, `Concluir` em `telaResumo.fxml`) mas não têm `onAction`.
- **Atendimento/entrevista** está em uma situação pior que "só falta UI": o backend (`EntrevistaService`/`EntrevistaDAO`) foi escrito para uma versão anterior do schema da tabela `entrevista` e **não é compatível com a estrutura real do banco hoje** (ver seção 7.2) — precisaria ser reescrito (model, DAO e provavelmente a validação do Service) antes mesmo de cogitar ligar uma tela a ele.
- `telaResumo.fxml`: layout completo, sem dados reais nem navegação até ela.

### O que parece faltar
- Telas de cadastro/gestão de **Benefício** e de **Profissional** (o backend existe, a UI não).
- Uma tela de gestão de **Sentenciados** fora do fluxo de "novo pedido" (o botão "Sentenciados" no menu não tem ação).
- Uma tela de **Relatórios** (mencionada apenas no protótipo abandonado `telaBase.fxml` — inferência, não confirmado como plano ativo).
- *Wiring* (ligação `onAction`) dos botões "Pesquisar" (Exames Pendentes), "Atender" e "Reiterar".
- Carregamento de dados reais em `telaResumo.fxml`, e uma forma de navegar até ela a partir de algum ponto do sistema.
- Preenchimento da coluna "Benefício" na tabela de Exames Pendentes (`colTipoBeneficio` sem `setCellValueFactory`).

### Problemas ou inconsistências encontradas
1. **[CRÍTICO, confirmado via schema real do banco] `EntrevistaDAO` incompatível com a tabela `entrevista` atual.** A tabela real tem `data_agendamento`, `data_realizacao`, `tipo_atendimento` (NOT NULL) e `status` (NOT NULL), e não tem mais `data_entrevista` nem `laudo_entregue`. O código Java (`Entrevista.java`, `EntrevistaDAO.java`) ainda é todo escrito em cima da coluna `data_entrevista` (que não existe mais) e não conhece as colunas novas. Na prática, `inserir`, `atualizar`, `buscarPorId` e `listarTodos` de `EntrevistaDAO` não funcionariam se executados contra o banco atual. Só `foiAtendidoPorAmbos()` continua compatível. Ver seção 7.2 para o detalhamento completo.
2. **Credencial do banco de dados em texto puro no código-fonte** (`util/Conexao.java`), sem uso de variável de ambiente ou arquivo de configuração externo. O `.gitignore` do projeto não exclui esse arquivo — portanto, se ele já foi commitado ao Git em algum momento, a senha estará no histórico do repositório. **Não confirmado** (não foi examinado o histórico de commits nesta análise) se isso já ocorreu; recomenda-se verificar manualmente (`git log -p -- src/br/com/sigec/util/Conexao.java`) e, havendo exposição, trocar a senha do banco.
3. **Script `01_criacao_banco.sql` do projeto está desatualizado em relação ao banco real (confirmado)** — ver seção 7.1: falta `beneficio.sigla` (que existe no banco real), declara `pedido_exame.data_conclusao` como `NOT NULL` (no banco real é `NULL`-ável), declara `sentenciado.matricula` como `VARCHAR(11)` (no banco real é `VARCHAR(8)`) e ainda traz uma versão antiga de `entrevista` (sem `data_agendamento`/`data_realizacao`/`tipo_atendimento`/`status`, com `laudo_entregue` que não existe mais). Recomenda-se substituir esse script por um novo, gerado a partir do banco real.
4. **`ProfissionalDAO.atualizar()`** monta a instrução `UPDATE profissional SET (nome = ?,tipo=? ,ativo=? ) WHERE id =?` — essa sintaxe com parênteses ao redor da lista de atribuições **não é válida** para um `UPDATE` simples em T-SQL. **Não confirmado** se esse método já foi executado com sucesso (não há classe de teste manual para `ProfissionalDAO.atualizar` no projeto); é um risco concreto de falha em tempo de execução.
5. **`SessaoUsuario` busca o usuário de id 1 em um inicializador estático**, executado assim que a classe é referenciada pela primeira vez — antes de qualquer tela de login. Isso acopla a aplicação à existência de um registro específico (`id = 1`) na tabela `usuario` e pode lançar exceção se esse registro não existir ou o banco estiver inacessível nesse momento.
6. **`UsuarioDAO.autenticar(login, senha)`** compara senha em texto puro via SQL — método morto (não utilizado pelo fluxo real), mas presente no código; risco caso seja reativado por engano no futuro.
7. **Inconsistência de nomenclatura:** `PedidoExameDAO.listarPendendes()` (com erro de digitação) é o método efetivamente chamado por `PedidoExameService.listarPendentes()` — apenas uma questão de nome interno, sem efeito funcional.
8. **Mismatch de metadados JavaFX:** os arquivos FXML declaram `xmlns="http://javafx.com/javafx/26"`, enquanto as bibliotecas JavaFX do projeto são da versão `17.0.12`. Provavelmente reflexo da versão do Scene Builder usada para editar os FXML sendo mais nova que o runtime configurado; **não confirmado** se isso causa qualquer problema em tempo de execução.
9. **Telas órfãs:** `telaBase.fxml` (sem controller) e `telaResumo.fxml`/`ResumoPedidoController` não são referenciadas por nenhum `FXMLLoader` no código atual.
10. **`profissional.tipo` sem `CHECK` constraint no banco**, ao contrário de `entrevista.tipo_atendimento` (que tem `CK_entrevista_tipo_atendimento`, restrita a `PSICOLOGO`/`ASSISTENTE_SOCIAL`). A integridade de `profissional.tipo` depende inteiramente da validação em `ProfissionalService` — não é reforçada pelo banco.

### Código aparentemente antigo ou não utilizado
- `telaBase.fxml` (protótipo de menu, substituído por `TelaPrincipal.fxml`).
- `UsuarioDAO.autenticar(login, senha)` (código morto/vestigial).
- Botões sem ação: "Sentenciados" (menu principal), "Pesquisar" (Exames Pendentes).
- As 16 classes `Teste*`/`*ServiceTeste` (mais `TesteBcrypt`, `TesteConexao`) não fazem parte do runtime de produção — são, aparentemente, ferramentas de verificação manual mantidas intencionalmente pelo autor durante o desenvolvimento, não testes automatizados nem código de produção esquecido.

---

## 11. CONFIGURAÇÃO E EXECUÇÃO

O que é necessário para executar o projeto atualmente, com base exclusivamente no que foi encontrado:

- **JDK:** 21 (`.idea/misc.xml`: `languageLevel="JDK_21"`, `project-jdk-name="ms-21"` — nome sugere Microsoft Build of OpenJDK 21, mas isso depende de como o SDK "ms-21" está configurado localmente no IntelliJ do usuário; **não confirmado** o instalador/distribuição exata).
- **JavaFX:** 17.0.12, build "win" (Windows) — os 4 jars (`javafx-base`, `javafx-graphics`, `javafx-controls`, `javafx-fxml`) são referenciados a partir do repositório Maven local do usuário (`$USER_HOME$/.m2/repository/org/openjfx/...`), **não** de uma pasta dentro do projeto. Ou seja, esses jars precisam já existir na máquina (tipicamente baixados via Maven em algum momento), mesmo o projeto não usando Maven como build tool.
- **Driver JDBC:** `mssql-jdbc-13.4.0.jre11.jar`, incluído diretamente em `lib/` dentro do próprio projeto (portanto, portátil — não depende do `.m2`).
- **jBCrypt:** versão 0.4, também referenciado do repositório Maven local do usuário.
- **Classe principal (Main class):** `br.com.sigec.Main`.
- **Argumentos de VM necessários** (conforme `.idea/runConfigurations/Main.xml`):
  ```
  --module-path "<caminho para os 4 jars do JavaFX 17.0.12 win no .m2 local>" --add-modules javafx.controls,javafx.fxml
  ```
- **Banco de dados:** SQL Server acessível em `localhost:1433`, com um banco chamado `SIGEC`, usuário `sa`, com a senha configurada em `util/Conexao.java`. Conexão configurada com `encrypt=true;trustServerCertificate=true` (certificado do servidor não é validado — aceitável apenas em ambiente de desenvolvimento local). **Importante:** o script `banco/scripts/01_criacao_banco.sql` versionado no projeto **está confirmadamente desatualizado** (seção 7.1) — recriar o banco a partir dele geraria uma estrutura diferente da que o código realmente usa hoje (faltaria `beneficio.sigla`; `entrevista` ficaria com a estrutura antiga). Para reproduzir o ambiente em outra máquina, é necessário um script atualizado a partir do banco real, não o script atualmente versionado.
- **IDE:** o projeto está fortemente acoplado ao IntelliJ IDEA (arquivo `.iml`, pasta `.idea/` com SDK, bibliotecas e run configuration específicos). **Não há** wrapper Maven (`mvnw`), Gradle (`gradlew`) nem qualquer script de build alternativo (`.sh`/`.bat`) encontrado no projeto — rodar fora do IntelliJ exigiria reproduzir manualmente o classpath e os argumentos de VM acima.
- **Não confirmado:** se existe, no banco em uso, um usuário com `id = 1` pré-cadastrado — necessário para que `SessaoUsuario` não lance exceção ao ser referenciada pela primeira vez (ver seção 10, item 4).

---

## 12. MAPA DO SISTEMA

Mapa das partes principais, usando os nomes reais encontrados no projeto:

```
Main.java
  ↓
telaLoginMaior.fxml → LoginController
  ↓ (autenticar)
UsuarioService.autenticar() → UsuarioDAO.buscarPorLogin() → tabela usuario
  ↓ (sucesso)
TelaPrincipal.fxml → TelaPrincipalController
  ↓ (abrirExamesPendentes, padrão)
telaExamesPendentes.fxml → TelaExamesPendentesController
  ↓ (listarPendentes)
PedidoExameService.listarPendentes() → PedidoExameDAO.listarPendendes()
  → tabelas pedido_exame + usuario + sentenciado (JOIN)
  ↓ (botão NOVO)
telaNovoPedido.fxml → TelaNovoPedidoController
  ├─ pesquisarSentenciado() → SentenciadoDAO.buscarPorMatricula() → tabela sentenciado
  │     ↓ (não encontrado)
  │   telaCadastroSentenciado.fxml → TelaCadastroSentenciadoController
  │     → SentenciadoService.inserir() → SentenciadoDAO.inserir() → tabela sentenciado
  ├─ cbBeneficio ← BeneficioService.listarTodos() → BeneficioDAO.listarTodos() → tabela beneficio
  └─ salvar() → PedidoExameService.inserir()
        → PedidoExameDAO.existePedidoAtivoParaSentenciadoEBeneficio() (validação)
        → PedidoExameDAO.inserir() → tabelas pedido_exame + pedido_beneficio

TelaPrincipal (menu lateral)
  ├─ btnUsuarios → cadastroUsuario.fxml → CadastroUsuarioController
  │     → UsuarioService.cadastrar() → UsuarioDAO.inserir() → tabela usuario
  ├─ lnkAlterarSenha → telaAlterarSenha.fxml → TelaAlterarSenhaController
  │     → UsuarioService.alterarSenha() → UsuarioDAO.atualizar() → tabela usuario
  └─ btnSair → volta para telaLoginMaior.fxml

[Backend pronto, sem UI que aciona — ver seções 6/9/10]
PedidoExameService.concluirPedido() → EntrevistaDAO.foiAtendidoPorAmbos() → tabelas entrevista + profissional  (este método está OK com o schema atual)
EntrevistaService / EntrevistaDAO (inserir/atualizar/buscarPorId/listarTodos) → tabela entrevista  [DESATUALIZADO em relação ao schema real — seção 7.2]
ReiteracaoService / ReiteracaoDAO → tabela reiteracao
telaResumo.fxml → ResumoPedidoController (sem dados)
```

---

## 13. PONTOS IMPORTANTES PARA RETOMAR O DESENVOLVIMENTO

### 1. Muito importante
- **Reescrever `Entrevista.java`/`EntrevistaDAO.java`/`EntrevistaService.java`** para o schema real e atual da tabela `entrevista` (`data_agendamento`, `data_realizacao`, `tipo_atendimento`, `status`, `id_profissional` opcional) — hoje esse código não é compatível com o banco (seção 7.2). Isso é pré-requisito para qualquer trabalho em "Atender"/agendamento de exame criminológico, que parece ser uma funcionalidade central do sistema.
- **Atualizar (ou substituir) `banco/scripts/01_criacao_banco.sql`** a partir de um script gerado do banco real — o script atual do projeto está confirmadamente desatualizado em vários pontos (seção 7.1) e não deve ser usado para recriar o ambiente.
- **Decidir e implementar a ligação das telas com o backend já pronto**: Reiterar e Concluir Pedido já têm backend funcional e só precisam de UI — é a lacuna mais rápida de fechar. Atender depende primeiro do item acima.
- **Verificar `ProfissionalDAO.atualizar()`** — a query como está parece sintaticamente inválida para T-SQL; testar antes de depender dela.
- **Tratar a credencial do banco**: mover para fora do código-fonte versionado (variável de ambiente, arquivo de configuração ignorado pelo Git) e verificar se já não foi exposta no histórico do Git.

### 2. Importante
- **Revisar `SessaoUsuario`** — o carregamento estático do usuário `id = 1` no carregamento da classe é um padrão frágil; entender por que foi feito assim e se é intencional (ex.: usuário "sistema" padrão) antes de mexer.
- **Decidir o destino de `telaResumo.fxml`** — ela parece ser a tela natural para reunir Atender/Reiterar/Concluir; vale considerar reaproveitá-la como o hub dessas ações, em vez de wire-ar os botões da tabela de Exames Pendentes separadamente.
- **Criar telas de gestão de Benefício e Profissional** — sem elas, essas tabelas só podem ser populadas manualmente/via classe de teste.
- **Remover ou wire-ar** os botões sem ação ("Sentenciados" no menu, "Pesquisar" em Exames Pendentes) e preencher a coluna "Benefício" da tabela de pendentes.

### 3. Secundário
- Remover ou documentar explicitamente o propósito de `telaBase.fxml` e de `UsuarioDAO.autenticar()` (código morto).
- Corrigir o nome `listarPendendes` → `listarPendentes` no DAO, por consistência.
- Avaliar se vale introduzir um framework de teste (JUnit) para substituir as classes `Teste*`/`*ServiceTeste` por testes automatizados reais — hoje a cobertura de regressão depende de rodar manualmente essas classes e ler o console.
- Considerar adicionar controle de transação explícito em operações que envolvem múltiplas tabelas (ex.: inserção de `pedido_exame` + `pedido_beneficio`).

---

## 14. RESUMO EXECUTIVO

O SIGEC está em um estado de **MVP funcional parcial**: o esqueleto arquitetural (View/FXML → Controller → Service → DAO → SQL Server, via JDBC puro, sem framework) está implementado de forma consistente e é seguido na grande maioria do código, e o fluxo central do sistema — **login, cadastro de usuário, cadastro de sentenciado e criação de um pedido de exame vinculado a benefícios, com listagem dos pedidos pendentes** — funciona ponta a ponta, com validações de negócio razoavelmente maduras (duplicidade de matrícula, duplicidade de pedido ativo por benefício, hashing de senha com BCrypt).

O ponto que mais chama atenção nesta revisão, feita já com a estrutura real e atual do banco em mãos, é que a tabela `entrevista` foi evoluída no banco (agora com `status`, `tipo_atendimento`, `data_agendamento` e `data_realizacao`, refletindo um fluxo explícito de agendamento → realização do atendimento), mas o código Java correspondente (`Entrevista`, `EntrevistaDAO`, `EntrevistaService`) ainda é o da versão anterior do schema e **não é compatível com o banco de hoje** — a coluna que ele espera (`data_entrevista`) não existe mais. Isso é mais sério do que "falta UI": é um trabalho de código pendente antes mesmo de cogitar uma tela de atendimento. Reiteração e Conclusão de Pedido, por outro lado, têm backend íntegro e compatível com o banco atual — só falta ligar uma tela a eles. Da mesma forma, não existem telas de cadastro para `Benefício` e `Profissional`, o que significa que, hoje, popular essas duas tabelas exige rodar código manualmente ou inserir dados direto no banco.

As partes mais importantes do sistema para retomar o trabalho são: (1) a camada `service`, que concentra praticamente todas as regras de negócio reais e é a parte mais sólida e completa do projeto (com a exceção pontual de `Entrevista`, hoje desatualizada); (2) a tabela `pedido_exame` e seu ciclo de status (`CADASTRADO` → `SOLICITADO` → `CONCLUIDO`/`CANCELADO`/`TRANSFERIDO`), que é o eixo central do domínio; (3) a nova estrutura da tabela `entrevista`, que precisa ser refletida no código antes de qualquer avanço no fluxo de atendimento; e (4) `telaExamesPendentes.fxml`, que é hoje o hub de navegação real do sistema (mesmo tendo botões inertes).

Antes de qualquer nova funcionalidade, vale revisar quatro pontos que podem gerar surpresas: (a) a incompatibilidade confirmada entre `EntrevistaDAO` e a tabela `entrevista` real (seção 7.2) — a mais urgente das quatro; (b) o fato de que `banco/scripts/01_criacao_banco.sql`, o script versionado no projeto, está desatualizado em vários pontos e não deve ser usado para recriar o ambiente; (c) a instrução de `UPDATE` em `ProfissionalDAO.atualizar()`, que parece sintaticamente inválida e nunca foi exercitada por nenhuma classe de teste; e (d) a exposição da senha do banco em texto puro no código-fonte versionado. Nenhum desses pontos foi corrigido nesta análise — apenas registrados, conforme solicitado.
