package br.com.sigec.dao;

import br.com.sigec.model.Beneficio;

import java.util.ArrayList;
import java.util.List;

public class TesteBeneficioDAO {
    public static void main(String[] args) {
        Beneficio beneficio = new Beneficio();
        BeneficioDAO dao = new BeneficioDAO();
        beneficio.setDescricao("Progressão de Regime ");
        beneficio.setSigla("LC");
        dao.inserir(beneficio);

        System.out.println("Dados apos a inserção: ");
        System.out.println("dao.buscarPorId(5) = " + dao.buscarPorId(5));

    }
}
