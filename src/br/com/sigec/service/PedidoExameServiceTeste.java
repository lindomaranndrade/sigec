package br.com.sigec.service;

import br.com.sigec.model.PedidoExame;
import br.com.sigec.model.Sentenciado;
import br.com.sigec.model.StatusPedidoExame;
import br.com.sigec.model.Usuario;

import java.time.LocalDate;

public class PedidoExameServiceTeste {
    private static PedidoExameService service =
            new PedidoExameService();
    
    public static void main(String[] args) {
        testarPedidoNulo();
        testarSentenciadoNulo();
        testarUsuarioNulo();
        testarDataSolicitacaoNula();
        testarDataSolicitacaoFutura();
        testarNumeroSeiNulo();
        testarPedidoJaConcluido();
        testarPedidoCancelado();
        testarConclusaoValida();

    }

    private static void testarPedidoNulo() {

        System.out.println("\n=== TESTE PEDIDO NULO ===");

        try {
            service.inserir(null);
            System.out.println("ERRO: teste falhou");
        } catch (Exception e) {
            System.out.println("OK: " + e.getMessage());
        }
    }

    private static void testarSentenciadoNulo() {

        System.out.println("\n=== TESTE SENTENCIADO NULO ===");

        try {

            PedidoExame pedido = new PedidoExame();

            pedido.setUsuario(new Usuario());
            pedido.setDataSolicitacao(LocalDate.now());

            service.inserir(pedido);

            System.out.println("ERRO: teste falhou");

        } catch (Exception e) {

            System.out.println("OK: " + e.getMessage());
        }
    }

    private static void testarUsuarioNulo() {

        System.out.println("\n=== TESTE USUÁRIO NULO ===");

        try {

            PedidoExame pedido = new PedidoExame();

            pedido.setSentenciado(new Sentenciado());
            pedido.setDataSolicitacao(LocalDate.now());

            service.inserir(pedido);

            System.out.println("ERRO: teste falhou");

        } catch (Exception e) {

            System.out.println("OK: " + e.getMessage());
        }
    }

    private static void testarDataSolicitacaoNula() {

        System.out.println("\n=== TESTE DATA SOLICITAÇÃO NULA ===");

        try {

            PedidoExame pedido = new PedidoExame();

            pedido.setSentenciado(new Sentenciado());
            pedido.setUsuario(new Usuario());

            service.inserir(pedido);

            System.out.println("ERRO: teste falhou");

        } catch (Exception e) {

            System.out.println("OK: " + e.getMessage());
        }
    }

    private static void testarDataSolicitacaoFutura() {

        System.out.println("\n=== TESTE DATA FUTURA ===");

        try {

            PedidoExame pedido = new PedidoExame();

            pedido.setSentenciado(new Sentenciado());
            pedido.setUsuario(new Usuario());

            pedido.setDataSolicitacao(
                    LocalDate.now().plusDays(1)
            );

            service.inserir(pedido);

            System.out.println("ERRO: teste falhou");

        } catch (Exception e) {

            System.out.println("OK: " + e.getMessage());
        }
    }

    private static void testarNumeroSeiNulo() {

        System.out.println("\n=== TESTE NÚMERO SEI NULO ===");

        try {

            PedidoExame pedido = criarPedidoValido();

            pedido.setNumeroSEI(null);

            service.concluirPedido(pedido);

            System.out.println("ERRO: teste falhou");

        } catch (Exception e) {

            System.out.println("OK: " + e.getMessage());
        }
    }

    private static void testarPedidoJaConcluido() {

        System.out.println("\n=== TESTE PEDIDO JÁ CONCLUÍDO ===");

        try {

            PedidoExame pedido = criarPedidoValido();

            pedido.setNumeroSEI("123456");
            pedido.setStatus(StatusPedidoExame.CONCLUIDO);

            service.concluirPedido(pedido);

            System.out.println("ERRO: teste falhou");

        } catch (Exception e) {

            System.out.println("OK: " + e.getMessage());
        }
    }

    private static void testarPedidoCancelado() {

        System.out.println("\n=== TESTE PEDIDO CANCELADO ===");

        try {

            PedidoExame pedido = criarPedidoValido();

            pedido.setNumeroSEI("123456");
            pedido.setStatus(StatusPedidoExame.CANCELADO);

            service.concluirPedido(pedido);

            System.out.println("ERRO: teste falhou");

        } catch (Exception e) {

            System.out.println("OK: " + e.getMessage());
        }
    }

    private static void testarConclusaoValida() {

        System.out.println("\n=== TESTE CONCLUSÃO VÁLIDA ===");

        try {

            PedidoExame pedido = criarPedidoValido();

            pedido.setNumeroSEI("123456");

            service.concluirPedido(pedido);

            System.out.println("OK: Pedido concluído com sucesso");

            System.out.println("Status: "
                    + pedido.getStatus());

            System.out.println("Data conclusão: "
                    + pedido.getDataConclusao());

        } catch (Exception e) {

            System.out.println("ERRO: " + e.getMessage());
        }
    }

    private static PedidoExame criarPedidoValido() {

        Sentenciado sentenciado = new Sentenciado();
        Usuario usuario = new Usuario();

        PedidoExame pedido = new PedidoExame();

        pedido.setSentenciado(sentenciado);
        pedido.setUsuario(usuario);
        pedido.setDataSolicitacao(LocalDate.now());
        pedido.setStatus(StatusPedidoExame.CADASTRADO);

        return pedido;
    }
    }


