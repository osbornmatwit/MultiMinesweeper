package multiminesweeper.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import multiminesweeper.connector.AbstractConnector;
import multiminesweeper.connector.events.EventType;
import multiminesweeper.connector.events.MultiplayerEvent;

import java.util.ArrayList;

public class ChatBox extends VBox {

    private final ArrayList<ChatMessage> messages = new ArrayList<>();
    private final TextField chatEntry = new TextField();
    private final VBox chatMessages = new VBox();
    private AbstractConnector connector;

    public ChatBox(AbstractConnector connector) {
        this.connector = connector;
        connector.addEventListener(EventType.CHAT, this::recieveMessage);


        // Listing of chat messages
        chatMessages.setStyle("-fx-background-color: white");
        chatMessages.setFillWidth(true);
        ScrollPane chatPane = new ScrollPane(chatMessages);

        // Form to enter new chat messages
        chatEntry.setPromptText("Message here...");
        chatEntry.setMaxWidth(10000);
        chatEntry.setMinWidth(200);
        chatEntry.setStyle("-fx-fill-width: true");
        Button sendButton = new Button("Send");
        chatEntry.setOnAction(event -> sendMessage());
        sendButton.setOnAction(event -> sendMessage());
        HBox chatControls = new HBox(5, chatEntry, sendButton);
        chatControls.setAlignment(Pos.BOTTOM_CENTER);
        this.setAlignment(Pos.BOTTOM_CENTER);
        chatPane.setFitToWidth(true);
        chatPane.setFitToHeight(true);

        // Add them to the tree
        this.getChildren().addAll(chatPane, chatControls);
        addSystemMessage("Welcome to the game!");
    }

    private void recieveMessage(MultiplayerEvent multiplayerEvent) {
        String message = multiplayerEvent.data;
        String name = connector.getPartnerName();
        addOtherMessage(name, message);
    }

    private void sendMessage() {
        String message = chatEntry.getText();
        chatEntry.clear();
        connector.sendChat(message);
        addSelfMessage(message);
    }

    private void reRenderMessages() {
        chatMessages.getChildren().clear();
        chatMessages.getChildren().addAll(messages);
    }

    private void addMessage(ChatMessage message) {
        messages.add(message);
        chatMessages.getChildren().add(message);
    }

    public void addSystemMessage(String contents) {
        ChatMessage message = new ChatMessage("SYSTEM", contents, ChatRole.SYSTEM);
    }

    public void addErrorMessage(String contents) {
        ChatMessage message = new ChatMessage("ERROR", contents, ChatRole.ERROR);
    }

    public void addOtherMessage(String name, String contents) {
        ChatMessage message = new ChatMessage(name, contents, ChatRole.OTHER);
    }

    public void addSelfMessage(String contents) {
        ChatMessage message = new ChatMessage(null, contents, ChatRole.SELF);
    }

    private enum ChatRole {
        SYSTEM("red", "black"),
        SELF("darkgrey"),
        OTHER("black", "blue"),
        ERROR("black", "red"),
        DEFAULT;

        String textColor;
        String backgroundColor;

        ChatRole(String textColor, String backgroundColor) {
            this.textColor = textColor;
            this.backgroundColor = backgroundColor;
        }

        ChatRole(String textColor) {
            this(textColor, null);
        }

        ChatRole() {
            this(null, null);
        }

        String getStyle() {
            StringBuilder builder = new StringBuilder();
            if (this.textColor != null) {
                builder.append(formatRule("text-color", this.textColor));
            }
            if (this.backgroundColor != null) {
                builder.append(formatRule("background-color", this.backgroundColor));
            }
            return builder.toString();
        }

        String formatRule(String rule, String value) {
            return String.format("-fx-%s: %s;%n", rule, value);
        }
    }

    private static class ChatMessage extends Text {
        public final String name;
        public final String contents;
        public final ChatRole role;

        public ChatMessage(String name, String contents, ChatRole role) {
            this.name = name;
            this.contents = contents;
            this.role = role;

            String message = contents;
            if (name != null) {
                message = name + ": " + message;
            }

            Text element = new Text(message);
            element.setStyle(role.getStyle());
        }
    }
}
