package multiminesweeper.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import multiminesweeper.Position;

import static multiminesweeper.ui.MineState.MINE;

public class MineGrid extends GridPane {
    public final int width;
    public final int height;
    public final GameState gameState;
    public final MineCell[] cells;
    private final Color FILL_HIDDEN = Color.LIGHTGRAY;
    private final Color FILL_EXPLODE = Color.RED;
    private final Color FILL_SHOWN = Color.WHITE;
    private final Color FILL_STARTING = Color.GREEN;

    private boolean clicksDisabled = false;

    private boolean setupMode;

    public MineGrid(int width, int height, boolean setupMode, GameState state) {
        this.width = width;
        this.height = height;
        this.cells = new MineCell[width * height];
        this.setupMode = setupMode;
        gameState = state;
        gameState.setStartingLocation(new Position(5, 5));
        gameState.addEventListener(this::handleMineEvent);
        setPadding(Insets.EMPTY);
        setAlignment(Pos.CENTER);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                try {
                    state.checkPosition(new Position(x, y));
                } catch (IllegalArgumentException ex) {
                    System.out.printf("x: %d, y: %d%n width: %d, height: %d%n", x, y, state.width, state.height);
                }
                MineCell newCell = new MineCell(x, y);
                GridPane.setConstraints(newCell, x, y);
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

                    if (clicksDisabled) return;
                    Position pos = ((MineCell) event.getSource()).position;
                    System.out.printf("Square clicked at (%d, %d)%n", pos.x, pos.y);
                    if (this.setupMode) {
                        gameState.toggleBomb(pos);
                        refreshCell(pos);
                        gameState.getNeighbors(pos).forEach(neighbor -> refreshCell(pos));

                    } else if (event.getButton() == MouseButton.PRIMARY && !gameState.isFlagged(pos)) {
                        gameState.activate(pos);
                    } else if (event.getButton() == MouseButton.SECONDARY) {
                        gameState.toggleFlagged(pos);
                        refreshCell(pos);
                    }
                });
            }
        }
        this.getChildren().addAll(cells);
//        showAll();
        refreshAll();
    }

    public MineGrid(int width, int height, boolean setupMode) {
        this(width, height, setupMode, new GameState(width, height));
    }

    public MineGrid(int width, int height) {
        this(width, height, true);
    }

    public MineGrid(Minefield field) {
        this(field.width, field.height, false, new GameState(field));

        gameState.startingLocation = field.startingPosition;
        gameState.startGame();
    }

    private MineCell getCell(int x, int y) {
        return cells[x + width * y];
    }

    private MineCell getCell(Position pos) {
        return getCell(pos.x, pos.y);
    }

    public void setSetupMode(boolean setupMode) {
//        if (setupMode == this.setupMode) return; 
        this.setupMode = setupMode;
        refreshAll();
    }

    public void startGame() {
        setupMode = false;
        gameState.startGame();
        refreshAll();
    }

    public boolean getClicksDisabled() {
        return clicksDisabled;
    }

    public void setClicksDisabled(boolean value) {
        clicksDisabled = value;
    }

    private void refreshAll() {
        for (var cell : cells) {
            refreshCell(cell);
        }
    }

    // Refresh the look of a cell
    private void refreshCell(MineCell cell, MineState state) {
        cell.setState(state);
        cell.resetStyle();

        int bombNeighbors = gameState.getNeighboringBombCount(cell.position);

        if (setupMode) {
            cell.setFill(FILL_SHOWN);
            if (state == MINE) {
                cell.showText("X");
            } else if (gameState.startingLocation.equals(cell.position)) {
                cell.setFill(FILL_STARTING);
            } else if (bombNeighbors > 0) {
                cell.showText(bombNeighbors);
            } else {
                cell.hideText();
            }
        } else {
            cell.hideText();
            if (!gameState.isShown(cell.position)) {
                cell.setFill(FILL_HIDDEN);
                return;
            }
            if (state == MINE) {
                cell.setFill(FILL_EXPLODE);
                cell.showText("X");
            } else if (bombNeighbors > 0) {
                cell.setFill(FILL_SHOWN);
                cell.showText(bombNeighbors);
            } else if (gameState.isFlagged(cell.position)) {
                // \uD83D\uDEA9 =  🚩
                cell.setFill(FILL_HIDDEN);
                cell.showText("\uD83D\uDEA9");
            } else {
                cell.setFill(FILL_SHOWN);
            }
        }
    }

    private void refreshCell(MineCell cell) {
        refreshCell(cell, gameState.getState(cell.position));
    }

    private void refreshCell(Position pos) {
        refreshCell(getCell(pos));
    }

    private void handleMineEvent(MineEvent mineEvent) {
        refreshCell(getCell(mineEvent.position), mineEvent.state);
        refreshAll();
    }
}
