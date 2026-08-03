package br.com.sigec.controller;

import br.com.sigec.dao.ProfissionalDAO;
import br.com.sigec.model.Entrevista;
import br.com.sigec.model.PedidoExame;
import br.com.sigec.model.Profissional;
import br.com.sigec.model.Sentenciado;
import br.com.sigec.model.Usuario;
import br.com.sigec.service.EntrevistaService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class EntrevistaController implements Initializable {

    @FXML private Label lblPedido;
    @FXML private Label lblSentenciado;
    @FXML private ComboBox<Profissional> cbProfissional;
    @FXML private DatePicker dpDataEntrevista;
    @FXML private DatePicker dpDataEntregaLaudo;

    private final ProfissionalDAO profissionalDAO = new ProfissionalDAO();
    private final EntrevistaService entrevistaService = new EntrevistaService();

    private PedidoExame pedido;
    private boolean salvoComSucesso;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarComboProfissional();
        carregarProfissionais();
        dpDataEntrevista.setValue(LocalDate.now());
    }

    public void setPedido(PedidoExame pedido) {
        this.pedido = pedido;
        Sentenciado sentenciado = pedido.getSentenciado();
        lblPedido.setText("Processo: " + texto(pedido.getNumeroProcesso()));
        lblSentenciado.setText(sentenciado == null ? "" : sentenciado.getMatricula() + " - " + sentenciado.getNome());
    }

    public boolean isSalvoComSucesso() {
        return salvoComSucesso;
    }

    @FXML
    private void handleSalvar() {
        Entrevista entrevista = new Entrevista(
                pedido,
                cbProfissional.getValue(),
                dpDataEntrevista.getValue(),
                usuarioTemporario()
        );
        entrevista.setDataEntregaLaudo(dpDataEntregaLaudo.getValue());

        try {
            entrevistaService.inserir(entrevista);
            salvoComSucesso = true;
            fechar();
        } catch (RuntimeException e) {
            exibirErro("Não foi possível registrar a entrevista: " + mensagemErro(e));
        }
    }

    @FXML
    private void handleCancelar() {
        salvoComSucesso = false;
        fechar();
    }

    private void carregarProfissionais() {
        try {
            List<Profissional> ativos = profissionalDAO.listarTodos().stream()
                    .filter(Profissional::isAtivo)
                    .toList();
            cbProfissional.setItems(FXCollections.observableArrayList(ativos));
        } catch (RuntimeException e) {
            exibirErro("Não foi possível carregar os profissionais: " + mensagemErro(e));
        }
    }

    private void configurarComboProfissional() {
        cbProfissional.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Profissional item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : formatarProfissional(item));
            }
        });
        cbProfissional.setCellFactory(lista -> new ListCell<>() {
            @Override
            protected void updateItem(Profissional item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : formatarProfissional(item));
            }
        });
    }

    private String formatarProfissional(Profissional profissional) {
        return profissional.getNome() + " (" + profissional.getTipo() + ")";
    }

    private void fechar() {
        ((Stage) lblPedido.getScene().getWindow()).close();
    }

    private void exibirErro(String mensagem) {
        Alert alerta = new Alert(AlertType.WARNING);
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }

    private String mensagemErro(Throwable erro) {
        Throwable causa = erro;
        while (causa.getCause() != null) {
            causa = causa.getCause();
        }
        return causa.getMessage() == null ? erro.getMessage() : causa.getMessage();
    }

    private String texto(String valor) {
        return valor == null ? "" : valor;
    }

    private Usuario usuarioTemporario() {
        Usuario usuario = new Usuario(1, "temporario");
        usuario.setAtivo(true);
        return usuario;
    }
}
