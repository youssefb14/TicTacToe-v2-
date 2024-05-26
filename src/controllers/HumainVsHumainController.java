package controllers;


import java.io.IOException; // Pour gérer les exceptions d'entrée/sortie
import java.net.URL; // Pour représenter une ressource
import java.util.Objects; // Pour les méthodes utilitaires
import java.util.ResourceBundle; // Pour gérer les ressources localisées

import javafx.animation.FadeTransition; // Pour les animations de fondu
import javafx.animation.ScaleTransition; // Pour les animations de zoom
import javafx.animation.TranslateTransition; // Pour les animations de translation
import javafx.application.Platform; // Pour exécuter des tâches sur le thread JavaFX
import javafx.event.ActionEvent; // Pour gérer les événements
import javafx.fxml.FXML; // Pour annoter les éléments du fichier FXML
import javafx.fxml.FXMLLoader; // Pour charger un fichier FXML
import javafx.fxml.Initializable; // Pour initialiser un contrôleur
import javafx.geometry.Pos; // Pour définir la position des éléments
import javafx.scene.Node; // Pour les éléments de l'interface utilisateur
import javafx.scene.Parent; // Pour les éléments de l'interface utilisateur
import javafx.scene.Scene; // Pour représenter une scène JavaFX
import javafx.scene.control.Button; // Pour les boutons
import javafx.scene.control.Label; // Pour les labels
import javafx.scene.effect.DropShadow; // Pour les ombres
import javafx.scene.image.Image; // Pour les images
import javafx.scene.input.MouseEvent; // Pour les événements de souris
import javafx.scene.layout.AnchorPane; // Pour les panneaux
import javafx.scene.layout.Background; // Pour les arrière-plans
import javafx.scene.layout.BackgroundImage; // Pour les images de fond
import javafx.scene.layout.BackgroundPosition; // Pour la position de l'arrière-plan
import javafx.scene.layout.BackgroundRepeat; // Pour la répétition de l'arrière-plan
import javafx.scene.layout.BackgroundSize; // Pour la taille de l'arrière-plan
import javafx.scene.layout.Pane; // Pour les panneaux
import javafx.scene.media.Media; // Pour les fichiers multimédias
import javafx.scene.media.MediaPlayer; // Pour les lecteurs multimédias
import javafx.scene.paint.Color; // Pour les couleurs
import javafx.scene.shape.Circle; // Pour les cercles
import javafx.scene.shape.Line; // Pour les lignes
import javafx.stage.Stage; // Pour les scènes
import javafx.util.Duration; // Pour les durées


/**
 * Classe HumainVsHumainController
 * Contrôleur pour le jeu Tic Tac Toe en mode Humain vs Humain.
 * Cette classe gère la logique du jeu, les animations, l'affichage et les effets sonores.
 * @author BOUDOUNT Youssef
 */
public class HumainVsHumainController implements Initializable {

    @FXML private Pane cell00, cell01, cell02, cell10, cell11, cell12, cell20, cell21, cell22; // Les cellules de la grille
    @FXML private Label statusLabel; // Label pour afficher le statut du jeu
    @FXML private AnchorPane humainvshumain; // Parent de la grille
    @FXML private Button back_menu; // Bouton pour revenir au menu
    @FXML private Button restartButton; // Bouton pour redémarrer le jeu

	private Pane[] allCells; // Tableau contenant toutes les cellules
    private boolean isPlayerXTurn = true; // Détermine le joueur actuel
    private double[] gameState = new double[9]; // État du jeu : 1 pour X, -1 pour O, 0 pour vide
    private boolean gameOver = false; // Indique si le jeu est terminé
    private Pane overlayPane = null; // Panneau pour empêcher les clics sur la grille
    private MediaPlayer soundPlayerX; // Joueur pour le son du joueur X
    private MediaPlayer soundPlayerO; // Joueur pour le son du joueur O
    int premier = 0;


    /************************************************************
     * Initialisation et chargement des ressources
    ************************************************************/

