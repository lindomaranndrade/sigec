package br.com.sigec.service;

import br.com.sigec.model.*;

import java.time.LocalDate;

public class EntrevistaServiceTeste {

    private static final EntrevistaService service =
            new EntrevistaService();

    public static void main(String[] args) {

        System.out.println("=================================");
        System.out.println("TESTES ENTREVISTA SERVICE");
        System.out.println("=================================");

        testarEntrevistaNula();
        testarPedidoExameNulo();

        testarPedidoCancelado();
        testarPedidoConcluido();
        testarPedidoTransferido();

        testarProfissionalNulo();

        testarUsuarioNulo();
        testarUsuarioInativo();

        testarDataEntrevistaNula();
        testarDataEntrevistaAnteriorPedido();
        testarDataEntrevistaFutura();

        System.out.println("\n=================================");
        System.out.println("FIM DOS TESTES");
        System.out.println("=================================");
    }

    private static void testarEntrevistaNula() {

        System.out.println("\n[TESTE] Entrevista nula");

        try {

            service.inserir(null);

            System.out.println(
                    "ERRO: deveria lançar exceção"
            );

        } catch (Exception e) {

            System.out.println(
                    "OK -> " + e.getMessage()
            );
        }
    }

    private static void testarPedidoExameNulo() {

        System.out.println("\n[TESTE] Pedido de exame nulo");

        try {

            Entrevista entrevista =
                    criarEntrevistaValida();

            entrevista.setPedidoExame(null);

            service.inserir(entrevista);

            System.out.println(
                    "ERRO: deveria lançar exceção"
            );

        } catch (Exception e) {

            System.out.println(
                    "OK -> " + e.getMessage()
            );
        }
    }

    private static void testarPedidoCancelado() {

        System.out.println("\n[TESTE] Pedido cancelado");

        try {

            Entrevista entrevista =
                    criarEntrevistaValida();

            entrevista.getPedidoExame()
                    .setStatus(
                            StatusPedidoExame.CANCELADO
                    );

            service.inserir(entrevista);

            System.out.println(
                    "ERRO: deveria lançar exceção"
            );

        } catch (Exception e) {

            System.out.println(
                    "OK -> " + e.getMessage()
            );
        }
    }

    private static void testarPedidoConcluido() {

        System.out.println("\n[TESTE] Pedido concluído");

        try {

            Entrevista entrevista =
                    criarEntrevistaValida();

            entrevista.getPedidoExame()
                    .setStatus(
                            StatusPedidoExame.CONCLUIDO
                    );

            service.inserir(entrevista);

            System.out.println(
                    "ERRO: deveria lançar exceção"
            );

        } catch (Exception e) {

            System.out.println(
                    "OK -> " + e.getMessage()
            );
        }
    }

    private static void testarPedidoTransferido() {

        System.out.println("\n[TESTE] Pedido transferido");

        try {

            Entrevista entrevista =
                    criarEntrevistaValida();

            entrevista.getPedidoExame()
                    .setStatus(
                            StatusPedidoExame.TRANSFERIDO
                    );

            service.inserir(entrevista);

            System.out.println(
                    "ERRO: deveria lançar exceção"
            );

        } catch (Exception e) {

            System.out.println(
                    "OK -> " + e.getMessage()
            );
        }
    }

    private static void testarProfissionalNulo() {

        System.out.println("\n[TESTE] Profissional nulo");

        try {

            Entrevista entrevista =
                    criarEntrevistaValida();

            entrevista.setProfissional(null);

            service.inserir(entrevista);

            System.out.println(
                    "ERRO: deveria lançar exceção"
            );

        } catch (Exception e) {

            System.out.println(
                    "OK -> " + e.getMessage()
            );
        }
    }

    private static void testarUsuarioNulo() {

        System.out.println("\n[TESTE] Usuário nulo");

        try {

            Entrevista entrevista =
                    criarEntrevistaValida();

            entrevista.setUsuario(null);

            service.inserir(entrevista);

            System.out.println(
                    "ERRO: deveria lançar exceção"
            );

        } catch (Exception e) {

            System.out.println(
                    "OK -> " + e.getMessage()
            );
        }
    }

    private static void testarUsuarioInativo() {

        System.out.println("\n[TESTE] Usuário inativo");

        try {

            Entrevista entrevista =
                    criarEntrevistaValida();

            entrevista.getUsuario()
                    .setAtivo(false);

            service.inserir(entrevista);

            System.out.println(
                    "ERRO: deveria lançar exceção"
            );

        } catch (Exception e) {

            System.out.println(
                    "OK -> " + e.getMessage()
            );
        }
    }

    private static void testarDataEntrevistaNula() {

        System.out.println("\n[TESTE] Data da entrevista nula");

        try {

            Entrevista entrevista =
                    criarEntrevistaValida();

            entrevista.setDataEntrevista(null);

            service.inserir(entrevista);

            System.out.println(
                    "ERRO: deveria lançar exceção"
            );

        } catch (Exception e) {

            System.out.println(
                    "OK -> " + e.getMessage()
            );
        }
    }

    private static void testarDataEntrevistaAnteriorPedido() {

        System.out.println("\n[TESTE] Data anterior ao pedido");

        try {

            Entrevista entrevista =
                    criarEntrevistaValida();

            entrevista.setDataEntrevista(
                    entrevista.getPedidoExame()
                            .getDataSolicitacao()
                            .minusDays(1)
            );

            service.inserir(entrevista);

            System.out.println(
                    "ERRO: deveria lançar exceção"
            );

        } catch (Exception e) {

            System.out.println(
                    "OK -> " + e.getMessage()
            );
        }
    }

    private static void testarDataEntrevistaFutura() {

        System.out.println("\n[TESTE] Data futura");

        try {

            Entrevista entrevista =
                    criarEntrevistaValida();

            entrevista.setDataEntrevista(
                    LocalDate.now().plusDays(1)
            );

            service.inserir(entrevista);

            System.out.println(
                    "ERRO: deveria lançar exceção"
            );

        } catch (Exception e) {

            System.out.println(
                    "OK -> " + e.getMessage()
            );
        }
    }

    private static Entrevista criarEntrevistaValida() {

        Usuario usuario = new Usuario(
                "Administrador",
                "admin",
                "123456"
        );

        Sentenciado sentenciado = new Sentenciado(
                "123456",
                "João da Silva"
        );

        PedidoExame pedido = new PedidoExame(
                sentenciado,
                LocalDate.now().minusDays(10),
                "000123",
                usuario
        );

        pedido.setStatus(
                StatusPedidoExame.CADASTRADO
        );

        Profissional profissional = new Profissional(
                "Maria Oliveira",
                TipoProfissional.PSICOLOGO
        );

        return new Entrevista(
                pedido,
                profissional,
                LocalDate.now().minusDays(5),
                usuario
        );
    }
}