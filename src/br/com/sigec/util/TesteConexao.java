package br.com.sigec.util;

import java.sql.Connection;
import java.sql.SQLException;

public class TesteConexao {

    public static void main(String[] args) {

        try {
            Connection conexao = Conexao.conectar();

            System.out.println("================================");
            System.out.println(" Conectado com sucesso ao SIGEC ");
            System.out.println("================================");

            conexao.close();

            System.out.println("Conexão encerrada.");

        } catch (SQLException e) {

            System.out.println("Erro ao conectar com o banco de dados.");

            System.out.println("Código do erro: " + e.getErrorCode());
            System.out.println("Mensagem: " + e.getMessage());

            e.printStackTrace();
        }
    }
}