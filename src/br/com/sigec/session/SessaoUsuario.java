package br.com.sigec.session;

import br.com.sigec.dao.UsuarioDAO;
import br.com.sigec.model.Usuario;

public class SessaoUsuario {
    private static UsuarioDAO usuarioDAO = new UsuarioDAO();
    private static Usuario usuario = usuarioDAO.buscarPorId(1);

    public static Usuario getUsuario() {
        return usuario;
    }

    public static void setUsuario(Usuario usuario) {
        SessaoUsuario.usuario = usuario;
    }

    public void limpar(){
        usuario = null;
    }
}
