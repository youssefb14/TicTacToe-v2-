package controllers;

import java.io.File; // Pour les fichiers
import java.io.IOException; // Pour les exceptions d'entrée/sortie
import java.net.URL; // Pour les URL
import java.util.Arrays; // Pour les tableaux
import java.util.Objects; // Pour les objets
import java.util.ResourceBundle; // Pour les ressources
import java.util.concurrent.atomic.AtomicInteger; // Pour les atomes

import ai.Config; // Pour la configuration AI
import ai.ConfigFileLoader;  // Pour le chargeur de fichiers de configuration
import ai.MultiLayerPerceptron; // Pour le perceptron multicouche
import ai.SigmoidalTransferFunction; // Pour la fonction de transfert sigmoïde
import javafx.animation.FadeTransition; // Pour les transitions de fondu
import javafx.animation.ScaleTransition; // Pour les transitions de mise à l'échelle
import javafx.animation.TranslateTransition; // Pour les transitions de translation
import javafx.application.Platform; // Pour la plateforme JavaFX
import javafx.event.ActionEvent; // Pour les événements d'action
import javafx.fxml.FXML; // Pour annoter les éléments du fichier FXML
import javafx.fxml.FXMLLoader; // Pour charger un fichier FXML
import javafx.fxml.Initializable; // Pour initialiser un contrôleur
import javafx.scene.Node; // Pour les nœuds JavaFX
import javafx.scene.Parent; // Pour les éléments parent
import javafx.scene.Scene; // Pour les scènes JavaFX
import javafx.scene.control.Button; // Pour les boutons JavaFX
import javafx.scene.control.Label; // Pour les étiquettes JavaFX
import javafx.scene.effect.DropShadow; // Pour les ombres portées
import javafx.scene.image.Image; // Pour les images JavaFX
import javafx.scene.input.MouseEvent; // Pour les événements de souris
import javafx.scene.layout.AnchorPane; // Pour les panneaux d'ancrage
import javafx.scene.layout.Background; // Pour les arrière-plans JavaFX
import javafx.scene.layout.BackgroundImage; // Pour les images d'arrière-plan
import javafx.scene.layout.BackgroundPosition; // Pour les positions d'arrière-plan
import javafx.scene.layout.BackgroundRepeat; // Pour les répétitions d'arrière-plan
import javafx.scene.layout.BackgroundSize; // Pour les tailles d'arrière-plan
import javafx.scene.layout.Pane; // Pour les panneaux
import javafx.scene.media.Media; // Pour les médias
import javafx.scene.media.MediaPlayer; // Pour les lecteurs multimédias
import javafx.scene.paint.Color; // Pour les couleurs
import javafx.scene.shape.Circle; // Pour les cercles
import javafx.scene.shape.Line;
import javafx.stage.Stage; // Pour les scènes JavaFX
import javafx.util.Duration; // Pour les durées

/**
 * Classe HumainVsIAController
 * Contrôleur pour le jeu Tic Tac Toe en mode Humain contre IA.
 * Cette classe gère la logique du jeu, les animations, l'affichage et les effets sonores.
 * @author BOUDOUNT Youssef
 */
public class HumainVsIAController implements Initializable{

	private boolean isPlayerXTurn = true; // Le joueur commence toujours
    private MultiLayerPerceptron aiPlayer; // Joueur AI

    private double[] gameState = new double[9]; // État du jeu : 0 = vide, 1 = X, -1 = O
    private boolean gameOver = false; // État du jeu

    private Config aiConfig; // Configuration AI
    private Config config; // Configuration du jeu
    private ConfigFileLoader configFileLoader = new ConfigFileLoader(); // Chargeur de fichiers de configuration

    private MediaPlayer soundPlayerHuman; // Lecteur multimédia pour les effets sonores
    private MediaPlayer soundPlayerAI; // Lecteur multimédia pour les effets sonores

    private static final int[][] WINNING_COMBINATIONS = {
        {0, 1, 2}, {3, 4, 5}, {6, 7, 8},
        {0, 3, 6}, {1, 4, 7}, {2, 5, 8},
        {0, 4, 8}, {2, 4, 6}
    }; // Combinaisons gagnantes

