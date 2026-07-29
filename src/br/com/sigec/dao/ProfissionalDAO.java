package br.com.sigec.dao;

import br.com.sigec.model.Profissional;
import br.com.sigec.model.TipoProfissional;
import br.com.sigec.util.Conexao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProfissionalDAO {

    public void inserir(Profissional profissional){
        String sql = "INSERT INTO profissional (nome, tipo, ativo) VALUES(? , ?, ?)";
        try(Connection conexao = Conexao.conectar();PreparedStatement comando = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){

            comando.setString(1, profissional.getNome());

            //O enum é convertido para String para que seu nome seja armazenado no banco.
            comando.setString(2,profissional.getTipo().name());
            comando.setBoolean(3, profissional.isAtivo());
            comando.executeUpdate();

            try(ResultSet resposta = comando.getGeneratedKeys()){
                //Se uma chave foi retornada, atualiza o objeto com o ID gerado pelo banco.
                if(resposta.next()){
                    profissional.setId(resposta.getInt(1));
                }
            }

        }catch (SQLException e){
            throw  new RuntimeException(e);
        }
    }

    public Profissional buscarPorId(int id){
        String sql = "SELECT * FROM profissional WHERE id = ?";
        try(Connection conexao = Conexao.conectar(); PreparedStatement comando = conexao.prepareStatement(sql)){
            comando.setInt(1,id);
            try(ResultSet resultado = comando.executeQuery()){
                if(resultado.next()){
                    return montarProfissional(resultado);
                }
            }

        }catch(SQLException e){
            throw new RuntimeException(e);
        }
        return null;
    }

    public List<Profissional> buscarPorNome(String nome){
        String sql = "SELECT * FROM profissional WHERE nome LIKE ?";

        try(Connection conexao = Conexao.conectar(); PreparedStatement comando = conexao.prepareStatement(sql)){

            comando.setString(1,"%"+nome+"%");

            List<Profissional> profissionais = new ArrayList<>();

            try(ResultSet resultado = comando.executeQuery()){
                while(resultado.next()){
                    profissionais.add(montarProfissional(resultado));
                }
            }

            return profissionais;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Profissional> listarTodos(){
        String sql = "SELECT * FROM profissional";
        try(Connection conexao = Conexao.conectar(); PreparedStatement comando = conexao.prepareStatement(sql)){
            List<Profissional> profissionais =new ArrayList<>();
            try(ResultSet resultado = comando.executeQuery()){
                while(resultado.next()){
                    profissionais.add(montarProfissional(resultado));
                }
            }

            return profissionais;

        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public void atualizar(Profissional profissional){
        String sql = "UPDATE profissional SET (nome = ?,tipo=? ,ativo=? ) WHERE id =?";
        try(Connection conexao = Conexao.conectar(); PreparedStatement comando = conexao.prepareStatement(sql)){
            comando.setString(1, profissional.getNome());
            comando.setString(2,profissional.getTipo().name());
            comando.setBoolean(3, profissional.isAtivo());
            comando.setInt(4,profissional.getId());
            comando.executeUpdate();

        }catch (SQLException e ){
            throw new RuntimeException(e);
        }
    }

    public void excluir(Profissional profissional){
        String sql = "DELETE FROM profissional WHERE id = ?";
        try(Connection conexao = Conexao.conectar(); PreparedStatement comando = conexao.prepareStatement(sql)){
            comando.setInt(1,profissional.getId());
            comando.executeUpdate();

        }catch (SQLException e){
            throw new RuntimeException(e);
        }

    }


    private Profissional montarProfissional(ResultSet resultado) throws SQLException{
        Profissional profissional = new Profissional();
        profissional.setId(resultado.getInt("id"));
        profissional.setNome(resultado.getString("nome"));
        String tipo = resultado.getString("tipo");
        profissional.setTipo(TipoProfissional.valueOf(tipo));
        profissional.setAtivo(resultado.getBoolean("ativo"));
        return profissional;
    }
}
