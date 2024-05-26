package controllers;

import java.io.File; // Pour les fichiers
import java.io.IOException; // Pour les exceptions d'entrée/sortie
import java.net.URL; // Pour les UR
import java.util.ArrayList; // Pour les listes
import java.util.HashMap; // Pour les maps
import java.util.List; // Pour les listes
import java.util.Map; // Pour les maps
import java.util.Objects; // Pour les objets
import java.util.ResourceBundle; // Pour les ressources

import javafx.application.Platform; // Pour la plateforme
import javafx.beans.value.ObservableValue; // Pour les valeurs observables
import javafx.event.ActionEvent; // Pour les événements
import javafx.fxml.FXML; // Pour annoter les éléments du fichier FXML
import javafx.fxml.FXMLLoader; // Pour charger un fichier FXML
import javafx.fxml.Initializable; // Pour initialiser un contrôleur
import javafx.scene.Node; // Pour les nœuds
import javafx.scene.Parent; // Pour les parents
import javafx.scene.Scene; // Pour les scènes
import javafx.scene.control.Button; // Pour les boutons
import javafx.scene.control.CheckBox; // Pour les cases à cocher
import javafx.scene.control.ListView; // Pour les listes
import javafx.scene.control.cell.CheckBoxListCell; // Pour les cellules de liste à cases à cocher
import javafx.scene.effect.DropShadow; // Pour les ombres
import javafx.scene.image.Image; // Pour les images
import javafx.scene.layout.AnchorPane; // Pour les panneaux d'ancrage
import javafx.scene.layout.Background; // Pour les arrière-plans
import javafx.scene.layout.BackgroundImage; // Pour les images d'arrière-plan
import javafx.scene.layout.BackgroundPosition; // Pour les positions d'arrière-plan
import javafx.scene.layout.BackgroundRepeat; // Pour les répétitions d'arrière-plan
import javafx.scene.layout.BackgroundSize; // Pour les tailles d'arrière-plan
import javafx.scene.paint.Color; // Pour les couleurs
import javafx.stage.Stage; // Pour les étapes
import javafx.util.Callback; // Pour les rappels

/**
 * Classe ModelController
 * Contrôleur pour la page des modèles.
 * Cette classe gère les modèles de l'application.
 * @author GENTI Anthony
 */
public class ModelController implements Initializable{

	private Map<String, CheckBox> modelCheckBoxes = new HashMap<>();
	@FXML private AnchorPane modelPage;
	@FXML private ListView<String> model_list;
	@FXML private Button back_menu;
	@FXML private Button btn_deleteSelected;

