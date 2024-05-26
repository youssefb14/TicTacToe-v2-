package controllers;

import java.io.File; // Pour les fichiers
import java.io.IOException; // Pour les exceptions d'entrée/sortie
import java.net.URL; // Pour les URL
import java.util.HashMap; // Pour les hachs
import java.util.Objects; // Pour les objets
import java.util.ResourceBundle; // Pour les ressources

import ai.Config; // Pour la configuration
import ai.ConfigFileLoader; // Pour le chargeur de fichiers de configuration
import ai.Coup; // Pour les coups
import ai.MultiLayerPerceptron; // Pour les perceptrons multicouches
import ai.SigmoidalTransferFunction; // Pour la fonction de transfert sigmoïde
import ai.Test; // Pour les tests
import javafx.application.Platform; // Pour les applications
import javafx.concurrent.Task; // Pour les tâches
import javafx.event.ActionEvent; // Pour les événements
import javafx.fxml.FXML; // Pour annoter les éléments du fichier FXML
import javafx.fxml.FXMLLoader; // Pour charger un fichier FXML
import javafx.fxml.Initializable; // Pour initialiser un contrôleur
import javafx.scene.Node; // Pour les nœuds
import javafx.scene.Parent; // Pour les parents
import javafx.scene.Scene; // Pour les scènes
import javafx.scene.control.Button; // Pour les boutons
import javafx.scene.control.MenuItem; // Pour les éléments de menu
import javafx.scene.control.RadioButton; // Pour les boutons radio
import javafx.scene.control.ToggleGroup; // Pour les groupes de bascule
import javafx.scene.image.Image; // Pour les images
import javafx.scene.image.ImageView; // Pour les vues d'images
import javafx.scene.layout.AnchorPane; // Pour les panneaux d'ancrage
import javafx.scene.layout.Background; // Pour les arrière-plans
import javafx.scene.layout.BackgroundImage; // Pour les images d'arrière-plan
import javafx.scene.layout.BackgroundPosition; // Pour les positions d'arrière-plan
import javafx.scene.layout.BackgroundRepeat; // Pour les répétitions d'arrière-plan
import javafx.scene.layout.BackgroundSize; // Pour les tailles d'arrière-plan
import javafx.scene.media.Media; // Pour les médias
import javafx.scene.media.MediaException; // Pour les exceptions de média
import javafx.scene.media.MediaPlayer; // Pour les lecteurs multimédias
import javafx.stage.Stage; // Pour les étapes

/**
 * Classe MenuController
 * Contrôleur pour la page de menu.
 * Cette classe gère les actions de l'utilisateur sur la page de menu.
 * @author BOUDOUNT Youssef
 */
public class MenuController implements Initializable, TrainingCompletionListener {

    private ToggleGroup difficultyGroup = new ToggleGroup(); // Groupe de bascule pour les niveaux de difficulté
    private ConfigFileLoader configFileLoader = new ConfigFileLoader(); // Chargeur de fichiers de configuration
    private MediaPlayer clickSoundPlayer; // Lecteur multimédia pour le son de clic

    @FXML private AnchorPane menuPage; // Panneau d'ancrage de la page de menu
    @FXML private Button btn_vs_human; // Bouton pour jouer contre un humain
    @FXML private Button btn_vs_ia; // Bouton pour jouer contre l'IA
    @FXML private MenuItem menuitem_models; // Élément de menu pour ouvrir les modèles
    @FXML private MenuItem menuitem_setting; // Élément de menu pour ouvrir les paramètres
    @FXML private RadioButton radiobox_difficile; // Bouton radio pour le niveau de difficulté difficile
    @FXML private RadioButton radiobox_facile; // Bouton radio pour le niveau de difficulté facile
    @FXML private RadioButton radiobox_moyen; // Bouton radio pour le niveau de difficulté moyen
    @FXML private ImageView logoHumain; // Vue d'image pour le logo de l'humain
    @FXML private ImageView logoIA; // Vue d'image pour le logo de l'IA


