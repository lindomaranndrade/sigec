package br.com.sigec.dao;

import br.com.sigec.model.Usuario;
import br.com.sigec.util.Conexao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    public void inserir(Usuario usuario){
        String sql = "INSERT INTO usuario(nome, login, senha, ativo) VALUES (?,?,?,?)";
        try(Connection conexao = Conexao.conectar(); PreparedStatement comando = conexao.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)){
            comando.setString(1, usuario.getNome());
            comando.setString(2, usuario.getLogin());
            comando.setString(3, usuario.getSenha());
            comando.setBoolean(4, usuario.isAtivo());
            comando.executeUpdate();

            try(ResultSet chave = comando.getGeneratedKeys()){
                if(chave.next()){
                    usuario.setId(chave.getInt(1));
                }
            }

        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public Usuario buscarPorId(int id){
        String sql = "SELECT * FROM usuario WHERE ID = ?";
        try(Connection conexao = Conexao.conectar(); PreparedStatement comando = conexao.prepareStatement(sql)){

            comando.setInt(1,id);
            try(ResultSet resultado = comando.executeQuery()){
                if(resultado.next()){
                    Usuario usuario = new Usuario();
                    usuario.setId(resultado.getInt("id"));
                    usuario.setNome(resultado.getString("nome"));
                    usuario.setLogin(resultado.getString("login"));
                    usuario.setSenha(resultado.getString("senha"));
                    usuario.setAtivo(resultado.getBoolean("ativo"));
                    return usuario;
                }
            }

        }catch (SQLException e){
            throw new RuntimeException(e);
        }
        return null;
    }

    public boolean existeLogin(String login){
        String sql = "SELECT 1 FROM usuario WHERE login = ?";

        try(Connection conexao = Conexao.conectar();
        PreparedStatement comando = conexao.prepareStatement(sql)){
            comando.setString(1, login);
            try(ResultSet resultado = comando.executeQuery()){
                return resultado.next();
            }

        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public List<Usuario> buscarPorNome(String nome){
        String sql = "SELECT * FROM usuario WHERE nome LIKE ?";
        try(Connection conexao = Conexao.conectar(); PreparedStatement comando = conexao.prepareStatement(sql)){
           comando.setString(1,"%" + nome + "%");
           List<Usuario> listaUsuarios = new ArrayList<>();

            try(ResultSet resultado = comando.executeQuery()){
                while(resultado.next()){
                    Usuario usuario = new Usuario();
                    usuario.setId(resultado.getInt("id"));
                    usuario.setNome(resultado.getString("nome"));
                    usuario.setLogin(resultado.getString("login"));
                    usuario.setSenha(resultado.getString("senha"));
                    usuario.setAtivo(resultado.getBoolean("ativo"));
                    listaUsuarios.add(usuario);
                }
            }

           return listaUsuarios;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Usuario> listarTodos(){
        String sql = "SELECT * FROM usuario";
        try(Connection conexao = Conexao.conectar(); PreparedStatement comando = conexao.prepareStatement(sql)){
            List<Usuario> listaUsuarios = new ArrayList<>();
            try(ResultSet resultado = comando.executeQuery()){
                while(resultado.next()){
                    Usuario usuario = new Usuario();
                    usuario.setId(resultado.getInt("id"));
                    usuario.setNome(resultado.getString("nome"));
                    usuario.setLogin(resultado.getString("login"));
                    usuario.setSenha(resultado.getString("senha"));
                    usuario.setAtivo(resultado.getBoolean("ativo"));
                    listaUsuarios.add(usuario);
                }
            }

            return listaUsuarios;

        }catch (SQLException e){
            throw  new RuntimeException(e);
        }
    }

    public void atualizar(Usuario usuario){
        String sql = "UPDATE usuario SET nome = ?, login = ?, senha = ?, ativo = ? WHERE id = ?";
        try(Connection conexao = Conexao.conectar(); PreparedStatement comando = conexao.prepareStatement(sql)){
            comando.setString(1,usuario.getNome());
            comando.setString(2,usuario.getLogin());
            comando.setString(3,usuario.getSenha());
            comando.setBoolean(4, usuario.isAtivo());
            comando.setInt(5,usuario.getId());
            comando.executeUpdate();

        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public Usuario autenticar(String login, String senha){
        String sql = "SELECT * FROM usuario WHERE login = ? AND senha = ? AND ativo = 1";
        try(Connection conexao = Conexao.conectar();
        PreparedStatement comando = conexao.prepareStatement(sql)){
            comando.setString(1,login);
            comando.setString(2,senha);
            ResultSet resultado = comando.executeQuery();

            if(resultado.next()){
                Usuario usuario = new Usuario();

                usuario.setId(resultado.getInt("id"));
                usuario.setNome(resultado.getString("nome"));
                usuario.setLogin(resultado.getString("login"));
                usuario.setSenha(resultado.getString("senha"));
                usuario.setAtivo(resultado.getBoolean("ativo"));

                return usuario;
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
        return null;
    }
}
