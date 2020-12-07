package multiminesweeper.ui;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class GameOverWindow extends Stage {

    // default to using local variables, use class variables when you need to share an item between functions on the class

    // maybe use javafx.controls.Dialog instead
    public GameOverWindow(boolean winner) {

        // ternary expression: condition : "ifTrue" ? "ifFalse"
        // if condition is true, the expression equal to "ifTrue", otherwise "ifFalse"
        String messageText = winner ? "Congratulations, you won!" : "Too bad, you lost.";
        Text message = new Text(messageText);

        Button restartButton = new Button("Restart Game");
        Button exitButton = new Button("End Game");
        // a container that holds columns of items
        HBox buttonRow = new HBox(restartButton, exitButton);

        // a container that holds rows of items
        VBox rows = new VBox(message, buttonRow);

        Scene scene = new Scene(rows, 300, 100);

        // have the parent of the entire item be extended by the class, rather than constructing a component and showing it by hand in the constructor
        // also, let the caller decide when to show the window
        this.setTitle("Game Over");
        this.initModality(Modality.APPLICATION_MODAL);
        this.setScene(scene);
    }
}
