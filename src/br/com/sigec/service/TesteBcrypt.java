package br.com.sigec.service;

import org.mindrot.jbcrypt.BCrypt;

public class TesteBcrypt {
    public static void main(String[] args) {
        String senha = "123456";
        String hash = BCrypt.hashpw(senha, BCrypt.gensalt());
        System.out.println("senha = " + senha);
        System.out.println("hash = " + hash);

        boolean senhaCorreta = BCrypt.checkpw(senha,hash);
        System.out.println("Senha correta? " + senhaCorreta);
    }
}
