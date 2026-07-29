package br.com.sigec.model;

public class Usuario {
    private int id;
    private String nome;
    private String login;
    private String senha;
    private boolean ativo;

    public Usuario(){

    }

    //Não colocamos o ID pq sera preenchido diretamente pelo BD.
    public Usuario(String nome, String login, String senha){
        this.nome = nome;
        this.login = login;
        this.senha = senha;
        this.ativo = true;
    }

    public Usuario(int id, String login){
        this.id = id;
        this.login=login;
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

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", login='" + login + '\'' +
                ", ativo=" + ativo +
                '}';
    }
}
