package br.com.sigec.util;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {
    private static final String URL= "jdbc:sqlserver://localhost:1433;databaseName=SIGEC;encrypt=true;trustServerCertificate=true";
    private static final String USUARIO = "sa";
    private static final String SENHA = "Se0455064@";

    public static Connection conectar() throws SQLException{
        return DriverManager.getConnection(URL,USUARIO,SENHA);
    }
}