    @FXML private AnchorPane humainvsiaPage; // Panneau d'ancrage pour la page Humain vs IA
    @FXML private Button back_menu; // Bouton pour revenir au menu
    @FXML private Button restartButton; // Bouton pour redémarrer le jeu
    @FXML private Pane cell00, cell01, cell02, cell10, cell11, cell12, cell20, cell21, cell22; // Cellules de la grille
    @FXML private Label statusLabel; // Étiquette pour le statut du jeu
    @FXML private Label chooseDifficultyLabel; // Étiquette pour choisir la difficulté
    @FXML private AnchorPane overlayPane; // Panneau d'ancrage pour l'effet de fondu
    @FXML private AnchorPane difficultyOverlayPane; // Panneau d'ancrage pour la difficulté


    /************************************************************
     * Initialisation et chargement des ressources
    ************************************************************/

    /**
     * Méthode initialize ()
     * Cette méthodee permet d'nitialisation principale de la vue HumainVsIA.
     */
    public void initialize() {
    	configFileLoader.loadConfigFile("resources/config.txt"); // On charge le fichier de configuration
        resetGame(); // On réinitialise le jeu


        try {
            // On charge l'image de fond
            Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/2.png")));

            // On crée un arrière-plan avec l'image chargée
            BackgroundImage backgroundImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
            humainvsiaPage.setBackground(new Background(backgroundImage));  // On applique l'arrière-plan à la page
        } catch (Exception e) {
            e.printStackTrace();
        }

		// On stylise les boutons
		styleButton(restartButton, "#4CAF50", "#45a049");
        styleButton(back_menu, "#2196F3", "#1e88e5");
    }

    /**
     * Méthode initialize ()
     * Cette méthode permet d'initialiser le contrôleur après le chargement de l'interface utilisateur.
     * @param location : URL de l'emplacement de l'objet racine
     * @param resources : ResourceBundle pour localiser l'objet racine
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    	loadSoundEffects(); // On charge les effets sonores
        configFileLoader.loadConfigFile("resources/config.txt"); // On charge le fichier de configuration
        resetGame(); // On réinitialise le jeu

        Platform.runLater(() -> {
            try {
                // On charge l'image de fond
                Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/2.png")));

                // On crée un arrière-plan avec l'image chargée
                BackgroundImage backgroundImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
                humainvsiaPage.setBackground(new Background(backgroundImage)); // On applique l'arrière-plan à la page
                styleButton(restartButton, "#4CAF50", "#45a049"); // On stylise les boutons
                styleButton(back_menu, "#2196F3", "#1e88e5"); // On stylise les boutons

                if (overlayPane != null && difficultyOverlayPane != null) {
                    overlayPane.setVisible(false);
                    difficultyOverlayPane.setVisible(false);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Méthode setConfig ()
     * Cette méthode permet de définir la configuration du jeu.
     * @param config : configuration du jeu
     */
    public void setConfig(Config config) {
        this.config = config; // On définit la configuration du jeu
        loadAIConfiguration();  // On charge la configuration AI
        resetGame();  // On réinitialise le jeu
    }

    /**
     * Méthode loadAIConfiguration ()
     * Cette méthode permet de charger la configuration AI.
     */
    private void loadAIConfiguration() {
        aiConfig = this.config; // On charge la configuration AI
        if (aiConfig == null) { // Si la configuration AI n'est pas fournie
            System.out.println("Erreur: Configuration AI non fournie."); // On affiche un message d'erreur
            return;
        }

        // On charge le modèle AI à partir du fichier s'il existe
        aiPlayer = new MultiLayerPerceptron(new int[] {9, aiConfig.hiddenLayerSize, 9}, aiConfig.learningRate, new SigmoidalTransferFunction());
    }

