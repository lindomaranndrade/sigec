# SIGEC

Sistema Integrado de Gestão de Execução Criminal desenvolvido em Java.

---

## Sobre o Projeto

O SIGEC (Sistema Integrado de Gestão de Execução Criminal) é um projeto desenvolvido com o objetivo de aprofundar conhecimentos em Java, Programação Orientada a Objetos, JDBC e SQL Server através da construção de um sistema de gerenciamento de processos da execução penal.

O projeto foi concebido como uma experiência prática de aprendizado, aplicando conceitos de desenvolvimento de software, modelagem de banco de dados, arquitetura em camadas, regras de negócio, persistência de dados e desenvolvimento de interfaces gráficas em um cenário próximo ao encontrado em aplicações corporativas.

---

## Diferencial do Projeto

Toda a camada de persistência foi desenvolvida manualmente utilizando JDBC, sem o uso de frameworks de persistência como:

* Hibernate
* JPA
* Spring Data JPA

O objetivo foi compreender detalhadamente o funcionamento da comunicação entre uma aplicação Java e um banco de dados relacional antes da adoção de frameworks que abstraem essas operações.

Durante o desenvolvimento foram estudados e aplicados conceitos como:

* Gerenciamento de conexões JDBC
* PreparedStatement
* ResultSet
* CRUD completo
* Recuperação de chaves geradas
* Relacionamentos entre entidades
* Chaves estrangeiras
* Chaves compostas
* Relacionamentos N:N
* Mapeamento objeto-relacional manual
* Conversão de tipos Java e SQL
* Tratamento de exceções
* Gerenciamento de recursos com try-with-resources

A proposta é construir uma base sólida de conhecimento sobre persistência de dados antes da utilização de tecnologias como Spring Boot, JPA e Hibernate.

---

## Tecnologias Utilizadas

* Java 21
* JavaFX
* FXML
* JavaFX CSS
* Scene Builder
* JDBC
* SQL Server
* IntelliJ IDEA
* Git
* GitHub

---

## Arquitetura

O projeto utiliza uma arquitetura em camadas:

```text
View (FXML)
 ↓
Controller
 ↓
Service
 ↓
DAO
 ↓
SQL Server
