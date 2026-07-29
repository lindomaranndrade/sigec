package br.com.sigec.model;

import java.time.LocalDate;
import java.util.List;

public class PedidoExame {
    private int id;
    private StatusPedidoExame status;
    private LocalDate dataCadastro;
    private LocalDate dataSolicitacao;
    private String numeroProcesso;
    private String numeroSEI;
    private LocalDate dataConclusao;

    //Se relaciona com Sentenciado
    private Sentenciado sentenciado;

    //Se relaciona com usuario
    private Usuario usuario;
    private List<PedidoBeneficio> pedidosBeneficios;

    public PedidoExame(){

    }

    public PedidoExame(Sentenciado sentenciado,LocalDate dataSolicitacao, String numeroProcesso, Usuario usuario){
        this.sentenciado = sentenciado;
        this.dataCadastro = LocalDate.now();
        this.dataSolicitacao = dataSolicitacao;
        this.numeroProcesso = numeroProcesso;
        this.usuario = usuario;
        this.status = StatusPedidoExame.CADASTRADO;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public StatusPedidoExame getStatus() {
        return status;
    }

    public void setStatus(StatusPedidoExame status) {
        this.status = status;
    }

    public LocalDate getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDate dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public LocalDate getDataSolicitacao() {
        return dataSolicitacao;
    }

    public void setDataSolicitacao(LocalDate dataSolicitacao) {
        this.dataSolicitacao = dataSolicitacao;
    }

    public String getNumeroProcesso() {
        return numeroProcesso;
    }

    public void setNumeroProcesso(String numeroProcesso) {
        this.numeroProcesso = numeroProcesso;
    }

    public String getNumeroSEI() {
        return numeroSEI;
    }

    public void setNumeroSEI(String numeroSEI) {
        this.numeroSEI = numeroSEI;
    }

    public LocalDate getDataConclusao() {
        return dataConclusao;
    }

    public void setDataConclusao(LocalDate dataConclusao) {
        this.dataConclusao = dataConclusao;
    }

    public Sentenciado getSentenciado() {
        return sentenciado;
    }

    public void setSentenciado(Sentenciado sentenciado) {
        this.sentenciado = sentenciado;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    @Override
    public String toString() {
        return "PedidoExame{" +
                "id=" + id +
                ", status=" + status +
                ", dataCadastro=" + dataCadastro +
                ", dataSolicitacao=" + dataSolicitacao +
                ", numeroProcesso='" + numeroProcesso + '\'' +
                ", numeroSEI='" + numeroSEI + '\'' +
                ", dataConclusao=" + dataConclusao +
                ", matricula=" + sentenciado.getMatricula() +
                '}';
    }
}
