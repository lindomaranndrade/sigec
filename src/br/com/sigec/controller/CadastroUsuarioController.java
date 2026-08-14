package br.com.sigec.controller;

import br.com.sigec.dao.UsuarioDAO;
import br.com.sigec.model.Usuario;
import br.com.sigec.service.UsuarioService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class CadastroUsuarioController {

    @FXML
    private TextField txtNome;

    @FXML
    private TextField txtLogin;

    @FXML
    private PasswordField txtSenha;

    @FXML
    private PasswordField txtConfirmarSenha;

    @FXML
    private void limpar(){
        txtNome.setText("");
        txtLogin.setText("");
        txtSenha.setText("");
        txtConfirmarSenha.setText("");
    }

    @FXML
    private void cadastrar(){
        if(!txtSenha.getText().equals(txtConfirmarSenha.getText())){

            Alert alerta =  new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("ERRO");
            alerta.setHeaderText("Os campos não conferem");
            alerta.showAndWait();
            return;
        }

        UsuarioService usuarioService = new UsuarioService();
        Usuario usuario = new Usuario(
                txtNome.getText(),
                txtLogin.getText(),
                txtSenha.getText()
        );

        try{
            usuarioService.cadastrar(usuario);
        } catch (RuntimeException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERRO");
            alert.setHeaderText("Não foi possível realizar o cadastro\n");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
}
