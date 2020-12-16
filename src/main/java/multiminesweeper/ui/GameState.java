package multiminesweeper.ui;

import multiminesweeper.Position;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static multiminesweeper.ui.MineState.*;

public class GameState {
    public HashSet<Position> bombLocations = new HashSet<>();
    public HashSet<Position> shownLocations = new HashSet<>();

    Position startingLocation;
    public final int width;
    public final int height;


    public final ArrayList<Consumer<MineEvent>> listeners = new ArrayList<>();

    public Runnable gameOverListener;

    public void setOnGameOver(Runnable listener) {
        gameOverListener = listener;
    }

    public void removeOnGameOver() {
        gameOverListener = null;
    }

    public GameState(int width, int height) {
        startingLocation = new Position(0, 0);
        this.width = width;
        this.height = height;
    }

    public GameState(Minefield minefield) {
        this(minefield.width, minefield.height);
        startingLocation = minefield.startingPosition;
        for (var mine : minefield.mines) {
            setBomb(mine, true);
        }
    }

    public Minefield transportable() {
        return new Minefield(this);
    }

    public void addEventListener(Consumer<MineEvent> listener) {
        listeners.add(listener);
    }

    public void removeEventListener(Consumer<MineEvent> listener) {
        listeners.remove(listener);
    }

    private void triggerEvent(MineEvent event) {
        for (var listener : listeners) {
            listener.accept(event);
        }
    }

    public void startGame() {
        activate(startingLocation);
    }

    public void activate(Position pos) {
        checkPosition(pos);
        if (isShown(pos)) return;
        setShown(pos);

        List<Position> neighbors = getNonShownNeighbors(pos);

        int bombNeighbors = (int) neighbors.stream().filter(this::isBomb).count();
        boolean isBomb = isBomb(pos);

        triggerEvent(new MineEvent(pos, isBomb, bombNeighbors));

        if (isBomb) {
            gameOver();
            return;
        }

        if (bombNeighbors == 0) {
            for (Position neighbor : neighbors) {
                activate(neighbor);
            }
        }
    }

    private void gameOver() {
        if (gameOverListener != null) {
            gameOverListener.run();
        }
    }

    public MineState getState(Position pos) {
        MineState state;
        if (isBomb(pos)) {
            state = MINE;
        } else if (getNeighboringBombCount(pos) > 0) {
            state = NUMBER;
        } else {
            state = BLANK;
        }
        return state;
    }

    public void setStartingLocation(Position startingLocation) {
        this.startingLocation = startingLocation;
    }

    private void setShown(Position pos) {
        setShown(pos, true);
    }

    private void setShown(Position pos, boolean value) {
        checkPosition(pos);
        if (value) {
            shownLocations.add(pos);
        } else {
            shownLocations.remove(pos);
        }
    }

    public void checkPosition(Position pos) {
        if (!isValidPosition(pos)) {
            throw new IllegalArgumentException("Invalid position in grid: " + pos.toString());
        }
    }

    public boolean isValidPosition(Position pos) {
        return !(pos.x < 0 || pos.x >= width || pos.y < 0 || pos.y >= height);
    }

    public boolean isBomb(Position pos) {
        checkPosition(pos);
        return bombLocations.contains(pos);
    }

    public void toggleBomb(Position pos) {
        System.out.println(pos);
        checkPosition(pos);
        if (isBomb(pos)) {
            bombLocations.remove(pos);
        } else {
            bombLocations.add(pos);
        }
    }

    public void setBomb(Position pos, boolean value) {
        checkPosition(pos);
        if (value) {
            bombLocations.add(pos);
        } else {
            bombLocations.remove(pos);
        }
    }

    public List<Position> getNonShownNeighbors(Position pos) {
        return getNeighbors(pos).stream().filter(Predicate.not(this::isShown)).collect(Collectors.toList());
    }

    public List<Position> getNeighbors(Position pos) {
        checkPosition(pos);

        ArrayList<Position> neighbors = new ArrayList<>();

        final int[][] neighboringParts = {
            {-1, -1}, {1, 1}, {-1, 1}, {1, -1}, {-1, 0}, {1, 0}, {0, -1}, {0, 1}
        };

        for (var part : neighboringParts) {
            Position newPos = new Position(pos.x + part[0], pos.y + part[1]);
            if (isValidPosition(newPos)) neighbors.add(newPos);
        }

//        boolean isOnLeftBorder = (pos.x == 0);
//        boolean isOnRightBorder = (pos.x == width - 1);
//        boolean isOnTopBorder = (pos.y == 0);
//        boolean isOnBottomBorder = (pos.x == height - 1);
//        
//        if (!isOnLeftBorder) {
//            neighbors.add(new Position(pos.x - 1, pos.y));
//        }
//
//        if (!isOnTopBorder) {
//            neighbors.add(new Position(pos.x, pos.y - 1));
//        }
//
//        if (!isOnRightBorder) {
//            neighbors.add(new Position(pos.x - 1, pos.y - 1));
//        }
//        if (!isOnBottomBorder) {
//            neighbors.add(new Position(pos.x, pos.y + 1));
//        }
//
//        if (!isOnLeftBorder && !isOnTopBorder) {
//            neighbors.add(new Position(pos.x - 1, pos.y - 1));
//        }
//        if (!isOnRightBorder && !isOnTopBorder) {
//            neighbors.add(new Position(pos.x + 1, pos.y - 1));
//        }
//        if (!isOnLeftBorder && !isOnBottomBorder) {
//            neighbors.add(new Position(pos.x - 1, pos.y + 1));
//        }
//        if (!isOnRightBorder && !isOnBottomBorder) {
//            neighbors.add(new Position(pos.x + 1, pos.y + 1));
//        }

        return neighbors;
    }

    public int getNeighboringBombCount(Position pos) {
        List<Position> neighbors = getNeighbors(pos);
        int sum = 0;
        for (var neighbor : neighbors) {
            if (isBomb(neighbor)) sum += 1;
        }
        return sum;
    }

    public boolean isShown(Position pos) {
        return shownLocations.contains(pos);
    }

    // turn the bomb mapping into a
    public Position[] getBombList() {
        return bombLocations.toArray(new Position[0]);
    }

    public void clearEventListeners() {
        listeners.clear();
    }
}
