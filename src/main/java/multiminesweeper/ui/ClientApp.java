package multiminesweeper.ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import multiminesweeper.Move;
import multiminesweeper.Position;
import multiminesweeper.connector.AbstractConnector;
import multiminesweeper.connector.LocalPeerConnector;
import multiminesweeper.connector.PeerConnector;
import multiminesweeper.connector.RelayConnector;
import multiminesweeper.connector.events.EventType;
import multiminesweeper.connector.events.MoveResult;
import multiminesweeper.message.BoardMessage;
import multiminesweeper.message.ReadyMessage;

import java.io.IOException;

import static javafx.scene.control.Alert.AlertType;

public class ClientApp extends Application {

    private static final int X_TILES = 10;
    private static final int Y_TILES = 10;

    private AbstractConnector connector;
    private boolean partnerReady = false;
    private boolean ready = false;
    private Button startGameButton;
    private boolean gameStarted = false;

    private MineGrid grid;
    private ChatBox chatBox;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        LoginWindow loginWindow = new LoginWindow();
        loginWindow.showAndWait();
        String name = loginWindow.getName();
        String password = loginWindow.getPassword();
        String externalAddress = loginWindow.getExternalAddress();
        ConnectorType connectorType = loginWindow.getConnectorType();

        try {
            switch (connectorType) {
                case RELAY:
                    connector = new RelayConnector(externalAddress, 8080);
                    break;
                case P2P:
                    connector = new PeerConnector(externalAddress, 8080);
                    break;
                case LOCAL_P2P:
                    connector = new LocalPeerConnector(externalAddress);
                    break;
            }
        } catch (IOException ex) {
            new Alert(AlertType.ERROR, "Connection error").showAndWait();
            ex.printStackTrace();
            Platform.exit();
        }

        connector.setName(name);
        stage.setTitle(name + "'s game.");

        Thread loopThread = new Thread((Runnable) connector);
        loopThread.start();

        stage.setOnCloseRequest(event -> System.exit(0));

        Alert connectingMessage = new Alert(AlertType.NONE, "Connecting...");
        connectingMessage.show();
        connector.addEventListener(EventType.ERROR, event -> {
            new Alert(AlertType.ERROR, "Error: " + event.data).showAndWait();
            Platform.exit();
        });
        connector.addEventListener(EventType.CONNECT, event -> {
            String partnerName = event.data;
            stage.setTitle(name + "(you) vs " + partnerName + "(opponent)");
            System.out.println("Connected to " + partnerName);
        });

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                connector.waitForPartner(password);
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            connectingMessage.setResult(ButtonType.OK);
            connectingMessage.close();
            stage.setScene(setupGrid());
            stage.show();
        });
        new Thread(task).start();
    }

    private Scene setupGrid() {
        grid = new MineGrid(X_TILES, Y_TILES);

        startGameButton = new Button("Start Game");
        startGameButton.setOnAction(event -> {
            grid.setSetupMode(false);
            startGameButton.setDisable(true);
            grid.setClicksDisabled(true);
            ready = true;
            connector.sendMessage(new ReadyMessage());
            if (partnerReady) {
                gameStarted = true;
                Platform.runLater(this::startGame);
            }
        });

        // if this listener runs before the partner is ready, then other code just runs the startGame method,
        // otherwise, this listener runs it
        connector.addEventListener(EventType.READY, event -> {
            partnerReady = true;
            if (ready) {
                Platform.runLater(this::startGame);
            }
        });

        grid.gameState.setOnGameOver(this::gameOver);


        chatBox = new ChatBox(connector);

        VBox controls = new VBox(startGameButton, chatBox);

        SplitPane splitPane = new SplitPane(grid, controls);

        connector.addEventListener(EventType.BOARD, event -> {
            assert event.originalMessage != null;
            Minefield newBoard = ((BoardMessage) event.originalMessage).state;
            MineGrid newGrid = new MineGrid(newBoard);
            newGrid.startGame();
            Platform.runLater(() -> {
                splitPane.getItems().set(0, newGrid);
                grid = newGrid;
                gameStarted();
            });
        });
        splitPane.setOrientation(Orientation.HORIZONTAL);
        splitPane.setDividerPosition(0, 0.6);

        connector.setOnClose(() -> {
            new Alert(AlertType.NONE, "Connection closed.", ButtonType.OK).showAndWait();
            Platform.runLater(() -> {
                splitPane.getScene().getWindow().hide();
                Platform.exit();
            });
        });

        return new Scene(splitPane, 800, 600);
    }

    // stuff to do after the game starts
    private void gameStarted() {
        grid.gameState.addEventListener(event -> {
            connector.sendMove(new Move(event.position.x, event.position.y, false, false));
        });
        grid.gameState.addFlagListener(event -> {
            connector.sendMove(new Move(event.position.x, event.position.y, true, event.newValue));
        });

        connector.setMoveHandler(move -> {
            String result;
            if (move.flag) {
                if (move.newValue) {
                    result = "set flag on ";
                } else {
                    result = "removed flag from ";
                }
            } else {
                result = "activated ";
            }
            result += new Position(move.x, move.y).toString();

            chatBox.addSystemMessage(connector.getPartnerName() + " " + result);
            return MoveResult.HIT;
        });
    }

    private void startGame() {
        if (gameStarted) return;
        gameStarted = true;
        grid.setClicksDisabled(false);
        connector.sendBoard(grid.gameState.transportable());
        startGameButton.setVisible(false);
    }

    private void resetGame() {

    }

    private void askNewGame() {
        Alert newGame = new Alert(AlertType.NONE, "New game?");
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        task.setOnSucceeded(event -> {
            newGame.getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);
            newGame.showAndWait().ifPresent(type -> {
                if (ButtonType.YES.equals(type)) {
                    resetGame();
                } else if (ButtonType.NO.equals(type)) {
                    connector.close();
                    Platform.exit();
                }
            });
        });
        new Thread(task).start();
    }

    private void gameOver() {
        connector.gameOver();
        grid.setClicksDisabled(true);
        askNewGame();
    }
}