    /**
     * Méthode initialize ()
     * Cette permet d'initialiser le contrôleur après que son élément racine ait été complètement traité.
     * @param location : URL de l'emplacement de l'objet racine
     * @param resources : ResourceBundle pour localiser l'objet racine
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(this::initializeAfterLoad); // On s'assure que l'initialisation est effectuée après le chargement de l'interface utilisateur
        loadSoundEffects(); // On charge les effets sonores
    }

    /**
     * Méthode initializeAfterLoad()
     * Cette méthode permet d'initialiser le contrôleur après le chargement de l'interface utilisateur.
     */
    @FXML
    public void initializeAfterLoad() {
        allCells = new Pane[]{cell00, cell01, cell02, cell10, cell11, cell12, cell20, cell21, cell22}; // On initialise le tableau des cellules
        resetGame();  // On réinitialise le jeu

        Platform.runLater(() -> { // On charge l'image de fond de la grille
            try {
                Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/3.png"))); // On charge l'image

                // On définit l'image de fond de la grille
                BackgroundImage backgroundImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
                humainvshumain.setBackground(new Background(backgroundImage)); // On l'applique à la grille
            } catch (Exception e) {
                e.printStackTrace(); // On affiche l'erreur
            }
        });

        // On stylise les boutons du jeu
		styleButton(restartButton, "#4CAF50", "#45a049");
        styleButton(back_menu, "#2196F3", "#1e88e5");
    }

    /**
     * Methode loadSoundEffects()
     * Cette méthode permet de charger les effets sonores du jeu.
     */
    private void loadSoundEffects() {
        try {
            String soundX = getClass().getResource("/sound/mouvX.mp3").toExternalForm(); // On charge le son du joueur X
            Media soundMediaX = new Media(soundX); // On crée un média pour le son du joueur X
            soundPlayerX = new MediaPlayer(soundMediaX); // On crée un lecteur pour le son du joueur X

            String soundO = getClass().getResource("/sound/mouvO.mp3").toExternalForm(); // On charge le son du joueur O
            Media soundMediaO = new Media(soundO); // On crée un média pour le son du joueur O
            soundPlayerO = new MediaPlayer(soundMediaO); // On crée un lecteur pour le son du joueur O
        } catch (NullPointerException e) {
            System.err.println("Le chemin du fichier son n'a pas pu être résolu.");
        } catch (Exception e) {
            System.err.println("Failed to load sound files: " + e);
        }
    }


    /************************************************************
     * Logique du jeu
    ************************************************************/

    /**
     * Méthode resetGame()
     * Cette méthode permet de réinitialiser le jeu.
     */
    private void resetGame() {
    	if (overlayPane != null) { // Si le panneau de superposition est déjà créé
            humainvshumain.getChildren().remove(overlayPane); // On le retire
            overlayPane = null; // On le réinitialise
        }
        isPlayerXTurn = true; // On définit le joueur X comme joueur actuel
        gameState = new double[9]; // On réinitialise l'état du jeu
        gameOver = false; // On réinitialise le statut du jeu
        clearBoard(); // On efface la grille
        clearEndGameMessage(); // On efface le message de fin de jeu
        updateStatusLabel(); // On met à jour le statut du jeu
        
    }

    /**
     * Méthode handleCellClick()
     * Cette méthode permet de gérer le clic sur une cellule de la grille.
     * @param event : l'événement de souris
     */
    @FXML
    private void handleCellClick(MouseEvent event) {
        if (gameOver) return; // Si le jeu est terminé, on ne fait rien

        Pane clickedCell = (Pane) event.getSource(); // On récupère la cellule cliquée
        int cellIndex = getCellIndex(clickedCell); // On récupère l'index de la cellule

        if (gameState[cellIndex] == 0 && !gameOver) { // Si la cellule est vide et que le jeu n'est pas terminé
            gameState[cellIndex] = isPlayerXTurn ? 1 : -1;  // On met à jour l'état du jeu
            drawSymbol(clickedCell, isPlayerXTurn);  // On dessine le symbole
            isPlayerXTurn = !isPlayerXTurn;  // On change de joueur
            checkGameState();  // On vérifie l'état du jeu
        }
    }

