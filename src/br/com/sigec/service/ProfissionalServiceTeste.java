package br.com.sigec.service;

import br.com.sigec.model.Profissional;
import br.com.sigec.model.TipoProfissional;

public class ProfissionalServiceTeste {
    public static void main(String[] args) {
        ProfissionalService service = new ProfissionalService();

        testarProfissionalNulo(service);

        testarNomeNulo(service);
        testarNomeEmBranco(service);
        testarNomeMuitoCurto(service);
        testarNomeMuitoGrande(service);
        testarTipoNulo(service);
        testarCadastroValido(service);
    }

    private static void testarProfissionalNulo(ProfissionalService service) {
        System.out.println("\n=== TESTE: Profissional Nulo ===");

        try {
            service.inserir(null);
        } catch (Exception e) {
            System.out.println("ERRO: " + e.getMessage());
        }
    }

    private static void testarNomeNulo(ProfissionalService service) {
        System.out.println("\n=== TESTE: Nome Nulo ===");

        try {
            Profissional profissional =
                    new Profissional(null, TipoProfissional.PSICOLOGO);

            service.inserir(profissional);

        } catch (Exception e) {
            System.out.println("ERRO: " + e.getMessage());
        }
    }

    private static void testarNomeEmBranco(ProfissionalService service) {
        System.out.println("\n=== TESTE: Nome em Branco ===");

        try {
            Profissional profissional =
                    new Profissional("   ", TipoProfissional.PSICOLOGO);

            service.inserir(profissional);

        } catch (Exception e) {
            System.out.println("ERRO: " + e.getMessage());
        }
    }

    private static void testarNomeMuitoCurto(ProfissionalService service) {
        System.out.println("\n=== TESTE: Nome Muito Curto ===");

        try {
            Profissional profissional =
                    new Profissional("Jo", TipoProfissional.PSICOLOGO);

            service.inserir(profissional);

        } catch (Exception e) {
            System.out.println("ERRO: " + e.getMessage());
        }
    }

    private static void testarNomeMuitoGrande(ProfissionalService service) {
        System.out.println("\n=== TESTE: Nome Muito Grande ===");

        try {
            Profissional profissional =
                    new Profissional(
                            "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
                            TipoProfissional.PSICOLOGO
                    );

            service.inserir(profissional);

        } catch (Exception e) {
            System.out.println("ERRO: " + e.getMessage());
        }
    }

    private static void testarTipoNulo(ProfissionalService service) {
        System.out.println("\n=== TESTE: Tipo Nulo ===");

        try {
            Profissional profissional =
                    new Profissional("Carlos Silva", null);

            service.inserir(profissional);

        } catch (Exception e) {
            System.out.println("ERRO: " + e.getMessage());
        }
    }

    private static void testarCadastroValido(ProfissionalService service) {
        System.out.println("\n=== TESTE: Cadastro Válido ===");

        try {
            Profissional profissional =
                    new Profissional(
                            "Carlos Silva",
                            TipoProfissional.PSICOLOGO
                    );

            service.inserir(profissional);

            System.out.println("SUCESSO: Profissional cadastrado!");

        } catch (Exception e) {
            System.out.println("ERRO: " + e.getMessage());
        }
    }

    }

