package br.com.sigec.controller;

import br.com.sigec.model.Sentenciado;
import br.com.sigec.service.SentenciadoService;
import javafx.fxml.FXML;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import javafx.stage.Stage;

public class TelaCadastroSentenciadoController {
    @FXML
    private TextField txtMatricula;

    @FXML
    private TextField txtNome;

    @FXML
    private Button btnCancelar;

    @FXML
    private Button btnSalvar;

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
            SentenciadoService sentenciadoService = new SentenciadoService();
            sentenciadoService.inserir(new Sentenciado(matricula, nome));

            Stage stage = (Stage) btnSalvar.getScene().getWindow();
            stage.close();

        } catch (IllegalArgumentException e) {

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Dados inválidos");
            alert.setHeaderText("Não foi possível cadastrar o sentenciado");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
}
