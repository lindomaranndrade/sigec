/*
---------------------------------------------------------
Projeto : SIGEC
Autor   : Lindomar Andrade

Descrição:
Script responsável pela criação completa do banco de dados
do Sistema Integrado de Gestão de Execução Criminal.
---------------------------------------------------------
*/


CREATE DATABASE SIGEC;
GO

USE SIGEC;
GO

CREATE TABLE usuario(
id INT IDENTITY(1,1) PRIMARY KEY,
nome VARCHAR(100) NOT NULL,
login VARCHAR(100) NOT NULL UNIQUE,
senha VArCHAR(255) NOT NULL,
ativo BIT NOT NULL
);

CREATE TABLE sentenciado(
id INT IDENTITY(1,1) PRIMARY KEY,
matricula VARCHAR(11) NOT NULL UNIQUE,
nome VARCHAR(100) NOT NULL
);

CREATE TABLE beneficio(
id INT IDENTITY(1,1) PRIMARY KEY,
descricao VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE profissional(
id INT IDENTITY(1,1) PRIMARY KEY,
nome VARCHAR(100) NOT NULL,
tipo VARCHAR(30) NOT NULL,
ativo BIT NOT NULL
);

CREATE TABLE pedido_exame(
id INT IDENTITY(1,1) PRIMARY KEY,
id_sentenciado INT NOT NULL,
data_cadastro DATE NOT NULL,
data_solicitacao DATE NOT NULL,
status VARCHAR(30) NOT NULL,
id_usuario INT NOT NULL,
numero_processo VARCHAR(100),
numero_sei VARCHAR(100),
data_conclusao DATE NOT NULL,
FOREIGN KEY (id_sentenciado) REFERENCES sentenciado(id),
FOREIGN KEY (id_usuario) REFERENCES usuario(id)
);

CREATE TABLE pedido_beneficio(
id_beneficio INT NOT NULL,
id_pedido_exame INT NOT NULL,
PRIMARY KEY(id_beneficio, id_pedido_exame),

FOREIGN KEY (id_beneficio) REFERENCES beneficio(id),
FOREIGN KEY (id_pedido_exame) REFERENCES pedido_exame(id)
);

CREATE TABLE reiteracao(
id INT IDENTITY(1,1) PRIMARY KEY,
id_pedido_exame INT NOT NULL,
data_reiteracao DATE NOT NULL,
id_usuario INT NOT NULL,
observacoes VARCHAR(200),
despacho VARCHAR(100),
data_cadastro DATE NOT NULL,

FOREIGN KEY (id_pedido_exame) REFERENCES pedido_exame(id),
FOREIGN KEY (id_usuario) REFERENCES usuario(id)
);

CREATE TABLE entrevista(
id INT IDENTITY(1,1) PRIMARY KEY,
id_pedido_exame INT NOT NULL,
id_profissional INT NOT NULL,
data_entrevista DATE NOT NULL,
id_usuario INT NOT NULL,
laudo_entregue BIT,
data_entrega_laudo DATE,
data_cadastro DATE NOT NULL,

FOREIGN KEY (id_pedido_exame) REFERENCES pedido_exame(id),
FOREIGN KEY (id_profissional) REFERENCES profissional(id),
FOREIGN KEY (id_usuario) REFERENCES usuario(id)
);