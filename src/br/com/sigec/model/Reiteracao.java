package br.com.sigec.model;

import java.time.LocalDate;

public class Reiteracao {
    private int id;
    private PedidoExame pedido;
    private LocalDate dataReiteracao;
    private String observacoes;
    private String despacho;
    private Usuario usuario;
    private LocalDate dataCadastro;

    public Reiteracao(){

    }

    public Reiteracao(PedidoExame pedido, LocalDate dataReiteracao, String observacoes, String despacho, Usuario usuario){
        this.pedido = pedido;
        this.dataReiteracao = dataReiteracao;
        this.observacoes = observacoes;
        this.despacho = despacho;
        this.usuario = usuario;
        this.dataCadastro = LocalDate.now();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public PedidoExame getPedido() {
        return pedido;
    }

    public void setPedido(PedidoExame pedido) {
        this.pedido = pedido;
    }

    public LocalDate getDataReiteracao() {
        return dataReiteracao;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public String getDespacho() {
        return despacho;
    }

    public void setDespacho(String despacho) {
        this.despacho = despacho;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public LocalDate getDataCadastro() {
        return dataCadastro;
    }

    public void setDataReiteracao(LocalDate dataReiteracao) {
        this.dataReiteracao = dataReiteracao;
    }

    public void setDataCadastro(LocalDate dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    @Override
    public String toString() {
        return "Reiteracao{" +
                "id=" + id +
                ", matricula='" + pedido.getSentenciado().getMatricula() + '\'' +
                ", nome='" + pedido.getSentenciado().getNome() + '\'' +
                ", dataReiteracao=" + dataReiteracao +
                ", dataCadastro=" + dataCadastro +
                ", despacho='" + despacho + '\'' +
                '}';
    }
}
