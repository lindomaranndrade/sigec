package br.com.sigec.dao;

import br.com.sigec.model.Reiteracao;
import br.com.sigec.util.Conexao;

import java.sql.*;

public class ReiteracaoDAO {

    public void inserir(Reiteracao reiteracao){
        String sql = """
                INSERT INTO reiteracao(id_pedido, data_reiteracao, id_usuario, observacoes, despacho)
                            VALUES(?, ?, ?, ?, ?)
                """;

        try(Connection conexao = Conexao.conectar(); PreparedStatement comando = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            comando.setInt(1,reiteracao.getPedido().getId());
            comando.setDate(2,java.sql.Date.valueOf(reiteracao.getDataReiteracao()));
            comando.setInt(3,reiteracao.getUsuario().getId());
            comando.setString(4,reiteracao.getObservacoes());
            comando.setString(5,reiteracao.getDespacho());
            comando.executeUpdate();

            try(ResultSet resposta = comando.getGeneratedKeys()){
                if(resposta.next()){
                    reiteracao.setId(resposta.getInt(1));
                }
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public Reiteracao buscarPorId(int id){

       Reiteracao reiteracao = new Reiteracao();

    }

}
