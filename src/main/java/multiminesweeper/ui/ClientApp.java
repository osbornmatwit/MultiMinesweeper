package multiminesweeper.ui;

import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

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

    public static void main(String[] args) {
        launch(args);
    }

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

                if (tile.hasBomb) {
                    continue;
                }

                // obtain stream of elements and filter them
                long bombs = getNeighbors(tile).stream()
                    .filter(t -> t.hasBomb)
                    .count();
                //set numerical values to the tiles
                if (bombs > 0)
                    tile.text.setText(String.valueOf(bombs));
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

    @Override
    public void start(Stage stage) {
//        this.gameScene = new Scene(createContent());
//        stage.setScene(this.gameScene);
//        stage.show();
        LoginWindow loginWindow = new LoginWindow();
        Stage loginStage = new Stage();
        loginStage.setScene(loginWindow.scene);
        loginStage.showAndWait();
        String name = loginWindow.nameField.getText();
        String password = loginWindow.passField.getText();

        // TODO: Connect to client, while showing a loading screen

        MineGrid grid = new MineGrid(WIDTH / TILE_SIZE, HEIGHT / TILE_SIZE);

        Button finishSetupButton = new Button("Finish Setup");
        finishSetupButton.setOnAction(event -> {
            grid.setupMode = false;
            finishSetupButton.setVisible(false);
        });

        Button enterSetupButton = new Button("Enter Setup");
        enterSetupButton.visibleProperty().bind(finishSetupButton.visibleProperty().not());
        enterSetupButton.setOnAction(event -> {
            grid.setupMode = true;
            finishSetupButton.setVisible(true);
        });


        HBox buttonBox = new HBox(finishSetupButton, enterSetupButton);

        ChatBox chatBox = new ChatBox();

        VBox controls = new VBox(buttonBox, chatBox);

        SplitPane splitPane = new SplitPane(grid, controls);
        splitPane.setOrientation(Orientation.VERTICAL);

        Scene mainScene = new Scene(splitPane, WIDTH, 800);
        stage.setScene(mainScene);
        stage.show();
    }

    private class Tile extends StackPane {
        private final int x;
        private final int y;
        private final boolean hasBomb;
        private final Rectangle border = new Rectangle(TILE_SIZE - 2, TILE_SIZE - 2);
        private final Text text = new Text();
        private boolean isOpen = false;

        public Tile(int x, int y, boolean hasBomb) {
            this.x = x;
            this.y = y;
            this.hasBomb = hasBomb;

            border.setStroke(Color.LIGHTGRAY);

            //set font size
            this.text.setFont(Font.font(18));
            // set bombs to have "X"
            text.setText(hasBomb ? "X" : "");
            //text not visible to user
            text.setVisible(false);

            getChildren().addAll(border, text);

            setTranslateX(x * TILE_SIZE);
            setTranslateY(y * TILE_SIZE);

            setOnMouseClicked(event -> open());
        }

        public void open() {
            if (isOpen)
                return;

            //end game
            if (hasBomb) {
                System.out.println("Game Over");
                gameScene.setRoot(createContent());
                return;
            }

            isOpen = true;
            text.setVisible(true);
            border.setFill(null);

            if (text.getText().isEmpty()) {
                getNeighbors(this).forEach(Tile::open);
            }
        }
    }
}