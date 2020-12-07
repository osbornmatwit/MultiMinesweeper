package multiminesweeper;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.control.TextField;
import multiminesweeper.ui.GameOverWindow;
import multiminesweeper.ui.LoginWindow;

import javax.swing.text.Position;
import java.util.ArrayList;
import java.util.List;

public class ClientApp extends Application {

    private static final int TILE_SIZE = 40;
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    private static final int X_TILES = WIDTH / TILE_SIZE;
    private static final int Y_TILES = HEIGHT / TILE_SIZE;

    //declare grid
    private final Tile[][] grid = new Tile[X_TILES][Y_TILES];
    private Scene gameScene;

    private Parent createContent() {
        Pane root = new Pane();
        root.setPrefSize(WIDTH, HEIGHT);

        for (int y = 0; y < Y_TILES; y++) {
            for (int x = 0; x < X_TILES; x++) {
                Tile tile = new Tile(x, y, Math.random() < 0.2);

                grid[x][y] = tile;
                root.getChildren().add(tile);
            }
        }
        for (int y = 0; y < Y_TILES; y++) {
            for (int x = 0; x < X_TILES; x++) {
                Tile tile = grid[x][y];

                if (tile.hasBomb){
                    continue;
                }

                // obtain stream of elements and filter them
                long bombs = getNeighbors(tile).stream()
                    .filter(t -> t.hasBomb)
                    .count();
                //set numerical values to the tiles
                if ( bombs > 0 )
                    tile.text.setText( String.valueOf( bombs ) ) ;
            }
        }

        return root;
    }

    private List<Tile> getNeighbors(Tile tile) {
        List<Tile> neighbors = new ArrayList<>();

        int[] points = new int[]{-1, -1, -1, 0, -1, 1, 0, -1, 1, -1, 1, 0, 1, 1};

        for (int i = 0; i < points.length; i++) {
            int dx = points[i];
            int dy = points[++i];

            // neighbors X & y coordinate
            int newX = tile.x + dx;
            int newY = tile.y + dy;

            //check if new x & y are valid
            // replace with method... is valid point...
            if ((newX >= 0 && newX < X_TILES) && (newY >= 0 && newY < Y_TILES)) {
                neighbors.add(this.grid[newX][newY]);
            }
        }

        return neighbors;
    }

    private class Tile extends StackPane {
        private final int x;
        private final int y;
        private final boolean hasBomb;
        private boolean isOpen = false;

        private final Rectangle border = new Rectangle(TILE_SIZE - 2, TILE_SIZE - 2);
        private final Text text = new Text();

        public Tile(int x, int y, boolean hasBomb) {
            this.x = x;
            this.y = y;
            this.hasBomb = hasBomb;

            border.setStroke(Color.LIGHTGRAY);

            //set font size
            this.text.setFont( Font.font( 18 ) ) ;
            // set bombs to have "X"
            text.setText(hasBomb ? "X" : "");
            //text not visible to user
            text.setVisible(false);

            getChildren().addAll(border, text);

            setTranslateX(x * TILE_SIZE);
            setTranslateY(y * TILE_SIZE);

            setOnMouseClicked(event -> open());
        }

        public void open(){
            if (isOpen)
                return;

            //end game
            if(hasBomb){
                System.out.println("Game Over");
                gameScene.setRoot( createContent() ) ;
                return;
            }

            isOpen = true;
            text.setVisible(true);
            border.setFill(null);

            if(text.getText().isEmpty()){
                getNeighbors(this).forEach(Tile::open);
            }
        }
    }
//TODO: Starting Position
    public class Minefield {
        public Position[] mines;
        public Position startingPosition;
        // Constructors
        public Minefield(Position[] mines, Position startingPosition) {
            this.mines = mines;
            this.startingPosition = startingPosition;
        }
    }

    @Override
    public void start(Stage stage) {
//        this.gameScene = new Scene(createContent());
//        stage.setScene(this.gameScene);
//        stage.show();
        GameOverWindow gameOver = new GameOverWindow();
//        LoginWindow loginWindow = new LoginWindow();
    }

    public static void main(String[] args) {
        launch(args);
    }

}