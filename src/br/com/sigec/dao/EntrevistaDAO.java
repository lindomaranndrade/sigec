package br.com.sigec.dao;

import br.com.sigec.model.*;
import br.com.sigec.util.Conexao;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EntrevistaDAO {

    public void inserir(Entrevista entrevista) {
        String sql = """
                INSERT INTO entrevista
                (id_pedido_exame, id_profissional, tipo_atendimento, status,
                 data_agendamento, data_realizacao, id_usuario, data_entrega_laudo, data_cadastro)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            comando.setInt(1, entrevista.getPedidoExame().getId());

            if (entrevista.getProfissional() != null) {
                comando.setInt(2, entrevista.getProfissional().getId());
            } else {
                comando.setNull(2, Types.INTEGER);
            }

            comando.setString(3, entrevista.getTipoAtendimento().name());
            comando.setString(4, entrevista.getStatus().name());

            setDataOuNull(comando, 5, entrevista.getDataAgendamento());
            setDataOuNull(comando, 6, entrevista.getDataRealizacao());

            comando.setInt(7, entrevista.getUsuario().getId());

            setDataOuNull(comando, 8, entrevista.getDataEntregaLaudo());

            comando.setDate(9, Date.valueOf(entrevista.getDataCadastro()));

            comando.executeUpdate();

            try (ResultSet resultado = comando.getGeneratedKeys()) {
                if (resultado.next()) {
                    entrevista.setId(resultado.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void atualizar(Entrevista entrevista) {
        String sql = """
                UPDATE entrevista
                SET id_profissional = ?,
                    status = ?,
                    data_agendamento = ?,
                    data_realizacao = ?,
                    data_entrega_laudo = ?
                WHERE id = ?
                """;

        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            if (entrevista.getProfissional() != null) {
                comando.setInt(1, entrevista.getProfissional().getId());
            } else {
                comando.setNull(1, Types.INTEGER);
            }

            comando.setString(2, entrevista.getStatus().name());

            setDataOuNull(comando, 3, entrevista.getDataAgendamento());
            setDataOuNull(comando, 4, entrevista.getDataRealizacao());
            setDataOuNull(comando, 5, entrevista.getDataEntregaLaudo());

            comando.setInt(6, entrevista.getId());

            comando.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar entrevista.", e);
        }
    }

    public void excluir(Entrevista entrevista) {
        String sql = "DELETE FROM entrevista WHERE id = ?";
        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setInt(1, entrevista.getId());
            comando.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Entrevista buscarPorId(int id) {
        String sql = baseSelect() + " WHERE e.id = ?";
        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setInt(1, id);
            try (ResultSet resultado = comando.executeQuery()) {
                if (resultado.next()) {
                    return montarEntrevista(resultado);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public List<Entrevista> listarTodos() {
        String sql = baseSelect() + " ORDER BY e.id";
        return executarListagem(sql);
    }

    /**
     * NOVO: lista a fila (tudo que ainda não foi realizado nem cancelado),
     * ordenado pela data de solicitação do pedido — quem pediu primeiro
     * aparece primeiro.
     */
    public List<Entrevista> listarFila() {
        String sql = baseSelect() +
                " WHERE e.status IN ('PENDENTE_AGENDAMENTO', 'AGENDADA')" +
                " ORDER BY p.data_solicitacao";
        return executarListagem(sql);
    }

    public List<Entrevista> listarPorPedido(int idPedidoExame) {
        String sql = baseSelect() + " WHERE e.id_pedido_exame = ? ORDER BY e.id";
        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setInt(1, idPedidoExame);
            List<Entrevista> entrevistas = new ArrayList<>();
            try (ResultSet resultado = comando.executeQuery()) {
                while (resultado.next()) {
                    entrevistas.add(montarEntrevista(resultado));
                }
            }
            return entrevistas;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Busca as entrevistas de vários pedidos em uma única consulta
     * (evita 1 query por pedido ao montar listagens em tela).
     */
    public List<Entrevista> listarPorPedidos(List<Integer> idsPedidoExame) {
        if (idsPedidoExame == null || idsPedidoExame.isEmpty()) {
            return new ArrayList<>();
        }

        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < idsPedidoExame.size(); i++) {
            if (i > 0) {
                placeholders.append(",");
            }
            placeholders.append("?");
        }

        String sql = baseSelect() + " WHERE e.id_pedido_exame IN (" + placeholders + ") ORDER BY e.id";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            for (int i = 0; i < idsPedidoExame.size(); i++) {
                comando.setInt(i + 1, idsPedidoExame.get(i));
            }

            List<Entrevista> entrevistas = new ArrayList<>();
            try (ResultSet resultado = comando.executeQuery()) {
                while (resultado.next()) {
                    entrevistas.add(montarEntrevista(resultado));
                }
            }
            return entrevistas;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Entrevista> executarListagem(String sql) {
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

    private String baseSelect() {
        return """
                SELECT
                    e.id,
                    p.id AS pedido_exame_id,
                    p.numero_processo AS pedido_exame_numero_processo,
                    p.status AS pedido_exame_status,
                    p.data_solicitacao AS pedido_exame_data_solicitacao,
                    s.id AS sentenciado_id,
                    s.matricula AS sentenciado_matricula,
                    s.nome AS sentenciado_nome,
                    pro.id AS profissional_id,
                    pro.nome AS profissional_nome,
                    e.tipo_atendimento,
                    e.status,
                    e.data_agendamento,
                    e.data_realizacao,
                    u.id AS usuario_id,
                    u.login AS usuario_login,
                    u.ativo AS usuario_ativo,
                    e.data_entrega_laudo,
                    e.data_cadastro
                FROM entrevista e
                    INNER JOIN pedido_exame p ON e.id_pedido_exame = p.id
                    INNER JOIN sentenciado s ON p.id_sentenciado = s.id
                    LEFT JOIN profissional pro ON e.id_profissional = pro.id
                    INNER JOIN usuario u ON e.id_usuario = u.id
                """;
    }

    private Entrevista montarEntrevista(ResultSet resultado) throws SQLException {
        Sentenciado sentenciado = new Sentenciado();
        sentenciado.setId(resultado.getInt("sentenciado_id"));
        sentenciado.setMatricula(resultado.getString("sentenciado_matricula"));
        sentenciado.setNome(resultado.getString("sentenciado_nome"));

        PedidoExame pedidoExame = new PedidoExame();
        pedidoExame.setId(resultado.getInt("pedido_exame_id"));
        pedidoExame.setNumeroProcesso(resultado.getString("pedido_exame_numero_processo"));
        pedidoExame.setStatus(StatusPedidoExame.valueOf(resultado.getString("pedido_exame_status")));
        pedidoExame.setDataSolicitacao(converteData(resultado, "pedido_exame_data_solicitacao"));
        pedidoExame.setSentenciado(sentenciado);

        Entrevista entrevista = new Entrevista();
        entrevista.setId(resultado.getInt("id"));
        entrevista.setPedidoExame(pedidoExame);
        entrevista.setTipoAtendimento(
                TipoProfissional.valueOf(resultado.getString("tipo_atendimento")));
        entrevista.setStatus(
                StatusEntrevista.valueOf(resultado.getString("status")));

        // profissional pode ser null (LEFT JOIN) enquanto está na fila
        int idProfissional = resultado.getInt("profissional_id");
        if (!resultado.wasNull()) {
            Profissional profissional = new Profissional();
            profissional.setId(idProfissional);
            profissional.setNome(resultado.getString("profissional_nome"));
            entrevista.setProfissional(profissional);
        }

        entrevista.setDataAgendamento(converteData(resultado, "data_agendamento"));
        entrevista.setDataRealizacao(converteData(resultado, "data_realizacao"));

        Usuario usuario = new Usuario(
                resultado.getInt("usuario_id"),
                resultado.getString("usuario_login")
        );
        usuario.setAtivo(resultado.getBoolean("usuario_ativo"));
        entrevista.setUsuario(usuario);

        entrevista.setDataEntregaLaudo(converteData(resultado, "data_entrega_laudo"));
        entrevista.setDataCadastro(converteData(resultado, "data_cadastro"));

        return entrevista;
    }

    private LocalDate converteData(ResultSet resposta, String nomeDaColuna) throws SQLException {
        Date data = resposta.getDate(nomeDaColuna);
        return data != null ? data.toLocalDate() : null;
    }

    private void setDataOuNull(PreparedStatement comando, int indice, LocalDate data) throws SQLException {
        if (data != null) {
            comando.setDate(indice, Date.valueOf(data));
        } else {
            comando.setNull(indice, Types.DATE);
        }
    }

    /**
     * ATUALIZADO: agora só conta como "atendido" quando o status é REALIZADA
     * — antes, qualquer linha na tabela contava, o que vai gerar falso positivo
     * assim que existirem entrevistas pendentes/agendadas.
     */
    public boolean foiAtendidoPorAmbos(int idPedido) {
        String sql = """
                SELECT COUNT(DISTINCT tipo_atendimento)
                FROM entrevista
                WHERE id_pedido_exame = ?
                  AND status = 'REALIZADA'
                """;

        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setInt(1, idPedido);
            try (ResultSet resultado = comando.executeQuery()) {
                if (resultado.next()) {
                    return resultado.getInt(1) >= 2;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }
}
