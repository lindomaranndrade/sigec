# RELATÓRIO DE ESTADO ATUAL — SIGEC

**Data da análise:** 06/09/2026 (revisada três vezes no mesmo dia: 1ª revisão após confirmação do schema real do banco; 2ª revisão após o usuário reportar que a adequação de `Entrevista`/`EntrevistaDAO`/`EntrevistaService` havia sido executada — baseada apenas nesse relato, sem releitura de código; 3ª revisão — esta — após acesso direto e leitura dos arquivos atualizados na pasta do projeto no computador do usuário, confirmando o que a 2ª revisão havia tratado como reportado)
**Projeto analisado:** `C:\Users\Lindomar\Documents\SIGEC\SIGEC`
**Método:** leitura direta dos arquivos do projeto (código-fonte, FXML, arquivos de configuração do IntelliJ e do Git), via acesso de leitura à pasta do projeto no computador do usuário. Nenhum arquivo do projeto foi alterado, criado ou excluído durante esta análise (em nenhuma das três revisões) — apenas cópias de leitura foram utilizadas para exame. O único arquivo criado foi este relatório. Adicionalmente, o usuário forneceu um script `.sql` gerado pelo SSMS ("Generate Scripts") com a estrutura **real e atual** do banco `SIGEC` (tabelas, chaves e constraints), que foi usado para confirmar ou corrigir os pontos que a versão inicial deste relatório havia marcado como "Não confirmado" a partir apenas do script `banco/scripts/01_criacao_banco.sql` versionado no projeto (esse script se mostrou desatualizado — ver seção 7).

Este relatório reflete **exclusivamente** o que foi encontrado no código do projeto e nesse script de schema real. Onde ainda não foi possível confirmar algo, isso está marcado explicitamente como **"Não confirmado"**. Trechos marcados como *inferência* são conclusões do analista a partir de evidências indiretas, não fatos diretamente lidos no código/schema.

