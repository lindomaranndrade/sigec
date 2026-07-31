package br.com.sigec.service;

import br.com.sigec.dao.BeneficioDAO;
import br.com.sigec.model.Beneficio;

public class BeneficioService {
    private static final int TAMANHO_MAXIMO_DESCRICAO = 100;
    private BeneficioDAO beneficioDAO;

    public BeneficioService(){
        this.beneficioDAO = new BeneficioDAO();
    }
    public void inserir(Beneficio beneficio){
        validarBeneficioNulo(beneficio);
        validarDescricao(beneficio);
        validarTamanhoDescricao(beneficio);
        validarDescricaoDuplicada(beneficio);

        beneficioDAO.inserir(beneficio);
    }

    private void validarBeneficioNulo(Beneficio beneficio){
        if(beneficio == null){
            throw new IllegalArgumentException("Beneficio não pode ser nulo.");
        }
    }

    private void validarDescricao(Beneficio beneficio){
        if(beneficio.getDescricao() == null || beneficio.getDescricao().isBlank()){
            throw new IllegalArgumentException("Descrição é obrigatória.");
        }
    }

    private void validarDescricaoDuplicada(Beneficio beneficio){
       String descricao = beneficio.getDescricao().trim();
       if(beneficioDAO.descricaoExiste(descricao)){
           throw new IllegalArgumentException("Beneficio ja cadastrado.");
       }
    }

    private void validarTamanhoDescricao(Beneficio beneficio){
        String descricao = beneficio.getDescricao().trim();
        if(descricao.length() > TAMANHO_MAXIMO_DESCRICAO){
            throw new IllegalArgumentException("Tamanho maximo excedido");
        }
    }
}
