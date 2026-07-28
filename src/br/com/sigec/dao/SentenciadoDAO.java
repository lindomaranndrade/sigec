package br.com.sigec.dao;

import br.com.sigec.model.Sentenciado;
import br.com.sigec.util.Conexao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SentenciadoDAO {

    public void inserir(Sentenciado sentenciado){
        try(Connection conexao = Conexao.conectar()){
            String sql = "INSERT INTO sentenciado(matricula, nome) VALUES (?, ?)";
            PreparedStatement comando = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            comando.setString(1, sentenciado.getMatricula());
            comando.setString(2, sentenciado.getNome());
            comando.executeUpdate();
            ResultSet resposta = comando.getGeneratedKeys();

            if(resposta.next()){
                sentenciado.setId(resposta.getInt(1));
            }

        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public Sentenciado buscarPorId(int id){
        try(Connection conexao = Conexao.conectar()){
            String sql = "SELECT * FROM sentenciado WHERE id = ?";
            PreparedStatement comando = conexao.prepareStatement(sql);
            comando.setInt(1,id);
            ResultSet resposta = comando.executeQuery();

            if(resposta.next()){
               return montarSentenciado(resposta);
            }

        }catch (SQLException e){
            throw  new RuntimeException(e);
        }
        return  null;
    }

    public List<Sentenciado> buscarPorNome(String nome){
        try(Connection conexao = Conexao.conectar()){
            String sql = "SELECT * FROM sentenciado WHERE nome LIKE ?";
            PreparedStatement comando = conexao.prepareStatement(sql);
            comando.setString(1,"%"+nome+"%");
            ResultSet resposta = comando.executeQuery();
            List<Sentenciado> sentenciados = new ArrayList<>();

            while(resposta.next()){
                sentenciados.add(montarSentenciado(resposta));
            }
            return sentenciados;

        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public List<Sentenciado> listarTodos(){
        try(Connection conexao = Conexao.conectar()){
            String sql = "SELECT * FROM sentenciado";
            PreparedStatement comando = conexao.prepareStatement(sql);
            ResultSet resposta = comando.executeQuery();
            List<Sentenciado> sentenciados = new ArrayList<>();

            while(resposta.next()){
                sentenciados.add(montarSentenciado(resposta));
            }
            return sentenciados;
        }catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    public void atualizar(Sentenciado sentenciado){
        try(Connection conexao = Conexao.conectar()){
            String sql = "UPDATE sentenciado SET(matricula = ?, nome=?) WHERE id = ?";
            PreparedStatement comando = conexao.prepareStatement(sql);
            comando.setString(1, sentenciado.getMatricula());
            comando.setString(2,sentenciado.getNome());
            comando.setInt(3,sentenciado.getId());
            comando.executeUpdate();

        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public void excluir(Sentenciado sentenciado){
        try(Connection conexao = Conexao.conectar()){
           String sql = "DELETE FROM sentenciado WHERE id = ?";
           PreparedStatement comando = conexao.prepareStatement(sql);
           comando.setInt(1,sentenciado.getId());
           comando.executeUpdate();

        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public Sentenciado montarSentenciado(ResultSet resposta) throws SQLException{
        Sentenciado sentenciado = new Sentenciado();
        sentenciado.setId(resposta.getInt("id"));
        sentenciado.setNome(resposta.getString("nome"));
        sentenciado.setMatricula(resposta.getString("matricula"));
        return sentenciado;
    }
}
