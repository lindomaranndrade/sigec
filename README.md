# SIGEC

Sistema Integrado de Gestão de Execução Criminal desenvolvido em Java.

## Sobre o projeto

O SIGEC é um projeto desenvolvido com o objetivo de aprofundar conhecimentos em Java, Programação Orientada a Objetos, JDBC e SQL Server.

Todo o sistema está sendo construído sem frameworks de persistência, permitindo compreender detalhadamente o funcionamento da comunicação entre a aplicação e o banco de dados.

## Tecnologias

- Java 21
- JDBC
- SQL Server
- IntelliJ IDEA
- Git
- GitHub

## Estrutura do projeto

```
src
├── br.com.sigec.dao
├── br.com.sigec.model
├── br.com.sigec.service
├── br.com.sigec.util
└── br.com.sigec.view

banco
└── Scripts de criação do banco de dados
```

## Funcionalidades implementadas

- Conexão com SQL Server
- Camada DAO
- CRUD de Benefícios
- CRUD de Usuários
- Estrutura inicial de Profissionais
- Enum para tipos de profissionais
- Persistência utilizando JDBC

## Funcionalidades planejadas

- Cadastro de Sentenciados
- Cadastro de Profissionais
- Cadastro de Entrevistas
- Cadastro de Benefícios
- Controle de Pedidos de Exames
- Camada Service
- Interface gráfica (Swing)
- Sistema de autenticação
- Relatórios em PDF

## Objetivos de aprendizagem

Durante o desenvolvimento deste projeto estão sendo estudados os seguintes conceitos:

- Programação Orientada a Objetos
- JDBC
- SQL Server
- DAO (Data Access Object)
- Arquitetura em Camadas
- Persistência de Dados
- Tratamento de Exceções
- Git e GitHub

## Como executar

1. Execute o script localizado na pasta `banco`.
2. Configure a conexão com o SQL Server na classe `Conexao`.
3. Abra o projeto no IntelliJ IDEA.
4. Execute a aplicação.

## Autor

**Lindomar Andrade**

Projeto desenvolvido para fins de estudo e aperfeiçoamento em desenvolvimento Java.
