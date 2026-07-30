package br.com.sigec.dao;

import br.com.sigec.model.Entrevista;
import br.com.sigec.model.PedidoExame;
import br.com.sigec.model.Profissional;
import br.com.sigec.model.Usuario;

import java.time.LocalDate;

public class TesteEntrevistaDAO {
    public static void main(String[] args) {


            EntrevistaDAO entrevistaDAO = new EntrevistaDAO();

            try {

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
                entrevista.setProfissional(profissional);
                entrevista.setUsuario(usuario);
                entrevista.setDataEntrevista(LocalDate.now());
                entrevista.setDataEntregaLaudo(null);
                entrevista.setDataCadastro(LocalDate.now());

                entrevistaDAO.inserir(entrevista);

                System.out.println("✅ Entrevista inserida com sucesso!");
                System.out.println("ID gerado: " + entrevista.getId());



                System.out.println("\n=================================");
                System.out.println("TESTE DE BUSCA");
                System.out.println("=================================");

                Entrevista encontrada =
                        entrevistaDAO.buscarPorId(entrevista.getId());

                if (encontrada != null) {
                    System.out.println("✅ Entrevista encontrada!");
                    System.out.println(encontrada);
                }



                System.out.println("\n=================================");
                System.out.println("TESTE DE ATUALIZAÇÃO");
                System.out.println("=================================");

                encontrada.setDataEntregaLaudo(LocalDate.now());

                entrevistaDAO.atualizar(encontrada);

                System.out.println("✅ Entrevista atualizada com sucesso!");



                System.out.println("\n=================================");
                System.out.println("BUSCA APÓS ATUALIZAÇÃO");
                System.out.println("=================================");

                Entrevista atualizada =
                        entrevistaDAO.buscarPorId(encontrada.getId());

                System.out.println(atualizada);

                System.out.println("\n🎉 TESTE FINALIZADO COM SUCESSO!");

            } catch (Exception e) {

                System.out.println("\n❌ ERRO DURANTE O TESTE");
                e.printStackTrace();

            }
        }
    }

