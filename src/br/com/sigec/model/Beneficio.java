package br.com.sigec.model;

public class Beneficio {
    private int id;
    private String descricao;

    public Beneficio(){

    }

    public Beneficio(String descricao){
        this.descricao = descricao;
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
                '}';
    }
}
