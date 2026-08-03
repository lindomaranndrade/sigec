package br.com.sigec.service;

import br.com.sigec.model.*;

import java.time.LocalDate;

public class PedidoBeneficioServiceTeste {

    private static final PedidoBeneficioService service =
            new PedidoBeneficioService();

    public static void main(String[] args) {

        testarPedidoBeneficioNulo();

        testarPedidoExameNulo();

        testarIdPedidoInvalido();

        testarBeneficioNulo();

        testarIdBeneficioInvalido();

        testarPedidoCancelado();

        testarPedidoConcluido();

        testarPedidoTransferido();

        System.out.println("\nTodos os testes executados.");
    }

    private static void testarPedidoBeneficioNulo() {

        try {
            service.inserir(null);
            System.out.println("ERRO: deveria lançar exceção.");
        } catch (IllegalArgumentException e) {
            System.out.println("OK - PedidoBeneficio nulo");
        }
    }

    private static void testarPedidoExameNulo() {

        try {

            PedidoBeneficio pedidoBeneficio =
                    criarPedidoBeneficioValido();

            pedidoBeneficio.setPedido(null);

            service.inserir(pedidoBeneficio);

            System.out.println("ERRO: deveria lançar exceção.");

        } catch (IllegalArgumentException e) {

            System.out.println("OK - PedidoExame nulo");
        }
    }

    private static void testarIdPedidoInvalido() {

        try {

            PedidoBeneficio pedidoBeneficio =
                    criarPedidoBeneficioValido();

            pedidoBeneficio.getPedido().setId(0);

            service.inserir(pedidoBeneficio);

            System.out.println("ERRO: deveria lançar exceção.");

        } catch (IllegalArgumentException e) {

            System.out.println("OK - ID pedido inválido");
        }
    }

    private static void testarBeneficioNulo() {

        try {

            PedidoBeneficio pedidoBeneficio =
                    criarPedidoBeneficioValido();

            pedidoBeneficio.setBeneficio(null);

            service.inserir(pedidoBeneficio);

            System.out.println("ERRO: deveria lançar exceção.");

        } catch (IllegalArgumentException e) {

            System.out.println("OK - Benefício nulo");
        }
    }

    private static void testarIdBeneficioInvalido() {

        try {

            PedidoBeneficio pedidoBeneficio =
                    criarPedidoBeneficioValido();

            pedidoBeneficio.getBeneficio().setId(0);

            service.inserir(pedidoBeneficio);

            System.out.println("ERRO: deveria lançar exceção.");

        } catch (IllegalArgumentException e) {

            System.out.println("OK - ID benefício inválido");
        }
    }

    private static void testarPedidoCancelado() {

        try {

            PedidoBeneficio pedidoBeneficio =
                    criarPedidoBeneficioValido();

            pedidoBeneficio.getPedido()
                    .setStatus(StatusPedidoExame.CANCELADO);

            service.inserir(pedidoBeneficio);

            System.out.println("ERRO: deveria lançar exceção.");

        } catch (IllegalArgumentException e) {

            System.out.println("OK - Pedido cancelado");
        }
    }

    private static void testarPedidoConcluido() {

        try {

            PedidoBeneficio pedidoBeneficio =
                    criarPedidoBeneficioValido();

            pedidoBeneficio.getPedido()
                    .setStatus(StatusPedidoExame.CONCLUIDO);

            service.inserir(pedidoBeneficio);

            System.out.println("ERRO: deveria lançar exceção.");

        } catch (IllegalArgumentException e) {

            System.out.println("OK - Pedido concluído");
        }
    }

    private static void testarPedidoTransferido() {

        try {

            PedidoBeneficio pedidoBeneficio =
                    criarPedidoBeneficioValido();

            pedidoBeneficio.getPedido()
                    .setStatus(StatusPedidoExame.TRANSFERIDO);

            service.inserir(pedidoBeneficio);

            System.out.println("ERRO: deveria lançar exceção.");

        } catch (IllegalArgumentException e) {

            System.out.println("OK - Pedido transferido");
        }
    }

    private static PedidoBeneficio criarPedidoBeneficioValido() {

        Usuario usuario =
                new Usuario(
                        "Administrador",
                        "admin",
                        "123456"
                );

        usuario.setId(1);

        Sentenciado sentenciado =
                new Sentenciado(
                        "123456",
                        "João da Silva"
                );

        PedidoExame pedido =
                new PedidoExame(
                        sentenciado,
                        LocalDate.now().minusDays(10),
                        "000123",
                        usuario
                );

        pedido.setId(1);
        pedido.setStatus(
                StatusPedidoExame.CADASTRADO
        );

        Beneficio beneficio =
                new Beneficio(
                        "Progressão de Regime",
                        "PR"
                );

        beneficio.setId(1);

        return new PedidoBeneficio(
                pedido,
                beneficio
        );
    }
}