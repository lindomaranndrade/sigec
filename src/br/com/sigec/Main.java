package br.com.sigec;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader =
                new FXMLLoader(
                        getClass().getResource(
                                "/br/com/sigec/view/telaLoginMaior.fxml"
                        )
                );

        Scene scene = new Scene(loader.load());

        stage.setTitle("SIGEC");

        stage.getIcons().add(
                new Image(
                        getClass().getResourceAsStream(
                                "/br/com/sigec/images/logo.png"
                        )
                )
        );

        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}