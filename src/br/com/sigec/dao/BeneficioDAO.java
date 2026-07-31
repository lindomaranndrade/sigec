package br.com.sigec.dao;
import br.com.sigec.model.Beneficio;
import br.com.sigec.util.Conexao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BeneficioDAO {

    public void inserir(Beneficio beneficio){
        String sql = "INSERT INTO beneficio (descricao) VALUES (?)";
        try(Connection conexao = Conexao.conectar();PreparedStatement comando = conexao.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)){

            comando.setString(1, beneficio.getDescricao());
            comando.executeUpdate();
           try( ResultSet chaves = comando.getGeneratedKeys()){
               if(chaves.next()){
                   beneficio.setId(chaves.getInt(1));
               }
           }

        } catch (SQLException e) {
           throw new RuntimeException(e);
        }
    }

    public Beneficio buscarPorId(int id){
        String sql = "SELECT * FROM beneficio WHERE id = ?";
        try(Connection conexao = Conexao.conectar(); PreparedStatement comando = conexao.prepareStatement(sql)){
            comando.setInt(1,id);

            try(ResultSet resultado =  comando.executeQuery()){
                if(resultado.next()){
                    Beneficio beneficio = new Beneficio();
                    beneficio.setId(resultado.getInt("id"));
                    beneficio.setDescricao(resultado.getString("descricao"));
                    return beneficio;
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    public List<Beneficio> buscarPorDescricao(String descricao){
        String sql = "SELECT * FROM beneficio WHERE descricao LIKE ?";
        try(Connection conexao = Conexao.conectar();PreparedStatement comando = conexao.prepareStatement(sql)){

            comando.setString(1,"%" + descricao + "%");
            List<Beneficio> listaBeneficios = new ArrayList<>();
            try(ResultSet resultado = comando.executeQuery()) {
                while(resultado.next()){
                    Beneficio beneficio = new Beneficio();
                    beneficio.setId(resultado.getInt("id"));
                    beneficio.setDescricao(resultado.getString("descricao"));
                    listaBeneficios.add(beneficio);
                }
            }

            return listaBeneficios;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean descricaoExiste(String descricao){
        String sql = "SELECT 1 FROM beneficio WHERE descricao = ?";
        try(Connection conexao = Conexao.conectar();
            PreparedStatement comando = conexao.prepareStatement(sql)){

            comando.setString(1, descricao );
            try(ResultSet resultado = comando.executeQuery()) {
                  return resultado.next();

            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Beneficio> listarTodos(){
        String sql = "SELECT * FROM beneficio";
        try(Connection conexao = Conexao.conectar(); PreparedStatement comando = conexao.prepareStatement(sql)){
            List<Beneficio> beneficios = new ArrayList<>();
            try(ResultSet resultado = comando.executeQuery()){
                while(resultado.next()){
                    Beneficio beneficio = new Beneficio();
                    beneficio.setId(resultado.getInt("id"));
                    beneficio.setDescricao(resultado.getString("descricao"));
                    beneficios.add(beneficio);
                }
            }

            return beneficios;

        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public void atualizar(Beneficio beneficio){
        String sql = "UPDATE beneficio SET descricao = ? WHERE id = ?";
        try(Connection conexao = Conexao.conectar(); PreparedStatement comando = conexao.prepareStatement(sql)){

            comando.setString(1,beneficio.getDescricao());
            comando.setInt(2,beneficio.getId());
            comando.executeUpdate();
            
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public void excluir(int id){
        String sql = "DELETE FROM beneficio WHERE id = ?";
        try(Connection conexao = Conexao.conectar();  PreparedStatement comando = conexao.prepareStatement(sql)){

          comando.setInt(1,id);
          int linhasAfetads = comando.executeUpdate();
            System.out.println("linhasAfetads = " + linhasAfetads);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}
