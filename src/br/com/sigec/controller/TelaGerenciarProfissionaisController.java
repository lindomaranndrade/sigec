package br.com.sigec.controller;

import br.com.sigec.model.Profissional;
import br.com.sigec.model.TipoProfissional;
import br.com.sigec.service.ProfissionalService;
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
import java.util.List;
import java.util.Optional;

public class TelaGerenciarProfissionaisController {

    private static final String FILTRO_ATIVO = "Ativo";
    private static final String FILTRO_INATIVO = "Inativo";

    @FXML private TextField txtNome;
    @FXML private ComboBox<TipoProfissional> cbFiltroTipo;
    @FXML private ComboBox<String> cbFiltroAtivo;
    @FXML private Button btnPesquisar;
    @FXML private TableView<Profissional> tabelaProfissionais;
    @FXML private TableColumn<Profissional, String> colNome;
    @FXML private TableColumn<Profissional, String> colTipo;
    @FXML private TableColumn<Profissional, String> colAtivo;
    @FXML private Button btnNovo;
    @FXML private Button btnEditar;
    @FXML private Button btnAlternarStatus;

    private final ProfissionalService profissionalService = new ProfissionalService();

    @FXML
    public void initialize() {

        cbFiltroTipo.setItems(FXCollections.observableArrayList(TipoProfissional.values()));
        cbFiltroTipo.setConverter(new StringConverter<>() {
            @Override
            public String toString(TipoProfissional tipo) {
                return formatarTipo(tipo);
            }

            @Override
            public TipoProfissional fromString(String string) {
                return null;
            }
        });

        cbFiltroAtivo.setItems(FXCollections.observableArrayList(FILTRO_ATIVO, FILTRO_INATIVO));

        colNome.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().getNome())
        );

        colTipo.setCellValueFactory(
                cellData -> new SimpleStringProperty(formatarTipo(cellData.getValue().getTipo()))
        );

        colAtivo.setCellValueFactory(
                cellData -> new SimpleStringProperty(
                        cellData.getValue().isAtivo() ? FILTRO_ATIVO : FILTRO_INATIVO
                )
        );

        carregarProfissionais();
    }

    @FXML
    private void pesquisar() {
        carregarProfissionais();
    }

    private void carregarProfissionais() {

        List<Profissional> profissionais = profissionalService.listarTodos();

        String nomeFiltro = txtNome.getText() != null
                ? txtNome.getText().trim().toLowerCase()
                : "";
        TipoProfissional tipoFiltro = cbFiltroTipo.getValue();
        String statusFiltro = cbFiltroAtivo.getValue();

        List<Profissional> filtrados = profissionais.stream()
                .filter(p -> nomeFiltro.isEmpty() || p.getNome().toLowerCase().contains(nomeFiltro))
                .filter(p -> tipoFiltro == null || p.getTipo() == tipoFiltro)
                .filter(p -> statusFiltro == null
                        || (FILTRO_ATIVO.equals(statusFiltro) && p.isAtivo())
                        || (FILTRO_INATIVO.equals(statusFiltro) && !p.isAtivo()))
                .toList();

        ObservableList<Profissional> dados = FXCollections.observableArrayList(filtrados);
        tabelaProfissionais.setItems(dados);
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

    @FXML
    private void novoProfissional() {
        abrirCadastro(null);
    }

    @FXML
    private void editarProfissional() {
        Profissional selecionado = tabelaProfissionais.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            mostrarAviso("Selecione um profissional para editar.");
            return;
        }
        abrirCadastro(selecionado);
    }

    private void abrirCadastro(Profissional profissional) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/br/com/sigec/view/telaCadastroProfissional.fxml")
            );
            Parent root = loader.load();

            if (profissional != null) {
                TelaCadastroProfissionalController controller = loader.getController();
                controller.setProfissional(profissional);
            }

            Stage stage = new Stage();
            stage.initOwner(btnNovo.getScene().getWindow());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(profissional == null ? "Novo Profissional" : "Editar Profissional");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.showAndWait();

            carregarProfissionais();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void alternarStatus() {
        Profissional selecionado = tabelaProfissionais.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            mostrarAviso("Selecione um profissional para ativar/inativar.");
            return;
        }

        String acao = selecionado.isAtivo() ? "inativar" : "ativar";

        Alert confirmacao = new Alert(
                Alert.AlertType.CONFIRMATION,
                "Tem certeza que deseja " + acao + " " + selecionado.getNome() + "?",
                ButtonType.YES, ButtonType.NO
        );
        confirmacao.setTitle("Alterar status");
        confirmacao.setHeaderText(null);

        Optional<ButtonType> resposta = confirmacao.showAndWait();

        if (resposta.isPresent() && resposta.get() == ButtonType.YES) {
            profissionalService.alternarStatus(selecionado);
            carregarProfissionais();
        }
    }

    private void mostrarAviso(String mensagem) {
        Alert alerta = new Alert(Alert.AlertType.WARNING, mensagem, ButtonType.OK);
        alerta.setTitle("Aviso");
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }
}
