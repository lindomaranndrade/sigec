package br.com.sigec.dao;

import br.com.sigec.model.Profissional;
import br.com.sigec.util.Conexao;

import java.sql.*;

public class ProfissionalDAO {

    public void inserir(Profissional profissional){
        try(Connection conexao = Conexao.conectar()){
            String sql = "INSERT INTO profissional (nome, tipo, ativo) VALUES(? , ?, ?)";
            PreparedStatement comando = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            comando.setString(1, profissional.getNome());

            //O enum é convertido para String para que seu nome seja armazenado no banco.
            comando.setString(2,profissional.getTipo().name());
            comando.setBoolean(3, profissional.isAtivo());
            comando.executeUpdate();
            ResultSet resposta = comando.getGeneratedKeys();

            //Se uma chave foi retornada, atualiza o objeto com o ID gerado pelo banco.
            if(resposta.next()){
                profissional.setId(resposta.getInt("id"));
            }

        }catch (SQLException e){
            throw  new RuntimeException(e);
        }
    }
}
