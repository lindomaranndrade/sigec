# SIGEC

Sistema Integrado de Gestão de Execução Criminal desenvolvido em Java.

## Sobre o projeto

O SIGEC é um projeto desenvolvido com o objetivo de aprofundar conhecimentos em Java, Programação Orientada a Objetos, JDBC e SQL Server.

Todo o sistema está sendo construído sem frameworks de persistência, permitindo compreender detalhadamente o funcionamento da comunicação entre a aplicação e o banco de dados.

Além do desenvolvimento das funcionalidades, o projeto busca aplicar boas práticas de organização de código, arquitetura em camadas e gerenciamento de recursos utilizando `try-with-resources`.

---

## Tecnologias

- Java 21
- JDBC
- SQL Server
- IntelliJ IDEA
- Git
- GitHub

---

## Estrutura do projeto

```
src
├── br.com.sigec.dao
├── br.com.sigec.model
├── br.com.sigec.service
├── br.com.sigec.util
└── br.com.sigec.view

banco
├── scripts
│   └── Scripts de criação do banco de dados
└── modelos
    ├── Modelo conceitual
    └── Modelo lógico
```

---

## Funcionalidades implementadas

- Conexão com SQL Server
- Arquitetura em camadas (DAO, Model, Service e View)
- Persistência utilizando JDBC
- CRUD de Benefícios
- CRUD de Usuários
- CRUD de Profissionais
- CRUD de Sentenciados
- Estrutura inicial do módulo de Pedidos de Exames
- Utilização de Enums persistidos no banco de dados
- Conversão entre `LocalDate` e `java.sql.Date`
- Uso de `PreparedStatement`
- Uso de `ResultSet`
- Utilização de `try-with-resources` para gerenciamento automático de recursos JDBC
- Recuperação de chaves geradas (`RETURN_GENERATED_KEYS`)

---

## Funcionalidades em desenvolvimento

- Finalização do módulo de Pedidos de Exames
- Cadastro de Entrevistas
- Camada Service
- Interface gráfica (Swing)
- Sistema de autenticação
- Relatórios em PDF

---

## Objetivos de aprendizagem

Durante o desenvolvimento deste projeto estão sendo estudados os seguintes conceitos:

- Programação Orientada a Objetos
- JDBC
- SQL Server
- DAO (Data Access Object)
- Arquitetura em Camadas
- Persistência de Dados
- Tratamento de Exceções
- Mapeamento objeto-relacional manual
- Boas práticas de gerenciamento de recursos JDBC
- Git e GitHub

---

## Como executar

1. Execute o script localizado na pasta `banco`.
2. Configure a conexão com o SQL Server na classe `Conexao`.
3. Abra o projeto no IntelliJ IDEA.
4. Execute a aplicação.

---

## Status do Projeto

| Módulo | Status |
|--------|--------|
| Benefícios | Concluído |
| Usuários | Concluído |
| Profissionais | Concluído |
| Sentenciados | Concluído |
| Pedidos de Exames | Em desenvolvimento |
| Entrevistas | Planejado |
| Camada Service | Planejado |
| Interface gráfica (Swing) | Planejado |
| Sistema de autenticação | Planejado |
| Relatórios em PDF | Planejado |

## Autor

**Lindomar Andrade**

Projeto desenvolvido para fins de estudo e aperfeiçoamento em desenvolvimento Java.
