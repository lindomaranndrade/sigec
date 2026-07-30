package br.com.sigec.dao;

import br.com.sigec.model.Reiteracao;

import java.time.LocalDate;

public class TesteReiteracaoDAO {

    public static void main(String[] args) {

        ReiteracaoDAO reiteracaoDAO = new ReiteracaoDAO();

        // =====================================================
        // TESTE 1 - Inserir
        // =====================================================
        /*
        PedidoExameDAO pedidoExameDAO = new PedidoExameDAO();
        PedidoExame pedido = pedidoExameDAO.buscarPorId(3);

        UsuarioDAO usuarioDAO = new UsuarioDAO();
        Usuario usuario = usuarioDAO.buscarPorId(1);

        Reiteracao reiteracao = new Reiteracao(
                pedido,
                LocalDate.now(),
                "Teste de observação",
                "Despacho de teste",
                usuario
        );

        reiteracaoDAO.inserir(reiteracao);

        System.out.println("ID gerado: " + reiteracao.getId());
        */

        // =====================================================
        // TESTE 2 - Buscar por ID
        // =====================================================
        /*
        Reiteracao encontrada = reiteracaoDAO.buscarPorId(1);

        System.out.println("\nBuscar por ID:");
        System.out.println(encontrada);
        */

        // =====================================================
        // TESTE 3 - Listar todos
        // =====================================================
        /*
        System.out.println("\nListagem:");

        List<Reiteracao> reiteracoes = reiteracaoDAO.listarTodos();

        for (Reiteracao r : reiteracoes) {
            System.out.println(r);
        }
        */

        // =====================================================
        // TESTE 4 - Atualizar
        // =====================================================

        Reiteracao reiteracao = reiteracaoDAO.buscarPorId(2); // use um ID existente

        reiteracao.setDataReiteracao(
                LocalDate.of(2026, 7, 20)
        );

        reiteracao.setObservacoes("Observação alterada");
        reiteracao.setDespacho("Despacho alterado");

        reiteracaoDAO.atualizar(reiteracao);

        System.out.println("\nApós atualização:");
        System.out.println(
                reiteracaoDAO.buscarPorId(reiteracao.getId())
        );

        // =====================================================
        // TESTE 5 - Excluir
        // =====================================================
        /*
        reiteracaoDAO.excluir(1);

        System.out.println(
                reiteracaoDAO.buscarPorId(1)
        );
        */
    }
}