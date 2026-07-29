package br.com.sigec.dao;

import br.com.sigec.model.PedidoExame;
import br.com.sigec.model.Sentenciado;
import br.com.sigec.model.StatusPedidoExame;
import br.com.sigec.model.Usuario;
import br.com.sigec.util.Conexao;
import java.sql.Types;

import java.sql.*;
import java.time.LocalDate;

public class PedidoExameDAO {

    public void inserir(PedidoExame pedido){
        String sql = """
                    INSERT INTO pedido_exame(id_sentenciado, data_cadastro, data_solicitacao, status, id_usuario, 
                    numero_processo, numero_sei, data_conclusao)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                    """;
        try(Connection conexao = Conexao.conectar();PreparedStatement comando = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            comando.setInt(1,pedido.getSentenciado().getId());
            comando.setDate(2,java.sql.Date.valueOf(pedido.getDataCadastro()));
            comando.setDate(3,java.sql.Date.valueOf(pedido.getDataSolicitacao()));
            comando.setString(4,pedido.getStatus().name());
            comando.setInt(5,pedido.getUsuario().getId());
            comando.setString(6,pedido.getNumeroProcesso());
            comando.setString(7, pedido.getNumeroSEI());

            // Regra de negócio:
            // Um pedido recém-cadastrado ainda não foi concluído.
            // Portanto, a data de conclusão deve ser gravada como NULL.
            comando.setNull(8,Types.DATE);
            comando.executeUpdate();

                try(ResultSet resposta = comando.getGeneratedKeys()){
                    if(resposta.next()){
                        pedido.setId(resposta.getInt(1));
                    }
                }

        }catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    public PedidoExame buscarPorId(int id){
        String sql = """
                    SELECT
                        pe.id,
                        pe.data_solicitacao,
                        pe.status,
                        pe.data_cadastro,
                        pe.numero_processo,
                        pe.numero_sei,
                        pe.data_conclusao,
                   
                        u.id AS usuario_id,
                        u.login AS usuario_login,
                   
                        s.id AS sentenciado_id,
                        s.nome AS sentenciado_nome,
                        s.matricula AS sentenciado_matricula
                    FROM pedido_exame pe
                         INNER JOIN  usuario u ON (pe.id_usuario = u.id)
                         INNER JOIN sentenciado s ON (s.id = pe.id_sentenciado)
                    WHERE pe.id = ?
                   
                    """;
        try(Connection conexao = Conexao.conectar(); PreparedStatement comando = conexao.prepareStatement(sql)){

            comando.setInt(1,id);

            try(ResultSet resultado = comando.executeQuery()){
                if(resultado.next()){
                    return montarPedidoExame(resultado);
                }
            }

        }catch (SQLException e){
            throw new RuntimeException(e);
        }
        return null;
    }


    public PedidoExame montarPedidoExame (ResultSet resultado) throws SQLException{
        PedidoExame pedido = new PedidoExame();

        pedido.setId(resultado.getInt("id"));
        pedido.setStatus(StatusPedidoExame.valueOf(resultado.getString("status")));
        pedido.setDataCadastro(converteData(resultado, "data_cadastro"));
        pedido.setDataSolicitacao(converteData(resultado,"data_solicitacao"));
        pedido.setNumeroProcesso(resultado.getString("numero_processo"));
        pedido.setNumeroSEI(resultado.getString("numero_sei"));
        pedido.setDataConclusao(converteData(resultado,"data_conclusao"));

        Usuario usuario = new Usuario(resultado.getInt("usuario_id"), resultado.getString("usuario_login"));
        pedido.setUsuario(usuario);

        Sentenciado sentenciado = new Sentenciado();
        sentenciado.setId(resultado.getInt("sentenciado_id"));
        sentenciado.setNome(resultado.getString("sentenciado_nome"));
        sentenciado.setMatricula(resultado.getString("sentenciado_matricula"));
        pedido.setSentenciado(sentenciado);
        return pedido;

    }

    private LocalDate converteData(ResultSet resposta, String nomeDaColuna) throws SQLException {

        Date data = resposta.getDate(nomeDaColuna);

        if (data != null) {
            return data.toLocalDate();
        }

        return null;
    }
}
