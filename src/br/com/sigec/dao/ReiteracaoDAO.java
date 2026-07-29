package br.com.sigec.dao;

import br.com.sigec.model.PedidoExame;
import br.com.sigec.model.Reiteracao;
import br.com.sigec.model.Sentenciado;
import br.com.sigec.model.Usuario;
import br.com.sigec.util.Conexao;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReiteracaoDAO {

    public void inserir(Reiteracao reiteracao){
        String sql = """
                INSERT INTO reiteracao(id_pedido_exame, data_reiteracao, id_usuario, observacoes, despacho)
                            VALUES(?, ?, ?, ?, ?)
                """;

        try(
                Connection conexao = Conexao.conectar();
                PreparedStatement comando = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){

            comando.setInt(1,reiteracao.getPedido().getId());
            comando.setDate(2,java.sql.Date.valueOf(reiteracao.getDataReiteracao()));
            comando.setInt(3,reiteracao.getUsuario().getId());
            comando.setString(4,reiteracao.getObservacoes());
            comando.setString(5,reiteracao.getDespacho());
            comando.executeUpdate();

            try(ResultSet resultado = comando.getGeneratedKeys()){
                if(resultado.next()){
                    reiteracao.setId(resultado.getInt(1));
                }
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public Reiteracao buscarPorId(int id){

       String sql = """
               SELECT
               r.id AS id_reiteracao,
               r.data_reiteracao,
               r.observacoes,
               r.despacho,
               r.data_cadastro,
               
               p.id AS id_pedido_exame,
               
               s.id AS id_sentenciado,
               s.matricula,
               s.nome
               
               FROM reiteracao r INNER JOIN pedido_exame p ON (p.id = r.id_pedido_exame)
               	INNER JOIN sentenciado s ON (s.id = p.id_sentenciado)
               WHERE r.id =?
               """;
        try(
                Connection conexao = Conexao.conectar();
                PreparedStatement comando = conexao.prepareStatement(sql);){

                comando.setInt(1,id);
                try(ResultSet resultado = comando.executeQuery()){
                    if(resultado.next()){
                        return montarReiteracao(resultado);
                    }
                }


        }catch (SQLException e){
            throw new RuntimeException(e);
        }
        return null;
    }


    public List<Reiteracao> listarTodos(){
        List<Reiteracao> reiteracoes = new ArrayList<>();
        String sql = """
               SELECT
               r.id AS id_reiteracao,
               r.data_reiteracao,
               r.observacoes,
               r.despacho,
               r.data_cadastro,
               
               p.id AS id_pedido_exame,
               
               s.id AS id_sentenciado,
               s.matricula,
               s.nome
               
               FROM reiteracao r INNER JOIN pedido_exame p ON (p.id = r.id_pedido_exame)
               	INNER JOIN sentenciado s ON (s.id = p.id_sentenciado)
               """;
        try(
                Connection conexao = Conexao.conectar();
                PreparedStatement comando = conexao.prepareStatement(sql);
                ResultSet resultado = comando.executeQuery() ){

            while (resultado.next()){
                reiteracoes.add(montarReiteracao(resultado));
            }
            return reiteracoes;
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public void atualizar(Reiteracao reiteracao){
        String sql = """
                UPDATE reiteracao
                SET id_pedido_exame = ?, 
                    data_reiteracao = ?, 
                    id_usuario= ?, 
                    observacoes = ?, 
                    despacho = ?
                WHERE reiteracao.id = ?;
                """;
        try (Connection conexao = Conexao.conectar();
                PreparedStatement comando = conexao.prepareStatement(sql)){
            comando.setInt(1,reiteracao.getPedido().getId());
            comando.setDate(2, Date.valueOf(reiteracao.getDataReiteracao()));
            comando.setInt(3,reiteracao.getUsuario().getId());
            comando.setString(4, reiteracao.getObservacoes());
            comando.setString(5, reiteracao.getDespacho());
            comando.setInt(6,reiteracao.getId());

            comando.executeUpdate();

        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public boolean excluir(int id){
        String sql = "DELETE FROM reiteracao WHERE id = ?";
        try(Connection conexao = Conexao.conectar();
            PreparedStatement comando = conexao.prepareStatement(sql)){

            comando.setInt(1,id);

            int linhasAfetadas = comando.executeUpdate();
            if(linhasAfetadas == 1){
                return true;
            }else{
                return false;
            }

        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    private Reiteracao montarReiteracao(ResultSet resultado) throws SQLException{
        Reiteracao reiteracao = new Reiteracao();
        PedidoExame pedido = new PedidoExame();
        Sentenciado sentenciado = new Sentenciado();

        reiteracao.setId(resultado.getInt("id_reiteracao"));
        reiteracao.setDataReiteracao(converteData(resultado, "data_reiteracao"));
        reiteracao.setObservacoes(resultado.getString("observacoes"));
        reiteracao.setDespacho(resultado.getString("despacho"));
        reiteracao.setDataCadastro(converteData(resultado, "data_cadastro"));
        pedido.setId(resultado.getInt("id_pedido_exame"));

        sentenciado.setId(resultado.getInt("id_sentenciado"));
        sentenciado.setMatricula(resultado.getString("matricula"));
        sentenciado.setNome(resultado.getString("nome"));

        pedido.setSentenciado(sentenciado);
        reiteracao.setPedido(pedido);
        return reiteracao;
    }

    private LocalDate converteData(ResultSet resposta, String nomeDaColuna) throws SQLException {
        Date data = resposta.getDate(nomeDaColuna);
        return data != null ? data.toLocalDate() : null;
    }

}