    /**
     * Méthode loadSoundEffects ()
     * Cette méthode permet de charger les effets sonores.
     */
    private void loadSoundEffects() {
    try {
        // On charge les fichiers sonores pour les effets sonores du joueur
        String soundHuman = getClass().getResource("/sound/mouvX.mp3").toExternalForm();
        Media soundMediaHuman = new Media(soundHuman);
        soundPlayerHuman = new MediaPlayer(soundMediaHuman);

        // On charge les fichiers sonores pour les effets sonores de l'IA
        String soundAI = getClass().getResource("/sound/mouvO.mp3").toExternalForm();
        Media soundMediaAI = new Media(soundAI);
        soundPlayerAI = new MediaPlayer(soundMediaAI);
    } catch (Exception e) {
        System.err.println("Failed to load sound files: " + e);
    }
}

    /************************************************************
     * Logique de jeu
    ************************************************************/
    /**
     * Méthode resetGame ()
     * Cette méthode permet de réinitialiser le jeu.
     */
    private void resetGame() {
        isPlayerXTurn = true; // Le joueur commence toujours
        gameState = new double[9];  // État du jeu : 0 = vide, 1 = X, -1 = O
        clearBoard(); // On nettoie le plateau
        updateStatusLabel(); // On met à jour le statut du jeu
        gameOver = false;  // Le jeu n'est pas terminé par défaut

        // On réinitialise le style des cellules
        for (Pane cell : new Pane[]{cell00, cell01, cell02, cell10, cell11, cell12, cell20, cell21, cell22}) {
            cell.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-opacity: 1;");
        }

        // On supprime les éléments de la page Humain vs IA sauf l'étiquette de statut afin de les réinitialiser
        ((AnchorPane) statusLabel.getParent()).getChildren().removeIf(node -> node instanceof Label && !node.equals(statusLabel));
    }

    /**
     * Méthode handleCellClick ()
     * Cette méthode permet de gérer le clic sur une cellule de la grille.
     * @param event : clic de souris sur une cellule
     */
    @FXML
    private void handleCellClick(MouseEvent event) {
        // Si le jeu est terminé ou si ce n'est pas le tour du joueur, on ne fait rien
        if (gameOver || !isPlayerXTurn) return;

        Pane clickedCell = (Pane) event.getSource(); // On récupère la cellule cliquée
        String cellIdStr = clickedCell.getId().substring(4); // On récupère l'ID de la cellule
        int rowIndex = Integer.parseInt(cellIdStr.substring(0, 1)); // On récupère l'indice de la ligne
        int colIndex = Integer.parseInt(cellIdStr.substring(1, 2)); // On récupère l'indice de la colonne
        int cellId = rowIndex * 3 + colIndex;  // On calcule l'indice de la cellule

        if (gameState[cellId] == 0) {  // Si la cellule est vide
            gameState[cellId] = 1;  // On met à jour l'état du jeu
            drawX(clickedCell);  // On dessine X dans la cellule
            isPlayerXTurn = false; // On passe au tour de l'IA
            updateStatusLabel(); // On met à jour le statut du jeu
            checkGameState(); // On vérifie l'état du jeu

            if (!gameOver && !isPlayerXTurn) { // Si le jeu n'est pas terminé et ce n'est pas le tour du joueur
                Platform.runLater(this::aiTurn); // On appelle la méthode aiTurn() dans la file d'attente de la plateforme
            }
        }
    }

    /**
     * Méthode aiTurn ()
     * Cette méthode permet de gérer le tour de l'IA.
     */
    private void aiTurn() {
        if (gameOver) return; // Si le jeu est terminé, on ne fait rien
        double[] aiMove = aiPlayer.forwardPropagation(gameState); // On calcule le mouvement de l'IA

        int bestMove = -1; // On initialise le meilleur mouvement à -1
        double bestValue = Double.NEGATIVE_INFINITY; // On initialise la meilleure valeur à -∞

        for (int i = 0; i < aiMove.length; i++) { // Pour chaque mouvement possible de l'IA
            if (gameState[i] == 0 && aiMove[i] > bestValue) { // Si la cellule est vide et que la valeur est meilleure
                bestValue = aiMove[i]; // On met à jour la meilleure valeur
                bestMove = i; // On met à jour le meilleur mouvement
            }
        }

        if (bestMove != -1) { // Si le meilleur mouvement est valide
            gameState[bestMove] = -1; // On met à jour l'état du jeu
            drawO(getCellByIndex(bestMove)); // On dessine O dans la cellule
            isPlayerXTurn = true; // On passe au tour du joueur
            updateStatusLabel(); // On met à jour le statut du jeu
            checkGameState(); // On vérifie l'état du jeu
        }
    }

