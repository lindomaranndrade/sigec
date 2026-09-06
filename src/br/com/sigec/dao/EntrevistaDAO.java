package br.com.sigec.dao;

import br.com.sigec.model.Entrevista;
import br.com.sigec.model.PedidoExame;
import br.com.sigec.model.Profissional;
import br.com.sigec.model.StatusEntrevista;
import br.com.sigec.model.TipoProfissional;
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
                entrevista(id_pedido_exame, tipo_atendimento, status, id_usuario, data_cadastro)
                VALUES (?, ?, ?, ?, ?)
                """;
        try(Connection conexao = Conexao.conectar();
            PreparedStatement comando = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            comando.setInt(1,entrevista.getPedidoExame().getId());
            comando.setString(2,entrevista.getTipoAtendimento().name());
            comando.setString(3,entrevista.getStatus().name());
            comando.setInt(4,entrevista.getUsuario().getId());
            comando.setDate(5,java.sql.Date.valueOf(entrevista.getDataCadastro()));
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

    public void agendar(int idEntrevista, int idProfissional, LocalDate dataAgendamento){
        String sql = """
                UPDATE entrevista
                SET id_profissional = ?, data_agendamento = ?, status = 'AGENDADA'
                WHERE id = ?
                """;

        try(Connection conexao = Conexao.conectar();
            PreparedStatement comando = conexao.prepareStatement(sql)){

            comando.setInt(1, idProfissional);
            comando.setDate(2, Date.valueOf(dataAgendamento));
            comando.setInt(3, idEntrevista);

            comando.executeUpdate();

        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public void registrarRealizacao(int idEntrevista, LocalDate dataRealizacao, LocalDate dataEntregaLaudo){
        String sql = """
                UPDATE entrevista
                SET data_realizacao = ?, data_entrega_laudo = ?, status = 'REALIZADA'
                WHERE id = ?
                """;

        try(Connection conexao = Conexao.conectar();
            PreparedStatement comando = conexao.prepareStatement(sql)){

            comando.setDate(1, Date.valueOf(dataRealizacao));

            if (dataEntregaLaudo != null) {
                comando.setDate(2, Date.valueOf(dataEntregaLaudo));
            } else {
                comando.setNull(2, Types.DATE);
            }

            comando.setInt(3, idEntrevista);

            comando.executeUpdate();

        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public void cancelar(int idEntrevista){
        String sql = """
                UPDATE entrevista
                SET status = 'CANCELADA'
                WHERE id = ?
                """;

        try(Connection conexao = Conexao.conectar();
            PreparedStatement comando = conexao.prepareStatement(sql)){

            comando.setInt(1, idEntrevista);

            comando.executeUpdate();

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

            e.tipo_atendimento,
            e.status,
            e.data_agendamento,
            e.data_realizacao,

            u.id AS usuario_id,
            u.login AS usuario_login,

            e.data_entrega_laudo,
            e.data_cadastro

        FROM entrevista e
            INNER JOIN pedido_exame p
                ON e.id_pedido_exame = p.id

            LEFT JOIN profissional pro
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

            e.tipo_atendimento,
            e.status,
            e.data_agendamento,
            e.data_realizacao,

            u.id AS usuario_id,
            u.login AS usuario_login,

            e.data_entrega_laudo,
            e.data_cadastro

        FROM entrevista e
            INNER JOIN pedido_exame p
                ON e.id_pedido_exame = p.id

            LEFT JOIN profissional pro
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

    public List<Entrevista> listarPorPedidoExame(int idPedidoExame) {
        String sql = """
        SELECT
            e.id,

            p.id AS pedido_exame_id,
            p.numero_processo AS pedido_exame_numero_processo,

            pro.id AS profissional_id,
            pro.nome AS profissional_nome,

            e.tipo_atendimento,
            e.status,
            e.data_agendamento,
            e.data_realizacao,

            u.id AS usuario_id,
            u.login AS usuario_login,

            e.data_entrega_laudo,
            e.data_cadastro

        FROM entrevista e
            INNER JOIN pedido_exame p
                ON e.id_pedido_exame = p.id

            LEFT JOIN profissional pro
                ON e.id_profissional = pro.id

            INNER JOIN usuario u
                ON e.id_usuario = u.id

        WHERE e.id_pedido_exame = ?

        ORDER BY e.id
        """;

        List<Entrevista> entrevistas = new ArrayList<>();

        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setInt(1, idPedidoExame);

            try (ResultSet resultado = comando.executeQuery()) {
                while (resultado.next()) {
                    entrevistas.add(montarEntrevista(resultado));
                }
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

        Usuario usuario = new Usuario(
                resultado.getInt("usuario_id"),
                resultado.getString("usuario_login")
        );

        Entrevista entrevista = new Entrevista();

        entrevista.setId(resultado.getInt("id"));
        entrevista.setPedidoExame(pedidoExame);

        int idProfissional = resultado.getInt("profissional_id");
        if (resultado.wasNull()) {
            entrevista.setProfissional(null);
        } else {
            Profissional profissional = new Profissional();
            profissional.setId(idProfissional);
            profissional.setNome(resultado.getString("profissional_nome"));
            entrevista.setProfissional(profissional);
        }

        entrevista.setTipoAtendimento(
                TipoProfissional.valueOf(resultado.getString("tipo_atendimento")));
        entrevista.setStatus(
                StatusEntrevista.valueOf(resultado.getString("status")));
        entrevista.setDataAgendamento(
                converteData(resultado, "data_agendamento"));
        entrevista.setDataRealizacao(
                converteData(resultado, "data_realizacao"));
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

    public boolean foiAtendidoPorAmbos(int idPedido) {
        String sql = """
        SELECT COUNT(DISTINCT tipo_atendimento)
        FROM entrevista
        WHERE id_pedido_exame = ?
          AND status = 'REALIZADA'
        """;

        try(Connection conexao = Conexao.conectar();
            PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setInt(1, idPedido);

            try(ResultSet resultado = comando.executeQuery()) {

                if(resultado.next()) {
                    return resultado.getInt(1) >= 2;
                }
            }

        } catch(SQLException e) {
            throw new RuntimeException(e);
        }

        return false;
    }
}
