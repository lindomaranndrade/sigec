package br.com.sigec.controller;

import br.com.sigec.model.PedidoExame;
import br.com.sigec.model.Sentenciado;
import br.com.sigec.service.PedidoExameService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ConcluirPedidoController {

    @FXML private Label lblPedido;
    @FXML private Label lblSentenciado;
    @FXML private TextField txtNumeroSei;

    private final PedidoExameService pedidoExameService = new PedidoExameService();

    private PedidoExame pedido;
    private boolean salvoComSucesso;

    public void setPedido(PedidoExame pedido) {
        this.pedido = pedido;
        Sentenciado sentenciado = pedido.getSentenciado();
        lblPedido.setText("Processo: " + texto(pedido.getNumeroProcesso()));
        lblSentenciado.setText(sentenciado == null ? "" : sentenciado.getMatricula() + " - " + sentenciado.getNome());
        txtNumeroSei.setText(texto(pedido.getNumeroSEI()));
    }

    public boolean isSalvoComSucesso() {
        return salvoComSucesso;
    }

    @FXML
    private void handleConcluir() {
        pedido.setNumeroSEI(txtNumeroSei.getText() == null ? "" : txtNumeroSei.getText().trim());

        try {
            pedidoExameService.concluirPedido(pedido);
            salvoComSucesso = true;
            fechar();
        } catch (RuntimeException e) {
            exibirErro("Não foi possível concluir o pedido: " + mensagemErro(e));
        }
    }

    @FXML
    private void handleCancelar() {
        salvoComSucesso = false;
        fechar();
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
}