> **⚠️ Nota sobre a 3ª revisão (importante para a disciplina fato vs. inferência deste relatório):** a 2ª revisão havia atualizado o relatório com base apenas no relato do usuário de que a adequação de Entrevista (especificada em `PROMPT_ADEQUACAO_ENTREVISTA_SIGEC.md`) tinha sido executada, sem reler o código — todos os trechos correspondentes estavam marcados como "conforme especificação/reportado pelo usuário". Nesta 3ª revisão, o código atual do projeto foi **efetivamente lido de volta**, diretamente da pasta do projeto no computador do usuário: `model/Entrevista.java`, `model/StatusEntrevista.java` (arquivo novo), `dao/EntrevistaDAO.java`, `service/EntrevistaService.java`, `dao/TesteEntrevistaDAO.java`, `service/EntrevistaServiceTeste.java` e `service/PedidoExameService.java` foram lidos por inteiro; `controller/TelaExamesPendentesController.java` e `view/telaExamesPendentes.fxml` foram lidos para confirmar que nenhuma tela foi ligada ao novo backend; e `banco/scripts/01_criacao_banco.sql` foi conferido (mesmo tamanho e data de modificação do arquivo original — **confirmado que não foi alterado**). **O que estava marcado como "conforme especificação/reportado pelo usuário" nas seções abaixo agora está confirmado por leitura direta do código atual** e o texto foi atualizado de acordo. Uma ressalva permanece: **o código foi lido, mas não foi executado** contra o banco de dados real nesta análise (nem nas revisões anteriores) — ou seja, a compilação e o comportamento em tempo de execução (rodar `TesteEntrevistaDAO.main()`, por exemplo) continuam **não confirmados**, apenas verificados por leitura/inspeção do SQL e da lógica Java. As duas restrições de escopo definidas pelo usuário continuam confirmadas como respeitadas: (1) `banco/scripts/01_criacao_banco.sql` não foi alterado — continua desatualizado (seção 7.1); (2) nenhuma tela/Controller foi ligada ao novo backend de Entrevista — a funcionalidade continua sem UI.

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
- **Atendimento/Entrevista com profissional** — *(confirmado por leitura direta do código atualizado na 3ª revisão — ver nota no topo do relatório)* `EntrevistaService`/`EntrevistaDAO` foram reescritos para o ciclo de vida `PENDENTE_AGENDAMENTO → AGENDADA → REALIZADA`, com `CANCELADA` alcançável a partir de `PENDENTE_AGENDAMENTO` ou `AGENDADA`, e estão hoje alinhados à estrutura real da tabela `entrevista` (seção 7.2 detalha o histórico da divergência e a adequação). **Como o fluxo funciona, lido diretamente do código:**
  1. `EntrevistaService.criarPendente(pedidoExame, tipoAtendimento)` valida que o pedido não está `CANCELADO`/`CONCLUIDO`/`TRANSFERIDO`, que `tipoAtendimento` não é nulo e que o usuário logado existe e está ativo; monta uma `Entrevista` já com `status = PENDENTE_AGENDAMENTO` e chama `EntrevistaDAO.inserir()` (`INSERT` só com `id_pedido_exame`, `tipo_atendimento`, `status`, `id_usuario`, `data_cadastro` — as demais colunas ficam `NULL` até o agendamento).
  2. `EntrevistaService.agendar(idEntrevista, profissional, dataAgendamento)` busca a entrevista, exige que o status atual seja `PENDENTE_AGENDAMENTO` ou `AGENDADA` (permite reagendar), valida que o profissional existe, está ativo e que `profissional.getTipo()` bate com `entrevista.getTipoAtendimento()`, valida que a data não é anterior à data de solicitação do pedido, e chama `EntrevistaDAO.agendar()` (`UPDATE` que grava `id_profissional`, `data_agendamento` e muda `status` para `AGENDADA`).
  3. `EntrevistaService.registrarRealizacao(idEntrevista, dataRealizacao, dataEntregaLaudo)` exige que o status atual seja `AGENDADA`, valida que a data de realização não é futura nem anterior à data de agendamento, e que a data de entrega do laudo (se informada) não é anterior à data de realização; chama `EntrevistaDAO.registrarRealizacao()` (`UPDATE` que grava `data_realizacao`/`data_entrega_laudo` e muda `status` para `REALIZADA`).
  4. `EntrevistaService.cancelar(idEntrevista)` exige que o status atual seja `PENDENTE_AGENDAMENTO` ou `AGENDADA` (não permite cancelar uma entrevista já `REALIZADA`) e chama `EntrevistaDAO.cancelar()` (`UPDATE ... SET status = 'CANCELADA'`).
  5. `EntrevistaDAO.buscarPorId`/`listarTodos`/`listarPorPedidoExame` usam `LEFT JOIN` com `profissional` (em vez do `INNER JOIN` antigo) e o método `montarEntrevista()` verifica `resultado.wasNull()` no `id_profissional` antes de montar o objeto `Profissional`, deixando `entrevista.setProfissional(null)` quando ainda não há profissional atribuído — corrigindo o problema que a 1ª revisão havia apontado.
  6. `EntrevistaDAO.foiAtendidoPorAmbos(idPedido)` — usado por `PedidoExameService.concluirPedido()` — foi simplificado para `SELECT COUNT(DISTINCT tipo_atendimento) FROM entrevista WHERE id_pedido_exame = ? AND status = 'REALIZADA'`, retornando `true` quando o resultado é `>= 2`; ou seja, agora só conta como "atendido" um tipo de atendimento cuja entrevista tenha de fato sido `REALIZADA` (antes bastava existir o registro).

  Continua **sem tela/controller que o utilize** (botão "ATENDER" em `telaExamesPendentes.fxml`, confirmado nesta revisão, segue sem `onAction`) — o escopo dessa adequação foi deliberadamente só de backend, por decisão do usuário. Ou seja, esta funcionalidade passa a estar na mesma situação que Reiteração e Conclusão de Pedido: backend pronto e funcionalmente coerente pela leitura do código, só falta UI. **Ressalva:** o código foi lido e analisado, mas não foi executado contra o banco nesta análise — o comportamento em tempo de execução permanece **não confirmado**.
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

