package br.com.sigec.dao;

import br.com.sigec.model.Sentenciado;

import java.util.ArrayList;
import java.util.List;

public class TesteSentenciadoDAO {
    public static void main(String[] args) {
        Sentenciado sentenciado = new Sentenciado("123.456-7","Jose da Silva");
        SentenciadoDAO dao = new SentenciadoDAO();
        dao.inserir(sentenciado);

        List<Sentenciado> sentenciados = dao.listarTodos();
        for(Sentenciado s: sentenciados){
            System.out.println("s = " + s);
        }
        
    }
}