    /**
     * Méthode checkGameState ()
     * Cette méthode permet de vérifier l'état du jeu.
     */
    private void checkGameState() {
        int[] playerWinCombo = checkWin(1); // On vérifie si le joueur a gagné
        int[] aiWinCombo = checkWin(-1); //  On vérifie si l'IA a gagné

        if (playerWinCombo != null || aiWinCombo != null) { // Si un joueur a gagné

            // On récupère la combinaison gagnante
            int[] winningCombination = playerWinCombo != null ? playerWinCombo : aiWinCombo;
            highlightWinningCombination(winningCombination); // On met en évidence la combinaison gagnante
            shakeWinningLine(winningCombination, () -> { // On secoue la ligne gagnante
                String winnerMessage = playerWinCombo != null ? "Player X wins!" : "AI wins!"; // On affiche le message de victoire
                displayEndGameAnimation(winnerMessage); // On affiche l'animation de fin de jeu
                disableGridWithAnimation(); // On désactive la grille avec une animation
            });

            gameOver = true; // Le jeu est terminé
        } else if (isBoardFull()) { // Si le plateau est plein
            gameOver = true; // Le jeu est terminé
            displayEndGameAnimation("Draw!"); // On affiche le message de match nul
            disableGridWithAnimation(); // On désactive la grille avec une animation
        }
    }

    /**
     * Méthode checkWin ()
     * Cette méthode permet de vérifier si un joueur a gagné.
     * @param player : joueur
     * @return la combinaison gagnante
     */
    private int[] checkWin(int player) {
        for (int[] combo : WINNING_COMBINATIONS) { // Pour chaque combinaison gagnante

            // Si le joueur a gagné avec la combinaison
            if (gameState[combo[0]] == player && gameState[combo[1]] == player && gameState[combo[2]] == player) {
                return combo; // On retourne la combinaison gagnante
            }
        }
        return null; // Sinon, on retourne null
    }

    /**
     * Méthode isBoardFull ()
     * Cette méthode permet de vérifier si le plateau est plein.
     * @return true si le plateau est plein, sinon false
     */
    private boolean isBoardFull() {
        for (double val : gameState) { // Pour chaque cellule du plateau
            if (val == 0) { // Si la cellule est vide
                return false; // On retourne false
            }
        }
        return true; // Sinon, le plateau est plein, on retourne true
    }


    /************************************************************
     * Affichage et interactions UI
    ************************************************************/

    /**
     * Méthode updateStatusLabel ()
     * Cette méthode permet de mettre à jour l'étiquette de statut.
     */
    private void updateStatusLabel() {
        if (gameOver) { // Si le jeu est terminé
            statusLabel.setText("Game Over!"); // On affiche le message de fin de jeu
        } else if (isPlayerXTurn) { // Si c'est le tour du joueur
            statusLabel.setText("Player's Turn: X");
        } else { // Sinon, c'est le tour de l'IA
            statusLabel.setText("AI's Turn");
        }
    }

    /**
     * Méthode drawX ()
     * Cette méthode permet de dessiner X dans une cellule.
     * @param cell : cellule
     */
    private void drawX(Pane cell) {
        Line line1 = new Line(10, 10, cell.getWidth() - 10, cell.getHeight() - 10); // Ligne 1
        Line line2 = new Line(10, cell.getHeight() - 10, cell.getWidth() - 10, 10); // Ligne 2
        cell.getChildren().addAll(line1, line2); // On ajoute les lignes à la cellule
        playSound(soundPlayerHuman); // On joue l'effet sonore
    }