### 7.2 Divergência entre a tabela `entrevista` e o código Java — histórico e adequação (confirmada por leitura direta do código)

Esta subseção documenta um achado da 1ª revisão deste relatório (a divergência), o que o usuário reportou como resolução na 2ª revisão (na época, não reverificado por leitura de código) e o que a 3ª revisão confirmou ao ler diretamente os arquivos atualizados na pasta do projeto no computador do usuário.

**Situação original (confirmada por leitura de código na 1ª revisão):** a tabela `entrevista` no banco real tinha uma estrutura **significativamente diferente** da que `Entrevista.java`, `EntrevistaDAO.java` e `EntrevistaService.java` esperavam:

| No banco real (hoje) | No código Java original (`Entrevista`/`EntrevistaDAO`) |
|---|---|
| `data_agendamento` (DATE, NULL) | não existia no model nem era lida/gravada pelo DAO |
| `data_realizacao` (DATE, NULL) | não existia no model nem era lida/gravada pelo DAO |
| `tipo_atendimento` (VARCHAR(30), **NOT NULL**, restrita por CHECK a `PSICOLOGO`/`ASSISTENTE_SOCIAL`) | não existia no model nem era gravada pelo DAO |
| `status` (VARCHAR(30), **NOT NULL**, restrita por CHECK a `AGENDADA`/`REALIZADA`/`CANCELADA`/`PENDENTE_AGENDAMENTO`) | não existia no model nem era gravada pelo DAO |
| `id_profissional` agora **NULL**-ável | model/DAO tratavam `Profissional` como sempre presente (`entrevista.getProfissional().getId()` sem checagem de nulo) |
| não existia mais uma coluna `data_entrevista` | `EntrevistaDAO` lia/gravava `data_entrevista` em **todos** os seus métodos (`inserir`, `atualizar`, `buscarPorId`, `listarTodos`) |

**Consequência que motivou a adequação:** todo SQL do `EntrevistaDAO` original que referenciava `data_entrevista` tentaria acessar uma coluna inexistente no banco real, e não preenchia `tipo_atendimento`/`status` (obrigatórias) — ou seja, `inserir`, `atualizar`, `buscarPorId` e `listarTodos` não funcionariam se executados contra o banco no estado em que a 1ª revisão os encontrou. Só `foiAtendidoPorAmbos(idPedido)` permanecia compatível.

