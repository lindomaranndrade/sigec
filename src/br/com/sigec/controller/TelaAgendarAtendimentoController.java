package br.com.sigec.controller;

import br.com.sigec.model.Entrevista;
import br.com.sigec.model.Profissional;
import br.com.sigec.model.TipoProfissional;
import br.com.sigec.service.EntrevistaService;
import br.com.sigec.service.ProfissionalService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.util.List;

public class TelaAgendarAtendimentoController {

    @FXML private Label lblResumoSentenciado;
    @FXML private Label lblResumoTipoAtendimento;
    @FXML private ComboBox<Profissional> cbProfissional;
    @FXML private DatePicker dpDataAgendamento;
    @FXML private Label lblMensagemErro;
    @FXML private Button btnCancelar;
    @FXML private Button btnConfirmar;

    private final EntrevistaService entrevistaService = new EntrevistaService();

    // ATENÇÃO: estou assumindo que existe ProfissionalService.listarAtivosPorTipo(TipoProfissional).
    // Se o nome do método/classe for outro no seu projeto, é só ajustar esta linha.
    private final ProfissionalService profissionalService = new ProfissionalService();

    private Entrevista entrevista;

    public void setEntrevista(Entrevista entrevista) {
        this.entrevista = entrevista;

        lblResumoSentenciado.setText(
                "Sentenciado: " + entrevista.getPedidoExame().getSentenciado().getNome()
        );
        lblResumoTipoAtendimento.setText(
                "Tipo de Atendimento: " + formatarTipo(entrevista.getTipoAtendimento())
        );

        carregarProfissionais(entrevista.getTipoAtendimento());
    }

    @FXML
    public void initialize() {
        cbProfissional.setConverter(new StringConverter<>() {
            @Override
            public String toString(Profissional profissional) {
                return profissional != null ? profissional.getNome() : "";
            }

            @Override
            public Profissional fromString(String string) {
                return null;
            }
        });
    }

    private void carregarProfissionais(TipoProfissional tipo) {
        List<Profissional> profissionais = profissionalService.listarAtivosPorTipo(tipo);
        cbProfissional.setItems(FXCollections.observableArrayList(profissionais));
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

        Profissional profissional = cbProfissional.getValue();
        LocalDate data = dpDataAgendamento.getValue();

        try {
            entrevistaService.agendar(entrevista, profissional, data);
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
