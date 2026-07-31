package br.com.sigec.dao;

import br.com.sigec.model.Sentenciado;
import br.com.sigec.util.Conexao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SentenciadoDAO {

    public void inserir(Sentenciado sentenciado){
        String sql = "INSERT INTO sentenciado(matricula, nome) VALUES (?, ?)";
        try(Connection conexao = Conexao.conectar(); PreparedStatement comando = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            comando.setString(1, sentenciado.getMatricula());
            comando.setString(2, sentenciado.getNome());
            comando.executeUpdate();
            try(ResultSet resposta = comando.getGeneratedKeys()){
                if(resposta.next()){
                    sentenciado.setId(resposta.getInt(1));
                }
            }

        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public Sentenciado buscarPorId(int id){
        String sql = "SELECT * FROM sentenciado WHERE id = ?";
        try(Connection conexao = Conexao.conectar(); PreparedStatement comando = conexao.prepareStatement(sql)){
            comando.setInt(1,id);
            try(ResultSet resposta = comando.executeQuery()){

                if(resposta.next()){
                    return montarSentenciado(resposta);
                }
            }

        }catch (SQLException e){
            throw  new RuntimeException(e);
        }
        return  null;
    }

    public Sentenciado buscarPorMatricula(String matricula){
        String sql = "SELECT * FROM sentenciado WHERE matricula = ?";
        try(Connection conexao = Conexao.conectar(); PreparedStatement comando = conexao.prepareStatement(sql)){
            comando.setString(1,matricula);
            try(ResultSet resposta = comando.executeQuery()){

                if(resposta.next()){
                    return montarSentenciado(resposta);
                }
            }

        }catch (SQLException e){
            throw  new RuntimeException(e);
        }
        return  null;
    }

    public List<Sentenciado> buscarPorNome(String nome){
        String sql = "SELECT * FROM sentenciado WHERE nome LIKE ?";
        try(Connection conexao = Conexao.conectar();PreparedStatement comando = conexao.prepareStatement(sql)){
            comando.setString(1,"%"+nome+"%");
            List<Sentenciado> sentenciados = new ArrayList<>();
            try(ResultSet resposta = comando.executeQuery()){
                while(resposta.next()){
                    sentenciados.add(montarSentenciado(resposta));
                }
            }
            return sentenciados;

        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public List<Sentenciado> listarTodos(){
        String sql = "SELECT * FROM sentenciado";
        try(Connection conexao = Conexao.conectar();PreparedStatement comando = conexao.prepareStatement(sql)){
            List<Sentenciado> sentenciados = new ArrayList<>();
            try(ResultSet resposta = comando.executeQuery()){
                while(resposta.next()){
                    sentenciados.add(montarSentenciado(resposta));
                }
            }
            return sentenciados;
        }catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    public void atualizarNome(Sentenciado sentenciado){
        String sql = "UPDATE sentenciado SET nome=? WHERE id = ?";
        try(Connection conexao = Conexao.conectar(); PreparedStatement comando = conexao.prepareStatement(sql)){

            comando.setString(1,sentenciado.getNome());
            comando.setInt(2,sentenciado.getId());
            comando.executeUpdate();

        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public void excluir(Sentenciado sentenciado){
        String sql = "DELETE FROM sentenciado WHERE id = ?";
        try(Connection conexao = Conexao.conectar(); PreparedStatement comando = conexao.prepareStatement(sql)){
           comando.setInt(1,sentenciado.getId());
           comando.executeUpdate();

        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    private Sentenciado montarSentenciado(ResultSet resposta) throws SQLException{
        Sentenciado sentenciado = new Sentenciado();
        sentenciado.setId(resposta.getInt("id"));
        sentenciado.setNome(resposta.getString("nome"));
        sentenciado.setMatricula(resposta.getString("matricula"));
        return sentenciado;
    }
}
