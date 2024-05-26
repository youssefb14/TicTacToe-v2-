package controllers;

import javafx.application.Application; // Pour les applications
import javafx.fxml.FXMLLoader;  // Pour charger un fichier FXML
import javafx.scene.Parent; // Pour les parents
import javafx.scene.Scene; // Pour les scènes
import javafx.stage.Stage; // Pour les étapes

/**
 * Classe Main
 * Classe principale de l'application.
 */
public class Main extends Application {

    private static Stage primaryMainStage;

    @Override
    public void start(Stage primaryStage) {
        try {
            primaryMainStage = primaryStage;
            Parent root = FXMLLoader.load(getClass().getResource("/views/menu.fxml"));
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static Stage getPrimaryMainStage() {
        return primaryMainStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
