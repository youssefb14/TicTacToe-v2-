package controllers;

import java.io.IOException; // Pour les exceptions d'entrée/sortie
import java.net.URL; // Pour les URL
import java.util.HashMap; // Pour les hachs
import java.util.Objects; // Pour les objets
import java.util.ResourceBundle; // Pour les ressources

import ai.Config; // Pour la configuration
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
import javafx.scene.control.ProgressIndicator; // Pour les indicateurs de progression
import javafx.scene.control.TextField; // Pour les champs de texte
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

/**
 * Classe LearnController
 * Contrôleur pour la page d'apprentissage.
 * Cette classe gère l'apprentissage du réseau de neurones.
 * @author GENTI Anthony
 */
public class LearnController implements Initializable{

    @FXML private AnchorPane learnPage; // Panneau d'ancrage de la page d'apprentissage
    @FXML private TextField text_learn; // Champ de texte pour l'apprentissage
    @FXML private ProgressIndicator prgressbar_learn; // Indicateur de progression pour l'apprentissage
    @FXML private Button back_menu; // Bouton pour revenir au menu

    private Config config; // Configuration de l'apprentissage
    private TrainingCompletionListener completionListener; // Écouteur de fin d'apprentissage


    /************************************************************
     * Initialisation et chargement des ressources
    ************************************************************/
    /**
     * Méthode initialize()
     * Cette methode est appelée pour initialiser un contrôleur après que son élément racine a été complètement traité.
     * @param location : URL de l'emplacement de l'objet racine
     * @param resources : ResourceBundle pour localiser l'objet racine
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    	try {
            Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/5.png")));
            BackgroundImage backgroundImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
            learnPage.setBackground(new Background(backgroundImage));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (config != null) {
        	System.out.println("Lancmeent  de startTraining");
            startTraining();

        }
        else {
        	System.out.println("Pas de Lancmeent  de startTraining");
        }

		// Style :
        styleButton(back_menu, "#2196F3", "#1e88e5");
    }

    /************************************************************
     * Configuration et démarrage de la formation
    ************************************************************/
    /**
     * Méthode setCompletionListener()
     * Cette méthode permet de définir un écouteur pour la fin de l'apprentissage.
     * @param listener : Écouteur de fin d'apprentissage
    */
    public void setCompletionListener(TrainingCompletionListener listener) {
        this.completionListener = listener;
    }

    /**
     * Méthode setConfig()
     * Cette méthode permet de définir la configuration de l'apprentissage.
     * @param config : Configuration de l'apprentissage
    */
    public void setConfig(Config config) {
        this.config = config;
        startTraining();
    }

    /**
     * Méthode startTraining()
     * Cette méthode permet de démarrer l'apprentissage du réseau de neurones.
    */
    @FXML
    public void startTraining()
    {

    	if (this.config == null) {
            System.err.println("Erreur : La configuration n'a pas été définie.");
            return;
        }

    	int l = config.numberOfhiddenLayers;
    	int h = config.hiddenLayerSize;
    	double lr = config.learningRate;


        boolean verbose = true;

        int[] layers = new int[l + 2];
        layers[0] = 9;

        for (int i = 0; i < l; i++) {
            layers[i + 1] = h;
        }
        layers[layers.length - 1] = 9;

        MultiLayerPerceptron net = new MultiLayerPerceptron(layers, lr, new SigmoidalTransferFunction());

        double epochs = 10000;
        HashMap<Integer, Coup> mapTrain = Test.loadCoupsFromFile("resources/train_dev_test/train.txt");

        Task<Void> task = new Task<>() {

            @Override
            protected Void call() throws Exception {
                double error = 0.0;
                for (int i = 0; i < epochs; i++) {

                    Coup c = null;
                    while (c == null)
                        c = mapTrain.get((int) (Math.round(Math.random() * (mapTrain.size() - 1))));

                    error += net.backPropagate(c.in, c.out);

                    if (i % 100 == 0 && verbose) {
                        updateMessage("Etape " + i + " est " + (error / (i + 1)));
                        updateProgress(i + 1, epochs);
                    }
                }
                Platform.runLater(() -> {
                    String modelFileName = "model_" + config.numberOfhiddenLayers + "_" + config.hiddenLayerSize + "_" + config.learningRate + ".srl";
                    boolean saveSuccess = net.save("resources/models/" + modelFileName);
                    if (saveSuccess) {
                    	System.out.println("Modèle enregistré avec succès.");
                        if (completionListener != null) {
                            completionListener.onTrainingComplete(config);
                        }
                    } else {
                        System.out.println("Erreur lors de la sauvegarde du modèle.");
                    }
                });
                return null;
            }
        };

         prgressbar_learn.progressProperty().bind(task.progressProperty());

        task.messageProperty().addListener((observable, oldValue, newValue) -> {
            Platform.runLater(() -> {
                text_learn.setText(newValue);
            });
        });

        new Thread(task).start();
    }


    /************************************************************
     * Gestion des événements
    ************************************************************/
    /**
     * Méthode backMenu()
     * Cette méthode permet de revenir au menu principal.
     * @param event : Événement
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
     * Méthode openSetting()
     * Cette méthode permet d'ouvrir la page des paramètres.
     * @param event : Événement de clic sur le bouton
    */
    @FXML
    void openSetting(ActionEvent event) {
    	// TODO ; Implémenter l'ouverture de la page des paramètres. Pas utilie pour le moment
    }

    /************************************************************
     * Affichage et style
    ************************************************************/

    /**
     * Méthode styleButton()
     * Cette méthode permet de styliser un bouton.
     * @param button : Bouton à styliser
     * @param backgroundColor : Couleur de fond
     * @param hoverColor : Couleur de survol
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

}