**Situação corrigida (confirmada por leitura direta de `model/Entrevista.java`, `model/StatusEntrevista.java`, `dao/EntrevistaDAO.java` e `service/EntrevistaService.java` na pasta do projeto):**
- `Entrevista.java` foi reescrito com os campos `pedidoExame`, `profissional` (agora nullable), `tipoAtendimento` (reaproveitando o enum já existente `TipoProfissional`), `status` (novo enum `StatusEntrevista`: `PENDENTE_AGENDAMENTO`/`AGENDADA`/`REALIZADA`/`CANCELADA`, em arquivo próprio `model/StatusEntrevista.java`), `dataAgendamento`, `dataRealizacao`, `dataEntregaLaudo`, `usuario`, `dataCadastro` — o antigo campo `dataEntrevista` foi removido por completo (nenhuma referência a ele no arquivo atual). Há um construtor dedicado `Entrevista(PedidoExame, TipoProfissional)` que já monta a entrevista com `status = PENDENTE_AGENDAMENTO`, `usuario = SessaoUsuario.getUsuarioLogado()` e `dataCadastro = LocalDate.now()`.
- `EntrevistaDAO.java` passa a ter métodos dedicados por transição de estado, no lugar de um `inserir`/`atualizar` genérico: `inserir(Entrevista)` (`INSERT` com `id_pedido_exame`, `tipo_atendimento`, `status`, `id_usuario`, `data_cadastro`, recuperando a chave gerada via `Statement.RETURN_GENERATED_KEYS`), `agendar(idEntrevista, idProfissional, dataAgendamento)` (→ `AGENDADA`), `registrarRealizacao(idEntrevista, dataRealizacao, dataEntregaLaudo)` (→ `REALIZADA`, com `dataEntregaLaudo` gravada como `NULL` via `Types.DATE` quando não informada), `cancelar(idEntrevista)` (→ `CANCELADA`), `listarPorPedidoExame(idPedidoExame)` (novo), além de `buscarPorId`/`listarTodos` reescritos com **`LEFT JOIN`** com `profissional` (no lugar do `INNER JOIN` original) e `excluir` mantido como estava (`DELETE FROM entrevista WHERE id = ?`). O método privado `montarEntrevista()` lê `resultado.getInt("profissional_id")` e checa `resultado.wasNull()` antes de montar o objeto `Profissional`, deixando `entrevista.setProfissional(null)` quando não há profissional atribuído — a lacuna que a 1ª revisão havia identificado (JOIN excluindo silenciosamente entrevistas sem profissional) está corrigida.
- `foiAtendidoPorAmbos(idPedido)` **manteve nome e assinatura** (continua sendo chamado por `PedidoExameService.concluirPedido()` sem alteração — confirmado lendo `PedidoExameService.java`), mas a consulta foi simplificada para `SELECT COUNT(DISTINCT tipo_atendimento) FROM entrevista WHERE id_pedido_exame = ? AND status = 'REALIZADA'` (sem JOIN com `profissional`) — refinando a semântica de "atendido" para exigir que o atendimento tenha de fato ocorrido (`status = 'REALIZADA'`), não apenas que exista um registro de entrevista.
- `EntrevistaService.java` passou a ter métodos `criarPendente`, `agendar`, `registrarRealizacao` e `cancelar`, cada um validando as transições de estado permitidas (`registrarRealizacao` só a partir de `AGENDADA`; `cancelar` só a partir de `PENDENTE_AGENDAMENTO`/`AGENDADA`; `agendar` a partir de `PENDENTE_AGENDAMENTO` ou `AGENDADA`, permitindo reagendar) e regras de negócio associadas (`profissional.getTipo() == entrevista.getTipoAtendimento()`, datas de agendamento não anteriores à solicitação do pedido, realização não futura nem anterior ao agendamento, entrega de laudo não anterior à realização).
- `dao/TesteEntrevistaDAO.java` e `service/EntrevistaServiceTeste.java` (classes de teste manual) também foram reescritas para exercitar o novo fluxo (`criarPendente`/`agendar`/`registrarRealizacao`/`cancelar` e os casos de validação), no mesmo estilo `System.out.println`/`✅`/`❌` já usado no projeto.
- Por decisão explícita do usuário, esta adequação foi **só de código Java**: `banco/scripts/01_criacao_banco.sql` não foi tocado — **confirmado nesta revisão**: mesmo tamanho (2376 bytes) e mesma data de modificação do arquivo original, sem qualquer alteração (permanece desatualizado — seção 7.1) — e nenhuma tela/Controller foi ligada a esse backend — **confirmado nesta revisão** ao reler `TelaExamesPendentesController.java`/`telaExamesPendentes.fxml`: o botão "ATENDER" continua sem `onAction` (permanece sem UI — seções 6 e 10).

**Ressalva de método (válida desde a 1ª revisão e ainda válida aqui):** esta confirmação foi feita por **leitura e inspeção do código-fonte e do SQL nele embutido**, comparando-os com o schema real do banco — **nenhum código Java foi executado contra o banco nesta análise**. Ou seja, está confirmado que o código *deveria* funcionar conforme o schema (os nomes de coluna, tipos e a lógica de transição de estado batem com a estrutura real da tabela `entrevista`), mas o comportamento em tempo de execução (compilação limpa, ausência de erros de SQL, resultado real de rodar `TesteEntrevistaDAO.main()`) permanece **não confirmado**.

