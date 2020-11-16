import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class ClientApp extends Application {

    private static final int TILE_SIZE = 40;
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    private static final int X_TILES = WIDTH / TILE_SIZE;
    private static final int Y_TILES = HEIGHT / TILE_SIZE;

    //declare grid
    private Tile[][] grid = new Tile[X_TILES][Y_TILES];

    private Parent createContent() {
        Pane root = new Pane();
        root.setPrefSize(WIDTH, HEIGHT);

        for (int y=0; y <  Y_TILES; y++){
            for (int x = 0; x < X_TILES; x++){
                Tile tile = new Tile(x,y,Math.random() < 0.2);

                grid[x][y] = tile;
                root.getChildren().add(tile);
            }
        }
        for (int y=0; y <  Y_TILES; y++){
            for (int x = 0; x < X_TILES; x++){
             Tile tile = grid[x][y];

            }
        }

        return root;
    }

    private List<Tile> getNeighbors(Tile tile){
        List<Tile> neighbors = new ArrayList<>();

        return neighbors
    }

    private class Tile extends StackPane {
        private int x,y;
        private boolean hasBomb;
        private int bombs = 0;

        private Rectangle border = new Rectangle(TILE_SIZE - 2, TILE_SIZE -2);
        private Text text = new Text();

        public Tile(int x, int y, boolean hasBomb){
            this.x = x;
            this.y = y;
            this.hasBomb = hasBomb;

            border.setStroke(Color.LIGHTGRAY);

            // set bombs to have "X"
            text.setText(hasBomb ? "X" : "");

            getChildren().addAll(border,text);

            setTranslateX(x * TILE_SIZE);
            setTranslateY(y * TILE_SIZE);
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
    Scene scene = new Scene(createContent());

    stage.setScene(scene);
    stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}