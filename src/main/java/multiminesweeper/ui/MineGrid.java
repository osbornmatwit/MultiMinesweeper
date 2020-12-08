package multiminesweeper.ui;

import javafx.geometry.Insets;
import javafx.scene.layout.GridPane;
import multiminesweeper.Position;

import static multiminesweeper.ui.MineState.MINE;

public class MineGrid extends GridPane {
    public final int width;
    public final int height;
    public final GameState gameState;
    public boolean setupMode;

    public final MineCell[] cells;

    public MineGrid(int width, int height, boolean setupMode, GameState state) {
        this.width = width;
        this.height = height;
        this.cells = new MineCell[width * height];
        this.setupMode = setupMode;
        gameState = state;
        gameState.addEventListener(this::handleMineEvent);
        setPadding(Insets.EMPTY);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                MineCell newCell = new MineCell(x, y);
                GridPane.setConstraints(newCell, y, x);
                GridPane.setMargin(newCell, Insets.EMPTY);
                cells[x + y * width] = newCell;
//                newCell.stateLabel.setVisible(true);
//                newCell.stateLabel.setText(String.format("(%d, %d)", x, y));
                newCell.setOnMouseClicked(event -> {
                    if (!event.getSource().getClass().equals(MineCell.class)) {
                        System.err.println("Event received from non cell class");
                        System.err.println(event.getSource().getClass().toString());
                        return;
                    }
                    Position pos = ((MineCell) event.getSource()).position;
                    System.out.printf("Square clicked at (%d, %d)%n", pos.x, pos.y);
                    if (setupMode) {
                        gameState.toggleBomb(pos);
                    } else {
                        gameState.activate(pos);
                    }
                });
            }
        }
        this.getChildren().addAll(cells);
//        showAll();
    }

    public MineGrid(int width, int height, boolean setupMode) {
        this(width, height, setupMode, new GameState(width, height));
    }

    public MineGrid(int width, int height) {
        this(width, height, true);
    }

    // should maybe call copy constructor explicitly

    public MineGrid(Minefield field) {
        this(field.width, field.height, true, new GameState(field));

        gameState.startingLocation = field.startingPosition;
    }

    private MineCell getCell(int x, int y) {
        return cells[x * y];
    }

    private MineCell getCell(Position pos) {
        return getCell(pos.x, pos.y);
    }

    public void setSetupMode(boolean setupMode) {
        this.setupMode = setupMode;
        if (setupMode) {
            showAll();
        } else {
            hideAll();
        }
    }

    public void startGame() {
        hideAll();
        gameState.startGame();
        gameState.clearEventListeners();
        gameState.addEventListener(this::handleMineEvent);
    }

    private void showAll() {
        for (var cell : cells) {
            // this is because we only update cell data on clicks, so check if we need to update
            if (cell.state != MINE && gameState.isBomb(cell.position)) {
                cell.setState(MINE);
            }
            cell.show();
        }
    }

    private void hideAll() {
        for (var cell : cells) {
            cell.hide();
        }
    }

    private void handleMineEvent(MineEvent mineEvent) {
        MineCell cell = getCell(mineEvent.position);
        cell.setState(mineEvent.state);
        cell.bombNeighbors = mineEvent.bombNeighbors;
        cell.show();
    }
}
