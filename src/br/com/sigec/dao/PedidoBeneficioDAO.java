package br.com.sigec.dao;

import br.com.sigec.model.Beneficio;
import br.com.sigec.model.PedidoBeneficio;
import br.com.sigec.model.PedidoExame;
import br.com.sigec.util.Conexao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PedidoBeneficioDAO {

    public void inserir(PedidoBeneficio pedidoBeneficio) {

        String sql = """
                INSERT INTO pedido_beneficio
                (id_beneficio, id_pedido_exame)
                VALUES (?, ?)
                """;

        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setInt(1,
                    pedidoBeneficio.getBeneficio().getId());

            comando.setInt(2,
                    pedidoBeneficio.getPedido().getId());

            comando.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void excluir(PedidoBeneficio pedidoBeneficio) {

        String sql = """
                DELETE FROM pedido_beneficio
                WHERE id_beneficio = ?
                  AND id_pedido_exame = ?
                """;

        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setInt(1,
                    pedidoBeneficio.getBeneficio().getId());

            comando.setInt(2,
                    pedidoBeneficio.getPedido().getId());

            comando.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<PedidoBeneficio> listarTodos() {

        String sql = """
                SELECT

                    pb.id_pedido_exame,

                    p.numero_processo
                        AS pedido_exame_numero_processo,

                    pb.id_beneficio,

                    b.nome
                        AS beneficio_nome

                FROM pedido_beneficio pb

                    INNER JOIN pedido_exame p
                        ON pb.id_pedido_exame = p.id

                    INNER JOIN beneficio b
                        ON pb.id_beneficio = b.id

                ORDER BY pb.id_pedido_exame
                """;

        List<PedidoBeneficio> pedidosBeneficio =
                new ArrayList<>();

        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando =
                     conexao.prepareStatement(sql);
             ResultSet resultado =
                     comando.executeQuery()) {

            while (resultado.next()) {
                pedidosBeneficio.add(
                        montarPedidoBeneficio(resultado));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return pedidosBeneficio;
    }

    public List<PedidoBeneficio> listarPorPedidoExame(
            int idPedidoExame) {

        String sql = """
                SELECT

                    pb.id_pedido_exame,

                    p.numero_processo
                        AS pedido_exame_numero_processo,

                    pb.id_beneficio,

                    b.nome
                        AS beneficio_nome

                FROM pedido_beneficio pb

                    INNER JOIN pedido_exame p
                        ON pb.id_pedido_exame = p.id

                    INNER JOIN beneficio b
                        ON pb.id_beneficio = b.id

                WHERE pb.id_pedido_exame = ?

                ORDER BY b.nome
                """;

        List<PedidoBeneficio> pedidosBeneficio =
                new ArrayList<>();

        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando =
                     conexao.prepareStatement(sql)) {

            comando.setInt(1, idPedidoExame);

            try (ResultSet resultado =
                         comando.executeQuery()) {

                while (resultado.next()) {
                    pedidosBeneficio.add(
                            montarPedidoBeneficio(resultado));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return pedidosBeneficio;
    }

    public List<PedidoBeneficio> listarPorBeneficio(
            int idBeneficio) {

        String sql = """
                SELECT

                    pb.id_pedido_exame,

                    p.numero_processo
                        AS pedido_exame_numero_processo,

                    pb.id_beneficio,

                    b.nome
                        AS beneficio_nome

                FROM pedido_beneficio pb

                    INNER JOIN pedido_exame p
                        ON pb.id_pedido_exame = p.id

                    INNER JOIN beneficio b
                        ON pb.id_beneficio = b.id

                WHERE pb.id_beneficio = ?

                ORDER BY p.numero_processo
                """;

        List<PedidoBeneficio> pedidosBeneficio =
                new ArrayList<>();

        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando =
                     conexao.prepareStatement(sql)) {

            comando.setInt(1, idBeneficio);

            try (ResultSet resultado =
                         comando.executeQuery()) {

                while (resultado.next()) {
                    pedidosBeneficio.add(
                            montarPedidoBeneficio(resultado));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return pedidosBeneficio;
    }

    private PedidoBeneficio montarPedidoBeneficio(
            ResultSet resultado) throws SQLException {

        PedidoExame pedido = new PedidoExame();
        pedido.setId(
                resultado.getInt("id_pedido_exame"));
        pedido.setNumeroProcesso(
                resultado.getString(
                        "pedido_exame_numero_processo"));

        Beneficio beneficio = new Beneficio();
        beneficio.setId(
                resultado.getInt("id_beneficio"));
        beneficio.setDescricao(
                resultado.getString(
                        "beneficio_nome"));

        return new PedidoBeneficio(
                pedido,
                beneficio
        );
    }

    public boolean existeVinculo(
            int idPedido,
            int idBeneficio) {

        String sql = """
            SELECT 1
            FROM pedido_beneficio
            WHERE id_pedido_exame = ?
              AND id_beneficio = ?
            """;

        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando =
                     conexao.prepareStatement(sql)) {

            comando.setInt(1, idPedido);
            comando.setInt(2, idBeneficio);

            try (ResultSet resultado =
                         comando.executeQuery()) {

                return resultado.next();
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Erro ao verificar vínculo.",
                    e
            );
        }
    }
}