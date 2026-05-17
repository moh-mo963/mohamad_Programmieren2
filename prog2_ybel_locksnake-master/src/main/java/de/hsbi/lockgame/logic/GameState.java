package de.hsbi.lockgame.logic;

import de.hsbi.lockgame.model.*;
import java.util.List;

public final class GameState {

    private final Level level;
    private final Snake snake;
    private final List<Pin> pins;
    private final Status status;
    private final Direction pendingDirection;

    public GameState(
        Level level, Snake snake, List<Pin> pins, Status status, Direction pendingDirection) {
        this.level = level;
        this.snake = snake;
        this.pins = List.copyOf(pins);
        this.status = status;
        this.pendingDirection = pendingDirection;
    }

    public Level level() {
        return this.level;
    }

    public Snake snake() {
        return this.snake;
    }

    public List<Pin> pins() {
        return this.pins;
    }

    public Status status() {
        return this.status;
    }

    public Direction pendingDirection() {
        return this.pendingDirection;
    }

    public GameState tick() {
        // Early Exit: Wenn das Spiel vorbei ist oder keine Richtung gedrückt wurde
        if (this.status != Status.RUNNING || this.pendingDirection == Direction.NONE) {
            return this;
        }

        // Berechne die nächste hypothetische Position des Kopfes
        Position nextHeadPos = this.snake.nextHead(this.pendingDirection);

        // (a) Prüfen, ob die Schlange das Spielfeld verlässt
        if (!this.level.isInside(nextHeadPos)) {
            return new GameState(this.level, this.snake, this.pins, Status.LOST_OUT_OF_BOUNDS, Direction.NONE);
        }

        // (b) Prüfen, ob die Schlange in eine Wand läuft
        if (this.level.cellAt(nextHeadPos) == CellType.WALL) {
            return new GameState(this.level, this.snake, this.pins, this.status, Direction.NONE);
        }

        // (c) Prüfen, ob die Schlange sich selbst beißt
        if (this.snake.occupies(nextHeadPos)) {
            return new GameState(this.level, this.snake, this.pins, Status.LOST_SELF_COLLISION, Direction.NONE);
        }

        // Schauen, ob auf dem Zielfeld ein Pin liegt
        Pin pinAtTarget = null;
        for (Pin p : this.pins) {
            if (p.position().equals(nextHeadPos)) {
                pinAtTarget = p;
                break;
            }
        }

        // (d) Pin-Kollision prüfen (wenn blockiert oder falsche Richtung)
        if (pinAtTarget != null) {
            if (pinAtTarget.state().isSet() || this.pendingDirection != pinAtTarget.activationDirection()) {
                return new GameState(this.level, this.snake, this.pins, this.status, Direction.NONE);
            }
        }

        // Pin aktivieren, falls wir aus der richtigen Richtung kommen
        if (pinAtTarget != null && !pinAtTarget.state().isSet() && this.pendingDirection == pinAtTarget.activationDirection()) {

            // Lambda #1: Aktualisiert den Zustand des getroffenen Pins in der Liste
            List<Pin> updatedPins = this.pins.stream()
                .map(p -> p.position().equals(nextHeadPos) ? p.withState(Pin.State.HIGH) : p)
                .toList();

            // Lambda #2: Prüft, ob alle Pins auf HIGH stehen (Gewinnbedingung)
            boolean allPinsSet = updatedPins.stream().allMatch(p -> p.state().isSet());
            Status nextStatus = allPinsSet ? Status.WON : this.status;

            // Schlange aktiviert den Pin, bewegt sich in diesem Tick aber nicht physisch darauf
            return new GameState(this.level, this.snake, updatedPins, nextStatus, Direction.NONE);
        }

        // Anderenfalls: Normale Vorwärtsbewegung der Schlange
        Snake movedSnake = this.snake.grow(this.pendingDirection);
        return new GameState(this.level, movedSnake, this.pins, this.status, this.pendingDirection);
    }

    public enum Status {
        RUNNING,
        WON,
        LOST_SELF_COLLISION,
        LOST_OUT_OF_BOUNDS;

        public boolean isRunning() {
            return this == RUNNING;
        }
    }
}