    /**
     * Méthode drawO ()
     * Cette méthode permet de dessiner O dans une cellule.
     * @param cell : cellule
     */
    private void drawO(Pane cell) {
        Circle circle = new Circle(cell.getWidth() / 2, cell.getHeight() / 2, 40); // Cercle
        circle.setCenterX(cell.getWidth() / 2); // Centre X
        circle.setCenterY(cell.getHeight() / 2); // Centre Y
        circle.setStrokeWidth(5); // Largeur du cercle
        circle.setFill(null); // Remplissage du cercle
        circle.setStroke(javafx.scene.paint.Color.BLUE); // Couleur du cercle
        Platform.runLater(() -> {  // On exécute la tâche dans la file d'attente de la plateforme
            cell.getChildren().add(circle); // On ajoute le cercle à la cellule
        });
        playSound(soundPlayerAI); // On joue l'effet sonore
    }

    /**
     * Méthode clearBoard ()
     * Cette méthode permet de nettoyer le plateau.
     */
    private void clearBoard() {
        Arrays.fill(gameState, 0);  // On remplit l'état du jeu avec des zéros

        // On nettoie les cellules de la grille
        for (Pane cell : Arrays.asList(cell00, cell01, cell02, cell10, cell11, cell12, cell20, cell21, cell22)) {
            cell.getChildren().clear(); // Enlève X et O
            cell.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-opacity: 1;"); // Remet le style par défaut
        }
        statusLabel.setText(""); // On efface le statut du jeu
    }

    /************************************************************
     * Animation et style visuel
    ************************************************************/

    /**
     * Méthode shakeWinningLine ()
     * Cette méthode permet de secouer la ligne gagnante.
     * @param winningCells : cellules gagnantes
     * @param onAnimationEnd : action à effectuer à la fin de l'animation
     */
    private void shakeWinningLine(int[] winningCells, Runnable onAnimationEnd) {
        Duration duration = Duration.millis(100); // On définit la durée de l'animation
        AtomicInteger animationsLeft = new AtomicInteger(winningCells.length); // On initialise le nombre d'animations restantes

        for (int index : winningCells) { // Pour chaque cellule gagnante
            Pane cell = getCellByIndex(index); // On récupère la cellule
            TranslateTransition shake = new TranslateTransition(duration, cell); // On crée une transition de translation
            shake.setByX(10); // On définit la translation en X
            shake.setCycleCount(6); // On définit le nombre de cycles
            shake.setAutoReverse(true); // On active l'effet de retour
            shake.setOnFinished(e -> { // À la fin de l'animation
                if (animationsLeft.decrementAndGet() == 0) { // Si toutes les animations sont terminées
                    onAnimationEnd.run(); // On exécute l'action à effectuer à la fin de l'animation
                }
            });
            shake.playFromStart(); // On démarre l'animation
        }
    }

