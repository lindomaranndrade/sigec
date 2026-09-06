package br.com.sigec.service;

import br.com.sigec.model.*;
import br.com.sigec.session.SessaoUsuario;

import java.time.LocalDate;

public class EntrevistaServiceTeste {

    private static final EntrevistaService service =
            new EntrevistaService();

    public static void main(String[] args) {

        System.out.println("=================================");
        System.out.println("TESTES ENTREVISTA SERVICE");
        System.out.println("=================================");

        testarPedidoExameNulo();

        testarPedidoCancelado();
        testarPedidoConcluido();
        testarPedidoTransferido();

        testarTipoAtendimentoNulo();

        testarUsuarioNulo();
        testarUsuarioInativo();

        testarAgendarProfissionalNulo();
        testarAgendarProfissionalInativo();
        testarAgendarProfissionalTipoIncompativel();
        testarAgendarDataNula();
        testarAgendarDataAnteriorPedido();

        testarRegistrarRealizacaoSemAgendamento();
        testarRegistrarRealizacaoDataFutura();

        testarCancelarEntrevistaJaCancelada();

        System.out.println("\n=================================");
        System.out.println("FIM DOS TESTES");
        System.out.println("=================================");
    }

    private static void testarPedidoExameNulo() {

        System.out.println("\n[TESTE] Pedido de exame nulo");

        try {

            service.criarPendente(null, TipoProfissional.PSICOLOGO);

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

            PedidoExame pedido = criarPedidoValido();
            pedido.setStatus(StatusPedidoExame.CANCELADO);

            service.criarPendente(pedido, TipoProfissional.PSICOLOGO);

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

            PedidoExame pedido = criarPedidoValido();
            pedido.setStatus(StatusPedidoExame.CONCLUIDO);

            service.criarPendente(pedido, TipoProfissional.PSICOLOGO);

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

            PedidoExame pedido = criarPedidoValido();
            pedido.setStatus(StatusPedidoExame.TRANSFERIDO);

            service.criarPendente(pedido, TipoProfissional.PSICOLOGO);

            System.out.println(
                    "ERRO: deveria lançar exceção"
            );

        } catch (Exception e) {

            System.out.println(
                    "OK -> " + e.getMessage()
            );
        }
    }

