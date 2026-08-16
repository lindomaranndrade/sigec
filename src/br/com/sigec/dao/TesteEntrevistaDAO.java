package br.com.sigec.dao;

import br.com.sigec.model.Entrevista;
import br.com.sigec.model.PedidoExame;
import br.com.sigec.model.Profissional;
import br.com.sigec.model.StatusEntrevista;
import br.com.sigec.model.TipoProfissional;
import br.com.sigec.model.Usuario;

import java.time.LocalDate;

public class TesteEntrevistaDAO {

    public static void main(String[] args) {

        EntrevistaDAO entrevistaDAO = new EntrevistaDAO();

        try {

            // =========================================================
            // TESTE DE INSERÇÃO
            // =========================================================

            System.out.println("=================================");
            System.out.println("TESTE DE INSERÇÃO");
            System.out.println("=================================");

            PedidoExame pedidoExame = new PedidoExame();
            pedidoExame.setId(3);

            Profissional profissional = new Profissional();
            profissional.setId(1);

            Usuario usuario = new Usuario();
            usuario.setId(1);

            Entrevista entrevista = new Entrevista();

            entrevista.setPedidoExame(pedidoExame);
            entrevista.setTipoAtendimento(TipoProfissional.PSICOLOGO);
            entrevista.setProfissional(profissional);
            entrevista.setUsuario(usuario);

            entrevista.setDataAgendamento(LocalDate.now());
            entrevista.setDataRealizacao(LocalDate.now());

            entrevista.setStatus(StatusEntrevista.REALIZADA);

            entrevista.setDataEntregaLaudo(null);
            entrevista.setDataCadastro(LocalDate.now());

            entrevistaDAO.inserir(entrevista);

            System.out.println("✅ Entrevista inserida com sucesso!");
            System.out.println("ID gerado: " + entrevista.getId());


            // =========================================================
            // TESTE DE BUSCA
            // =========================================================

            System.out.println("\n=================================");
            System.out.println("TESTE DE BUSCA");
            System.out.println("=================================");

            Entrevista encontrada =
                    entrevistaDAO.buscarPorId(entrevista.getId());

            if (encontrada == null) {

                throw new IllegalStateException(
                        "Entrevista não foi encontrada após a inserção."
                );
            }

            System.out.println("✅ Entrevista encontrada!");

            System.out.println("ID: "
                    + encontrada.getId());

            System.out.println("Tipo de atendimento: "
                    + encontrada.getTipoAtendimento());

            System.out.println("Status: "
                    + encontrada.getStatus());

            System.out.println("Data de agendamento: "
                    + encontrada.getDataAgendamento());

            System.out.println("Data de realização: "
                    + encontrada.getDataRealizacao());

            System.out.println("Data de entrega do laudo: "
                    + encontrada.getDataEntregaLaudo());

            System.out.println("Data de cadastro: "
                    + encontrada.getDataCadastro());


            // =========================================================
            // TESTE DE ATUALIZAÇÃO
            // =========================================================

            System.out.println("\n=================================");
            System.out.println("TESTE DE ATUALIZAÇÃO");
            System.out.println("=================================");

            LocalDate dataEntregaLaudo = LocalDate.now();

            encontrada.setDataEntregaLaudo(dataEntregaLaudo);

            entrevistaDAO.atualizar(encontrada);

            System.out.println("✅ Entrevista atualizada com sucesso!");
            System.out.println("Nova data de entrega do laudo: "
                    + encontrada.getDataEntregaLaudo());


            // =========================================================
            // BUSCA APÓS ATUALIZAÇÃO
            // =========================================================

            System.out.println("\n=================================");
            System.out.println("BUSCA APÓS ATUALIZAÇÃO");
            System.out.println("=================================");

            Entrevista atualizada =
                    entrevistaDAO.buscarPorId(encontrada.getId());

            if (atualizada == null) {

                throw new IllegalStateException(
                        "Entrevista não foi encontrada após a atualização."
                );
            }

            System.out.println("✅ Entrevista encontrada novamente!");

            System.out.println("ID: "
                    + atualizada.getId());

            System.out.println("Tipo de atendimento: "
                    + atualizada.getTipoAtendimento());

            System.out.println("Status: "
                    + atualizada.getStatus());

            System.out.println("Data de agendamento: "
                    + atualizada.getDataAgendamento());

            System.out.println("Data de realização: "
                    + atualizada.getDataRealizacao());

            System.out.println("Data de entrega do laudo: "
                    + atualizada.getDataEntregaLaudo());

            System.out.println("Data de cadastro: "
                    + atualizada.getDataCadastro());


            // =========================================================
            // VALIDAÇÃO FINAL
            // =========================================================

            System.out.println("\n=================================");
            System.out.println("VALIDAÇÃO FINAL");
            System.out.println("=================================");

            if (atualizada.getStatus() != StatusEntrevista.REALIZADA) {

                throw new IllegalStateException(
                        "Status não foi recuperado corretamente."
                );
            }

            if (atualizada.getTipoAtendimento()
                    != TipoProfissional.PSICOLOGO) {

                throw new IllegalStateException(
                        "Tipo de atendimento não foi recuperado corretamente."
                );
            }

            if (atualizada.getDataAgendamento() == null) {

                throw new IllegalStateException(
                        "Data de agendamento não foi recuperada."
                );
            }

            if (atualizada.getDataRealizacao() == null) {

                throw new IllegalStateException(
                        "Data de realização não foi recuperada."
                );
            }

            if (atualizada.getDataEntregaLaudo() == null) {

                throw new IllegalStateException(
                        "Data de entrega do laudo não foi atualizada."
                );
            }

            System.out.println("✅ Status correto");
            System.out.println("✅ Tipo de atendimento correto");
            System.out.println("✅ Data de agendamento correta");
            System.out.println("✅ Data de realização correta");
            System.out.println("✅ Data de entrega do laudo atualizada");

            System.out.println("\n🎉 TESTE FINALIZADO COM SUCESSO!");
            System.out.println("🎉 EntrevistaDAO funcionando com o novo modelo.");

        } catch (Exception e) {

            System.out.println("\n❌ ERRO DURANTE O TESTE");
            e.printStackTrace();

        }
    }
}