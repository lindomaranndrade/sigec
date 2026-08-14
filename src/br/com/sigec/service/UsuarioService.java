package br.com.sigec.service;

import br.com.sigec.dao.UsuarioDAO;
import br.com.sigec.model.Usuario;
import org.mindrot.jbcrypt.BCrypt;

public class UsuarioService {
    private UsuarioDAO usuarioDAO;
    private static final int TAMANHO_MINIMO_SENHA = 6;
    private static final int TAMANHO_MINIMO_LOGIN = 3;

    public UsuarioService(){
        this.usuarioDAO = new UsuarioDAO();
    }

    public void cadastrar(Usuario usuario){
        if(usuario == null){
            throw new IllegalArgumentException("Usuário não pode ser nulo.");
        }
        validarLogin(usuario);
        validarSenha(usuario);

        validarLoginDuplicado(usuario);
        //transforma a senha do usuario em hash
        usuario.setSenha(gerarHash(usuario.getSenha()));
        usuarioDAO.inserir(usuario);
    }

    private void validarSenha(Usuario usuario){
        if(usuario.getSenha() == null || usuario.getSenha().isBlank()){
            throw new IllegalArgumentException("Senha é obrigatória.");
        }

        if(usuario.getSenha().length() < TAMANHO_MINIMO_SENHA){
            throw new IllegalArgumentException("A senha deve possuir pelo menos " + TAMANHO_MINIMO_SENHA + " caracteres.");
        }
    }

    private void validarLogin(Usuario usuario){
        if(usuario.getLogin() == null || usuario.getLogin().isBlank()){
            throw new IllegalArgumentException("Login é obrigatório.");
        }

        if(usuario.getLogin().length() < TAMANHO_MINIMO_LOGIN){
            throw new IllegalArgumentException("O login deve possuir pelo menos " + TAMANHO_MINIMO_LOGIN + " caracteres");
        }
    }

    private void validarLoginDuplicado(Usuario usuario){
        if(usuarioDAO.existeLogin(usuario.getLogin())){
            throw  new IllegalArgumentException("Login não disponível.");
        }
    }

    private String gerarHash(String senha){
        return BCrypt.hashpw(senha,BCrypt.gensalt());
    }

    public Usuario autenticar(String login, String senha){
        Usuario usuario = usuarioDAO.buscarPorLogin(login);

        if(usuario == null){
            return null;
        }
        if (!BCrypt.checkpw(senha, usuario.getSenha())) {
            return null;
        }
        return usuario;
    }
}
