package br.com.sigec;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader =
                new FXMLLoader(
                        getClass().getResource(
                                "/br/com/sigec/view/MainView.fxml"
                        )
                );

        Scene scene = new Scene(loader.load());

        stage.setTitle("SIGEC");
        stage.show();
        stage.setScene(scene);
    }

    public static void main(String[] args) {
        launch(args);
    }
}