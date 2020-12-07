package multiminesweeper.ui;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import multiminesweeper.Position;

import static multiminesweeper.ui.MineState.*;

public class MineCell extends StackPane {
    public final int TILE_SIZE = 40;

    public MineState state;
    private final Text stateLabel = new Text();
    public boolean shown = false;
    public Position position;
    Rectangle border = new Rectangle(TILE_SIZE - 1, TILE_SIZE - 1, Color.LIGHTGREY);


    public MineCell(int x, int y) {
        this.state = HIDDEN;
        position = new Position(x, y);
        stateLabel.setFont(Font.font(18));
        stateLabel.setVisible(false);
        border.setStroke(Color.DARKGREY);
        getChildren().addAll(border, stateLabel);

        setTranslateX(x * TILE_SIZE);
        setTranslateY(y * TILE_SIZE);
    }

    private void showState(MineState newState) {
        if (shown) {
            return;
        }

        switch (newState) {
            case HIDDEN:
            case BLANK:
                stateLabel.setVisible(false);
                break;
            case NUMBER:
                stateLabel.setVisible(true);
                break;
            case MINE:
                stateLabel.setText("X");
                stateLabel.setVisible(true);
                break;
        }

        border.setFill(null);

        shown = true;
    }

    public void hide() {
        shown = true;
    }

    public void showBombCount(int count) {
        stateLabel.setText(String.valueOf(count));
        showState(NUMBER);
    }

    public void showMine() {
        showState(MINE);
    }

    public void showEmpty() {
        showState(BLANK);
    }
}
