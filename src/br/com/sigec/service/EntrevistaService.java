package br.com.sigec.service;

import br.com.sigec.dao.EntrevistaDAO;
import br.com.sigec.model.*;

import java.time.LocalDate;
import java.util.List;

public class EntrevistaService {
    private EntrevistaDAO entrevistaDAO;

    public EntrevistaService() {
        entrevistaDAO = new EntrevistaDAO();
    }

    // ---------- CRIAÇÃO NA FILA ----------

    public void criarPendente(PedidoExame pedido, TipoProfissional tipo) {
        if (pedido == null) {
            throw new IllegalArgumentException("O pedido de exame é obrigatório");
        }
        if (tipo == null) {
            throw new IllegalArgumentException("O tipo de atendimento é obrigatório");
        }

        Entrevista entrevista = new Entrevista(pedido, tipo);

        validarUsuario(entrevista);
        validarUsuarioAtivo(entrevista);
        validarStatusPedidoExame(entrevista);

        entrevistaDAO.inserir(entrevista);
    }

    // ---------- AGENDAMENTO ----------

    public void agendar(Entrevista entrevista, Profissional profissional, LocalDate dataAgendamento) {
        validarEntrevista(entrevista);
        validarId(entrevista);
        validarPedidoExame(entrevista);
        validarStatusPedidoExame(entrevista);
        validarUsuario(entrevista);
        validarUsuarioAtivo(entrevista);

        if (entrevista.getStatus() != StatusEntrevista.PENDENTE_AGENDAMENTO) {
            throw new IllegalArgumentException(
                    "Só é possível agendar uma entrevista que esteja pendente de agendamento"
            );
        }
        if (profissional == null) {
            throw new IllegalArgumentException("Profissional é obrigatório para agendar");
        }
        if (profissional.getTipo() != entrevista.getTipoAtendimento()) {
            throw new IllegalArgumentException(
                    "O tipo do profissional (" + profissional.getTipo() +
                            ") não corresponde ao tipo de atendimento exigido (" +
                            entrevista.getTipoAtendimento() + ")"
            );
        }
        if (dataAgendamento == null) {
            throw new IllegalArgumentException("Data de agendamento é obrigatória");
        }

        entrevista.setProfissional(profissional);
        entrevista.setDataAgendamento(dataAgendamento);
        entrevista.setStatus(StatusEntrevista.AGENDADA);

        entrevistaDAO.atualizar(entrevista);
    }

    // ---------- REALIZAÇÃO ----------

    public void registrarRealizacao(Entrevista entrevista, LocalDate dataRealizacao) {
        validarEntrevista(entrevista);
        validarId(entrevista);
        validarPedidoExame(entrevista);
        validarStatusPedidoExame(entrevista);

        if (entrevista.getStatus() != StatusEntrevista.AGENDADA) {
            throw new IllegalArgumentException(
                    "Só é possível registrar a realização de uma entrevista que esteja agendada"
            );
        }
        if (dataRealizacao == null) {
            throw new IllegalArgumentException("Data de realização é obrigatória");
        }
        if (dataRealizacao.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("A data de realização não pode ser futura");
        }
        if (dataRealizacao.isBefore(entrevista.getPedidoExame().getDataSolicitacao())) {
            throw new IllegalArgumentException(
                    "Data de realização não pode ser anterior à data do pedido"
            );
        }

        entrevista.setDataRealizacao(dataRealizacao);
        entrevista.setStatus(StatusEntrevista.REALIZADA);

        entrevistaDAO.atualizar(entrevista);
    }

    // ---------- CANCELAMENTO ----------

    public void cancelar(Entrevista entrevista) {
        validarEntrevista(entrevista);
        validarId(entrevista);

        if (entrevista.getStatus() == StatusEntrevista.REALIZADA) {
            throw new IllegalArgumentException("Não é possível cancelar uma entrevista já realizada");
        }

        entrevista.setStatus(StatusEntrevista.CANCELADA);
        entrevistaDAO.atualizar(entrevista);
    }

    /**
     * Cancela apenas o agendamento (profissional + data), não a entrevista.
     * A entrevista volta para PENDENTE_AGENDAMENTO para permitir um novo agendamento.
     */
    public void cancelarAgendamento(Entrevista entrevista) {
        validarEntrevista(entrevista);
        validarId(entrevista);

        if (entrevista.getStatus() != StatusEntrevista.AGENDADA) {
            throw new IllegalArgumentException(
                    "Só é possível cancelar o agendamento de uma entrevista que esteja agendada"
            );
        }

        entrevista.setProfissional(null);
        entrevista.setDataAgendamento(null);
        entrevista.setStatus(StatusEntrevista.PENDENTE_AGENDAMENTO);

        entrevistaDAO.atualizar(entrevista);
    }

    // ---------- ENTREGA DE LAUDO ----------

    public void registrarEntregaLaudo(Entrevista entrevista, LocalDate dataEntregaLaudo) {
        validarEntrevista(entrevista);
        validarId(entrevista);

        if (entrevista.getStatus() != StatusEntrevista.REALIZADA) {
            throw new IllegalArgumentException(
                    "O laudo só pode ser registrado depois que a entrevista foi realizada"
            );
        }

        entrevista.setDataEntregaLaudo(dataEntregaLaudo);
        entrevistaDAO.atualizar(entrevista);
    }

    // ---------- CONSULTAS ----------

    public List<Entrevista> listarPorPedido(int idPedidoExame) {
        return entrevistaDAO.listarPorPedido(idPedidoExame);
    }

    public List<Entrevista> listarPorPedidos(List<Integer> idsPedidoExame) {
        return entrevistaDAO.listarPorPedidos(idsPedidoExame);
    }

    public List<Entrevista> listarFila() {
        return entrevistaDAO.listarFila();
    }

    // ---------- VALIDAÇÕES INTERNAS ----------

    private void validarEntrevista(Entrevista entrevista) {
        if (entrevista == null) {
            throw new IllegalArgumentException("Entrevista não pode ser nula");
        }
    }

    private void validarId(Entrevista entrevista) {
        if (entrevista.getId() <= 0) {
            throw new IllegalArgumentException("Id inválido");
        }
    }

    private void validarPedidoExame(Entrevista entrevista) {
        if (entrevista.getPedidoExame() == null) {
            throw new IllegalArgumentException("O pedido de exame é obrigatório");
        }
    }

    private void validarUsuario(Entrevista entrevista) {
        if (entrevista.getUsuario() == null) {
            throw new IllegalArgumentException("Usuário é obrigatório");
        }
    }

    private void validarUsuarioAtivo(Entrevista entrevista) {
        if (!entrevista.getUsuario().isAtivo()) {
            throw new IllegalArgumentException("Usuario inativo");
        }
    }

    private void validarStatusPedidoExame(Entrevista entrevista) {
        StatusPedidoExame status = entrevista.getPedidoExame().getStatus();

        if (status == StatusPedidoExame.CANCELADO) {
            throw new IllegalArgumentException("O pedido do exame foi cancelado");
        }
        if (status == StatusPedidoExame.CONCLUIDO) {
            throw new IllegalArgumentException("Entrevista não pode ser alterada pois o pedido foi concluído");
        }
        if (status == StatusPedidoExame.TRANSFERIDO) {
            throw new IllegalArgumentException("Entrevista não pode ser alterada pois o sentenciado foi transferido");
        }
    }
}