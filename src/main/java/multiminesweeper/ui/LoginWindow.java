package multiminesweeper.ui;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginWindow {
    public Scene scene;
    Button loginButton = new Button("Login");
    public TextField nameField = new TextField();
    public PasswordField passField = new PasswordField();

    public LoginWindow() {
        Pane loginRoot = new Pane();
        loginRoot.setPrefSize(200, 200);

        VBox vBox = new VBox();

        vBox.setSpacing(8);
        vBox.setPadding(new Insets(10, 10, 10, 10));
        vBox.getChildren().addAll(new Label("Your Username"),
            nameField,
            new Label("Your Password"),
            passField,
            loginButton);
        loginRoot.getChildren().addAll(vBox);
        loginButton.setOnAction(this::onLoginPress);

        scene = new Scene(loginRoot, 200, 200);
    }

    private void onLoginPress(ActionEvent event) {
        Stage stage = (Stage) scene.getWindow();
        stage.close();
    }
}