**Interpretação (inferência, ainda válida):** o banco foi evoluído para representar um fluxo explícito de "agendamento → realização" do atendimento (com `status` controlando esse ciclo e `tipo_atendimento` guardando explicitamente psicólogo vs. assistente social), e o código Java foi trazido para acompanhar esse desenho.

---

## 8. PRINCIPAIS CLASSES

*(getters/setters triviais omitidos)*

- **`Main`** — ponto de entrada JavaFX (`extends Application`); carrega `telaLoginMaior.fxml` na inicialização e define o ícone/título da janela. Depende de `FXMLLoader`.

- **`util.Conexao`** — responsabilidade única: criar uma `java.sql.Connection` para o SQL Server (`conectar()`). Concentra URL, usuário e senha. É dependência direta de **todos** os DAOs.

- **`session.SessaoUsuario`** — mantém, em um campo `static`, o `Usuario` atualmente logado (`getUsuarioLogado`/`setUsuarioLogado`). **Ponto de atenção:** o campo é inicializado estaticamente com `usuarioDAO.buscarPorId(1)` — ou seja, ao carregar a classe pela primeira vez (antes de qualquer login), o sistema já tenta buscar o usuário de id 1 no banco (ver seção 10). Usada por `PedidoExame`, `Reiteracao`, `Entrevista` (para preencher o usuário responsável automaticamente) e pelos controllers de tela principal/logout.

- **`UsuarioService`** — responsável por cadastro, autenticação (`autenticar`, com `BCrypt.checkpw`) e troca de senha (`alterarSenha`) de usuários; aplica validações de tamanho mínimo de login/senha e unicidade de login. Depende de `UsuarioDAO` e `SessaoUsuario`.

- **`PedidoExameService`** — a classe de regra de negócio mais rica do sistema: `inserir()` (valida sentenciado/usuário/data, impede pedido ativo duplicado por benefício), `concluirPedido()` (valida SEI e atendimento por ambos profissionais antes de mudar status para `CONCLUIDO`), `listarPendentes()`. Depende de `PedidoExameDAO` e `EntrevistaDAO`.

- **`SentenciadoService`** — valida e normaliza nome e matrícula do sentenciado (remove pontuação, exige apenas números, checa duplicidade). Depende de `SentenciadoDAO`.

- **`EntrevistaService`** — *(confirmado por leitura direta do código na 3ª revisão)* expõe `criarPendente`, `agendar`, `registrarRealizacao` e `cancelar`, validando as transições permitidas do ciclo `PENDENTE_AGENDAMENTO → AGENDADA → REALIZADA` (com `CANCELADA` a partir de `PENDENTE_AGENDAMENTO`/`AGENDADA`), compatibilidade entre `profissional.getTipo()` e `tipoAtendimento`, e datas coerentes (agendamento não anterior à solicitação do pedido; realização não futura nem anterior ao agendamento; entrega de laudo não anterior à realização). Depende de `EntrevistaDAO`.

- **`ReiteracaoService`** — valida uma reiteração de forma análoga (status do pedido, datas, usuário ativo, tamanho da observação). Depende de `ReiteracaoDAO`.

- **`BeneficioService`** / **`ProfissionalService`** — validações de cadastro (descrição/sigla do benefício; nome/tipo do profissional, incluindo checagem de que o nome não contém números). Dependem de `BeneficioDAO`/`ProfissionalDAO` respectivamente.

- **`PedidoExameDAO`** — o DAO mais complexo: monta `PedidoExame` a partir de *joins* com `usuario` e `sentenciado`; possui `listarPendendes()` (nome com erro de digitação — ver seção 10), `existePedidoAtivoParaSentenciadoEBeneficio()` (usada pela regra de negócio de duplicidade).

- **`EntrevistaDAO`** — *(confirmado por leitura direta do código na 3ª revisão — ver seção 7.2)* inclui `inserir` (cria `PENDENTE_AGENDAMENTO`), `agendar`, `registrarRealizacao`, `cancelar`, `listarPorPedidoExame` (novo), `buscarPorId`/`listarTodos` (agora com `LEFT JOIN` em `profissional`, tratando `id_profissional` nulo) e `excluir`. Mantém `foiAtendidoPorAmbos(idPedido)` com o mesmo nome/assinatura usados por `PedidoExameService.concluirPedido()`, mas com a consulta simplificada (usa `tipo_atendimento` diretamente e exige `status = 'REALIZADA'`).

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