    /************************************************************
     *  Initialisation et configuration
    ************************************************************/
    /**
     * Méthode d'initialisation de l'interface utilisateur.
     * Cette méthode est appelée après que le fichier FXML a été chargé.
     * @param arg0 : URL de la ressource racine
     * @param arg1 : ResourceBundle pour les localisations
     */
    @Override
	public void initialize(URL arg0, ResourceBundle arg1) {
    	configFileLoader.loadConfigFile("resources/config.txt"); // On charge le fichier de configuration

        // On associe les boutons radio au groupe de bascule
        radiobox_facile.setToggleGroup(difficultyGroup);
        radiobox_moyen.setToggleGroup(difficultyGroup);
        radiobox_difficile.setToggleGroup(difficultyGroup);

        radiobox_facile.setSelected(true); // On sélectionne le niveau de difficulté facile par défaut

        // Chargement des images
        try {
            Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/1.png")));
            BackgroundImage backgroundImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
            menuPage.setBackground(new Background(backgroundImage));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Chargement des sons
        try {
            String clickSound = getClass().getResource("/sound/clickSound.wav").toExternalForm();
            Media sound = new Media(clickSound);
            clickSoundPlayer = new MediaPlayer(sound);
        } catch (MediaException e) {
            System.err.println("Failed to load sound: " + e.getMessage());
        }

        setupClickSoundHandlers(); // On configure les gestionnaires de son de clic
	}


    /************************************************************
     *  Gestion des événements de l'interface utilisateur
    ************************************************************/

    /**
     * Méthode openSetting()
     * Cette méthode permet d'ouvrir la page des paramètres.
     * @param event : Événement de clic sur l'élément de menu
     */
    @FXML
    public void openSetting(ActionEvent event) {
   	 try {
   	        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/setting.fxml"));
   	        Parent root = loader.load();
   	        Stage stage = (Stage) ((MenuItem) event.getSource()).getParentPopup().getOwnerWindow();
   	        Scene scene = new Scene(root);
   	        stage.setScene(scene);
   	        stage.setTitle("Settings");
   	        stage.show();
   	    } catch (IOException e) {
   	        e.printStackTrace();
   	    }
   }

    /**
     * Méthode openModel()
     * Cette méthode permet d'ouvrir la page des modèles.
     * @param event : Événement de clic sur l'élément de menu
     */
    @FXML
    public void openModel(ActionEvent event) {
   	 try {
   	        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/model.fxml"));
   	        Parent root = loader.load();
   	        Stage stage = (Stage) ((MenuItem) event.getSource()).getParentPopup().getOwnerWindow();
   	        Scene scene = new Scene(root);
   	        stage.setScene(scene);
   	        stage.setTitle("Model");
   	        stage.show();
   	    } catch (IOException e) {
   	        e.printStackTrace();
   	    }
   }

    /**
     * Méthode Human_vs_Human()
     * Cette méthode permet de lancer le jeu Humain vs Humain.
     * @param event : Événement de clic sur le bouton
     */
    @FXML
    public void Human_vs_Human(ActionEvent event) {
    	playClickSound();
    	try {
            Parent learnRoot = FXMLLoader.load(getClass().getResource("/views/humainvshumain.fxml"));
            Scene learnScene = new Scene(learnRoot);

            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();

            window.setScene(learnScene);
            window.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Méthode Human_vs_IA()
     * Cette méthode permet de lancer le jeu Humain vs IA.
     * @param event : Événement de clic sur le bouton
     */
    @FXML
    public void Human_vs_IA(ActionEvent event) {
    	String selectedDifficulty = getSelectedDifficulty(); // On récupère le niveau de difficulté sélectionné

        if (selectedDifficulty.isEmpty()) { // Si aucun niveau de difficulté n'est sélectionné
            System.out.println("Veuillez choisir un niveau de difficulté");
            return;
        }

        configFileLoader.loadConfigFile("resources/config.txt"); // On charge le fichier de configuration

        Config config = configFileLoader.get(selectedDifficulty); // On récupère la configuration du niveau de difficulté sélectionné

        // On crée le nom du fichier modèle en fonction de la configuration
        String modelFileName = "model_" + config.numberOfhiddenLayers + "_" + config.hiddenLayerSize + "_" + config.learningRate + ".srl";
        // System.out.println("Nom du fichier modèle : " + modelFileName);

        File modelFile = new File("resources/models/" + modelFileName); // On crée un fichier pour le modèle

        try {
            if (!modelFile.exists()) { // Si le fichier modèle n'existe pas
                // System.out.println("Le fichier modèle n'existe pas, début de l'apprentissage.");

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/learn.fxml")); // On charge la page d'apprentissage
                Parent root = loader.load(); // On charge le fichier FXML
                LearnController learnController = loader.getController(); // On récupère le contrôleur de la page d'apprentissage
                learnController.setCompletionListener(this); // On définit le gestionnaire de complétion
                learnController.setConfig(config); // On définit la configuration
                Scene scene = new Scene(root); // On crée une nouvelle scène
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow(); // On récupère la fenêtre actuelle
                stage.setScene(scene); // On définit la scène
                stage.setTitle("Apprentissage"); // On définit le titre de la fenêtre
                stage.show();


                /* Après l'apprentissage, vérifier si le fichier de modèle a été créé
                if (new File("resources/models/" + modelFileName).exists()) {
                    System.out.println("Modèle sauvegardé avec succès. Lancer le jeu.");

                    System.out.println("Lancer le jeu avec le modèle.");

                } else {
                    System.out.println("Echec de la sauvegarde du modèle.");
                }*/
            } else { // Si le fichier modèle existe déjà
                // System.out.println("Modèle existant trouvé. Lancement du jeu avec le modèle.");

                FXMLLoader gameLoader = new FXMLLoader(getClass().getResource("/views/humainvsia.fxml")); // On charge la page de jeu
                Parent gameRoot = gameLoader.load(); // On charge le fichier FXML
                HumainVsIAController gameController = gameLoader.getController();  // On récupère le contrôleur de la page de jeu
                gameController.setConfig(config); // On définit la configuration

                Scene gameScene = new Scene(gameRoot); // On crée une nouvelle scène
                Stage gameStage = (Stage) ((Node) event.getSource()).getScene().getWindow(); // On récupère la fenêtre actuelle
                gameStage.setScene(gameScene); // On définit la scène
                gameStage.setTitle("Humain vs IA"); // On définit le titre de la fenêtre
                gameStage.show(); // On affiche la fenêtre
            }

        } catch (IOException e) { // En cas d'erreur d'entrée/sortie
            e.printStackTrace();
        }
    }


   


    /************************************************************
     * Logique de jeu et configuration
    ************************************************************/
    /**
     * Méthode getSelectedDifficulty()
     * Cette méthode permet de récupérer le niveau de difficulté sélectionné.
     * @return : Niveau de difficulté sélectionné
     */
    private String getSelectedDifficulty() {
        if (radiobox_facile.isSelected()) return "F"; // Si le niveau de difficulté facile est sélectionné, retourner "F"
        if (radiobox_moyen.isSelected()) return "M";
        if (radiobox_difficile.isSelected()) return "D";
        return ""; // Sinon, retourner une chaîne vide
    }

    /**
     * Méthode onTrainingComplete()
     * Cette méthode est appelée lorsque l'apprentissage est terminé.
     * @param config : Configuration de l'apprentissage
     */
    @Override
    public void onTrainingComplete(Config config) {
        Platform.runLater(() -> {
            try {
                if (config == null) { // Si la configuration est nulle
                    // System.err.println("Training completed but config is null.");
                    return; // On arrête la méthode
                }

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/humainvsia.fxml")); // On charge la page de jeu
                Parent root = loader.load(); // On charge le fichier FXML
                HumainVsIAController gameController = loader.getController(); // On récupère le contrôleur de la page de jeu
                gameController.setConfig(config); // On définit la configuration

                Scene gameScene = new Scene(root); // On crée une nouvelle scène
                Stage primaryStage = Main.getPrimaryMainStage(); // On récupère la fenêtre principale
                primaryStage.setScene(gameScene); // On définit la scène
                primaryStage.setTitle("Humain vs IA"); // On définit le titre de la fenêtre
                primaryStage.show(); // On affiche la fenêtre
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }


    /************************************************************
     * Manipulation du son
    ************************************************************/
    /**
     * Méthode setupClickSoundHandlers()
     * Cette méthode permet de configurer les gestionnaires de son de clic.
     */
    private void setupClickSoundHandlers() {
        btn_vs_human.setOnAction(event -> {  // Si le bouton pour jouer contre un humain est cliqué
            playClickSound();  // On joue le son de clic
            Human_vs_Human(event); // On lance le jeu Humain vs Humain
        });

        btn_vs_ia.setOnAction(event -> { // Si le bouton pour jouer contre l'IA est cliqué
            playClickSound(); // On joue le son de clic
            Human_vs_IA(event);  // On lance le jeu Humain vs IA
        });

        menuitem_models.setOnAction(event -> { // Si l'élément de menu pour ouvrir les modèles est cliqué
            playClickSound(); // On joue le son de clic
            openModel(event);  // On ouvre la page des modèles
        });

        menuitem_setting.setOnAction(event -> { // Si l'élément de menu pour ouvrir les paramètres est cliqué
            playClickSound(); // On joue le son de clic
            openSetting(event);  // On ouvre la page des paramètres
        });
    }

    /**
     * Méthode playClickSound()
     * Cette méthode permet de jouer le son de clic.
     */
    private void playClickSound() {
        if (clickSoundPlayer != null) {
            clickSoundPlayer.stop();
            clickSoundPlayer.play();
        }
    }
}