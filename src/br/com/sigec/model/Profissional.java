package br.com.sigec.model;

public class Profissional {
    private int id;
    private String nome;
    private TipoProfissional tipo;
    private boolean ativo;

    public Profissional(){

    }

    //o ID sera preenchido pelo BD.
    public Profissional(String nome, TipoProfissional tipo){
        this.nome = nome;
        this.tipo = tipo;
        this.ativo = true;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public TipoProfissional getTipo() {
        return tipo;
    }

    public void setTipo(TipoProfissional tipo) {
        this.tipo = tipo;
    }

    @Override
    public String toString() {
        return "Profissional{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", tipo=" + tipo +
                ", ativo=" + ativo +
                '}';
    }
}