**Atendimento/Entrevista (confirmado por leitura direta do código na 3ª revisão — sem UI que o acione, ver seções 6 e 10):**
`EntrevistaService.criarPendente(pedidoExame, tipoAtendimento)` → valida pedido/tipo/usuário → `EntrevistaDAO.inserir()` grava a entrevista com `status = 'PENDENTE_AGENDAMENTO'` (sem profissional/data ainda) →
`EntrevistaService.agendar(idEntrevista, profissional, dataAgendamento)` → busca a entrevista, valida status (`PENDENTE_AGENDAMENTO`/`AGENDADA`), profissional (ativo e do tipo correto) e data → `EntrevistaDAO.agendar()` grava `id_profissional`/`data_agendamento` e muda `status` para `'AGENDADA'` →
`EntrevistaService.registrarRealizacao(idEntrevista, dataRealizacao, dataEntregaLaudo)` → valida status (`AGENDADA`) e datas → `EntrevistaDAO.registrarRealizacao()` grava `data_realizacao`/`data_entrega_laudo` e muda `status` para `'REALIZADA'` →
(alternativamente) `EntrevistaService.cancelar(idEntrevista)` → valida status (`PENDENTE_AGENDAMENTO`/`AGENDADA`) → `EntrevistaDAO.cancelar()` muda `status` para `'CANCELADA'`.
Este fluxo completo é hoje coerente com o schema real da tabela `entrevista` (seção 7.2), mas **nenhum ponto de UI o aciona** — confirmado nesta revisão que `telaExamesPendentes.fxml`/`TelaExamesPendentesController` continuam sem `onAction` no botão "ATENDER".

**Demais fluxos existentes apenas na camada de regra de negócio (sem UI que os acione — ver seções 6 e 10):** reiteração (`ReiteracaoService`/`ReiteracaoDAO`), conclusão de pedido (`PedidoExameService.concluirPedido`, que continua chamando `EntrevistaDAO.foiAtendidoPorAmbos()` sem alteração de assinatura), resumo de pedido (`ResumoPedidoController`, sem carregamento de dados).

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
- Backend completo (Service + DAO, com regras de negócio inclusive) para **reiteração**, **conclusão de pedido** e, agora, **atendimento/entrevista** — mas nenhuma das três tem uma tela funcional que as acione. Os botões correspondentes existem na UI (`REITERAR`/`ATENDER` em Exames Pendentes; `Reiterar`, `Concluir` em `telaResumo.fxml`) mas não têm `onAction`.
- **Atendimento/entrevista** *(confirmado por leitura direta do código na 3ª revisão — ver seção 7.2)*: o backend (`EntrevistaService`/`EntrevistaDAO`) foi reescrito para o ciclo `PENDENTE_AGENDAMENTO → AGENDADA → REALIZADA`/`CANCELADA` e está alinhado à estrutura real da tabela `entrevista` — deixou de ter o problema de incompatibilidade de schema que a 1ª revisão deste relatório havia identificado. Continua exatamente como reiteração/conclusão: falta só a UI (o código em si não foi executado nesta análise, apenas lido e comparado ao schema).
- `telaResumo.fxml`: layout completo, sem dados reais nem navegação até ela.

### O que parece faltar
- Telas de cadastro/gestão de **Benefício** e de **Profissional** (o backend existe, a UI não).
- Uma tela de gestão de **Sentenciados** fora do fluxo de "novo pedido" (o botão "Sentenciados" no menu não tem ação).
- Uma tela de **Relatórios** (mencionada apenas no protótipo abandonado `telaBase.fxml` — inferência, não confirmado como plano ativo).
- *Wiring* (ligação `onAction`) dos botões "Pesquisar" (Exames Pendentes), "Atender" e "Reiterar".
- Carregamento de dados reais em `telaResumo.fxml`, e uma forma de navegar até ela a partir de algum ponto do sistema.
- Preenchimento da coluna "Benefício" na tabela de Exames Pendentes (`colTipoBeneficio` sem `setCellValueFactory`).