	/************************************************************
     * Initialisation et Configuration
    ************************************************************/
	/**
	 * Méthode d'initialisation du contrôleur.
	 * Cette méthode initialise le contrôleur et configure l'interface.
	 * @param location : L'emplacement de la ressource de l'interface utilisateur.
	 * @param resources : Les ressources utilisées pour localiser l'objet racine.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// System.out.println("Initializing ModelController...");
		File modelDir = new File("resources/models");
		// System.out.println("Directory path: " + modelDir.getAbsolutePath());
		File[] files = modelDir.listFiles();
		if (files == null) {
			// System.out.println("No files found or directory does not exist");
		} else {
			// System.out.println("Found " + files.length + " files");
		}

		modelDir = new File("resources/models");
		files = modelDir.listFiles();
		model_list.getItems().clear();
		modelCheckBoxes.clear();

		if (files != null) {
		    for (File file : files) {
		        if (file.isFile() && file.getName().endsWith(".srl")) {
		            // Récupérer le niveau de difficulté depuis le nom du fichier
		            String fileName = file.getName();
		            String difficulty = getDifficultyFromFileName(fileName);
		            // Ajouter le niveau de difficulté et le nom du modèle à la liste
		            model_list.getItems().add(difficulty + " - " + fileName);
		        }
		    }
		}


		Callback<String, ObservableValue<Boolean>> getProperty = item -> {
			CheckBox cb = modelCheckBoxes.getOrDefault(item, new CheckBox());
			modelCheckBoxes.putIfAbsent(item, cb);
			return cb.selectedProperty();
		};

		model_list.setCellFactory(CheckBoxListCell.forListView(getProperty));

		Platform.runLater(() -> {
			try {
				Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/4.png")));
				BackgroundImage backgroundImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
				modelPage.setBackground(new Background(backgroundImage));
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		// Style :
		styleButton(btn_deleteSelected, "#4CAF50", "#45a049");
		styleButton(back_menu, "#2196F3", "#1e88e5");
	}


	/************************************************************
     * Gestion des Événements UI
    ************************************************************/
	/**
	 * Méthode appelée lorsqu'un utilisateur clique sur le bouton "Retour au Menu".
	 * Cette méthode permet de retourner à la page du menu.
	 * @param event : L'événement de clic sur le bouton.
	 */
	@FXML
	public void backMenu(ActionEvent event) {
		try {

			FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/menu.fxml"));
			Parent root = loader.load();
			Scene scene = new Scene(root);
			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			stage.setScene(scene);
			stage.setTitle("Menu");
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Méthode appelée lorsqu'un utilisateur clique sur le bouton "Supprimer les modèles sélectionnés".
	 * Cette méthode permet de supprimer les modèles sélectionnés.
	 * @param event : L'événement de clic sur le bouton.
	 */
	@FXML
	public void onDeleteSelectedClicked(ActionEvent event) {
		List<String> selectedModels = new ArrayList<>();
		modelCheckBoxes.forEach((modelName, checkBox) -> {
			if (checkBox.isSelected()) {
				selectedModels.add(modelName);
				new File("resources/models/" + modelName).delete();
			}
		});
		model_list.getItems().removeAll(selectedModels);
		selectedModels.forEach(modelCheckBoxes::remove);
	}


	/************************************************************
     * Configuration de l'Interface et du Style
    ************************************************************/
	/**
	 * Méthode pour styliser un bouton.
	 * Cette méthode permet de styliser un bouton avec un arrière-plan et un effet de survol.
	 * @param button : Le bouton à styliser.
	 * @param backgroundColor : La couleur de l'arrière-plan du bouton.
	 * @param hoverColor : La couleur de l'arrière-plan du bouton lorsqu'il est survolé.
	 */
	private void styleButton(Button button, String backgroundColor, String hoverColor) {
		button.setStyle("-fx-background-color: " + backgroundColor + "; -fx-text-fill: white; -fx-font-size: 20px; -fx-border-radius: 5; -fx-background-radius: 5;");
		DropShadow dropShadow = new DropShadow();
		dropShadow.setColor(Color.valueOf(backgroundColor).darker());
		dropShadow.setRadius(5);
		dropShadow.setSpread(0.5);

		button.setEffect(dropShadow);
		button.setPrefSize(240, 60); // Définir une grande taille

		// Effet de survol
		button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: " + hoverColor + "; -fx-text-fill: white; -fx-font-size: 20px; -fx-border-radius: 5; -fx-background-radius: 5;"));
		button.setOnMouseExited(e -> button.setStyle("-fx-background-color: " + backgroundColor + "; -fx-text-fill: white; -fx-font-size: 20px; -fx-border-radius: 5; -fx-background-radius: 5;"));
	}
	
	private String getDifficultyFromFileName(String fileName) {
	    String[] parts = fileName.split("_");
	    if (parts.length >= 2) {
	        String difficultyCode = parts[1];
	        switch (difficultyCode) {
	            case "1":
	                return "Facile";
	            case "2":
	                return "Moyen";
	            case "3":
	                return "Difficile";
	            default:
	                return "Inconnu";
	        }
	    } else {
	        return "Inconnu";
	    }
	}



}
