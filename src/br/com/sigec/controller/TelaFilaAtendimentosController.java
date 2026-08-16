package br.com.sigec.controller;

import br.com.sigec.model.Entrevista;
import br.com.sigec.model.StatusEntrevista;
import br.com.sigec.model.TipoProfissional;
import br.com.sigec.service.EntrevistaService;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class TelaFilaAtendimentosController {

    private static final DateTimeFormatter FORMATO_DATA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML private TextField txtMatricula;
    @FXML private ComboBox<StatusEntrevista> cbStatus;
    @FXML private Button btnPesquisar;
    @FXML private TableView<Entrevista> tabelaFila;
    @FXML private TableColumn<Entrevista, String> colMatricula;
    @FXML private TableColumn<Entrevista, String> colNome;
    @FXML private TableColumn<Entrevista, String> colTipoAtendimento;
    @FXML private TableColumn<Entrevista, String> colProfissional;
    @FXML private TableColumn<Entrevista, String> colDataAgendamento;
    @FXML private TableColumn<Entrevista, String> colStatus;
    @FXML private Button btnAgendar;
    @FXML private Button btnRegistrarRealizacao;
    @FXML private Button btnCancelar;

    private final EntrevistaService entrevistaService = new EntrevistaService();

    @FXML
    public void initialize() {

        cbStatus.setItems(FXCollections.observableArrayList(StatusEntrevista.values()));
        cbStatus.setConverter(new StringConverter<>() {
            @Override
            public String toString(StatusEntrevista status) {
                return formatarStatus(status);
            }

            @Override
            public StatusEntrevista fromString(String string) {
                return null;
            }
        });

        colMatricula.setCellValueFactory(
                cellData -> new SimpleStringProperty(
                        cellData.getValue().getPedidoExame().getSentenciado().getMatricula()
                )
        );

        colNome.setCellValueFactory(
                cellData -> new SimpleStringProperty(
                        cellData.getValue().getPedidoExame().getSentenciado().getNome()
                )
        );

        colTipoAtendimento.setCellValueFactory(
                cellData -> new SimpleStringProperty(
                        formatarTipo(cellData.getValue().getTipoAtendimento())
                )
        );

        colProfissional.setCellValueFactory(
                cellData -> new SimpleStringProperty(
                        cellData.getValue().getProfissional() != null
                                ? cellData.getValue().getProfissional().getNome()
                                : "Aguardando"
                )
        );

        colDataAgendamento.setCellValueFactory(
                cellData -> new SimpleObjectProperty<>(
                        cellData.getValue().getDataAgendamento() != null
                                ? cellData.getValue().getDataAgendamento().format(FORMATO_DATA)
                                : "-"
                )
        );

        colStatus.setCellValueFactory(
                cellData -> new SimpleStringProperty(
                        formatarStatus(cellData.getValue().getStatus())
                )
        );

        carregarFila();
    }

    @FXML
    private void pesquisar() {
        carregarFila();
    }

    private void carregarFila() {

        List<Entrevista> fila = entrevistaService.listarFila();

        String matriculaFiltro = txtMatricula.getText() != null
                ? txtMatricula.getText().trim().toLowerCase()
                : "";
        StatusEntrevista statusFiltro = cbStatus.getValue();

        List<Entrevista> filtradas = fila.stream()
                .filter(e -> matriculaFiltro.isEmpty()
                        || e.getPedidoExame().getSentenciado().getMatricula().toLowerCase().contains(matriculaFiltro))
                .filter(e -> statusFiltro == null || e.getStatus() == statusFiltro)
                .toList();

        ObservableList<Entrevista> dados = FXCollections.observableArrayList(filtradas);
        tabelaFila.setItems(dados);
    }

    private String formatarTipo(TipoProfissional tipo) {
        if (tipo == null) {
            return "";
        }
        return switch (tipo) {
            case PSICOLOGO -> "Psicólogo";
            case ASSISTENTE_SOCIAL -> "Assistente Social";
        };
    }

    private String formatarStatus(StatusEntrevista status) {
        if (status == null) {
            return "";
        }
        return switch (status) {
            case PENDENTE_AGENDAMENTO -> "Aguardando agendamento";
            case AGENDADA -> "Agendada";
            case REALIZADA -> "Realizada";
            case CANCELADA -> "Cancelada";
        };
    }

    @FXML
    private void agendar() {
        Entrevista selecionada = tabelaFila.getSelectionModel().getSelectedItem();
        if (selecionada == null) {
            mostrarAviso("Selecione um atendimento para agendar.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/br/com/sigec/view/telaAgendarAtendimento.fxml"));
            Parent root = loader.load();

            TelaAgendarAtendimentoController controller = loader.getController();
            controller.setEntrevista(selecionada);

            Stage stage = new Stage();
            stage.initOwner(btnAgendar.getScene().getWindow());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Agendar Atendimento");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.showAndWait();

            carregarFila();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void registrarRealizacao() {
        Entrevista selecionada = tabelaFila.getSelectionModel().getSelectedItem();
        if (selecionada == null) {
            mostrarAviso("Selecione um atendimento para registrar a realização.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/br/com/sigec/view/telaRegistrarRealizacao.fxml"));
            Parent root = loader.load();

            TelaRegistrarRealizacaoController controller = loader.getController();
            controller.setEntrevista(selecionada);

            Stage stage = new Stage();
            stage.initOwner(btnRegistrarRealizacao.getScene().getWindow());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Registrar Realização");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.showAndWait();

            carregarFila();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void cancelar() {
        Entrevista selecionada = tabelaFila.getSelectionModel().getSelectedItem();
        if (selecionada == null) {
            mostrarAviso("Selecione um atendimento para cancelar o agendamento.");
            return;
        }

        Alert confirmacao = new Alert(
                Alert.AlertType.CONFIRMATION,
                "Tem certeza que deseja cancelar este agendamento?",
                ButtonType.YES, ButtonType.NO
        );
        confirmacao.setTitle("Cancelar Agendamento");
        confirmacao.setHeaderText(null);

        Optional<ButtonType> resposta = confirmacao.showAndWait();

        if (resposta.isPresent() && resposta.get() == ButtonType.YES) {
            try {
                entrevistaService.cancelarAgendamento(selecionada);
                carregarFila();
            } catch (IllegalArgumentException e) {
                Alert erro = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
                erro.setTitle("Não foi possível cancelar");
                erro.setHeaderText(null);
                erro.showAndWait();
            }
        }
    }

    private void mostrarAviso(String mensagem) {
        Alert alerta = new Alert(Alert.AlertType.WARNING, mensagem, ButtonType.OK);
        alerta.setTitle("Aviso");
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }
}
