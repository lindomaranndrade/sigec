package br.com.sigec.controller;

import br.com.sigec.model.Sentenciado;
import br.com.sigec.service.SentenciadoService;
import javafx.fxml.FXML;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import javafx.stage.Stage;

public class TelaCadastroSentenciadoController {
    @FXML
    private Label lblTitulo;

    @FXML
    private TextField txtMatricula;

    @FXML
    private TextField txtNome;

    @FXML
    private Button btnCancelar;

    @FXML
    private Button btnSalvar;

    private final SentenciadoService sentenciadoService = new SentenciadoService();

    private Sentenciado sentenciado;
    private boolean salvo = false;

    public void setSentenciado(Sentenciado sentenciado) {
        this.sentenciado = sentenciado;

        lblTitulo.setText("Editar Sentenciado");
        txtMatricula.setText(sentenciado.getMatricula());
        txtMatricula.setDisable(true);
        txtNome.setText(sentenciado.getNome());
    }

    public boolean isSalvo() {
        return salvo;
    }

    @FXML
    public void cancelar(){
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void salvar(){
        String matricula = txtMatricula.getText();
        String nome = txtNome.getText();

        try {
            if (sentenciado == null) {
                sentenciadoService.inserir(new Sentenciado(matricula, nome));
            } else {
                sentenciado.setNome(nome);
                sentenciadoService.atualizarNome(sentenciado);
            }

            salvo = true;

            Stage stage = (Stage) btnSalvar.getScene().getWindow();
            stage.close();

        } catch (IllegalArgumentException e) {

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Dados inválidos");
            alert.setHeaderText("Não foi possível salvar o sentenciado");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
}
