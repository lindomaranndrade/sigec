package br.com.sigec.controller;

import br.com.sigec.model.Sentenciado;
import br.com.sigec.service.SentenciadoService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class CadastroSentenciadoController {

    @FXML private TextField txtMatricula;
    @FXML private TextField txtNome;

    private final SentenciadoService sentenciadoService = new SentenciadoService();

    private Sentenciado sentenciadoCadastrado;
    private boolean salvoComSucesso;

    public void setMatriculaInicial(String matricula) {
        txtMatricula.setText(matricula);
        txtNome.requestFocus();
    }

    public Sentenciado getSentenciadoCadastrado() {
        return sentenciadoCadastrado;
    }

    public boolean isSalvoComSucesso() {
        return salvoComSucesso;
    }

    @FXML
    private void handleSalvar() {
        Sentenciado sentenciado = new Sentenciado(
                texto(txtMatricula),
                texto(txtNome)
        );

        try {
            sentenciadoService.inserir(sentenciado);
            sentenciadoCadastrado = sentenciado;
            salvoComSucesso = true;
            fechar();
        } catch (RuntimeException e) {
            exibirErro(mensagemErro(e));
        }
    }

    @FXML
    private void handleCancelar() {
        salvoComSucesso = false;
        fechar();
    }

    private String texto(TextField campo) {
        return campo.getText() == null ? "" : campo.getText().trim();
    }

    private void fechar() {
        ((Stage) txtMatricula.getScene().getWindow()).close();
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
}
