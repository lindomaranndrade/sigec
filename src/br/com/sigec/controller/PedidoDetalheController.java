package br.com.sigec.controller;

import br.com.sigec.model.PedidoExame;
import br.com.sigec.model.Sentenciado;
import br.com.sigec.model.Usuario;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class PedidoDetalheController {

    @FXML private Label lblMatricula;
    @FXML private Label lblNomeSentenciado;
    @FXML private Label lblNumeroProcesso;
    @FXML private Label lblNumeroSei;
    @FXML private Label lblStatus;
    @FXML private Label lblDataCadastro;
    @FXML private Label lblDataSolicitacao;
    @FXML private Label lblDataConclusao;
    @FXML private Label lblUsuario;

    private static final DateTimeFormatter DATA_BR = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public void setPedido(PedidoExame pedido) {
        Sentenciado sentenciado = pedido.getSentenciado();
        Usuario usuario = pedido.getUsuario();

        lblMatricula.setText(sentenciado == null ? "" : texto(sentenciado.getMatricula()));
        lblNomeSentenciado.setText(sentenciado == null ? "" : texto(sentenciado.getNome()));
        lblNumeroProcesso.setText(texto(pedido.getNumeroProcesso()));
        lblNumeroSei.setText(texto(pedido.getNumeroSEI()));
        lblStatus.setText(pedido.getStatus() == null ? "" : pedido.getStatus().name());
        lblDataCadastro.setText(formatarData(pedido.getDataCadastro()));
        lblDataSolicitacao.setText(formatarData(pedido.getDataSolicitacao()));
        lblDataConclusao.setText(formatarData(pedido.getDataConclusao()));
        lblUsuario.setText(usuario == null ? "" : texto(usuario.getLogin()));
    }

    @FXML
    private void handleFechar() {
        ((Stage) lblMatricula.getScene().getWindow()).close();
    }

    private String formatarData(LocalDate data) {
        return data == null ? "" : data.format(DATA_BR);
    }

    private String texto(String valor) {
        return valor == null ? "" : valor;
    }
}
