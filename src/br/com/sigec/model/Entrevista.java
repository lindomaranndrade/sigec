package br.com.sigec.model;

import java.time.LocalDate;

public class Entrevista {
    private int id;
    private PedidoExame pedidoExame;
    private Profissional profissional;
    private LocalDate dataEntrevista;
    private Usuario usuario;
    private LocalDate dataEntregaLaudo;
    private LocalDate dataCadastro;
    // se a data de entrega estiver diferente de null é pq foi entregue

    public Entrevista(){

    }


    public Entrevista(PedidoExame pedido, Profissional profissional, LocalDate dataEntrevista, Usuario usuario){
        this.pedidoExame = pedido;
        this.profissional = profissional;
        this.dataEntrevista = dataEntrevista;
        this.usuario = usuario;
        this.dataCadastro = LocalDate.now();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public PedidoExame getPedidoExame() {
        return pedidoExame;
    }

    public void setPedidoExame(PedidoExame pedidoExame) {
        this.pedidoExame = pedidoExame;
    }

    public Profissional getProfissional() {
        return profissional;
    }

    public void setProfissional(Profissional profissional) {
        this.profissional = profissional;
    }

    public LocalDate getDataEntrevista() {
        return dataEntrevista;
    }

    public void setDataEntrevista(LocalDate dataEntrevista) {
        this.dataEntrevista = dataEntrevista;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public LocalDate getDataEntregaLaudo() {
        return dataEntregaLaudo;
    }

    public void setDataEntregaLaudo(LocalDate dataEntregaLaudo) {
        this.dataEntregaLaudo = dataEntregaLaudo;
    }

    public LocalDate getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDate dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    @Override
    public String toString() {
        return "Entrevista{" +
                "id=" + id +
                ", pedidoExame=" + pedidoExame.toString() +
                ", profissional=" + profissional +
                ", dataEntrevista=" + dataEntrevista +
                ", usuario=" + usuario.toString() +
                ", dataEntregaLaudo=" + dataEntregaLaudo +
                ", dataCadastro=" + dataCadastro +
                '}';
    }
}
