package br.com.sigec.controller;

import br.com.sigec.service.UsuarioService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;


public class TelaAlterarSenhaController {
    @FXML
    private PasswordField txtSenhaAtual;
    @FXML
    private PasswordField txtNovaSenha;
    @FXML
    private PasswordField txtConfirmarSenha;
    @FXML
    private Button btnCancelar;
    @FXML
    private Button btnSalvar;
    @FXML
    private Label lblMensagem;

    @FXML
    public void cancelar(){
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void salvar(){
        String senhaAtual = txtSenhaAtual.getText();
        String novaSenha = txtNovaSenha.getText();
        String confirmaSenha = txtConfirmarSenha.getText();

        try{
            if(!novaSenha.equals(confirmaSenha)){
                throw new IllegalArgumentException("As senhas não coincidem");
            }
            UsuarioService usuarioService = new UsuarioService();
            usuarioService.alterarSenha(senhaAtual, novaSenha);

            lblMensagem.getStyleClass().remove("validation-message");
            lblMensagem.getStyleClass().add("success-message");

            lblMensagem.setText("Senha alterada com sucesso.");
            lblMensagem.setVisible(true);
            lblMensagem.setManaged(true);

        }catch (IllegalArgumentException e){
            lblMensagem.getStyleClass().remove("success-message");
            lblMensagem.getStyleClass().add("validation-message");

            lblMensagem.setText(e.getMessage());
            lblMensagem.setVisible(true);
            lblMensagem.setManaged(true);
        }
    }


    }

