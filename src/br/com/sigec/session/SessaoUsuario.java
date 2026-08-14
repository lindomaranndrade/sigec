package br.com.sigec.session;

import br.com.sigec.dao.UsuarioDAO;
import br.com.sigec.model.Usuario;

public class SessaoUsuario {
    private static UsuarioDAO usuarioDAO = new UsuarioDAO();
    private static Usuario usuarioLogado = usuarioDAO.buscarPorId(1);

    public static Usuario getUsuarioLogado() {
        return usuarioLogado;
    }

    public static void setUsuarioLogado(Usuario usuarioLogado) {
        SessaoUsuario.usuarioLogado = usuarioLogado;
    }

    static void limpar(){
        usuarioLogado = null;
    }
}
