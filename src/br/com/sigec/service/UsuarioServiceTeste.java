package br.com.sigec.service;

import br.com.sigec.model.Usuario;

public class UsuarioServiceTeste {
    public static void main(String[] args) {
        UsuarioService usuarioService = new UsuarioService();

        testarUsuarioNulo(usuarioService);
        testarLoginVazio(usuarioService);
        testarSenhaVazia(usuarioService);
        testarSenhaCurta(usuarioService);
        testarLoginDuplicado(usuarioService);

    }

    private static void testarUsuarioNulo(UsuarioService usuarioService) {

        try {
            usuarioService.cadastrar(null);
        } catch (IllegalArgumentException e) {
            System.out.println("TESTE USUÁRIO NULO");
            System.out.println(e.getMessage());
            System.out.println();
        }
    }

    private static void testarLoginVazio(UsuarioService usuarioService) {

        try {

            Usuario usuario = new Usuario();
            usuario.setNome("Teste");
            usuario.setLogin("");
            usuario.setSenha("123456");

            usuarioService.cadastrar(usuario);

        } catch (IllegalArgumentException e) {

            System.out.println("TESTE LOGIN VAZIO");
            System.out.println(e.getMessage());
            System.out.println();
        }
    }

    private static void testarSenhaVazia(UsuarioService usuarioService) {

        try {

            Usuario usuario = new Usuario();
            usuario.setNome("Teste");
            usuario.setLogin("teste123");
            usuario.setSenha("");

            usuarioService.cadastrar(usuario);

        } catch (IllegalArgumentException e) {

            System.out.println("TESTE SENHA VAZIA");
            System.out.println(e.getMessage());
            System.out.println();
        }
    }

    private static void testarSenhaCurta(UsuarioService usuarioService) {

        try {

            Usuario usuario = new Usuario();
            usuario.setNome("Teste");
            usuario.setLogin("teste123");
            usuario.setSenha("123");

            usuarioService.cadastrar(usuario);

        } catch (IllegalArgumentException e) {

            System.out.println("TESTE SENHA CURTA");
            System.out.println(e.getMessage());
            System.out.println();
        }
    }

    private static void testarLoginDuplicado(UsuarioService usuarioService) {

        try {

            Usuario usuario = new Usuario();
            usuario.setNome("Teste");
            usuario.setLogin("admin"); // precisa existir no banco
            usuario.setSenha("123456");

            usuarioService.cadastrar(usuario);

        } catch (IllegalArgumentException e) {

            System.out.println("TESTE LOGIN DUPLICADO");
            System.out.println(e.getMessage());
            System.out.println();
        }
    }
    }

