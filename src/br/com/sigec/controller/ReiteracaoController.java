package br.com.sigec.controller;

import br.com.sigec.model.PedidoExame;
import br.com.sigec.model.Reiteracao;
import br.com.sigec.model.Sentenciado;
import br.com.sigec.model.Usuario;
import br.com.sigec.service.ReiteracaoService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class ReiteracaoController implements Initializable {

    @FXML private Label lblPedido;
    @FXML private Label lblSentenciado;
    @FXML private DatePicker dpDataReiteracao;
    @FXML private TextArea txtObservacoes;
    @FXML private TextArea txtDespacho;

    private final ReiteracaoService reiteracaoService = new ReiteracaoService();

    private PedidoExame pedido;
    private boolean salvoComSucesso;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dpDataReiteracao.setValue(LocalDate.now());
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
        Reiteracao reiteracao = new Reiteracao(
                pedido,
                dpDataReiteracao.getValue(),
                textoFormulario(txtObservacoes),
                textoFormulario(txtDespacho),
                usuarioTemporario()
        );

        try {
            reiteracaoService.inserir(reiteracao);
            salvoComSucesso = true;
            fechar();
        } catch (RuntimeException e) {
            exibirErro("Não foi possível registrar a reiteração: " + mensagemErro(e));
        }
    }

    @FXML
    private void handleCancelar() {
        salvoComSucesso = false;
        fechar();
    }

    private String textoFormulario(TextArea campo) {
        return campo.getText() == null ? null : campo.getText().trim();
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
