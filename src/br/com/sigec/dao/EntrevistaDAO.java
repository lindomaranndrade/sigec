package br.com.sigec.dao;

import br.com.sigec.model.Entrevista;
import br.com.sigec.model.PedidoExame;
import br.com.sigec.model.Profissional;
import br.com.sigec.model.Usuario;
import br.com.sigec.util.Conexao;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EntrevistaDAO {

    public void inserir(Entrevista entrevista){
        String sql = """
                INSERT INTO
                entrevista(id_pedido_exame, id_profissional, data_entrevista, id_usuario,data_entrega_laudo,data_cadastro)
                VALUES (?, ?, ?, ?, ?, ?)
                """;
        try(Connection conexao = Conexao.conectar();
            PreparedStatement comando = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            comando.setInt(1,entrevista.getPedidoExame().getId());
            comando.setInt(2,entrevista.getProfissional().getId());
            comando.setDate(3,java.sql.Date.valueOf(entrevista.getDataEntrevista()));
            comando.setInt(4,entrevista.getUsuario().getId());
            if (entrevista.getDataEntregaLaudo() != null) {
                comando.setDate(5,
                        Date.valueOf(entrevista.getDataEntregaLaudo()));
            } else {
                comando.setNull(5, Types.DATE);
            }
            comando.setDate(6,java.sql.Date.valueOf(entrevista.getDataCadastro()));
            comando.executeUpdate();

            try(ResultSet resultado = comando.getGeneratedKeys()){
                if(resultado.next()){
                    entrevista.setId(resultado.getInt(1));
                }
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public Entrevista buscarPorId(int id){
        String sql = """
        SELECT
            e.id,

            p.id AS pedido_exame_id,
            p.numero_processo AS pedido_exame_numero_processo,

            pro.id AS profissional_id,
            pro.nome AS profissional_nome,

            e.data_entrevista,

            u.id AS usuario_id,
            u.login AS usuario_login,

            e.data_entrega_laudo,
            e.data_cadastro

        FROM entrevista e
            INNER JOIN pedido_exame p
                ON e.id_pedido_exame = p.id

            INNER JOIN profissional pro
                ON e.id_profissional = pro.id

            INNER JOIN usuario u
                ON e.id_usuario = u.id

        WHERE e.id = ?
        """;

        try(Connection conexao = Conexao.conectar();
            PreparedStatement comando = conexao.prepareStatement(sql)){
            comando.setInt(1,id);

            try(ResultSet resultado = comando.executeQuery()){
                if(resultado.next()){
                    return montarEntrevista(resultado);

                }
            }

        }catch (SQLException e){
            throw new RuntimeException(e);
        }
        return null;
    }
    public void atualizar(Entrevista entrevista) {
        String sql = """
            UPDATE entrevista
            SET
                id_pedido_exame = ?,
                id_profissional = ?,
                data_entrevista = ?,
                id_usuario = ?,
                data_entrega_laudo = ?
            WHERE id = ?
            """;

        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setInt(1, entrevista.getPedidoExame().getId());
            comando.setInt(2, entrevista.getProfissional().getId());
            comando.setDate(3,
                    Date.valueOf(entrevista.getDataEntrevista()));
            comando.setInt(4, entrevista.getUsuario().getId());

            if (entrevista.getDataEntregaLaudo() != null) {
                comando.setDate(5,
                        Date.valueOf(entrevista.getDataEntregaLaudo()));
            } else {
                comando.setNull(5, Types.DATE);
            }

            comando.setInt(6, entrevista.getId());

            comando.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void excluir(Entrevista entrevista) {
        String sql = """
            DELETE FROM entrevista
            WHERE id = ?
            """;

        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setInt(1, entrevista.getId());

            comando.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Entrevista> listarTodos() {
        String sql = """
        SELECT
            e.id,

            p.id AS pedido_exame_id,
            p.numero_processo AS pedido_exame_numero_processo,

            pro.id AS profissional_id,
            pro.nome AS profissional_nome,

            e.data_entrevista,

            u.id AS usuario_id,
            u.login AS usuario_login,

            e.data_entrega_laudo,
            e.data_cadastro

        FROM entrevista e
            INNER JOIN pedido_exame p
                ON e.id_pedido_exame = p.id

            INNER JOIN profissional pro
                ON e.id_profissional = pro.id

            INNER JOIN usuario u
                ON e.id_usuario = u.id

        ORDER BY e.id
        """;

        List<Entrevista> entrevistas = new ArrayList<>();

        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql);
             ResultSet resultado = comando.executeQuery()) {

            while (resultado.next()) {
                entrevistas.add(montarEntrevista(resultado));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return entrevistas;
    }

    private Entrevista montarEntrevista(ResultSet resultado) throws SQLException {

        PedidoExame pedidoExame = new PedidoExame();
        pedidoExame.setId(resultado.getInt("pedido_exame_id"));
        pedidoExame.setNumeroProcesso(
                resultado.getString("pedido_exame_numero_processo"));

        Profissional profissional = new Profissional();
        profissional.setId(resultado.getInt("profissional_id"));
        profissional.setNome(
                resultado.getString("profissional_nome"));

        Usuario usuario = new Usuario(
                resultado.getInt("usuario_id"),
                resultado.getString("usuario_login")
        );

        Entrevista entrevista = new Entrevista();

        entrevista.setId(resultado.getInt("id"));
        entrevista.setPedidoExame(pedidoExame);
        entrevista.setProfissional(profissional);
        entrevista.setDataEntrevista(
                converteData(resultado, "data_entrevista"));
        entrevista.setUsuario(usuario);
        entrevista.setDataEntregaLaudo(
                converteData(resultado, "data_entrega_laudo"));
        entrevista.setDataCadastro(
                converteData(resultado, "data_cadastro"));

        return entrevista;
    }
    private LocalDate converteData(ResultSet resposta, String nomeDaColuna) throws SQLException {
        Date data = resposta.getDate(nomeDaColuna);
        return data != null ? data.toLocalDate() : null;
    }

}
