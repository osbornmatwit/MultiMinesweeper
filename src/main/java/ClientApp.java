import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
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