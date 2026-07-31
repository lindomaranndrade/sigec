package br.com.sigec.service;

import br.com.sigec.dao.SentenciadoDAO;
import br.com.sigec.model.Sentenciado;

public class SentenciadoService {
    private SentenciadoDAO sentenciadoDAO;
    private static final int TAMANHO_MINIMO_NOME = 3;
    private static final int TAMANHO_MAXIMO_NOME = 100;
    private static final int TAMANHO_MINIMO_MATRICULA= 5;
    private static final int TAMANHO_MAXIMO_MATRICULA= 7;


    public SentenciadoService(){
        this.sentenciadoDAO = new SentenciadoDAO();
    }

    public void inserir(Sentenciado sentenciado){

        validarSentenciadoNulo(sentenciado);

        validarNomeObrigatorio(sentenciado);

        sentenciado.setNome(
                sentenciado.getNome()
                        .trim()
                        .replaceAll("\\s+", " ")
        );

        validarTamanhoMinimoNome(sentenciado);
        validarTamanhoMaximoNome(sentenciado);

        validarMatriculaObrigatoria(sentenciado);

        sentenciado.setMatricula(normalizarMatricula(sentenciado.getMatricula()));

        validarMatriculaSomenteNumeros(sentenciado);
        validarTamanhoMinimoMatricula(sentenciado);
        validarTamanhoMaximoMatricula(sentenciado);
        validarMatriculaDuplicada(sentenciado);

        sentenciadoDAO.inserir(sentenciado);
    }

    public void validarSentenciadoNulo(Sentenciado sentenciado){
        if(sentenciado == null){
            throw new IllegalArgumentException(
                    "Sentenciado não pode ser nulo."
            );
        }
    }

    public void validarNomeObrigatorio(Sentenciado sentenciado){
        if(sentenciado.getNome() == null || sentenciado.getNome().isBlank()){
            throw new IllegalArgumentException(
                    "Nome é obrigatória."
            );
        }
    }

    public void validarTamanhoMinimoNome(Sentenciado sentenciado){
        if(sentenciado.getNome().length() < TAMANHO_MINIMO_NOME){
            throw new IllegalArgumentException(
                    "O nome deve ser maior que " + TAMANHO_MINIMO_NOME + " caracteres"
            );
        }
    }

    public void validarTamanhoMaximoNome( Sentenciado sentenciado){
        if(sentenciado.getNome().length() > TAMANHO_MAXIMO_NOME){
            throw new IllegalArgumentException(
                    "O nome deve ter no máximo " + TAMANHO_MAXIMO_NOME + " caracteres"
            );
        }
    }

    public void validarMatriculaObrigatoria(Sentenciado sentenciado){
        if(sentenciado.getMatricula() == null || sentenciado.getMatricula().isBlank()){
            throw new IllegalArgumentException(
                    "Matrícula é obrigatoria."
            );
        }
    }

    private void validarMatriculaSomenteNumeros(Sentenciado sentenciado){

        if(!sentenciado.getMatricula().matches("\\d+")){
            throw new IllegalArgumentException(
                    "Matrícula deve conter apenas números."
            );
        }
    }

    public void validarTamanhoMinimoMatricula(Sentenciado sentenciado){
        if(sentenciado.getMatricula().length() < TAMANHO_MINIMO_MATRICULA){
            throw new IllegalArgumentException(
                    "Tamanho da matricula não deve ser inferior a " + TAMANHO_MINIMO_MATRICULA + "numeros"
            );
        }
    }

    public void validarTamanhoMaximoMatricula(Sentenciado sentenciado){
        if(sentenciado.getMatricula().length() > TAMANHO_MAXIMO_MATRICULA){
            throw new IllegalArgumentException(
                    "Tamanho da matrícula não deve ser superior a " + TAMANHO_MAXIMO_MATRICULA + "mumeros"
            );
        }
    }

    public void validarMatriculaDuplicada(Sentenciado sentenciado){

        Sentenciado existente = sentenciadoDAO.buscarPorMatricula(sentenciado.getMatricula());

        if(existente != null){
            throw new IllegalArgumentException(
                    "Já existe um sentenciado com essa matrícula."
            );
        }
    }

    private String normalizarMatricula(String matricula){
        String matriculaNormalizada = matricula
                .replace(".", "")
                .replace("-", "")
                .trim()
                .replaceFirst("^0+", "");

        if(matriculaNormalizada.isEmpty()){
            throw new IllegalArgumentException(
                    "Matrícula inválida."
            );
        }

        return matriculaNormalizada;
    }

    public void atualizarNome(Sentenciado sentenciado){
        validarSentenciadoNulo(sentenciado);
        validarIdValido(sentenciado);
        validarNomeObrigatorio(sentenciado);

        sentenciado.setNome(sentenciado.getNome().trim().replaceAll("\\s+", " "));

        validarTamanhoMinimoNome(sentenciado);
        validarTamanhoMaximoNome(sentenciado);

        sentenciadoDAO.atualizarNome(sentenciado);
    }

    private void validarIdValido(Sentenciado sentenciado){
        if(sentenciado.getId() <= 0){
            throw new IllegalArgumentException(
                    "ID inválido."
            );
        }
    }

}