    /**
     * Méthode checkGameState()
     * Cette méthode permet de vérifier l'état du jeu.
     */
    private void checkGameState() {
        int[][] winningCombinations = {
            {0, 1, 2}, {3, 4, 5}, {6, 7, 8}, // Lignes
            {0, 3, 6}, {1, 4, 7}, {2, 5, 8}, // Colonnes
            {0, 4, 8}, {2, 4, 6} // Diagonales
        }; // Tableau des combinaisons gagnantes possibles

        for (int[] combination : winningCombinations) { // On parcourt toutes les combinaisons gagnantes

            // Si une combinaison gagnante est trouvée pour le joueur X
            if (gameState[combination[0]] == 1 && gameState[combination[1]] == 1 && gameState[combination[2]] == 1) {
                gameOver = true; // On met fin au jeu
                statusLabel.setText(" "); // On efface le statut du jeu
                highlightWinningCells(combination); // On met en évidence les cellules gagnantes
                shakeWinningLine(combination); // On secoue la ligne gagnante
                disableGridWithAnimation(); // On désactive la grille avec une animation
                displayEndGameAnimation("Player X wins!"); // On affiche l'animation de fin de jeu
                break; // On sort de la boucle
            }
            // Si une combinaison gagnante est trouvée pour le joueur O
            else if (gameState[combination[0]] == -1 && gameState[combination[1]] == -1 && gameState[combination[2]] == -1) {
                gameOver = true; // On met fin au jeu
                statusLabel.setText(" "); // On efface le statut du jeu
                highlightWinningCells(combination); // On met en évidence les cellules gagnantes
                shakeWinningLine(combination); // On secoue la ligne gagnante
                disableGridWithAnimation(); // On désactive la grille avec une animation
                displayEndGameAnimation("Player O wins!"); // On affiche l'animation de fin de jeu
                break;
            }
        }

        if (!gameOver && isBoardFull()) { // Si aucun gagnant n'a été déterminé et que le plateau est complet
            gameOver = true; // On met fin au jeu
            disableGridWithAnimation(); // On désactive la grille avec une animation
            displayEndGameAnimation("Draw!"); // On affiche l'animation de fin de jeu
        }

    }

    /**
     * Méthode checkWin()
     * Cette méthode permet de vérifier si un joueur a gagné.
     * @param player : le joueur
     * @return
     */
    private boolean checkWin(int player) {
        for (int i = 0; i < 3; i++) { // On parcourt les lignes et les colonnes

            // On vérifie les lignes de la grille
            if (gameState[i * 3] == player && gameState[i * 3 + 1] == player && gameState[i * 3 + 2] == player) {
                return true; // On retourne vrai si une ligne gagnante est trouvée
            }

            // On vérifie les colonnes de la grille
            if (gameState[i] == player && gameState[i + 3] == player && gameState[i + 6] == player) {
                return true;
            }
        }

        // On vérifie les diagonales de la grille
        if (gameState[0] == player && gameState[4] == player && gameState[8] == player) {
            return true;
        }

        // On vérifie les diagonales de la grille
        if (gameState[2] == player && gameState[4] == player && gameState[6] == player) {
            return true;
        }

        return false; // On retourne faux si aucune combinaison gagnante n'est trouvée
    }

    /**
     * Méthode isBoardFull()
     * Cette méthode permet de vérifier si la grille est pleine.
     * @return
     */
    private boolean isBoardFull() {
        for (double val : gameState) { // On parcourt l'état du jeu
            if (val == 0) { // Si une cellule est vide (0 = vide)
                return false; // On retourne faux car la grille n'est pas pleine
            }
        }
        return true; // On retourne vrai si la grille est pleine
    }

    /**
     * Méthode getCellIndex()
     * Cette méthode permet de récupérer l'index d'une cellule.
     * @param cell : la cellule
     * @return : l'index de la cellule
     */
    private int getCellIndex(Pane cell) {
        String cellId = cell.getId(); // On récupère l'identifiant de la cellule
        String cellIdNumericPart = cellId.substring(4, 6); // On récupère la partie numérique de l'identifiant
        int rowIndex = Character.getNumericValue(cellIdNumericPart.charAt(0)); // On récupère l'indice de la ligne
        int colIndex = Character.getNumericValue(cellIdNumericPart.charAt(1)); // On récupère l'indice de la colonne
        return rowIndex * 3 + colIndex; // On retourne l'index de la cellule
    }

    /************************************************************
     * Gestion des animations
    ************************************************************/

    /**
     * Méthode displayEndGameAnimation()
     * Cette méthode permet d'afficher l'animation de fin de jeu.
     * @param winner : le gagnant
     */
    private void displayEndGameAnimation(String winner) {
        Label winLabel = new Label(winner); // Création d'un label pour afficher le gagnant

        // On definit le style du label
        winLabel.setStyle("-fx-font-size: 36px; -fx-text-fill: red; -fx-background-color: white; -fx-background-radius: 20px; -fx-border-color: black; -fx-border-width: 2px; -fx-border-radius: 20px; -fx-padding: 10;");
        winLabel.setAlignment(Pos.CENTER); // On centre le texte dans le label

        // On calcule la largeur et la hauteur du label
        double labelWidthEstimate = winner.length() * 20; // On estime la largeur du label
        double labelHeightEstimate = 70; // On estime la hauteur du label

        // Calcul pour centrer le label
        double centerX = (1280 - labelWidthEstimate) / 2;
        double centerY = (720 - labelHeightEstimate) / 2;

        winLabel.setLayoutX(centerX); // On définit la position X du label
        winLabel.setLayoutY(centerY); // On définit la position Y du label

        Platform.runLater(() -> humainvshumain.getChildren().add(winLabel)); // On ajoute le label a la grille

        // Animation de zoom pour afficher le label
        ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(2), winLabel);
        scaleTransition.setFromX(0.1);
        scaleTransition.setFromY(0.1);
        scaleTransition.setToX(1);
        scaleTransition.setToY(1);
        scaleTransition.setCycleCount(1);
        scaleTransition.setAutoReverse(false);

