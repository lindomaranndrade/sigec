package br.com.sigec.model;

public class Sentenciado {
    private int id;
    private String matricula;
    private String nome;

    public Sentenciado(){

    }

    //id ser preenchido pelo BD posteriormente.
    public Sentenciado(String matricula, String nome){
        this.matricula = matricula;
        this.nome = nome;
    }

    public Sentenciado(int id){
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public String toString() {
        return "Sentenciado{" +
                "id=" + id +
                ", matricula='" + matricula + '\'' +
                ", nome='" + nome + '\'' +
                '}';
    }
}
