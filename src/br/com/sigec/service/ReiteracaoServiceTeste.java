package br.com.sigec.service;

import br.com.sigec.model.*;

import java.time.LocalDate;

public class ReiteracaoServiceTeste {

    private static final ReiteracaoService service =
            new ReiteracaoService();

    public static void main(String[] args) {

        System.out.println("=================================");
        System.out.println("TESTES REITERACAO SERVICE");
        System.out.println("=================================");

        testarReiteracaoNula();
        testarPedidoNulo();
        testarUsuarioNulo();
        testarUsuarioInativo();
        testarDataReiteracaoNula();
        testarDataReiteracaoFutura();
        testarDataReiteracaoAnteriorPedido();
        testarObservacaoMuitoGrande();

        // deixe por último porque grava no banco
        testarInsercaoValida();

        System.out.println("\n=================================");
        System.out.println("FIM DOS TESTES");
        System.out.println("=================================");
    }

    private static void testarReiteracaoNula() {

        System.out.println("\n[TESTE] Reiteração nula");

        try {
            service.inserir(null);
            System.out.println("ERRO: deveria lançar exceção");
        } catch (Exception e) {
            System.out.println("OK -> " + e.getMessage());
        }
    }

    private static void testarPedidoNulo() {

        System.out.println("\n[TESTE] Pedido nulo");

        try {
            Reiteracao reiteracao = criarReiteracaoValida();

            reiteracao.setPedido(null);

            service.inserir(reiteracao);

            System.out.println("ERRO: deveria lançar exceção");
        } catch (Exception e) {
            System.out.println("OK -> " + e.getMessage());
        }
    }

    private static void testarUsuarioNulo() {

        System.out.println("\n[TESTE] Usuário nulo");

        try {
            Reiteracao reiteracao = criarReiteracaoValida();

            reiteracao.setUsuario(null);

            service.inserir(reiteracao);

            System.out.println("ERRO: deveria lançar exceção");
        } catch (Exception e) {
            System.out.println("OK -> " + e.getMessage());
        }
    }

    private static void testarUsuarioInativo() {

        System.out.println("\n[TESTE] Usuário inativo");

        try {
            Reiteracao reiteracao = criarReiteracaoValida();

            reiteracao.getUsuario().setAtivo(false);

            service.inserir(reiteracao);

            System.out.println("ERRO: deveria lançar exceção");
        } catch (Exception e) {
            System.out.println("OK -> " + e.getMessage());
        }
    }

    private static void testarDataReiteracaoNula() {

        System.out.println("\n[TESTE] Data da reiteração nula");

        try {
            Reiteracao reiteracao = criarReiteracaoValida();

            reiteracao.setDataReiteracao(null);

            service.inserir(reiteracao);

            System.out.println("ERRO: deveria lançar exceção");
        } catch (Exception e) {
            System.out.println("OK -> " + e.getMessage());
        }
    }

    private static void testarDataReiteracaoFutura() {

        System.out.println("\n[TESTE] Data futura");

        try {
            Reiteracao reiteracao = criarReiteracaoValida();

            reiteracao.setDataReiteracao(
                    LocalDate.now().plusDays(1)
            );

            service.inserir(reiteracao);

            System.out.println("ERRO: deveria lançar exceção");
        } catch (Exception e) {
            System.out.println("OK -> " + e.getMessage());
        }
    }

    private static void testarDataReiteracaoAnteriorPedido() {

        System.out.println("\n[TESTE] Data anterior ao pedido");

        try {
            Reiteracao reiteracao = criarReiteracaoValida();

            reiteracao.setDataReiteracao(
                    reiteracao.getPedido()
                            .getDataSolicitacao()
                            .minusDays(1)
            );

            service.inserir(reiteracao);

            System.out.println("ERRO: deveria lançar exceção");
        } catch (Exception e) {
            System.out.println("OK -> " + e.getMessage());
        }
    }

    private static void testarObservacaoMuitoGrande() {

        System.out.println("\n[TESTE] Observação muito grande");

        try {
            Reiteracao reiteracao = criarReiteracaoValida();

            reiteracao.setObservacoes(
                    "A".repeat(101)
            );

            service.inserir(reiteracao);

            System.out.println("ERRO: deveria lançar exceção");
        } catch (Exception e) {
            System.out.println("OK -> " + e.getMessage());
        }
    }

    private static void testarInsercaoValida() {

        System.out.println("\n[TESTE] Inserção válida");

        try {

            Reiteracao reiteracao = criarReiteracaoValida();

            service.inserir(reiteracao);

            System.out.println(
                    "OK -> Reiteração cadastrada com sucesso"
            );

        } catch (Exception e) {

            System.out.println(
                    "ERRO -> " + e.getMessage()
            );
        }
    }

    private static Reiteracao criarReiteracaoValida() {

        Usuario usuario = new Usuario(
                "Administrador",
                "admin",
                "123456"
        );

        usuario.setId(1);

        Sentenciado sentenciado = new Sentenciado(
                "123456",
                "João da Silva"
        );

        sentenciado.setId(1);

        PedidoExame pedido = new PedidoExame(
                sentenciado,
                LocalDate.now().minusDays(10),
                "0001234-56.2026.8.26.0001",
                usuario
        );

        pedido.setId(1);

        return new Reiteracao(
                pedido,
                LocalDate.now().minusDays(5),
                "Observação de teste",
                null,
                usuario
        );
    }
}