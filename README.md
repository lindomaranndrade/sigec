# SIGEC

Sistema Integrado de Gestão de Execução Criminal desenvolvido em Java.

---

## Sobre o Projeto

O SIGEC (Sistema Integrado de Gestão de Execução Criminal) é um projeto desenvolvido com o objetivo de aprofundar conhecimentos em Java, Programação Orientada a Objetos, JDBC e SQL Server através da construção de um sistema de gerenciamento de processos da execução penal.

O projeto foi concebido como uma experiência prática de aprendizado, aplicando conceitos de desenvolvimento de software, modelagem de banco de dados, arquitetura em camadas, regras de negócio e persistência de dados em um cenário próximo ao encontrado em aplicações corporativas.

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
* Mapeamento objeto-relacional manual
* Conversão de tipos Java e SQL
* Tratamento de exceções
* Gerenciamento de recursos com try-with-resources

A proposta é construir uma base sólida de conhecimento sobre persistência de dados antes da utilização de tecnologias como Spring Boot, JPA e Hibernate.

---

## Tecnologias Utilizadas

* Java 21
* JavaFX
* JDBC
* SQL Server
* IntelliJ IDEA
* Git
* GitHub

---

## Arquitetura

O projeto utiliza uma arquitetura em camadas:

```text
View
 ↓
Service
 ↓
DAO
 ↓
SQL Server
```

Cada camada possui responsabilidades específicas, promovendo organização, manutenção e evolução do sistema.

---

## Estrutura do Projeto

```text
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

## Funcionalidades Implementadas

### Infraestrutura

* Conexão com SQL Server
* Arquitetura em Camadas
* Persistência utilizando JDBC
* Utilização de PreparedStatement
* Utilização de ResultSet
* Utilização de try-with-resources
* Conversão entre LocalDate e java.sql.Date
* Persistência de enums
* Recuperação de chaves geradas (RETURN_GENERATED_KEYS)
* Mapeamento manual de entidades e relacionamentos
* Camada Service para centralização das regras de negócio

### Módulos Implementados

#### Usuários

* Inserir
* Buscar por ID
* Atualizar
* Excluir
* Listar todos

#### Benefícios

* Inserir
* Buscar por ID
* Atualizar
* Excluir
* Listar todos

#### Profissionais

* Inserir
* Buscar por ID
* Atualizar
* Excluir
* Listar todos

#### Sentenciados

* Inserir
* Buscar por ID
* Atualizar
* Excluir
* Listar todos

#### Reiterações

* Inserir
* Buscar por ID
* Atualizar
* Excluir
* Listar todos

#### Pedidos de Exame

* Inserir
* Buscar por ID
* Atualizar
* Excluir
* Listar todos
* Relacionamento com Sentenciado
* Relacionamento com Usuário
* Relacionamento com Reiteração
* Validação de regras de negócio para conclusão de pedidos

#### Entrevistas

* Inserir
* Buscar por ID
* Atualizar
* Excluir
* Listar todos
* Relacionamento com Pedido de Exame
* Relacionamento com Profissional
* Relacionamento com Usuário

#### Pedido x Benefício

Implementação de relacionamento N:N utilizando chave composta.

* Inserir associação
* Excluir associação
* Listar todos
* Listar por Pedido de Exame
* Listar por Benefício

---

## Conceitos Aplicados

Durante o desenvolvimento deste projeto estão sendo estudados e praticados:

* Programação Orientada a Objetos
* Encapsulamento
* JDBC
* SQL Server
* DAO (Data Access Object)
* Service Layer
* Arquitetura em Camadas
* Modelagem Relacional
* Persistência de Dados
* Chaves Primárias
* Chaves Estrangeiras
* Chaves Compostas
* Relacionamentos 1:N
* Relacionamentos N:N
* Tratamento de Exceções
* Mapeamento Objeto-Relacional Manual
* Gerenciamento de Recursos JDBC
* Git
* GitHub

---

## Funcionalidades em Desenvolvimento

* Sistema de autenticação
* Controle de permissões de usuário
* Interface gráfica JavaFX
* Relatórios em PDF
* Dashboard gerencial

---

## Como Executar

1. Execute os scripts SQL localizados na pasta `banco/scripts`.
2. Configure a conexão com o SQL Server na classe `Conexao`.
3. Abra o projeto no IntelliJ IDEA.
4. Execute a aplicação.

---

## Status do Projeto

| Módulo                  | Status             |
| ----------------------- | ------------------ |
| Banco de Dados          | Concluído          |
| Benefícios              | Concluído          |
| Usuários                | Concluído          |
| Profissionais           | Concluído          |
| Sentenciados            | Concluído          |
| Reiterações             | Concluído          |
| Pedidos de Exame        | Concluído          |
| Entrevistas             | Concluído          |
| Pedido x Benefício      | Concluído          |
| Camada DAO              | Concluída          |
| Camada Service          | Concluída          |
| Interface Gráfica       | Em desenvolvimento |
| Sistema de Autenticação | Planejado          |
| Relatórios PDF          | Planejado          |

---

## Próximas Etapas

* Implementar autenticação de usuários
* Implementar controle de permissões
* Finalizar a interface gráfica em JavaFX
* Implementar geração de relatórios PDF
* Desenvolver dashboard gerencial
* Evoluir o projeto para utilização de frameworks modernos em etapas futuras

---

## Observações

A interface gráfica (camada View) está sendo desenvolvida com auxílio de ferramentas de Inteligência Artificial para acelerar a construção das telas e a prototipação visual da aplicação.

Toda a modelagem do banco de dados, arquitetura do sistema, implementação das entidades, camada DAO, camada Service, integração com SQL Server via JDBC, regras de negócio e demais funcionalidades foram desenvolvidas pelo autor como parte do processo de aprendizado e aprofundamento em desenvolvimento Java.

O uso de IA está restrito ao apoio na construção da interface gráfica, não substituindo o desenvolvimento da lógica de negócio e da persistência de dados do sistema.

---

## Autor

**Lindomar Andrade**

Projeto desenvolvido para fins de estudo e aperfeiçoamento em desenvolvimento Java, com foco no aprendizado dos fundamentos de persistência de dados, arquitetura em camadas, regras de negócio e desenvolvimento de aplicações corporativas.
