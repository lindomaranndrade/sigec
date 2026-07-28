package br.com.sigec.dao;
import br.com.sigec.model.Beneficio;
import br.com.sigec.util.Conexao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BeneficioDAO {

    public void inserir(Beneficio beneficio){
        try(Connection conexao = Conexao.conectar();){
            String sql = "INSERT INTO beneficio (descricao) VALUES (?);";
            PreparedStatement comando = conexao.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            comando.setString(1, beneficio.getDescricao());
            comando.executeUpdate();
            ResultSet chaves = comando.getGeneratedKeys();

            if(chaves.next()){
                beneficio.setId(chaves.getInt(1));
            }

        } catch (SQLException e) {
           throw new RuntimeException(e);
        }
    }

    public Beneficio buscarPorId(int id){
        try(Connection conexao = Conexao.conectar();){
            String sql = "SELECT * FROM beneficio WHERE id = ?";
            PreparedStatement comando = conexao.prepareStatement(sql);
            comando.setInt(1,id);
            ResultSet resultado =  comando.executeQuery();

            if(resultado.next()){
                Beneficio beneficio = new Beneficio();
                beneficio.setId(resultado.getInt("id"));
                beneficio.setDescricao(resultado.getString("descricao"));
                return beneficio;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }
    public List<Beneficio> buscarPorNome(String descricao){
        try(Connection conexao = Conexao.conectar()){
            String sql = "SELECT * FROM beneficio WHERE descricao LIKE ?";
            PreparedStatement comando = conexao.prepareStatement(sql);
            comando.setString(1,"%" + descricao + "%");
            ResultSet resultado = comando.executeQuery();
            List<Beneficio> listaBeneficios = new ArrayList<>();

            while(resultado.next()){
                Beneficio beneficio = new Beneficio();
                beneficio.setId(resultado.getInt("id"));
                beneficio.setDescricao(resultado.getString("descricao"));
                listaBeneficios.add(beneficio);
            }
            return listaBeneficios;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Beneficio> listarTodos(){
        try(Connection conexao = Conexao.conectar()){
            String sql = "SELECT * FROM beneficio";
            PreparedStatement comando = conexao.prepareStatement(sql);
            ResultSet resultado = comando.executeQuery();
            List<Beneficio> beneficios = new ArrayList<>();

            while(resultado.next()){
                Beneficio beneficio = new Beneficio();
                beneficio.setId(resultado.getInt("id"));
                beneficio.setDescricao(resultado.getString("descricao"));
                beneficios.add(beneficio);
            }
            return beneficios;

        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public void atualizar(Beneficio beneficio){
        try(Connection conexao = Conexao.conectar()){
            String sql = "UPDATE beneficio SET descricao = ? WHERE id = ?";
            PreparedStatement comando = conexao.prepareStatement(sql);
            comando.setString(1,beneficio.getDescricao());
            comando.setInt(2,beneficio.getId());
            comando.executeUpdate();
            
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public void excluir(int id){
        try(Connection conexao = Conexao.conectar()){
          String sql = "DELETE FROM beneficio WHERE id = ?";
          PreparedStatement comando = conexao.prepareStatement(sql);
          comando.setInt(1,id);
          int linhasAfetads = comando.executeUpdate();
            System.out.println("linhasAfetads = " + linhasAfetads);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}
