package br.com.sigec.controller;

import br.com.sigec.model.Usuario;
import br.com.sigec.service.UsuarioService;
import br.com.sigec.session.SessaoUsuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private TextField txtUsuario;

    @FXML
    private PasswordField txtSenha;

    @FXML
    private Label lblErro;


    @FXML
    public void initialize() {
        lblErro.setVisible(false);
    }

    @FXML
    private void entrar() {

        String login = txtUsuario.getText();
        String senha = txtSenha.getText();

        UsuarioService usuarioService = new UsuarioService();

        Usuario usuario = usuarioService.autenticar(login,senha);

        if (usuario != null) {

            SessaoUsuario.setUsuarioLogado(usuario);

            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource(
                                "/br/com/sigec/view/TelaPrincipal.fxml"
                        )
                );

                Parent root = loader.load();

                Scene scene = new Scene(root);

                Stage stage = (Stage) txtUsuario.getScene().getWindow();

                stage.setResizable(true);
                stage.setScene(scene);
                stage.setMaximized(true);

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {

            System.out.println("LOGIN INVÁLIDO");
            lblErro.setVisible(true);
        }
    }
}
