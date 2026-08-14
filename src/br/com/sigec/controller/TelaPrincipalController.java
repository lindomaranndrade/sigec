package br.com.sigec.controller;

import br.com.sigec.session.SessaoUsuario;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.scene.control.Button;

import java.io.IOException;

public class TelaPrincipalController {
    @FXML
    private VBox formContainer;

    @FXML
    private Button btnSair;

    @FXML
    public void initialize() {
        try {
            abrirExamesPendentes();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void abrirUsuarios() throws IOException {

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource(
                        "/br/com/sigec/view/cadastroUsuario.fxml"
                )
        );

        Parent root = loader.load();

        formContainer.getChildren().setAll(root);

    }

    public void sair() throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource
                ("/br/com/sigec/view/telaLoginMaior.fxml")
        );

        Parent root = loader.load();
        Scene scene = new Scene(root);
        Stage stage = (Stage) btnSair.getScene().getWindow();
        SessaoUsuario.setUsuarioLogado(null);

        stage.setMaximized(false);
        stage.setResizable(false);
        stage.setScene(scene);

    }

    public void abrirExamesPendentes() throws IOException{
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource(
                "/br/com/sigec/view/telaExamesPendentes.fxml"
        )
        );

        VBox root = loader.load();
        root.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        VBox.setVgrow(root, Priority.ALWAYS);
        formContainer.getChildren().setAll(root);
    }

}
