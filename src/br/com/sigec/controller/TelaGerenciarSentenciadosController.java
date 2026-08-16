package br.com.sigec.controller;

import br.com.sigec.model.Sentenciado;
import br.com.sigec.service.SentenciadoService;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class TelaGerenciarSentenciadosController {

    @FXML private TextField txtBusca;
    @FXML private Button btnPesquisar;
    @FXML private TableView<Sentenciado> tabelaSentenciados;
    @FXML private TableColumn<Sentenciado, String> colMatricula;
    @FXML private TableColumn<Sentenciado, String> colNome;
    @FXML private Button btnNovo;
    @FXML private Button btnEditar;

    private final SentenciadoService sentenciadoService = new SentenciadoService();

    @FXML
    public void initialize() {

        colMatricula.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().getMatricula())
        );

        colNome.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().getNome())
        );

        carregarSentenciados();
    }

    @FXML
    private void pesquisar() {
        carregarSentenciados();
    }

    private void carregarSentenciados() {

        List<Sentenciado> sentenciados = sentenciadoService.listarTodos();

        String busca = txtBusca.getText() != null
                ? txtBusca.getText().trim().toLowerCase()
                : "";

        List<Sentenciado> filtrados = sentenciados.stream()
                .filter(s -> busca.isEmpty()
                        || s.getNome().toLowerCase().contains(busca)
                        || s.getMatricula().toLowerCase().contains(busca))
                .toList();

        ObservableList<Sentenciado> dados = FXCollections.observableArrayList(filtrados);
        tabelaSentenciados.setItems(dados);
    }

    @FXML
    private void novoSentenciado() {
        abrirCadastro(null);
    }

    @FXML
    private void editarSentenciado() {
        Sentenciado selecionado = tabelaSentenciados.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            mostrarAviso("Selecione um sentenciado para editar.");
            return;
        }
        abrirCadastro(selecionado);
    }

    private void abrirCadastro(Sentenciado sentenciado) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/br/com/sigec/view/telaCadastroSentenciado.fxml")
            );
            Parent root = loader.load();

            if (sentenciado != null) {
                TelaCadastroSentenciadoController controller = loader.getController();
                controller.setSentenciado(sentenciado);
            }

            Stage stage = new Stage();
            stage.initOwner(btnNovo.getScene().getWindow());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(sentenciado == null ? "Novo Sentenciado" : "Editar Sentenciado");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.showAndWait();

            carregarSentenciados();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void mostrarAviso(String mensagem) {
        Alert alerta = new Alert(Alert.AlertType.WARNING, mensagem, ButtonType.OK);
        alerta.setTitle("Aviso");
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }
}
