package br.com.sigec.model;

public class Beneficio {
    private int id;
    private String descricao;
    private String sigla;
    public Beneficio(){

    }

    public Beneficio(String descricao, String sigla){
        this.descricao = descricao;
        this.sigla = sigla;
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return "Beneficio{" +
                "id=" + id +
                ", descricao='" + descricao + '\'' +
                ", sigla='" + sigla + '\'' +
                '}';
    }
}