### Problemas ou inconsistências encontradas
1. **[RESOLVIDO, confirmado por leitura direta do código na 3ª revisão] Incompatibilidade entre `EntrevistaDAO` e a tabela `entrevista` real.** A 1ª revisão deste relatório havia identificado que o código Java ainda estava escrito em cima da coluna `data_entrevista` (removida do banco) e não preenchia `tipo_atendimento`/`status` (obrigatórias). Nesta 3ª revisão, a leitura direta de `Entrevista.java`/`EntrevistaDAO.java`/`EntrevistaService.java` na pasta do projeto confirmou que foram reescritos para o schema real, sem nenhuma referência remanescente a `data_entrevista` (ver seção 7.2 para o detalhamento completo). Ressalva: confirmado por leitura/inspeção do código, não por execução — o comportamento em tempo de execução (compilar e rodar contra o banco) permanece não confirmado.
2. **Credencial do banco de dados em texto puro no código-fonte** (`util/Conexao.java`), sem uso de variável de ambiente ou arquivo de configuração externo. O `.gitignore` do projeto não exclui esse arquivo — portanto, se ele já foi commitado ao Git em algum momento, a senha estará no histórico do repositório. **Não confirmado** (não foi examinado o histórico de commits nesta análise) se isso já ocorreu; recomenda-se verificar manualmente (`git log -p -- src/br/com/sigec/util/Conexao.java`) e, havendo exposição, trocar a senha do banco.
3. **Script `01_criacao_banco.sql` do projeto está desatualizado em relação ao banco real (confirmado)** — ver seção 7.1: falta `beneficio.sigla` (que existe no banco real), declara `pedido_exame.data_conclusao` como `NOT NULL` (no banco real é `NULL`-ável), declara `sentenciado.matricula` como `VARCHAR(11)` (no banco real é `VARCHAR(8)`) e ainda traz uma versão antiga de `entrevista` (sem `data_agendamento`/`data_realizacao`/`tipo_atendimento`/`status`, com `laudo_entregue` que não existe mais). **Este item permanece confirmadamente em aberto** — por decisão explícita do usuário, a adequação de Entrevista foi só de código Java; o script SQL não foi tocado. Recomenda-se substituir esse script por um novo, gerado a partir do banco real.
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
PedidoExameService.concluirPedido() → EntrevistaDAO.foiAtendidoPorAmbos() → tabelas entrevista + profissional  (compatível com o schema atual)
EntrevistaService (criarPendente/agendar/registrarRealizacao/cancelar) / EntrevistaDAO → tabela entrevista + profissional (LEFT JOIN)  [confirmado por leitura direta do código na 3ª revisão — seção 7.2; alinhado ao schema real]
ReiteracaoService / ReiteracaoDAO → tabela reiteracao
telaResumo.fxml → ResumoPedidoController (sem dados)
```

---

## 13. PONTOS IMPORTANTES PARA RETOMAR O DESENVOLVIMENTO

### 1. Muito importante
- **Rodar `TesteEntrevistaDAO.main()` para validar em tempo de execução a adequação de `Entrevista`/`EntrevistaDAO`/`EntrevistaService`** *(o código já foi confirmado por leitura direta na 3ª revisão deste relatório — seção 7.2 — mas nunca foi executado nesta análise)*: com o SQL Server local acessível, rodar manualmente essa classe e confirmar visualmente que `criarPendente`/`agendar`/`registrarRealizacao`/`cancelar` funcionam contra o banco real e que o projeto compila sem erros. É o único passo que falta para fechar totalmente esta frente antes de partir para a UI de "Atender".
- **Decidir e implementar a ligação das telas com o backend já pronto**: Reiterar, Concluir Pedido e, agora, Atender/Entrevista já têm backend funcional (este último conforme especificação reportada) e só precisam de UI — é a lacuna mais evidente para fechar em seguida.
- **Atualizar (ou substituir) `banco/scripts/01_criacao_banco.sql`** a partir de um script gerado do banco real — o script atual do projeto está confirmadamente desatualizado em vários pontos (seção 7.1) e não deve ser usado para recriar o ambiente. Este item **permanece em aberto**: por decisão explícita do usuário, não fez parte da adequação de Entrevista.
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

A 1ª revisão deste relatório, feita já com a estrutura real e atual do banco em mãos, havia identificado como ponto mais crítico o fato de a tabela `entrevista` ter sido evoluída no banco (agora com `status`, `tipo_atendimento`, `data_agendamento` e `data_realizacao`, refletindo um fluxo explícito de agendamento → realização do atendimento) sem que o código Java correspondente acompanhasse essa mudança. Na 2ª revisão, o usuário reportou que essa adequação havia sido executada, mas isso não tinha sido reverificado por leitura de código. **Nesta 3ª revisão, o código atual foi lido diretamente da pasta do projeto no computador do usuário e a adequação foi confirmada:** `Entrevista`, `EntrevistaDAO` e `EntrevistaService` foram reescritos para o ciclo `PENDENTE_AGENDAMENTO → AGENDADA → REALIZADA`/`CANCELADA` (com métodos dedicados `criarPendente`/`agendar`/`registrarRealizacao`/`cancelar`), estão alinhados ao schema real (seção 7.2), e as classes de teste manuais (`TesteEntrevistaDAO`, `EntrevistaServiceTeste`) foram atualizadas junto. Com isso, Atendimento/Entrevista passa a estar na mesma situação que Reiteração e Conclusão de Pedido: backend íntegro e compatível com o banco atual (pela leitura do código — ainda não executado nesta análise), faltando apenas ligar uma tela a ele. Da mesma forma, não existem telas de cadastro para `Benefício` e `Profissional`, o que significa que, hoje, popular essas duas tabelas exige rodar código manualmente ou inserir dados direto no banco.

As partes mais importantes do sistema para retomar o trabalho são: (1) a camada `service`, que concentra praticamente todas as regras de negócio reais e é a parte mais sólida e completa do projeto; (2) a tabela `pedido_exame` e seu ciclo de status (`CADASTRADO` → `SOLICITADO` → `CONCLUIDO`/`CANCELADO`/`TRANSFERIDO`), que é o eixo central do domínio; (3) o ciclo de vida de `entrevista` (`PENDENTE_AGENDAMENTO` → `AGENDADA` → `REALIZADA`/`CANCELADA`), confirmado no código nesta revisão, e que vale validar em tempo de execução rodando `TesteEntrevistaDAO.main()` antes de construir a tela de atendimento sobre ele; e (4) `telaExamesPendentes.fxml`, que é hoje o hub de navegação real do sistema (mesmo tendo botões inertes).

Antes de qualquer nova funcionalidade, vale revisar quatro pontos que podem gerar surpresas: (a) rodar `TesteEntrevistaDAO.main()` para validar em tempo de execução a adequação de `EntrevistaDAO`/`EntrevistaService` ao schema real da tabela `entrevista` (seção 7.2) — o código já foi confirmado por leitura nesta revisão, mas nunca foi executado, então é o único passo que falta antes de construir a UI de "Atender" sobre ele; (b) o fato de que `banco/scripts/01_criacao_banco.sql`, o script versionado no projeto, está desatualizado em vários pontos e não deve ser usado para recriar o ambiente — confirmado nesta revisão que o arquivo continua exatamente como estava, sem nenhuma alteração; (c) a instrução de `UPDATE` em `ProfissionalDAO.atualizar()`, que parece sintaticamente inválida e nunca foi exercitada por nenhuma classe de teste; e (d) a exposição da senha do banco em texto puro no código-fonte versionado. Nenhum desses pontos foi corrigido pelo analista nesta análise — apenas lidos, comparados e registrados, conforme solicitado.
