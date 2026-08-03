package br.com.sigec.service;

import br.com.sigec.dao.EntrevistaDAO;
import br.com.sigec.model.Entrevista;
import br.com.sigec.model.StatusPedidoExame;
import br.com.sigec.model.TipoProfissional;

import java.time.LocalDate;

public class EntrevistaService {
    private EntrevistaDAO entrevistaDAO;

    public EntrevistaService(){
        entrevistaDAO = new EntrevistaDAO();
    }


    public void inserir(Entrevista entrevista){

        validarEntrevistaParaPersistencia(entrevista);

        entrevistaDAO.inserir(entrevista);
    }


    public void atualizar(Entrevista entrevista){

        validarEntrevistaParaPersistencia(entrevista);
        validarId(entrevista);

        entrevistaDAO.atualizar(entrevista);
    }

    private void validarEntrevista(Entrevista entrevista){
        if(entrevista == null){
            throw new IllegalArgumentException(
                    "Entrevista não pode ser nula"
            );
        }
    }

    private void validarPedidoExame( Entrevista entrevista){
        if(entrevista.getPedidoExame() == null){
            throw new IllegalArgumentException(
                    "O pedido de exame é obrigatorio"
            );
        }
    }
    private void validarProfissional(Entrevista entrevista){
        if(entrevista.getProfissional() == null){
            throw new IllegalArgumentException(
                    "Profissional é obrigatorio"
            );
        }
    }

    private void validarDataEntrevistaObrigatoria(
            Entrevista entrevista){

        if(entrevista.getDataEntrevista() == null){
            throw new IllegalArgumentException(
                    "Data da entrevista é obrigatória"
            );
        }
    }

    private void validarDataEntrevista(Entrevista entrevista){
        LocalDate hoje = LocalDate.now();
        LocalDate dataPedido = entrevista.getPedidoExame().getDataSolicitacao();

        if(entrevista.getDataEntrevista().isBefore(dataPedido)){
            throw new IllegalArgumentException(
                    "Data da entrevista não pode ser anterior a data do pedido"
            );
        }

        if(entrevista.getDataEntrevista().isAfter(hoje)){
            throw new IllegalArgumentException(
                    "A data da entrevista não pode ser futura"
            );
        }
    }

    private void validarUsuario(Entrevista entrevista){
        if(entrevista.getUsuario() == null){
            throw new IllegalArgumentException(
                    "Usuário é obrigatório"
            );
        }
    }

    private void validarUsuarioAtivo(Entrevista entrevista){
        if(!entrevista.getUsuario().isAtivo()){
            throw new IllegalArgumentException(
                    "Usuario inativo"
            );
        }
    }

    private void validarStatusPedidoExame(Entrevista entrevista){

        StatusPedidoExame status =
                entrevista.getPedidoExame().getStatus();

        if(status == StatusPedidoExame.CANCELADO){
            throw new IllegalArgumentException(
                    "O pedido do exame foi cancelado"
            );
        }

        if(status == StatusPedidoExame.CONCLUIDO){
            throw new IllegalArgumentException(
                    "Entrevista não pode ser realizada pois o pedido foi concluído"
            );
        }

        if(status == StatusPedidoExame.TRANSFERIDO){
            throw new IllegalArgumentException(
                    "Entrevista não pode ser realizada pois o sentenciado foi transferido"
            );
        }
    }

    private void validarId(Entrevista entrevista){
        if(entrevista.getId() <= 0){
            throw new IllegalArgumentException(
                    "Id invalido"
            );
        }
    }

    private void permiteAtualizar(Entrevista entrevista){
        validarEntrevista(entrevista);
        validarId(entrevista);

        validarPedidoExame(entrevista);
        validarStatusPedidoExame(entrevista);
        validarProfissional(entrevista);

        validarUsuario(entrevista);
        validarUsuarioAtivo(entrevista);

        validarDataEntrevistaObrigatoria(entrevista);
        validarDataEntrevista(entrevista);

    }

    private void validarEntrevistaParaPersistencia(
            Entrevista entrevista){

        validarEntrevista(entrevista);

        validarPedidoExame(entrevista);
        validarStatusPedidoExame(entrevista);
        validarProfissional(entrevista);

        validarUsuario(entrevista);
        validarUsuarioAtivo(entrevista);

        validarDataEntrevistaObrigatoria(entrevista);
        validarDataEntrevista(entrevista);
    }
}
