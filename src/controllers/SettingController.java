package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

import ai.Config;
import ai.ConfigFileLoader;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Classe SettingController
 * Contrôleur pour la page des paramètres.
 * Cette classe gère les paramètres de l'IA
 * @author GENTI Anthony
 */
public class SettingController implements Initializable {

	private ScaleTransition scaleTransition; // Instance de ScaleTransition pour animer le label de confirmation
	private ConfigFileLoader configFileLoader = new ConfigFileLoader();

    @FXML private TextField D_1, D_2, D_3, F_1, F_2, F_3, M_1, M_2, M_3;
    @FXML private Button btn_save; // Bouton pour enregistrer les paramètres
    @FXML private Button back_menu; // Bouton pour revenir au menu
	@FXML private Label confirmationLabel; // Label pour afficher la confirmation
	@FXML private StackPane stackPane; // StackPane pour afficher le label de confirmation
	@FXML private AnchorPane settingPage; // AnchorPane pour la page des paramètres

	/************************************************************
     * Initialisation et Chargement
    ************************************************************/
	/**
	 * Méthode d'initialisation du contrôleur.
	 * Cette méthode initialise le contrôleur et configure l'interface.
	 * @param location : L'emplacement de la ressource de l'interface utilisateur.
	 * @param resources : Les ressources utilisées pour localiser l'objet racine.
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		loadSettings();
		confirmationLabel.setVisible(false);
		stackPane.setVisible(false);

		Platform.runLater(() -> {
            try {
                Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/4.png")));
                BackgroundImage backgroundImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
                settingPage.setBackground(new Background(backgroundImage));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

		// Style :
		styleButton(btn_save, "#4CAF50", "#45a049");
        styleButton(back_menu, "#2196F3", "#1e88e5");

        // Appliquer le style au label
        styleConfirmationLabel();

	}

	/************************************************************
     * Gestion des Événements de l'Interface Utilisateur
    ************************************************************/
	/**
	 * Méthode pour enregistrer les paramètres.
	 * Cette méthode enregistre les paramètres de l'IA dans un fichier de configuration.
	 * @param event : L'événement de clic sur le bouton.
	 */
	public void save(ActionEvent event) {
		String level = "F";
		//si les champ ne sont pas vide
		if (!F_1.getText().isEmpty() || !F_2.getText().isEmpty() || !F_3.getText().isEmpty()) {
			int hiddenLayerSize = Integer.parseInt(F_1.getText());
			double learningRate = Double.parseDouble(F_2.getText());
			int numberOfhiddenLayers = Integer.parseInt(F_3.getText());
			Config f = new Config(level, hiddenLayerSize, numberOfhiddenLayers, learningRate);
			configFileLoader.saveConfigFile(f);
		}

		level = "M";
		if(!M_1.getText().isEmpty() || !M_2.getText().isEmpty() || !M_3.getText().isEmpty()){
			int hiddenLayerSize = Integer.parseInt(M_1.getText());
			double learningRate = Double.parseDouble(M_2.getText());
			int numberOfhiddenLayers = Integer.parseInt(M_3.getText());
			Config m = new Config(level, hiddenLayerSize, numberOfhiddenLayers, learningRate);
			configFileLoader.saveConfigFile(m);
		}

		level = "D";
		if (!D_1.getText().isEmpty() || !D_2.getText().isEmpty() || !D_3.getText().isEmpty()) {
			int hiddenLayerSize = Integer.parseInt(D_1.getText());
			double learningRate = Double.parseDouble(D_2.getText());
			int numberOfhiddenLayers = Integer.parseInt(D_3.getText());
			Config d = new Config(level, hiddenLayerSize, numberOfhiddenLayers, learningRate);
			configFileLoader.saveConfigFile(d);
		}

		// Afficher une boîte de dialogue de confirmation
	    confirmationLabel.setText("Les paramètres ont été enregistrés avec succès !");

	    // Animer le label
	    animateConfirmationLabel();

	}

	/**
	 * Méthode appelée lorsqu'un utilisateur clique sur le bouton "Retour au Menu".
	 * Cette méthode permet de retourner à la page du menu.
	 * @param event : L'événement de clic sur le bouton.
	 */
	public void backMenu(ActionEvent event) {
   	 try {
   	        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/menu.fxml"));
   	        Parent root = loader.load();
   	        Stage stage = (Stage) settingPage.getScene().getWindow();
   	        Scene scene = new Scene(root);
   	        stage.setScene(scene);
   	        stage.setTitle("Menu");
   	        stage.show();
   	    } catch (IOException e) {
   	        e.printStackTrace();
   	    }
   }

