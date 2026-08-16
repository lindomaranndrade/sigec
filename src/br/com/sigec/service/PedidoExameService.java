package br.com.sigec.service;

import br.com.sigec.dao.EntrevistaDAO;
import br.com.sigec.dao.PedidoExameDAO;
import br.com.sigec.model.Beneficio;
import br.com.sigec.model.PedidoBeneficio;
import br.com.sigec.model.PedidoExame;
import br.com.sigec.model.StatusPedidoExame;
import br.com.sigec.model.TipoProfissional;

import java.time.LocalDate;
import java.util.List;

public class PedidoExameService {

    private PedidoExameDAO pedidoExameDAO;
    private EntrevistaService entrevistaService;

    public PedidoExameService() {
        this.pedidoExameDAO = new PedidoExameDAO();
        this.entrevistaService = new EntrevistaService();
    }

    public void inserir(PedidoExame pedidoExame) {

        validarPedidoExameNulo(pedidoExame);

        validarSentenciadoObrigatorio(pedidoExame);
        validarUsuarioObrigatorio(pedidoExame);

        validarDataSolicitacaoObrigatoria(pedidoExame);
        validarDataSolicitacaoFutura(pedidoExame);

        for (PedidoBeneficio pedidoBeneficio : pedidoExame.getPedidosBeneficios()) {

            Beneficio beneficio = pedidoBeneficio.getBeneficio();

            if (pedidoExameDAO.existePedidoAtivoParaSentenciadoEBeneficio(
                    pedidoExame,
                    beneficio
            )) {
                throw new IllegalArgumentException(
                        "Já existe um pedido ativo para o benefício: "
                                + beneficio.getDescricao()
                );
            }
        }

        pedidoExame.setStatus(StatusPedidoExame.CADASTRADO);

        // Primeiro salva o pedido para obter o ID gerado.
        pedidoExameDAO.inserir(pedidoExame);

        // Depois cria automaticamente as duas entrevistas na fila.
        entrevistaService.criarPendente(
                pedidoExame,
                TipoProfissional.PSICOLOGO
        );

        entrevistaService.criarPendente(
                pedidoExame,
                TipoProfissional.ASSISTENTE_SOCIAL
        );
    }

    public void concluirPedido(PedidoExame pedidoExame) {

        validarPedidoExameNulo(pedidoExame);
        validarPedidoNaoConcluido(pedidoExame);
        validarPedidoNaoCancelado(pedidoExame);
        validarNumeroSeiObrigatorio(pedidoExame);
        validarAtendimentosObrigatorios(pedidoExame);

        pedidoExame.setDataConclusao(LocalDate.now());
        pedidoExame.setStatus(StatusPedidoExame.CONCLUIDO);

        pedidoExameDAO.atualizar(pedidoExame);
    }

    public List<PedidoExame> listarPendentes() {
        return pedidoExameDAO.listarPendendes();
    }

    private void validarPedidoNaoConcluido(PedidoExame pedidoExame) {

        if (pedidoExame.getStatus() == StatusPedidoExame.CONCLUIDO) {
            throw new IllegalArgumentException(
                    "Pedido já está concluído."
            );
        }
    }

    private void validarPedidoNaoCancelado(PedidoExame pedidoExame) {

        if (pedidoExame.getStatus() == StatusPedidoExame.CANCELADO) {
            throw new IllegalArgumentException(
                    "Pedido cancelado não pode ser alterado."
            );
        }
    }

    public void validarNumeroSeiObrigatorio(PedidoExame pedidoExame) {

        if (pedidoExame.getNumeroSEI() == null
                || pedidoExame.getNumeroSEI().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Informe o numero SEI para concluir"
            );
        }
    }

    private void validarPedidoExameNulo(PedidoExame pedidoExame) {

        if (pedidoExame == null) {
            throw new IllegalArgumentException(
                    "Pedido não pode ser nulo"
            );
        }
    }

    private void validarDataSolicitacaoObrigatoria(PedidoExame pedidoExame) {

        if (pedidoExame.getDataSolicitacao() == null) {
            throw new IllegalArgumentException(
                    "A data da solicitação não pode ser nula"
            );
        }
    }

    private void validarDataSolicitacaoFutura(PedidoExame pedidoExame) {

        LocalDate hoje = LocalDate.now();

        if (pedidoExame.getDataSolicitacao().isAfter(hoje)) {
            throw new IllegalArgumentException(
                    "Data da solicitação não pode ser futura"
            );
        }
    }

    private void validarSentenciadoObrigatorio(PedidoExame pedidoExame) {

        if (pedidoExame.getSentenciado() == null) {
            throw new IllegalArgumentException(
                    "Sentenciado não informado."
            );
        }
    }

    private void validarUsuarioObrigatorio(PedidoExame pedidoExame) {

        if (pedidoExame.getUsuario() == null) {
            throw new IllegalArgumentException(
                    "Usuario não informado."
            );
        }
    }

    private void validarAtendimentosObrigatorios(PedidoExame pedidoExame) {

        EntrevistaDAO entrevistaDAO = new EntrevistaDAO();

        if (!entrevistaDAO.foiAtendidoPorAmbos(pedidoExame.getId())) {

            throw new IllegalArgumentException(
                    "Não é possível concluir o pedido. "
                            + "É necessário atendimento do psicólogo "
                            + "e do assistente social."
            );
        }
    }
}