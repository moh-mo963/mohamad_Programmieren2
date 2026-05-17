package de.hsbi.lockgame.logic;

import de.hsbi.lockgame.model.Direction;

@FunctionalInterface
public interface InputObserver {
    void onDirectionChanged(Direction direction);
}
