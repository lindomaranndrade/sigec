package br.com.sigec.dao;

import br.com.sigec.model.Usuario;

import java.util.ArrayList;
import java.util.List;

public class TesteUsuarioDAO {
    public static void main(String[] args) {
        //Usuario usuario = new Usuario("Lindomar Andrade Silva", "lindomar", "123456");
        UsuarioDAO dao = new UsuarioDAO();
        //dao.inserir(usuario);
        List<Usuario> listaUsuarios = new ArrayList<>();
        listaUsuarios= dao.listarTodos();

        for(Usuario u: listaUsuarios){
            System.out.println(u);
        }

        Usuario usuario = new Usuario();
        usuario = dao.buscarPorId(1);
        System.out.println("Usuario Localizado:  " + usuario);
    }
}