	/************************************************************
     * Configuration et Chargement de Données
    ************************************************************/

	/**
	 * Méthode pour charger les paramètres de l'IA.
	 * Cette méthode charge les paramètres de l'IA à partir d'un fichier de configuration.
	 */
	private void loadSettings() {

		configFileLoader.loadConfigFile("resources/config.txt");
		Config f = configFileLoader.get("F");
		if (f != null) {
			F_1.setText(String.valueOf(f.hiddenLayerSize));
			F_2.setText(String.valueOf(f.learningRate));
			F_3.setText(String.valueOf(f.numberOfhiddenLayers));
		}

		Config m = configFileLoader.get("M");
		if (m != null) {
			M_1.setText(String.valueOf(m.hiddenLayerSize));
			M_2.setText(String.valueOf(m.learningRate));
			M_3.setText(String.valueOf(m.numberOfhiddenLayers));

		}

		Config d = configFileLoader.get("D");
		if (d != null) {
			D_1.setText(String.valueOf(d.hiddenLayerSize));
			D_2.setText(String.valueOf(d.learningRate));
			D_3.setText(String.valueOf(d.numberOfhiddenLayers));
		}
}


	/************************************************************
     * Style et Apparence Visuelle
    ************************************************************/
	/**
	 * Méthode pour styliser un bouton.
	 * Cette méthode applique un style personnalisé à un bouton.
	 * @param button : Le bouton à styliser.
	 * @param backgroundColor : La couleur de fond du bouton.
	 * @param hoverColor : La couleur de survol du bouton.
	 */
	private void styleButton(Button button, String backgroundColor, String hoverColor) {
        button.setStyle("-fx-background-color: " + backgroundColor + "; -fx-text-fill: white; -fx-font-size: 20px; -fx-border-radius: 5; -fx-background-radius: 5;");
        DropShadow dropShadow = new DropShadow();
        dropShadow.setColor(Color.valueOf(backgroundColor).darker());
        dropShadow.setRadius(5);
        dropShadow.setSpread(0.5);

        button.setEffect(dropShadow);
        button.setPrefSize(240, 60);

        // Effet de survol
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: " + hoverColor + "; -fx-text-fill: white; -fx-font-size: 20px; -fx-border-radius: 5; -fx-background-radius: 5;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: " + backgroundColor + "; -fx-text-fill: white; -fx-font-size: 20px; -fx-border-radius: 5; -fx-background-radius: 5;"));
    }

	/**
	 * Méthode pour styliser le label de confirmation.
	 * Cette méthode applique un style personnalisé au label de confirmation.
	 */
	private void styleConfirmationLabel() {
	    confirmationLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: #2196F3; -fx-background-color: white; -fx-background-radius: 20px; -fx-border-color: black; -fx-border-width: 2px; -fx-border-radius: 20px; -fx-padding: 10;");
	    confirmationLabel.setAlignment(Pos.CENTER);
	}

	/************************************************************
     * Animation et Affichage Dynamique
    ************************************************************/

	/**
	 * Méthode pour animer le label de confirmation.
	 * Cette méthode anime le label de confirmation en le faisant apparaître et disparaître.
	 */
	private void animateConfirmationLabel() {
	    stackPane.setVisible(true);
	    confirmationLabel.setVisible(true);

	    scaleTransition = new ScaleTransition(Duration.seconds(1), confirmationLabel);
	    scaleTransition.setFromX(0.5);
	    scaleTransition.setFromY(0.5);
	    scaleTransition.setToX(1);
	    scaleTransition.setToY(1);
	    scaleTransition.setCycleCount(1);
	    scaleTransition.setAutoReverse(false);

	    scaleTransition.setOnFinished(e -> {
	        PauseTransition pauseTransition = new PauseTransition(Duration.seconds(3));
	        pauseTransition.setOnFinished(event -> {
	            stackPane.setVisible(false);
	            confirmationLabel.setVisible(false);
	        });
	        pauseTransition.play();
	    });

	    scaleTransition.play();
	}


}