package br.com.sigec.dao;

import br.com.sigec.model.Profissional;
import br.com.sigec.model.TipoProfissional;

import java.util.ArrayList;
import java.util.List;

public class TesteProfissionalDAO {
    public static void main(String[] args) {
        //Profissional profissional = new Profissional("Marcelo de Paula", TipoProfissional.PSICOLOGO);
        ProfissionalDAO dao = new ProfissionalDAO();
        //dao.inserir(profissional);
        List<Profissional> profissionais = new ArrayList<>();
        dao.excluir(dao.buscarPorId(2));
        profissionais = dao.listarTodos();

        for(Profissional p: profissionais){
            System.out.println(p);
        }
    }

}
