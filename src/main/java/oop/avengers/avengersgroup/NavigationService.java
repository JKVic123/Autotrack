package oop.avengers.avengersgroup;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class NavigationService {

    public static void showLoginScreen(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(AutoTrackApplication.class.getResource("main-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 350, 400);
            stage.setTitle("AutoTrack Login");
            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
