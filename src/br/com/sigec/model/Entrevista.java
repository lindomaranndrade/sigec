package br.com.sigec.model;

import br.com.sigec.session.SessaoUsuario;

import java.time.LocalDate;

public class Entrevista {
    private int id;
    private PedidoExame pedidoExame;
    private TipoProfissional tipoAtendimento;
    private Profissional profissional;      // pode ser null enquanto PENDENTE_AGENDAMENTO
    private StatusEntrevista status;
    private LocalDate dataAgendamento;       // pode ser null enquanto PENDENTE_AGENDAMENTO
    private LocalDate dataRealizacao;        // só preenchida quando REALIZADA
    private Usuario usuario;
    private LocalDate dataEntregaLaudo;
    private LocalDate dataCadastro;

    public Entrevista() {
    }

    /**
     * Cria uma entrevista "vazia", ainda na fila, sem profissional
     * nem data definidos. Use este construtor quando o PedidoExame
     * é criado e o sistema gera automaticamente as entrevistas exigidas.
     */
    public Entrevista(PedidoExame pedido, TipoProfissional tipoAtendimento) {
        this.pedidoExame = pedido;
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

    public TipoProfissional getTipoAtendimento() {
        return tipoAtendimento;
    }

    public void setTipoAtendimento(TipoProfissional tipoAtendimento) {
        this.tipoAtendimento = tipoAtendimento;
    }

    public Profissional getProfissional() {
        return profissional;
    }

    public void setProfissional(Profissional profissional) {
        this.profissional = profissional;
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
                ", pedidoExame=" + pedidoExame +
                ", tipoAtendimento=" + tipoAtendimento +
                ", profissional=" + profissional +
                ", status=" + status +
                ", dataAgendamento=" + dataAgendamento +
                ", dataRealizacao=" + dataRealizacao +
                ", usuario=" + usuario +
                ", dataEntregaLaudo=" + dataEntregaLaudo +
                ", dataCadastro=" + dataCadastro +
                '}';
    }
}
