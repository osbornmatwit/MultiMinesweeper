package multiminesweeper.ui;

import javafx.scene.layout.Region;
import multiminesweeper.Position;

public class MineGrid extends Region {
    public final int width;
    public final int height;
    public final GameState gameState;
    public boolean setupMode;

    public final MineCell[] cells;

    public MineGrid(int width, int height, boolean setupMode) {
        this.width = width;
        this.height = height;
        this.cells = new MineCell[width * height];
        this.setupMode = setupMode;
        gameState = new GameState(width, height);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                MineCell newCell = new MineCell(x, y);
                cells[x + y * width] = newCell;
                int finalX = x;
                int finalY = y;
                newCell.setOnMouseClicked(event -> {
                    Position pos = new Position(finalX, finalY);
                    if (setupMode) {
                        gameState.toggleBomb(pos);
                    } else {
                        gameState.activate(pos);
                    }
                });
            }
        }
        this.getChildren().addAll(cells);
    }

    public MineGrid(int width, int height) {
        this(width, height, false);
    }

    private MineCell getCell(int x, int y) {
        return cells[x * y];
    }

    private MineCell getCell(Position pos) {
        return getCell(pos.x, pos.y);
    }
}
