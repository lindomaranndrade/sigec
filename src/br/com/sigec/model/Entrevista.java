package br.com.sigec.model;

import br.com.sigec.session.SessaoUsuario;

import java.time.LocalDate;

public class Entrevista {
    private int id;
    private PedidoExame pedidoExame;
    private Profissional profissional;
    private TipoProfissional tipoAtendimento;
    private StatusEntrevista status;
    private LocalDate dataAgendamento;
    private LocalDate dataRealizacao;
    private LocalDate dataEntregaLaudo;
    private Usuario usuario;
    private LocalDate dataCadastro;

    public Entrevista(){

    }

    // Cria uma entrevista "na fila", ainda sem profissional nem data de agendamento definidos
    public Entrevista(PedidoExame pedidoExame, TipoProfissional tipoAtendimento){
        this.pedidoExame = pedidoExame;
        this.tipoAtendimento = tipoAtendimento;
        this.status = StatusEntrevista.PENDENTE_AGENDAMENTO;
        this.usuario = SessaoUsuario.getUsuarioLogado();
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

    public TipoProfissional getTipoAtendimento() {
        return tipoAtendimento;
    }

    public void setTipoAtendimento(TipoProfissional tipoAtendimento) {
        this.tipoAtendimento = tipoAtendimento;
    }

    public StatusEntrevista getStatus() {
        return status;
    }

    public void setStatus(StatusEntrevista status) {
        this.status = status;
    }

    public LocalDate getDataAgendamento() {
        return dataAgendamento;
    }

    public void setDataAgendamento(LocalDate dataAgendamento) {
        this.dataAgendamento = dataAgendamento;
    }

    public LocalDate getDataRealizacao() {
        return dataRealizacao;
    }

    public void setDataRealizacao(LocalDate dataRealizacao) {
        this.dataRealizacao = dataRealizacao;
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
                ", tipoAtendimento=" + tipoAtendimento +
                ", status=" + status +
                ", dataAgendamento=" + dataAgendamento +
                ", dataRealizacao=" + dataRealizacao +
                ", usuario=" + usuario.toString() +
                ", dataEntregaLaudo=" + dataEntregaLaudo +
                ", dataCadastro=" + dataCadastro +
                '}';
    }
}
