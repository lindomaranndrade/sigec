package br.com.sigec.controller;

import br.com.sigec.model.Entrevista;
import br.com.sigec.model.TipoProfissional;
import br.com.sigec.service.EntrevistaService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TelaRegistrarRealizacaoController {

    private static final DateTimeFormatter FORMATO_DATA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML private Label lblResumoSentenciado;
    @FXML private Label lblResumoTipoAtendimento;
    @FXML private Label lblResumoProfissional;
    @FXML private Label lblResumoDataAgendada;
    @FXML private DatePicker dpDataRealizacao;
    @FXML private Label lblMensagemErro;
    @FXML private Button btnCancelar;
    @FXML private Button btnConfirmar;

    private final EntrevistaService entrevistaService = new EntrevistaService();

    private Entrevista entrevista;

    public void setEntrevista(Entrevista entrevista) {
        this.entrevista = entrevista;

        lblResumoSentenciado.setText(
                "Sentenciado: " + entrevista.getPedidoExame().getSentenciado().getNome()
        );
        lblResumoTipoAtendimento.setText(
                "Tipo de Atendimento: " + formatarTipo(entrevista.getTipoAtendimento())
        );
        lblResumoProfissional.setText(
                "Profissional: " + (entrevista.getProfissional() != null
                        ? entrevista.getProfissional().getNome()
                        : "-")
        );
        lblResumoDataAgendada.setText(
                "Data Agendada: " + (entrevista.getDataAgendamento() != null
                        ? entrevista.getDataAgendamento().format(FORMATO_DATA)
                        : "-")
        );
    }

    private String formatarTipo(TipoProfissional tipo) {
        return switch (tipo) {
            case PSICOLOGO -> "Psicólogo";
            case ASSISTENTE_SOCIAL -> "Assistente Social";
        };
    }

    @FXML
    private void confirmar() {

        esconderErro();

        LocalDate dataRealizacao = dpDataRealizacao.getValue();

        try {
            entrevistaService.registrarRealizacao(entrevista, dataRealizacao);
            fechar();
        } catch (IllegalArgumentException e) {
            mostrarErro(e.getMessage());
        }
    }

    @FXML
    private void cancelar() {
        fechar();
    }

    private void mostrarErro(String mensagem) {
        lblMensagemErro.setText(mensagem);
        lblMensagemErro.setVisible(true);
        lblMensagemErro.setManaged(true);
    }

    private void esconderErro() {
        lblMensagemErro.setVisible(false);
        lblMensagemErro.setManaged(false);
    }

    private void fechar() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }
}
