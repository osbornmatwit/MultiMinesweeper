package multiminesweeper.ui;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Button;


public class GameOverWindow {
    Button restartButton = new Button("Restart Game");
    Button endGame = new Button("End Game");

    public GameOverWindow() {
        StackPane gameOverRoot = new StackPane();
        gameOverRoot.getChildren().addAll(restartButton, endGame);

        Scene scene = new Scene(gameOverRoot, 300,100);
        Stage stage = new Stage();

        gameOverRoot.setAlignment(restartButton, Pos.TOP_CENTER);
        gameOverRoot.setAlignment(endGame, Pos.BOTTOM_CENTER);

        stage.setTitle("Game Over");
        stage.setScene(scene);
        stage.show();
    }
}
