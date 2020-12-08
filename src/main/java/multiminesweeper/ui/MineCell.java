package multiminesweeper.ui;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import multiminesweeper.Position;

import static multiminesweeper.ui.MineState.BLANK;

public class MineCell extends StackPane {
    public final int TILE_SIZE = 40;

    public MineState state;
    public final Text stateLabel = new Text();
    public boolean shown = false;
    public Position position;
    public int bombNeighbors = 0;
    Rectangle border = new Rectangle(TILE_SIZE - 1, TILE_SIZE - 1, Color.LIGHTGREY);


    public MineCell(int x, int y) {
        this.state = BLANK;
        position = new Position(x, y);
        stateLabel.setFont(Font.font(18));
        stateLabel.setVisible(false);
        border.setStroke(Color.DARKGREY);
        getChildren().addAll(border, stateLabel);

//        setTranslateX(x * TILE_SIZE);
//        setTranslateY(y * TILE_SIZE);
    }

    private void showState() {
        if (!shown) {
            border.setFill(Color.LIGHTGRAY);
            stateLabel.setVisible(false);
            return;
        }

        switch (state) {
            case BLANK:
                stateLabel.setVisible(false);
                break;
            case NUMBER:
                stateLabel.setText(String.valueOf(bombNeighbors));

                stateLabel.setVisible(true);
                break;
            case MINE:
                stateLabel.setText("X");
                border.setFill(Color.RED);
                stateLabel.setVisible(true);
                break;
        }
    }

    public void setState(MineState newState) {
        this.state = newState;
        // instead manually show
//        showState();
    }

    public void hide() {
        shown = false;
        showState();
    }

    public void show() {
        shown = true;
        showState();
    }
}
