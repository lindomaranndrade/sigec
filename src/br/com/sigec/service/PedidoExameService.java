package br.com.sigec.service;

import br.com.sigec.dao.PedidoExameDAO;
import br.com.sigec.model.PedidoExame;
import br.com.sigec.model.StatusPedidoExame;

import java.time.LocalDate;

public class PedidoExameService {
    private PedidoExameDAO pedidoExameDAO;

    public PedidoExameService(){
        this.pedidoExameDAO = new PedidoExameDAO();
    }

    public void inserir(PedidoExame pedidoExame){
        validarPedidoExameNulo(pedidoExame);

        validarSentenciadoObrigatorio(pedidoExame);
        validarUsuarioObrigatorio(pedidoExame);

        validarDataSolicitacaoObrigatoria(pedidoExame);
        validarDataSolicitacaoFutura(pedidoExame);
        pedidoExame.setStatus(StatusPedidoExame.CADASTRADO);
        pedidoExameDAO.inserir(pedidoExame);
    }

    public void concluirPedido(PedidoExame pedidoExame){
        validarPedidoExameNulo(pedidoExame);
        validarPedidoNaoConcluido(pedidoExame);
        validarPedidoNaoCancelado(pedidoExame);
        validarNumeroSeiObrigatorio(pedidoExame);
        pedidoExame.setDataConclusao(LocalDate.now());
        pedidoExame.setStatus(StatusPedidoExame.CONCLUIDO);
        pedidoExameDAO.atualizar(pedidoExame);
    }

    private void validarPedidoNaoConcluido(PedidoExame pedidoExame){
        if(pedidoExame.getStatus() == StatusPedidoExame.CONCLUIDO){
            throw new IllegalArgumentException(
                    "Pedido já está concluído."
            );
        }
    }

    private void validarPedidoNaoCancelado(PedidoExame pedidoExame){
        if(pedidoExame.getStatus() == StatusPedidoExame.CANCELADO){
            throw new IllegalArgumentException(
                    "Pedido cancelado não pode ser alterado."
            );
        }
    }

    public void validarNumeroSeiObrigatorio(PedidoExame pedidoExame){
        if(pedidoExame.getNumeroSEI() == null || pedidoExame.getNumeroSEI().trim().isEmpty()){
            throw new IllegalArgumentException("Informe o numero SEI para concluir");
        }
    }

    private void validarPedidoExameNulo(PedidoExame pedidoExame){
        if(pedidoExame == null){
            throw new IllegalArgumentException("Pedido não pode ser nulo");
        }
    }


    private void validarDataSolicitacaoObrigatoria(PedidoExame pedidoExame){
        if(pedidoExame.getDataSolicitacao() == null){
            throw new IllegalArgumentException("A data da solicitação não pode ser nula");
        }
    }

    private void validarDataSolicitacaoFutura(PedidoExame pedidoExame){
        LocalDate hoje = LocalDate.now();
        if(pedidoExame.getDataSolicitacao().isAfter(hoje)){
            throw new IllegalArgumentException("Data da solicitação não pode ser futura");
        }
    }


    private void validarSentenciadoObrigatorio(PedidoExame pedidoExame){
        if(pedidoExame.getSentenciado() == null){
            throw new IllegalArgumentException("Sentenciado não informado.");
        }
    }

    private void validarUsuarioObrigatorio(PedidoExame pedidoExame){
        if(pedidoExame.getUsuario() == null){
            throw new IllegalArgumentException("Usuario não informado.");
        }
    }


}
