package de.hsbi.lockgame.logic;

import de.hsbi.lockgame.model.Direction;
import de.hsbi.lockgame.model.Level;
import de.hsbi.lockgame.ui.GamePanel;
import java.util.ArrayList;
import java.util.List;

public final class GameEngine {

    private GameState currentGameState;

    // Liste für unsere Observer (nutzt Javas Consumer-Interface für die Updates)
    private final List<java.util.function.Consumer<GameState>> observers = new ArrayList<>();

    public GameEngine(Level level) {
        // Erstellt den ersten funktionsfähigen Start-Zustand des Spiels
        this.currentGameState = new GameState(
            level,
            new de.hsbi.lockgame.model.Snake(List.of(level.snakeStart())),
            level.pins(),
            GameState.Status.RUNNING,
            Direction.NONE
        );
    }

    public GameState state() {
        return this.currentGameState;
    }

    // DIESE METHODE HAT GEFEHLT: Damit Main.java (Zeile 26) glücklich ist
    public void setGamePanel(GamePanel panel) {
        // Methodenreferenz #1: Registriert das GamePanel als Observer für Zustandsänderungen
        this.addObserver(panel::update);
    }

    public void addObserver(java.util.function.Consumer<GameState> observer) {
        this.observers.add(observer);
    }

    private void notifyObservers() {
        // Lambda #3: Benachrichtigt alle registrierten Observer im Loop
        observers.forEach(observer -> observer.accept(this.currentGameState));
    }

    public void update(Direction d) {
        if (currentGameState.status().isRunning()) {
            // Ändert die anstehende Richtung (pendingDirection) im GameState
            this.currentGameState = new GameState(
                currentGameState.level(),
                currentGameState.snake(),
                currentGameState.pins(),
                currentGameState.status(),
                d
            );
            notifyObservers();
        }
    }

    public void tick() {
        // Lässt den Zustand einen Schritt weiterrechnen
        this.currentGameState = this.currentGameState.tick();
        notifyObservers();
    }
}
