package br.com.sigec.dao;

import br.com.sigec.model.Beneficio;

import java.util.ArrayList;
import java.util.List;

public class TesteBeneficioDAO {
    public static void main(String[] args) {
        Beneficio beneficio = new Beneficio();
        BeneficioDAO dao = new BeneficioDAO();
        List<Beneficio> beneficios = new ArrayList<>();
        beneficio = dao.buscarPorId(1);
        beneficio.setDescricao("REGIME-SEMIABERTO");
        dao.atualizar(beneficio);



        for(Beneficio b: beneficios){
            System.out.println(b);
        }

    }
}
