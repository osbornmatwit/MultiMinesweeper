package multiminesweeper.ui;

import multiminesweeper.Position;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class GameState {
    HashMap<Position, Boolean> bombLocations = new HashMap<>();
    HashMap<Position, Boolean> shownLocations = new HashMap<>();

    Position startingLocation;
    public final int width;
    public final int height;

    public final ArrayList<Consumer<MineEvent>> listeners = new ArrayList<>();

    public GameState(int width, int height) {
        startingLocation = new Position(0, 0);
        this.width = width;
        this.height = height;
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

    public void activate(Position pos) {
        checkPosition(pos);
        if (isShown(pos)) return;
        setShown(pos);

        List<Position> neighbors = getNonShownNeighbors(pos);

        int bombNeighbors = (int) neighbors.stream().filter(this::isBomb).count();
        boolean isBomb = isBomb(pos);

        triggerEvent(new MineEvent(pos, isBomb, bombNeighbors));

        if (isBomb) return;

        for (Position neighbor : neighbors) {
            activate(neighbor);
        }
    }


    private void setShown(Position pos) {
        setShown(pos, true);
    }

    private void setShown(Position pos, boolean value) {
        checkPosition(pos);
        if (value) {
            bombLocations.put(pos, true);
        } else {
            bombLocations.remove(pos);
        }
    }

    private boolean isShown(Position pos) {
        return shownLocations.getOrDefault(pos, false);
    }

    private void checkPosition(Position pos) {
        if (!isValidPosition(pos)) {
            throw new IllegalArgumentException("Invalid position in grid");
        }
    }

    public boolean isValidPosition(Position pos) {
        return !(pos.x < 0 || pos.x >= width || pos.y < 0 || pos.y >= height);
    }

    public boolean isBomb(Position pos) {
        checkPosition(pos);
        return bombLocations.getOrDefault(pos, false);
    }

    public void toggleBomb(Position pos) {
        checkPosition(pos);
        if (isBomb(pos)) {
            bombLocations.remove(pos);
        } else {
            bombLocations.put(pos, true);
        }
    }

    public void setBomb(Position pos, boolean value) {
        checkPosition(pos);
        if (value) {
            bombLocations.put(pos, true);
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

        boolean isOnLeftBorder = (pos.x == 0);
        boolean isOnRightBorder = (pos.x == width - 1);
        boolean isOnTopBorder = (pos.y == 0);
        boolean isOnBottomBorder = (pos.x == height - 1);
        if (!isOnLeftBorder) {
            neighbors.add(new Position(pos.x - 1, pos.y));
        }

        if (!isOnTopBorder) {
            neighbors.add(new Position(pos.x, pos.y - 1));
        }

        if (!isOnRightBorder) {
            neighbors.add(new Position(pos.x - 1, pos.y - 1));
        }
        if (!isOnBottomBorder) {
            neighbors.add(new Position(pos.x, pos.y + 1));
        }

        if (!isOnLeftBorder && !isOnTopBorder) {
            neighbors.add(new Position(pos.x - 1, pos.y - 1));
        }
        if (!isOnRightBorder && !isOnTopBorder) {
            neighbors.add(new Position(pos.x + 1, pos.y - 1));
        }
        if (!isOnLeftBorder && !isOnBottomBorder) {
            neighbors.add(new Position(pos.x - 1, pos.y + 1));
        }
        if (!isOnRightBorder && !isOnBottomBorder) {
            neighbors.add(new Position(pos.x + 1, pos.y + 1));
        }

        return neighbors;
    }
}
