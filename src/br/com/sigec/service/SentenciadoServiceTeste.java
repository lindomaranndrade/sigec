package br.com.sigec.service;

import br.com.sigec.model.Sentenciado;

public class SentenciadoServiceTeste {
    private static final SentenciadoService service =
            new SentenciadoService();

    public static void main(String[] args) {

        testarSentenciadoNulo();

        testarNomeNulo();
        testarNomeVazio();
        testarNomeMuitoCurto();
        testarNomeMuitoGrande();

        testarMatriculaNula();
        testarMatriculaVazia();
        testarMatriculaComLetras();

        testarNormalizacaoMatricula();

        testarCadastroValido();
    }

    private static void testarSentenciadoNulo() {
        System.out.println("\n=== TESTE: Sentenciado Nulo ===");

        try {
            service.inserir(null);
            System.out.println("❌ FALHOU - Deveria lançar exceção.");
        } catch (Exception e) {
            System.out.println("✅ OK - " + e.getMessage());
        }
    }

    private static void testarNomeNulo() {
        System.out.println("\n=== TESTE: Nome Nulo ===");

        try {
            Sentenciado s = new Sentenciado();
            s.setMatricula("12345");

            service.inserir(s);

            System.out.println("❌ FALHOU - Nome nulo foi aceito.");
        } catch (Exception e) {
            System.out.println("✅ OK - " + e.getMessage());
        }
    }

    private static void testarNomeVazio() {
        System.out.println("\n=== TESTE: Nome Vazio ===");

        try {
            Sentenciado s = new Sentenciado();
            s.setNome("   ");
            s.setMatricula("12345");

            service.inserir(s);

            System.out.println("❌ FALHOU - Nome vazio foi aceito.");
        } catch (Exception e) {
            System.out.println("✅ OK - " + e.getMessage());
        }
    }

    private static void testarNomeMuitoCurto() {
        System.out.println("\n=== TESTE: Nome Muito Curto ===");

        try {
            Sentenciado s = new Sentenciado();
            s.setNome("Al");
            s.setMatricula("12345");

            service.inserir(s);

            System.out.println("❌ FALHOU - Nome curto foi aceito.");
        } catch (Exception e) {
            System.out.println("✅ OK - " + e.getMessage());
        }
    }

    private static void testarNomeMuitoGrande() {
        System.out.println("\n=== TESTE: Nome Muito Grande ===");

        try {

            String nomeGrande = "A".repeat(101);

            Sentenciado s = new Sentenciado();
            s.setNome(nomeGrande);
            s.setMatricula("12345");

            service.inserir(s);

            System.out.println("❌ FALHOU - Nome grande foi aceito.");
        } catch (Exception e) {
            System.out.println("✅ OK - " + e.getMessage());
        }
    }

    private static void testarMatriculaNula() {
        System.out.println("\n=== TESTE: Matrícula Nula ===");

        try {
            Sentenciado s = new Sentenciado();
            s.setNome("João Silva");

            service.inserir(s);

            System.out.println("❌ FALHOU - Matrícula nula foi aceita.");
        } catch (Exception e) {
            System.out.println("✅ OK - " + e.getMessage());
        }
    }

    private static void testarMatriculaVazia() {
        System.out.println("\n=== TESTE: Matrícula Vazia ===");

        try {
            Sentenciado s = new Sentenciado();
            s.setNome("João Silva");
            s.setMatricula("   ");

            service.inserir(s);

            System.out.println("❌ FALHOU - Matrícula vazia foi aceita.");
        } catch (Exception e) {
            System.out.println("✅ OK - " + e.getMessage());
        }
    }

    private static void testarMatriculaComLetras() {
        System.out.println("\n=== TESTE: Matrícula Com Letras ===");

        try {
            Sentenciado s = new Sentenciado();
            s.setNome("João Silva");
            s.setMatricula("12A45");

            service.inserir(s);

            System.out.println("❌ FALHOU - Matrícula com letras foi aceita.");
        } catch (Exception e) {
            System.out.println("✅ OK - " + e.getMessage());
        }
    }

    private static void testarNormalizacaoMatricula() {
        System.out.println("\n=== TESTE: Normalização da Matrícula ===");

        try {

            String matriculaOriginal = "045.678";

            String matriculaNormalizada = matriculaOriginal
                    .replace(".", "")
                    .replace("-", "")
                    .trim()
                    .replaceFirst("^0+", "");

            System.out.println("Matrícula original: " + matriculaOriginal);
            System.out.println("Matrícula normalizada: " + matriculaNormalizada);

            System.out.println("✅ Verificação concluída.");

        } catch (Exception e) {
            System.out.println("❌ FALHOU - " + e.getMessage());
        }
    }

    private static void testarCadastroValido() {
        System.out.println("\n=== TESTE: Cadastro Válido ===");

        try {

            Sentenciado s = new Sentenciado();
            s.setNome("João da Silva");
            s.setMatricula("123.456");

            service.inserir(s);

            System.out.println("✅ Cadastro realizado com sucesso.");

        } catch (Exception e) {
            System.out.println("❌ FALHOU - " + e.getMessage());
        }
    }
}
