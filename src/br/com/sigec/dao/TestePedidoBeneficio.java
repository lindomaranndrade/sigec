package br.com.sigec.dao;

import br.com.sigec.model.Beneficio;
import br.com.sigec.model.PedidoBeneficio;
import br.com.sigec.model.PedidoExame;

import java.util.List;

public class TestePedidoBeneficio {

    public static void main(String[] args) {

        PedidoBeneficioDAO pedidoBeneficioDAO =
                new PedidoBeneficioDAO();

        try {

            PedidoExame pedido = new PedidoExame();
            pedido.setId(3);

            Beneficio beneficio = new Beneficio();
            beneficio.setId(1);

            PedidoBeneficio pedidoBeneficio =
                    new PedidoBeneficio(
                            pedido,
                            beneficio
                    );

            /*
            =====================================================
            TESTE DE INSERÇÃO
            =====================================================
            */

            System.out.println("\n=================================");
            System.out.println("TESTE DE INSERÇÃO");
            System.out.println("=================================");

            pedidoBeneficioDAO.inserir(pedidoBeneficio);

            System.out.println("✅ Associação inserida com sucesso!");

            /*
            =====================================================
            LISTAR POR PEDIDO
            =====================================================
            */

            System.out.println("\n=================================");
            System.out.println("LISTAR POR PEDIDO");
            System.out.println("=================================");

            List<PedidoBeneficio> listaPedido =
                    pedidoBeneficioDAO.listarPorPedidoExame(3);

            System.out.println(
                    "Quantidade encontrada: "
                            + listaPedido.size());

            for (PedidoBeneficio pb : listaPedido) {
                System.out.println(pb);
            }

            /*
            =====================================================
            LISTAR TODOS
            =====================================================
            */

            System.out.println("\n=================================");
            System.out.println("LISTAR TODOS");
            System.out.println("=================================");

            List<PedidoBeneficio> listaTodos =
                    pedidoBeneficioDAO.listarTodos();

            System.out.println(
                    "Quantidade total: "
                            + listaTodos.size());

            for (PedidoBeneficio pb : listaTodos) {
                System.out.println(pb);
            }

            /*
            =====================================================
            LISTAR POR BENEFÍCIO
            =====================================================
            */

            System.out.println("\n=================================");
            System.out.println("LISTAR POR BENEFÍCIO");
            System.out.println("=================================");

            List<PedidoBeneficio> listaBeneficio =
                    pedidoBeneficioDAO.listarPorBeneficio(1);

            System.out.println(
                    "Quantidade encontrada: "
                            + listaBeneficio.size());

            for (PedidoBeneficio pb : listaBeneficio) {
                System.out.println(pb);
            }

            /*
            =====================================================
            TESTE DE EXCLUSÃO
            =====================================================
            */

            System.out.println("\n=================================");
            System.out.println("TESTE DE EXCLUSÃO");
            System.out.println("=================================");

            pedidoBeneficioDAO.excluir(pedidoBeneficio);

            System.out.println("✅ Associação removida com sucesso!");

            System.out.println("\n🎉 TESTES CONCLUÍDOS!");

        } catch (Exception e) {

            System.out.println(
                    "\n❌ ERRO DURANTE O TESTE");

            e.printStackTrace();
        }
    }
}