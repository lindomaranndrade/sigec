package br.com.sigec.service;

import br.com.sigec.dao.ProfissionalDAO;
import br.com.sigec.model.Profissional;

public class ProfissionalService {
    private ProfissionalDAO profissionalDAO;
    private static final int TAMANHO_MINIMO_NOME = 3;
    private static final int TAMANHO_MAXIMO_NOME = 100;

    public ProfissionalService(){
        this.profissionalDAO = new ProfissionalDAO();
    }

    public void inserir(Profissional profissional){
        validarProfissionalNulo(profissional);

        validarNomeObrigatorio(profissional);

        profissional.setNome(
                profissional.getNome()
                        .trim()
                        .replaceAll("\\s+", " ")
        );
        validarTamanhoMinimoNome(profissional);
        validarTamanhoMaximoNome(profissional);
        validarNomeContemNumeros(profissional);

        validarTipoObrigatorio(profissional);

        profissionalDAO.inserir(profissional);
    }

    public void validarProfissionalNulo(Profissional profissional){
        if(profissional == null){
            throw new IllegalArgumentException(
                    "Profissional não pode ser nulo."
            );
        }
    }

    public void validarNomeObrigatorio(Profissional profissional){
        if(profissional.getNome() == null || profissional.getNome().isBlank()){
            throw new IllegalArgumentException("Nome é obrigatória.");
        }
    }

    public void validarTamanhoMinimoNome(Profissional profissional){
        String nome = profissional.getNome().trim();
        if(nome.length() < TAMANHO_MINIMO_NOME){
            throw new IllegalArgumentException("O nome deve ser maior que " + TAMANHO_MINIMO_NOME + " caracteres");
        }
    }

    public void validarTamanhoMaximoNome(Profissional profissional){
        String nome = profissional.getNome().trim();
        if(nome.length() > TAMANHO_MAXIMO_NOME){
            throw new IllegalArgumentException("O nome deve ter no máximo " + TAMANHO_MAXIMO_NOME + " caracteres");
        }
    }

    public void validarTipoObrigatorio(Profissional profissional){
        if(profissional.getTipo() == null){
            throw new IllegalArgumentException("Profissão é obrigatória.");
        }
    }

    public void validarNomeContemNumeros(Profissional profissional){
        if(profissional.getNome().matches(".*\\d.*")){
            throw new IllegalArgumentException("O campo Nome deve conter apelas letras.");
        }
    }

}