        Platform.runLater(scaleTransition::play); // On démarre l'animation
    }

    /**
     * Méthode shakeWinningLine()
     * @param winningCells : les cellules gagnantes
     */
    private void shakeWinningLine(int[] winningCells) {
        Duration duration = Duration.millis(100); // Durée de l'animation

        for (int index : winningCells) { // On parcourt les cellules gagnantes
            Pane cell = allCells[index]; // On récupère la cellule

            TranslateTransition shake = new TranslateTransition(duration, cell); // On crée une animation de translation
            shake.setByX(10); // On définit le déplacement en X
            shake.setCycleCount(6); // On définit le nombre de cycles
            shake.setAutoReverse(true); // On active l'effet de retour

            shake.playFromStart(); // On démarre l'animation
        }
    }

    /**
     * Méthode disableGridWithAnimation()
     * Cette méthode permet de désactiver la grille avec une animation.
     */
    private void disableGridWithAnimation() {
        if (overlayPane == null) { // Si le panneau de superposition n'est pas encore créé
            overlayPane = new Pane(); // Crée un panneau pour empêcher les clics sur la grille
            overlayPane.setPrefSize(humainvshumain.getWidth(), humainvshumain.getHeight()); // Définit la taille du panneau
            overlayPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.4);"); // Définit la couleur de fond
            overlayPane.setLayoutX(0); // Définit la position X
            overlayPane.setLayoutY(0); // Définit la position Y
            humainvshumain.getChildren().add(overlayPane); // Ajoute le panneau à la grille
        }

        // Animation de fondu pour rendre le panneau visible progressivement
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1), overlayPane);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);

        // On empêche les clics sur la grille après l'animation
        fadeTransition.setOnFinished(event -> overlayPane.setMouseTransparent(true));

        fadeTransition.play(); // On démarre l'animation
    }

    /**
     * Méthode highlightWinningCells()
     * Cette méthode permet de mettre en évidence les cellules gagnantes.
     * @param winningCells : les cellules gagnantes
     */
    private void highlightWinningCells(int[] winningCells) {
        for (int index : winningCells) { // On parcourt les cellules gagnantes

            // On met en évidence les cellules gagnantes
            allCells[index].setStyle("-fx-background-color: #90EE90; -fx-border-color: black; -fx-opacity: 1;");
        }
    }

    /************************************************************
     * Affichage et style
    ************************************************************/
    /**
     * Méthode drawSymbol()
     * Cette méthode permet de dessiner le symbole dans une cellule.
     * @param cell : la cellule
     * @param isX : le symbole
     */
    private void drawSymbol(Pane cell, boolean isX) {
    	
        if (isX) { // Si le symbole est X
            drawX(cell); // On dessine X
            playSound(soundPlayerX); // On joue le son du joueur X
        } else {
            drawO(cell);
            playSound(soundPlayerO);
        }
        updateStatusLabel(); // On met à jour le statut du jeu
    }

    /**
     * Méthode drawX()
     * Cette méthode permet de dessiner le symbole X dans une cellule.
     * @param cell : la cellule
     */
    private void drawX(Pane cell) {
        Line line1 = new Line(10, 10, cell.getWidth() - 10, cell.getHeight() - 10); // Dessine la première ligne
        Line line2 = new Line(10, cell.getHeight() - 10, cell.getWidth() - 10, 10); // Dessine la deuxième ligne
        cell.getChildren().addAll(line1, line2); // Ajoute les lignes à la cellule
    }

    /**
     * Méthode drawO(Pane cell)
     * Cette méthode permet de dessiner le symbole O dans une cellule.
     * @param cell : la cellule
     */
    private void drawO(Pane cell) {
        Circle circle = new Circle(cell.getWidth() / 2, cell.getHeight() / 2, 30); // Dessine un cercle
        circle.setCenterX(cell.getWidth() / 2); // Définit le centre en X
        circle.setCenterY(cell.getHeight() / 2); // Définit le centre en Y
        circle.setStrokeWidth(5); // Définit l'épaisseur du trait
        circle.setStroke(javafx.scene.paint.Color.BLACK); // Définit la couleur du trait
        circle.setFill(null); // Remplit le cercle avec une couleur transparente
        Platform.runLater(() -> cell.getChildren().add(circle)); // Ajoute le cercle à la cellule
    }

    /**
     * Méthode clearBoard()
     * Cette méthode permet d'effacer la grille.
     */
    private void clearBoard() {
        for (Pane cell : allCells) { // On parcourt toutes les cellules
            cell.getChildren().clear(); // On efface le contenu de la cellule
            cell.setStyle("-fx-background-color: white; -fx-border-color: black;"); // On réinitialise le style de la cellule
        }
    }

    /**
     * Méthode clearEndGameMessage()
     * Cette méthode permet d'effacer le message de fin de jeu.
     */
    private void clearEndGameMessage() {
        Platform.runLater(() -> { // On exécute la tâche sur le thread JavaFX

            // On récupère le parent du label de statut
            AnchorPane parent = (AnchorPane) statusLabel.getParent();

            // On supprime tous les labels sauf le label de statut
            parent.getChildren().removeIf(node -> node instanceof Label && !node.equals(statusLabel));
        });
    }

    /**
     * Méthode updateStatusLabel()
     * Cette méthode permet de mettre à jour le statut du jeu.
     */
    private void updateStatusLabel() {
    	
        if (gameOver) { // Si le jeu est terminé
            if (checkWin(1)) { // Si le joueur X gagne
                statusLabel.setText("Player X wins!"); // Affiche le message de victoire
            } else if (checkWin(-1)) { // Si le joueur O gagne
                statusLabel.setText("Player O wins!");
            } else { // Si le jeu est un match nul
                statusLabel.setText("Draw!");
            }
        } else { // Si le jeu n'est pas terminé
            // On affiche le statut du joueur actuel
        	
        	if (premier == 0) {
        		statusLabel.setText("Player X's Turn");
        		premier = 1;
        	} else {
            statusLabel.setText(isPlayerXTurn ? "Player O's Turn" : "Player X's Turn");
        	}
        }
    }

    /**
     * Méthode styleButton()
     *
     * @param button
     * @param backgroundColor
     * @param hoverColor
     */
    private void styleButton(Button button, String backgroundColor, String hoverColor) {
        // Style du bouton
        button.setStyle("-fx-background-color: " + backgroundColor + "; -fx-text-fill: white; -fx-font-size: 20px; -fx-border-radius: 5; -fx-background-radius: 5;");
        DropShadow dropShadow = new DropShadow(); // Ombre portée
        dropShadow.setColor(Color.valueOf(backgroundColor).darker()); // Couleur de l'ombre
        dropShadow.setRadius(5); // Rayon de l'ombre
        dropShadow.setSpread(0.5); // Étalement de l'ombre

        button.setEffect(dropShadow); // Appliquer l'ombre au bouton
        button.setPrefSize(240, 60);  // Taille du bouton

        // Effet de survol
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: " + hoverColor + "; -fx-text-fill: white; -fx-font-size: 20px; -fx-border-radius: 5; -fx-background-radius: 5;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: " + backgroundColor + "; -fx-text-fill: white; -fx-font-size: 20px; -fx-border-radius: 5; -fx-background-radius: 5;"));
    }

    /************************************************************
     * Gestion des événements
    ************************************************************/
    /**
     * Méthode backMenu()
     * Cette méthode permet de revenir au menu principal.
     * @param event : l'événement de clic
     */
    @FXML
    public void backMenu(ActionEvent event) {
        try {
            // On charge la vue du menu
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/menu.fxml"));
            Parent root = loader.load(); // On charge le parent
            Scene scene = new Scene(root); // On crée une nouvelle scène
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow(); // On récupère la scène actuelle
            stage.setScene(scene); // On définit la nouvelle scène
            stage.setTitle("Menu"); // On définit le titre de la fenêtre
            stage.show(); // On affiche la fenêtre
        } catch (IOException e) {
            e.printStackTrace(); // On affiche l'erreur dans le cas où le fichier FXML n'est pas trouvé
        }
    }

    /**
     * Méthode restartGame()
     * Cette méthode permet de redémarrer le jeu.
     */
    @FXML
    private void restartGame() {
        resetGame(); // On réinitialise le jeu
        premier = 0;
	}

    /************************************************************
     * Gestion des effets sonores
    ************************************************************/
    /**
     * Méthode playSound()
     * Cette méthode permet de jouer un son.
     * @param player : le lecteur multimédia
     */
    private void playSound(MediaPlayer player) {
        if (player != null) { // Si le lecteur multimédia est initialisé
            player.stop(); // On arrête le son
            player.play(); // On joue le son
        }
    }
}