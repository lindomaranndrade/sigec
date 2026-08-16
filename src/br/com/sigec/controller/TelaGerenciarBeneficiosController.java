package br.com.sigec.controller;

import br.com.sigec.model.Beneficio;
import br.com.sigec.service.BeneficioService;
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

public class TelaGerenciarBeneficiosController {

    @FXML private TextField txtBusca;
    @FXML private Button btnPesquisar;
    @FXML private TableView<Beneficio> tabelaBeneficios;
    @FXML private TableColumn<Beneficio, String> colDescricao;
    @FXML private TableColumn<Beneficio, String> colSigla;
    @FXML private Button btnNovo;
    @FXML private Button btnEditar;

    private final BeneficioService beneficioService = new BeneficioService();

    @FXML
    public void initialize() {

        colDescricao.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().getDescricao())
        );

        colSigla.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().getSigla())
        );

        carregarBeneficios();
    }

    @FXML
    private void pesquisar() {
        carregarBeneficios();
    }

    private void carregarBeneficios() {

        List<Beneficio> beneficios = beneficioService.listarTodos();

        String busca = txtBusca.getText() != null
                ? txtBusca.getText().trim().toLowerCase()
                : "";

        List<Beneficio> filtrados = beneficios.stream()
                .filter(b -> busca.isEmpty()
                        || b.getDescricao().toLowerCase().contains(busca)
                        || (b.getSigla() != null && b.getSigla().toLowerCase().contains(busca)))
                .toList();

        ObservableList<Beneficio> dados = FXCollections.observableArrayList(filtrados);
        tabelaBeneficios.setItems(dados);
    }

    @FXML
    private void novoBeneficio() {
        abrirCadastro(null);
    }

    @FXML
    private void editarBeneficio() {
        Beneficio selecionado = tabelaBeneficios.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            mostrarAviso("Selecione um benefício para editar.");
            return;
        }
        abrirCadastro(selecionado);
    }

    private void abrirCadastro(Beneficio beneficio) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/br/com/sigec/view/telaCadastroBeneficio.fxml")
            );
            Parent root = loader.load();

            if (beneficio != null) {
                TelaCadastroBeneficioController controller = loader.getController();
                controller.setBeneficio(beneficio);
            }

            Stage stage = new Stage();
            stage.initOwner(btnNovo.getScene().getWindow());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(beneficio == null ? "Novo Benefício" : "Editar Benefício");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.showAndWait();

            carregarBeneficios();

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
