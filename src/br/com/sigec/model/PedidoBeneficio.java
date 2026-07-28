package br.com.sigec.model;

public class PedidoBeneficio {
    private PedidoExame pedido;
    private Beneficio beneficio;

    public PedidoBeneficio(){

    }

    public PedidoBeneficio(PedidoExame pedido, Beneficio beneficio){
        this.pedido = pedido;
        this.beneficio = beneficio;
    }

    public PedidoExame getPedido() {
        return pedido;
    }

    public void setPedido(PedidoExame pedido) {
        this.pedido = pedido;
    }

    public Beneficio getBeneficio() {
        return beneficio;
    }

    public void setBeneficio(Beneficio beneficio) {
        this.beneficio = beneficio;
    }
}
