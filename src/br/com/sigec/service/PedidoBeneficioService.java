package br.com.sigec.service;

import br.com.sigec.dao.PedidoBeneficioDAO;
import br.com.sigec.model.PedidoBeneficio;
import br.com.sigec.model.StatusPedidoExame;

public class PedidoBeneficioService {
    private PedidoBeneficioDAO pedidoBeneficioDAO;

    public PedidoBeneficioService(){
        this.pedidoBeneficioDAO = new PedidoBeneficioDAO();

    }

    public void inserir(PedidoBeneficio pedidoBeneficio){

        validarPedidoBeneficioNulo(pedidoBeneficio);

        validarPedidoExameObrigatorio(pedidoBeneficio);
        validarIdPedido(pedidoBeneficio);

        validarBeneficioObrigatorio(pedidoBeneficio);
        validarIdBeneficio(pedidoBeneficio);

        validarStatusPedido(pedidoBeneficio);

        validarBeneficioNaoDuplicado(pedidoBeneficio);

        pedidoBeneficioDAO.inserir(pedidoBeneficio);
    }

    private void validarPedidoBeneficioNulo(PedidoBeneficio pedidoBeneficio){
        if(pedidoBeneficio == null){
            throw new IllegalArgumentException(
                    "Pedido de beneficio nulo"
            );
        }
    }

    private void validarPedidoExameObrigatorio(PedidoBeneficio pedidoBeneficio){

        if(pedidoBeneficio.getPedido() == null){
            throw new IllegalArgumentException(
                    "Pedido de exame obrigatório"
            );
        }
    }

    private void validarBeneficioObrigatorio(PedidoBeneficio pedidoBeneficio){
        if(pedidoBeneficio.getBeneficio() == null){
            throw new IllegalArgumentException(
                    "Beneficio obrigatorio"
            );
        }
    }

    private void validarStatusPedido(PedidoBeneficio pedidoBeneficio){
        StatusPedidoExame  status= pedidoBeneficio.getPedido().getStatus();

        if(status == StatusPedidoExame.CANCELADO){
            throw new IllegalArgumentException(
                    "O pedido foi cancelado"
            );
        }

        if(status == StatusPedidoExame.CONCLUIDO){
            throw new IllegalArgumentException(
                    "O pedido ja foi concluido"
            );
        }

        if(status == StatusPedidoExame.TRANSFERIDO){
            throw new IllegalArgumentException(
                    "O sentenciado foi transferido"
            );
        }
    }

    private void validarIdPedido(
            PedidoBeneficio pedidoBeneficio){

        if(pedidoBeneficio.getPedido().getId() <= 0){
            throw new IllegalArgumentException(
                    "ID do pedido inválido"
            );
        }
    }

    private void validarIdBeneficio(
            PedidoBeneficio pedidoBeneficio){

        if(pedidoBeneficio.getBeneficio().getId() <= 0){
            throw new IllegalArgumentException(
                    "ID do benefício inválido"
            );
        }
    }

    private void validarBeneficioNaoDuplicado(PedidoBeneficio pedidoBeneficio){

        boolean existe =
                pedidoBeneficioDAO.existeVinculo(
                        pedidoBeneficio.getPedido().getId(),
                        pedidoBeneficio.getBeneficio().getId()
                );

        if(existe){
            throw new IllegalArgumentException(
                    "Benefício já vinculado ao pedido."
            );
        }
    }
}
