package br.com.sigec.dao;

import br.com.sigec.model.Entrevista;
import br.com.sigec.model.PedidoExame;
import br.com.sigec.model.TipoProfissional;

import java.time.LocalDate;

public class TesteEntrevistaDAO {
    public static void main(String[] args) {

        EntrevistaDAO entrevistaDAO = new EntrevistaDAO();

        try {

            System.out.println("=================================");
            System.out.println("TESTE DE INSERÇÃO (PENDENTE_AGENDAMENTO)");
            System.out.println("=================================");

            PedidoExame pedidoExame = new PedidoExame();
            pedidoExame.setId(3);

            Entrevista entrevista = new Entrevista(pedidoExame, TipoProfissional.PSICOLOGO);

            entrevistaDAO.inserir(entrevista);

            System.out.println("✅ Entrevista inserida com sucesso!");
            System.out.println("ID gerado: " + entrevista.getId());

            System.out.println("\n=================================");
            System.out.println("BUSCA APÓS INSERÇÃO");
            System.out.println("=================================");

            Entrevista pendente = entrevistaDAO.buscarPorId(entrevista.getId());

            if (pendente != null) {
                System.out.println("✅ Entrevista encontrada!");
                System.out.println(pendente);
            } else {
                System.out.println("❌ Entrevista não encontrada!");
            }

            System.out.println("\n=================================");
            System.out.println("TESTE DE AGENDAMENTO");
            System.out.println("=================================");

            entrevistaDAO.agendar(entrevista.getId(), 1, LocalDate.now());

            System.out.println("✅ Entrevista agendada com sucesso!");

            Entrevista agendada = entrevistaDAO.buscarPorId(entrevista.getId());
            System.out.println(agendada);

            System.out.println("\n=================================");
            System.out.println("TESTE DE REGISTRO DE REALIZAÇÃO");
            System.out.println("=================================");

            entrevistaDAO.registrarRealizacao(entrevista.getId(), LocalDate.now(), LocalDate.now());

            System.out.println("✅ Realização registrada com sucesso!");

            Entrevista realizada = entrevistaDAO.buscarPorId(entrevista.getId());
            System.out.println(realizada);

            System.out.println("\n=================================");
            System.out.println("TESTE DE CANCELAMENTO (outra entrevista)");
            System.out.println("=================================");

            Entrevista outraEntrevista = new Entrevista(pedidoExame, TipoProfissional.ASSISTENTE_SOCIAL);
            entrevistaDAO.inserir(outraEntrevista);

            entrevistaDAO.cancelar(outraEntrevista.getId());

            System.out.println("✅ Entrevista cancelada com sucesso!");

            Entrevista cancelada = entrevistaDAO.buscarPorId(outraEntrevista.getId());
            System.out.println(cancelada);

            System.out.println("\n🎉 TESTE FINALIZADO COM SUCESSO!");

        } catch (Exception e) {

            System.out.println("\n❌ ERRO DURANTE O TESTE");
            e.printStackTrace();

        }
    }
}
