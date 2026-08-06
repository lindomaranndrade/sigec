package br.com.sigec.controller;

import br.com.sigec.dao.UsuarioDAO;
import br.com.sigec.model.Usuario;
import javafx.fxml.FXML;
import javafx.scene.control.*;

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

        System.out.println("CLICOU");

        String login = txtUsuario.getText();
        String senha = txtSenha.getText();

        UsuarioDAO dao = new UsuarioDAO();

        Usuario usuario = dao.autenticar(login, senha);

        if (usuario != null) {

            System.out.println("LOGIN OK");
            lblErro.setVisible(false);

        } else {

            System.out.println("LOGIN INVÁLIDO");
            lblErro.setVisible(true);
        }
    }
}
