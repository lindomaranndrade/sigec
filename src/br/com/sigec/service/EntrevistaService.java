package br.com.sigec.service;

import br.com.sigec.dao.EntrevistaDAO;
import br.com.sigec.model.Entrevista;
import br.com.sigec.model.PedidoExame;
import br.com.sigec.model.Profissional;
import br.com.sigec.model.StatusEntrevista;
import br.com.sigec.model.StatusPedidoExame;
import br.com.sigec.model.TipoProfissional;
import br.com.sigec.model.Usuario;
import br.com.sigec.session.SessaoUsuario;

import java.time.LocalDate;

public class EntrevistaService {
    private EntrevistaDAO entrevistaDAO;

    public EntrevistaService(){
        entrevistaDAO = new EntrevistaDAO();
    }

    public Entrevista criarPendente(PedidoExame pedidoExame, TipoProfissional tipoAtendimento){

        validarPedidoExame(pedidoExame);
        validarStatusPedidoExame(pedidoExame);
        validarTipoAtendimento(tipoAtendimento);

        Usuario usuarioLogado = SessaoUsuario.getUsuarioLogado();
        validarUsuario(usuarioLogado);
        validarUsuarioAtivo(usuarioLogado);

        Entrevista entrevista = new Entrevista(pedidoExame, tipoAtendimento);

        entrevistaDAO.inserir(entrevista);

        return entrevista;
    }

    public void agendar(int idEntrevista, Profissional profissional, LocalDate dataAgendamento){

        Entrevista entrevista = entrevistaDAO.buscarPorId(idEntrevista);
        validarEntrevistaExiste(entrevista);

        validarStatusPermiteAgendar(entrevista);

        validarProfissional(profissional);
        validarProfissionalAtivo(profissional);
        validarProfissionalCompativel(entrevista, profissional);

        validarDataAgendamento(entrevista, dataAgendamento);

        entrevistaDAO.agendar(idEntrevista, profissional.getId(), dataAgendamento);
    }

    public void registrarRealizacao(int idEntrevista, LocalDate dataRealizacao, LocalDate dataEntregaLaudo){

        Entrevista entrevista = entrevistaDAO.buscarPorId(idEntrevista);
        validarEntrevistaExiste(entrevista);

        validarStatusPermiteRegistrarRealizacao(entrevista);

        validarDataRealizacao(entrevista, dataRealizacao);
        validarDataEntregaLaudo(dataRealizacao, dataEntregaLaudo);

        entrevistaDAO.registrarRealizacao(idEntrevista, dataRealizacao, dataEntregaLaudo);
    }

    public void cancelar(int idEntrevista){

        Entrevista entrevista = entrevistaDAO.buscarPorId(idEntrevista);
        validarEntrevistaExiste(entrevista);

        validarStatusPermiteCancelar(entrevista);

        entrevistaDAO.cancelar(idEntrevista);
    }

    private void validarPedidoExame(PedidoExame pedidoExame){
        if(pedidoExame == null){
            throw new IllegalArgumentException(
                    "O pedido de exame é obrigatorio"
            );
        }
    }

    private void validarTipoAtendimento(TipoProfissional tipoAtendimento){
        if(tipoAtendimento == null){
            throw new IllegalArgumentException(
                    "O tipo de atendimento é obrigatório"
            );
        }
    }

    private void validarProfissional(Profissional profissional){
        if(profissional == null){
            throw new IllegalArgumentException(
                    "Profissional é obrigatorio"
            );
        }
    }

    private void validarProfissionalAtivo(Profissional profissional){
        if(!profissional.isAtivo()){
            throw new IllegalArgumentException(
                    "Profissional inativo"
            );
        }
    }

    private void validarProfissionalCompativel(Entrevista entrevista, Profissional profissional){
        if(profissional.getTipo() != entrevista.getTipoAtendimento()){
            throw new IllegalArgumentException(
                    "O tipo do profissional não corresponde ao tipo de atendimento da entrevista"
            );
        }
    }

    private void validarDataAgendamento(Entrevista entrevista, LocalDate dataAgendamento){
        if(dataAgendamento == null){
            throw new IllegalArgumentException(
                    "Data do agendamento é obrigatória"
            );
        }

        LocalDate dataPedido = entrevista.getPedidoExame().getDataSolicitacao();

        if(dataAgendamento.isBefore(dataPedido)){
            throw new IllegalArgumentException(
                    "Data do agendamento não pode ser anterior a data do pedido"
            );
        }
    }

    private void validarDataRealizacao(Entrevista entrevista, LocalDate dataRealizacao){
        if(dataRealizacao == null){
            throw new IllegalArgumentException(
                    "Data da realização é obrigatória"
            );
        }

        if(dataRealizacao.isAfter(LocalDate.now())){
            throw new IllegalArgumentException(
                    "A data da realização não pode ser futura"
            );
        }

        if(dataRealizacao.isBefore(entrevista.getDataAgendamento())){
            throw new IllegalArgumentException(
                    "A data da realização não pode ser anterior a data do agendamento"
            );
        }
    }

    private void validarDataEntregaLaudo(LocalDate dataRealizacao, LocalDate dataEntregaLaudo){
        if(dataEntregaLaudo == null){
            return;
        }

        if(dataEntregaLaudo.isBefore(dataRealizacao)){
            throw new IllegalArgumentException(
                    "A data de entrega do laudo não pode ser anterior a data da realização"
            );
        }
    }

    private void validarUsuario(Usuario usuario){
        if(usuario == null){
            throw new IllegalArgumentException(
                    "Usuário é obrigatório"
            );
        }
    }

    private void validarUsuarioAtivo(Usuario usuario){
        if(!usuario.isAtivo()){
            throw new IllegalArgumentException(
                    "Usuario inativo"
            );
        }
    }

    private void validarStatusPedidoExame(PedidoExame pedidoExame){

        StatusPedidoExame status = pedidoExame.getStatus();

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

    private void validarEntrevistaExiste(Entrevista entrevista){
        if(entrevista == null){
            throw new IllegalArgumentException(
                    "Entrevista não encontrada"
            );
        }
    }

    private void validarStatusPermiteAgendar(Entrevista entrevista){
        StatusEntrevista status = entrevista.getStatus();

        if(status != StatusEntrevista.PENDENTE_AGENDAMENTO && status != StatusEntrevista.AGENDADA){
            throw new IllegalArgumentException(
                    "Entrevista não pode ser agendada no status atual"
            );
        }
    }

    private void validarStatusPermiteRegistrarRealizacao(Entrevista entrevista){
        if(entrevista.getStatus() != StatusEntrevista.AGENDADA){
            throw new IllegalArgumentException(
                    "Entrevista precisa estar agendada para registrar a realização"
            );
        }
    }

    private void validarStatusPermiteCancelar(Entrevista entrevista){
        StatusEntrevista status = entrevista.getStatus();

        if(status != StatusEntrevista.PENDENTE_AGENDAMENTO && status != StatusEntrevista.AGENDADA){
            throw new IllegalArgumentException(
                    "Entrevista não pode ser cancelada no status atual"
            );
        }
    }
}
