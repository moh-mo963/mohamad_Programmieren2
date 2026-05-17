package de.hsbi.lockgame.logic;

@FunctionalInterface
public interface GameStateObserver {
    void onStateChanged(GameState state);
}
