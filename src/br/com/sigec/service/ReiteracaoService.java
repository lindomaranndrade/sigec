package br.com.sigec.service;

import br.com.sigec.dao.ReiteracaoDAO;
import br.com.sigec.model.Reiteracao;
import br.com.sigec.model.StatusPedidoExame;

import java.time.LocalDate;

public class ReiteracaoService {
    private static final int TAMANHO_MAXIMO_OBSERVACAO = 100;

    private ReiteracaoDAO reiteracaoDAO;

    public ReiteracaoService(){
        reiteracaoDAO = new ReiteracaoDAO();
    }

    public void inserir(Reiteracao reiteracao){
        validarReiteracaoNula(reiteracao);

        validarPedidoExameNulo(reiteracao);
        validarPedidoPermiteReiteracao(reiteracao);

        validarObservacao(reiteracao);

        validarDataReiteracao(reiteracao);
        validarDataReiteracaoFutura(reiteracao);
        validarDataReiteracaoAnteriorPedido(reiteracao);

        validarUsuarioNulo(reiteracao);
        validarUsuarioAtivo(reiteracao);

        reiteracaoDAO.inserir(reiteracao);
    }

    public void atualizar(Reiteracao reiteracao){

        validarReiteracaoNula(reiteracao);
        validarIdValido(reiteracao);

        validarPedidoExameNulo(reiteracao);
        validarPedidoPermiteReiteracao(reiteracao);

        validarObservacao(reiteracao);

        validarDataReiteracao(reiteracao);
        validarDataReiteracaoFutura(reiteracao);
        validarDataReiteracaoAnteriorPedido(reiteracao);

        validarUsuarioNulo(reiteracao);
        validarUsuarioAtivo(reiteracao);

        reiteracaoDAO.atualizar(reiteracao);
    }

    private void validarIdValido(Reiteracao reiteracao){
        if(reiteracao.getId() <= 0){
            throw new IllegalArgumentException(
                    "ID invalido"
            );
        }
    }

    private void validarReiteracaoNula(Reiteracao reiteracao){
        if(reiteracao == null){
            throw new IllegalArgumentException(
                    "Reiteracao não pode ser nula"
            );
        }
    }

    private void validarPedidoExameNulo(Reiteracao reiteracao){
        if(reiteracao.getPedido() == null){
            throw new IllegalArgumentException(
                    "Pedido de exame não pode ser nulo"
            );
        }
    }

    private void validarDataReiteracao(Reiteracao reiteracao){
        if(reiteracao.getDataReiteracao() == null){
            throw new IllegalArgumentException(
                    "Informe a data da reiteração"
            );
        }
    }

    private void validarDataReiteracaoFutura(Reiteracao reiteracao){
        LocalDate hoje = LocalDate.now();
        if(reiteracao.getDataReiteracao().isAfter(hoje)){
            throw new IllegalArgumentException(
                    "A data da reiteração não pode ser futura"
            );
        }
    }

    private void validarObservacao(Reiteracao reiteracao){
        if(reiteracao.getObservacoes() == null){
            return;
        }

        if(reiteracao.getObservacoes().length() > TAMANHO_MAXIMO_OBSERVACAO){
            throw new IllegalArgumentException(
                    "Tamanho do campo observação não pode exceder " + TAMANHO_MAXIMO_OBSERVACAO + " caracteres"
            );
        }
    }

    private void validarUsuarioNulo(Reiteracao reiteracao){
        if(reiteracao.getUsuario() == null){
            throw new IllegalArgumentException(
                    "Usuario não informado"
            );
        }
    }

    private void validarUsuarioAtivo(Reiteracao reiteracao){
        if(!reiteracao.getUsuario().isAtivo()){
            throw new IllegalArgumentException(
                    "Usuario inativo."
            );
        }
    }

    private void validarPedidoPermiteReiteracao(Reiteracao reiteracao){
        if(reiteracao.getPedido().getStatus() == StatusPedidoExame.CANCELADO){
            throw new IllegalArgumentException(
                    "O Pedido de Exame esta cancelado."
            );
        }

        if(reiteracao.getPedido().getStatus() == StatusPedidoExame.CONCLUIDO){
            throw new IllegalArgumentException(
                    "O Pedido de Exame ja foi concluido"
            );
        }

        if(reiteracao.getPedido().getStatus() == StatusPedidoExame.TRANSFERIDO){
            throw  new IllegalArgumentException(
                    "O sentenciado foi transferido"
            );
        }

    }

    private void validarDataReiteracaoAnteriorPedido(Reiteracao reiteracao){
        if(reiteracao.getPedido().getDataSolicitacao().isAfter(reiteracao.getDataReiteracao())){
            throw new IllegalArgumentException(
                    "A data da reiteração não pode ser anterior à data da solicitação do exame."
            );
        }
    }
}
