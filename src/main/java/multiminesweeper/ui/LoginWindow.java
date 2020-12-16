package multiminesweeper.ui;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginWindow extends Stage {
    private final Scene scene;
    Button loginButton = new Button("Login");
    private final TextField nameField = new TextField();
    private final PasswordField passField = new PasswordField();
    private final ChoiceBox<ConnectorType> connectorChoice = new ChoiceBox<>();
    private final TextField externalAddress = new TextField();

    private final int HEIGHT = 250;
    private final int WIDTH = 200;

    public LoginWindow() {
        connectorChoice.setItems(FXCollections.observableArrayList(ConnectorType.values()));
        connectorChoice.setValue(ConnectorType.RELAY);
        loginButton.setOnAction(this::onLoginPress);
        Label addressLabel = new Label("Server Address");
        externalAddress.setText("localhost");
        addressLabel.setLabelFor(externalAddress);

        connectorChoice.setOnAction(event -> {
            switch (connectorChoice.getValue()) {
                case LOCAL_P2P:
                    addressLabel.setText("Partner Address");
                    break;
                case P2P:
                    addressLabel.setText("Coordination Server Address");
                    break;
                case RELAY:
                    addressLabel.setText("Relay Server Address");
                    break;
            }
        });

        VBox vBox = new VBox();

        vBox.setSpacing(8);
        vBox.setPadding(new Insets(10, 10, 10, 10));
        vBox.getChildren().addAll(new Label("Your Username"),
            nameField,
            new Label("Your Password"),
            passField,
            connectorChoice,
            addressLabel,
            externalAddress,
            loginButton);

        Pane loginRoot = new Pane();
        loginRoot.setPrefSize(WIDTH, HEIGHT);
        loginRoot.getChildren().addAll(vBox);

        scene = new Scene(loginRoot, WIDTH, HEIGHT);
        this.setScene(scene);
    }

    private void onLoginPress(ActionEvent event) {
        if (nameField.getLength() == 0) return;
        if (externalAddress.getLength() == 0) externalAddress.setText("localhost");
        this.close();
    }

    public String getName() {
        return nameField.getText();
    }

    public String getPassword() {
        return passField.getText();
    }

    public ConnectorType getConnectorType() {
        return connectorChoice.getValue();
    }

    public String getExternalAddress() {
        return externalAddress.getText();
    }
}
