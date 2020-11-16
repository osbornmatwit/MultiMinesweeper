import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.layout.Pane;
public class ClientApp extends Application {

    private static final int TILE_SIZE = 40;
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    private static final int X_TILES = WIDTH / TILE_SIZE;
    private static final int Y_TILES = HEIGHT / TILE_SIZE;

    private Parent createContent() {
        Pane root = new Pane();
        root.setPrefSize(800, 600);

        return root;
    }

    private class Tile extends StackPane {
        private int x,y;
        private boolean hasBomb;
        public Tile(int x, int y, boolean hasBomb){
            this.x = x;
            this.y = y;
            this.hasBomb = hasBomb;
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