    /**
     * Méthode displayEndGameAnimation ()
     * Cette méthode permet d'afficher l'animation de fin de jeu.
     * @param winner : gagnant
     */
    private void displayEndGameAnimation(String winner) {
        Platform.runLater(() -> { // On exécute la tâche dans la file d'attente de la plateforme

            if (overlayPane != null) { // Si le panneau de superposition est défini
                overlayPane.setVisible(true); // On le rend visible
                overlayPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.75);");  // On définit le style

                // On crée une transition de fondu
                FadeTransition fade = new FadeTransition(Duration.seconds(1), overlayPane);
                fade.setFromValue(0); // Valeur de départ
                fade.setToValue(0.75); // Valeur d'arrivée
                fade.play(); // On démarre la transition
            }

            Label winLabel = new Label(winner); // On crée une étiquette pour le gagnant
            winLabel.setStyle("-fx-font-size: 36px; -fx-text-fill: red; -fx-background-color: white; -fx-background-radius: 20px; -fx-border-color: black; -fx-border-width: 2px; -fx-border-radius: 20px; -fx-padding: 10;");

            // On centre l'étiquette
            double centerX = (1025) / 2;
            double centerY = (250) / 2;
            winLabel.setLayoutX(centerX);
            winLabel.setLayoutY(centerY);

            // On ajoute l'étiquette à la fenêtre
            humainvsiaPage.getChildren().add(winLabel);
            ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(2), winLabel);
            scaleTransition.setFromX(0.1);
            scaleTransition.setFromY(0.1);
            scaleTransition.setToX(1);
            scaleTransition.setToY(1);
            scaleTransition.play();
        });
    }

    /**
     * Méthode disableGridWithAnimation ()
     * Cette méthode permet de désactiver la grille avec une animation.
     */
    private void disableGridWithAnimation() {
        Platform.runLater(() -> { // On exécute la tâche dans la file d'attente de la plateforme
            if (overlayPane != null && !overlayPane.isVisible()) { // Si le panneau de superposition n'est pas visible
                overlayPane.setVisible(true); // On le rend visible
                FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1), overlayPane); // On crée une transition de fondu
                fadeTransition.setFromValue(0); // Valeur de départ
                fadeTransition.setToValue(0.75); // Valeur d'arrivée
                fadeTransition.play(); // On démarre la transition
            }
        });
    }

    /**
     * Méthode styleButton ()
     * Cette méthode permet de styliser un bouton.
     * @param button : bouton à styliser
     * @param backgroundColor : couleur de fond
     * @param hoverColor : couleur de survol
     */
    private void styleButton(Button button, String backgroundColor, String hoverColor) {
        button.setStyle("-fx-background-color: " + backgroundColor + "; -fx-text-fill: white; -fx-font-size: 20px; -fx-border-radius: 5; -fx-background-radius: 5;");
        DropShadow dropShadow = new DropShadow();
        dropShadow.setColor(Color.valueOf(backgroundColor).darker());
        dropShadow.setRadius(5);
        dropShadow.setSpread(0.5);

        // Effet d'ombre portée
        button.setEffect(dropShadow);
        button.setPrefSize(240, 60);

        // Effet de survol
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: " + hoverColor + "; -fx-text-fill: white; -fx-font-size: 20px; -fx-border-radius: 5; -fx-background-radius: 5;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: " + backgroundColor + "; -fx-text-fill: white; -fx-font-size: 20px; -fx-border-radius: 5; -fx-background-radius: 5;"));
    }

    /**
     * Méthode highlightWinningCombination ()
     * Cette méthode permet de mettre en évidence la combinaison gagnante.
     * @param combo : combinaison gagnante
     */
    private void highlightWinningCombination(int[] combo) {
        for (int index : combo) { // Pour chaque cellule de la combinaison gagnante
            Pane winningCell = getCellByIndex(index); // On récupère la cellule
            Platform.runLater(() -> { // On exécute la tâche dans la file d'attente de la plateforme
                winningCell.setStyle("-fx-background-color: lightgreen; -fx-opacity: 1;"); // On met en évidence la cellule
            });
        }
    }



    /************************************************************
     * Gestion des événements
    ************************************************************/

    /**
     * Méthode restartGame ()
     * Cette méthode permet de redémarrer le jeu.
     * @param event : événement de clic sur le bouton
     */
    @FXML
    private void restartGame(ActionEvent event) {
        Platform.runLater(() -> { // On exécute la tâche dans la file d'attente de la plateforme
            clearEndGameDisplay(); // On efface l'affichage de fin de jeu
            difficultyOverlayPane.setVisible(true); // On rend le panneau de difficulté visible
        });
    }

    /**
     * Méthode restartGameWithDifficulty ()
     * Cette méthode permet de redémarrer le jeu avec une difficulté spécifique.
     * @param event : événement de clic sur le bouton
     */
    @FXML
    private void restartGameWithDifficulty(ActionEvent event) {
        Button clickedButton = (Button) event.getSource(); // On récupère le bouton cliqué
        String difficulty = clickedButton.getText(); // On récupère la difficulté

        configFileLoader.loadConfigFile("resources/config.txt"); // On charge le fichier de configuration
        Config config = configFileLoader.get(difficulty.substring(0, 1).toUpperCase()); // On récupère la configuration

        // On charge la configuration AI
        String modelFileName = "model_" + config.numberOfhiddenLayers + "_"
                                + config.hiddenLayerSize + "_" + config.learningRate + ".srl";

        // On charge le modèle AI à partir du fichier s'il existe
        File modelFile = new File("resources/models/" + modelFileName);

        // Logique pour gérer la présence ou non du fichier modèle
        try {
            if (!modelFile.exists()) { // Si le fichier modèle n'existe pas
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/learn.fxml")); // On charge la vue Learn
                Parent root = loader.load(); // On charge la racine
                LearnController learnController = loader.getController(); // On récupère le contrôleur Learn
                learnController.setConfig(config); // On définit la configuration

                learnController.setCompletionListener(newConfig -> { // On définit un écouteur de fin
                    transitionToGame(newConfig, event); // On passe à la vue de jeu
                });

                Scene scene = new Scene(root); // On crée une scène
                Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow(); // On récupère le stage
                stage.setScene(scene); // On définit la scène
                stage.show(); // On affiche le stage
            } else {
                transitionToGame(config, event); // On passe à la vue de jeu avec la configuration actuelle
            }
        } catch (IOException e) {
            e.printStackTrace(); // On affiche l'erreur en cas d'échec de chargement
        }
    }

    /**
     * Méthode backMenu ()
     * Cette méthode permet de revenir au menu.
     * @param event : événement de clic sur le bouton
     */
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
     * Méthode transitionToGame ()
     * Cette méthode permet de passer à la vue de jeu.
     * @param newConfig : nouvelle configuration
     * @param event : événement de clic sur le bouton
     */
    public void transitionToGame(Config newConfig, ActionEvent event) {
        Platform.runLater(() -> {
            try {
                if (newConfig == null) { // Si la nouvelle configuration est nulle
                    System.err.println("Erreur : La nouvelle configuration est nulle.");
                    return;
                }

                // On charge la vue Humain vs IA
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/humainvsia.fxml"));
                Parent root = loader.load();
                HumainVsIAController controller = loader.getController();
                controller.setConfig(newConfig);
                Scene scene = new Scene(root);

                // On affiche la vue Humain vs IA
                Stage stage = Main.getPrimaryMainStage();
                if (stage != null) { // Si le stage est défini
                    stage.setScene(scene); // On définit la scène
                    stage.setTitle("Humain vs IA"); // On définit le titre
                    stage.show(); // On affiche le stage
                } else {
                    System.err.println("Erreur critique : Le stage est nul.");
                }
            } catch (IOException e) {
                System.err.println("Erreur lors du chargement de la vue : " + e.getMessage());
                e.printStackTrace();
            }
        });
    }


    /************************************************************
     * Autres utilitaires
    ************************************************************/

    /**
     * Méthode getCellByIndex ()
     * Cette méthode permet de récupérer une cellule par son indice.
     * @param index : indice de la cellule
     * @return la cellule correspondante
     */
    private Pane getCellByIndex(int index) {
        switch(index) {
            case 0: return cell00;
            case 1: return cell01;
            case 2: return cell02;
            case 3: return cell10;
            case 4: return cell11;
            case 5: return cell12;
            case 6: return cell20;
            case 7: return cell21;
            case 8: return cell22;
            default: return null;
        }
    }

    /**
     * Méthode clearEndGameDisplay ()
     * Cette méthode permet d'effacer l'affichage de fin de jeu.
     */
    private void clearEndGameDisplay() {
        Platform.runLater(() -> {
            if (overlayPane != null && difficultyOverlayPane != null && humainvsiaPage != null) {
                overlayPane.setVisible(false);
                difficultyOverlayPane.setVisible(false);
                chooseDifficultyLabel.setVisible(false);
                statusLabel.setVisible(true);
                humainvsiaPage.getChildren().removeIf(node -> node instanceof Label && node != statusLabel && node != chooseDifficultyLabel);
                clearBoard();
            }
        });
    }

    /**
     * Méthode playSound ()
     * Cette méthode permet de jouer un son.
     * @param player : lecteur multimédia
     */
    private void playSound(MediaPlayer player) {
        if (player != null) {
            player.stop(); // Arrêter le son précédent pour éviter les superpositions
            player.play(); // Jouer le son
        }
    }

}