    private static void testarTipoAtendimentoNulo() {

        System.out.println("\n[TESTE] Tipo de atendimento nulo");

        try {

            PedidoExame pedido = criarPedidoValido();

            service.criarPendente(pedido, null);

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

        Usuario usuarioOriginal = SessaoUsuario.getUsuarioLogado();

        try {

            SessaoUsuario.setUsuarioLogado(null);

            PedidoExame pedido = criarPedidoValido();

            service.criarPendente(pedido, TipoProfissional.PSICOLOGO);

            System.out.println(
                    "ERRO: deveria lançar exceção"
            );

        } catch (Exception e) {

            System.out.println(
                    "OK -> " + e.getMessage()
            );

        } finally {
            SessaoUsuario.setUsuarioLogado(usuarioOriginal);
        }
    }

    private static void testarUsuarioInativo() {

        System.out.println("\n[TESTE] Usuário inativo");

        Usuario usuarioOriginal = SessaoUsuario.getUsuarioLogado();

        try {

            Usuario usuarioInativo = new Usuario("Administrador", "admin", "123456");
            usuarioInativo.setAtivo(false);
            SessaoUsuario.setUsuarioLogado(usuarioInativo);

            PedidoExame pedido = criarPedidoValido();

            service.criarPendente(pedido, TipoProfissional.PSICOLOGO);

            System.out.println(
                    "ERRO: deveria lançar exceção"
            );

        } catch (Exception e) {

            System.out.println(
                    "OK -> " + e.getMessage()
            );

        } finally {
            SessaoUsuario.setUsuarioLogado(usuarioOriginal);
        }
    }

    private static void testarAgendarProfissionalNulo() {

        System.out.println("\n[TESTE] Agendar com profissional nulo");

        try {

            service.agendar(1, null, LocalDate.now());

            System.out.println(
                    "ERRO: deveria lançar exceção"
            );

        } catch (Exception e) {

            System.out.println(
                    "OK -> " + e.getMessage()
            );
        }
    }

    private static void testarAgendarProfissionalInativo() {

        System.out.println("\n[TESTE] Agendar com profissional inativo");

        try {

            Profissional profissional = criarProfissionalValido();
            profissional.setAtivo(false);

            service.agendar(1, profissional, LocalDate.now());

            System.out.println(
                    "ERRO: deveria lançar exceção"
            );

        } catch (Exception e) {

            System.out.println(
                    "OK -> " + e.getMessage()
            );
        }
    }

    private static void testarAgendarProfissionalTipoIncompativel() {

        System.out.println("\n[TESTE] Agendar com profissional de tipo incompatível");

        try {

            Profissional profissional = new Profissional("João Souza", TipoProfissional.ASSISTENTE_SOCIAL);
            profissional.setId(2);

            // entrevista 1 é assumida como PSICOLOGO no ambiente de teste manual
            service.agendar(1, profissional, LocalDate.now());

            System.out.println(
                    "ERRO ou situação a ser verificada manualmente conforme dados do banco"
            );

        } catch (Exception e) {

            System.out.println(
                    "OK -> " + e.getMessage()
            );
        }
    }

    private static void testarAgendarDataNula() {

        System.out.println("\n[TESTE] Agendar com data nula");

        try {

            Profissional profissional = criarProfissionalValido();

            service.agendar(1, profissional, null);

            System.out.println(
                    "ERRO: deveria lançar exceção"
            );

        } catch (Exception e) {

            System.out.println(
                    "OK -> " + e.getMessage()
            );
        }
    }

    private static void testarAgendarDataAnteriorPedido() {

        System.out.println("\n[TESTE] Agendar com data anterior ao pedido");

        try {

            Profissional profissional = criarProfissionalValido();

            service.agendar(1, profissional, LocalDate.now().minusYears(10));

            System.out.println(
                    "ERRO ou situação a ser verificada manualmente conforme dados do banco"
            );

        } catch (Exception e) {

            System.out.println(
                    "OK -> " + e.getMessage()
            );
        }
    }

    private static void testarRegistrarRealizacaoSemAgendamento() {

        System.out.println("\n[TESTE] Registrar realização sem agendamento");

        try {

            // assume que a entrevista de id 999999 não existe ou não está agendada
            service.registrarRealizacao(999999, LocalDate.now(), null);

            System.out.println(
                    "ERRO: deveria lançar exceção"
            );

        } catch (Exception e) {

            System.out.println(
                    "OK -> " + e.getMessage()
            );
        }
    }

    private static void testarRegistrarRealizacaoDataFutura() {

        System.out.println("\n[TESTE] Registrar realização com data futura");

        try {

            service.registrarRealizacao(1, LocalDate.now().plusDays(1), null);

            System.out.println(
                    "ERRO ou situação a ser verificada manualmente conforme dados do banco"
            );

        } catch (Exception e) {

            System.out.println(
                    "OK -> " + e.getMessage()
            );
        }
    }

    private static void testarCancelarEntrevistaJaCancelada() {

        System.out.println("\n[TESTE] Cancelar entrevista inexistente");

        try {

            service.cancelar(999999);

            System.out.println(
                    "ERRO: deveria lançar exceção"
            );

        } catch (Exception e) {

            System.out.println(
                    "OK -> " + e.getMessage()
            );
        }
    }

    private static PedidoExame criarPedidoValido() {

        Sentenciado sentenciado = new Sentenciado(
                "123456",
                "João da Silva"
        );

        PedidoExame pedido = new PedidoExame(
                sentenciado,
                LocalDate.now().minusDays(10),
                "000123"
        );

        pedido.setStatus(
                StatusPedidoExame.CADASTRADO
        );

        return pedido;
    }

    private static Profissional criarProfissionalValido() {

        Profissional profissional = new Profissional(
                "Maria Oliveira",
                TipoProfissional.PSICOLOGO
        );
        profissional.setId(1);

        return profissional;
    }
}
