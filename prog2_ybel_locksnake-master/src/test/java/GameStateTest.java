import de.hsbi.lockgame.logic.GameState;
import de.hsbi.lockgame.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GameStateTest {

    private Level level;
    private Position start;
    private Position pinPos;

    @BeforeEach
    void setUp() {
        int width = 4;
        int height = 4;

        CellType[][] cells = new CellType[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                cells[x][y] = CellType.EMPTY;
            }
        }

        // Wall
        cells[0][0] = CellType.WALL;

        start = new Position(1, 1);
        pinPos = new Position(3, 1);

        Pin pin = new Pin(pinPos, Pin.State.LOW, Direction.DOWN);

        level = new Level(width, height, cells, List.of(pin), start);
    }

    // 1. Initialzustand
    @Test
    void givenGame_whenCreated_thenStatusRunning() {
        GameState state = new GameState(level,
            new Snake(List.of(start)),
            level.pins(),
            GameState.Status.RUNNING,
            Direction.NONE);

        assertEquals(GameState.Status.RUNNING, state.status());
    }

    // 2. Keine Bewegung ohne Input
    @Test
    void givenNoDirection_whenTick_thenSnakeStays() {
        GameState state = new GameState(level,
            new Snake(List.of(start)),
            level.pins(),
            GameState.Status.RUNNING,
            Direction.NONE).tick();

        assertEquals(start, state.snake().head());
    }

    // 3. Bewegung nach unten
    @Test
    void givenDown_whenTick_thenSnakeMovesDown() {
        GameState state = new GameState(level,
            new Snake(List.of(start)),
            level.pins(),
            GameState.Status.RUNNING,
            Direction.DOWN).tick();

        assertEquals(new Position(1, 2), state.snake().head());
    }

    // 4. Wand blockiert
    @Test
    void givenWall_whenMove_thenBlocked() {
        Position nearWall = new Position(1, 0);

        GameState state = new GameState(level,
            new Snake(List.of(nearWall)),
            level.pins(),
            GameState.Status.RUNNING,
            Direction.LEFT).tick();

        assertEquals(nearWall, state.snake().head());
    }

    // 5. Out of bounds → Verlust
    @Test
    void givenEdge_whenOut_thenLose() {
        Position edge = new Position(1, 0);

        GameState state = new GameState(level,
            new Snake(List.of(edge)),
            level.pins(),
            GameState.Status.RUNNING,
            Direction.UP).tick();

        assertEquals(GameState.Status.LOST_OUT_OF_BOUNDS, state.status());
    }

    // 6. Selbstkollision
    @Test
    void givenSelfCollision_thenLose() {
        // Head is at (1, 1). Moving UP takes it to (1, 0), where part of the body is waiting.
        Snake snake = new Snake(List.of(
            new Position(1, 1), // Head
            new Position(2, 1),
            new Position(2, 0),
            new Position(1, 0)  // Tail / Obstacle
        ));

        GameState state = new GameState(level, snake, level.pins(), GameState.Status.RUNNING, Direction.UP).tick();

        assertEquals(GameState.Status.LOST_SELF_COLLISION, state.status());
    }
    // 7. Pin wird geschoben
    @Test
    void givenPin_whenPushed_thenMoves() {
        // Arrange: Position snake head exactly next to the pin at (3, 1)
        Position rightNextToPin = new Position(2, 1);

        // Act: Tick right into the pin
        GameState state = new GameState(level,
            new Snake(List.of(rightNextToPin)),
            level.pins(),
            GameState.Status.RUNNING,
            Direction.RIGHT).tick();

        // Assert: Pin is pushed from (3, 1) to (4, 1)
        assertEquals(new Position(4, 1), state.pins().get(0).position());

        // Assert: Snake head successfully enters the old pin position (3, 1)
        assertEquals(new Position(3, 1), state.snake().head());
    }

    // 8. Pin wird aktiviert
    @Test
    void givenPin_whenPushed_thenActivated() {
        // Arrange: Position snake head next to the pin
        Position rightNextToPin = new Position(2, 1);

        // Act: Push the pin
        GameState state = new GameState(level,
            new Snake(List.of(rightNextToPin)),
            level.pins(),
            GameState.Status.RUNNING,
            Direction.RIGHT).tick();

        // Assert: The pin state is now active / set
        assertTrue(state.pins().get(0).state().isSet());
    }

    // 9. Gewinnbedingung
    @Test
    void givenPinsSet_whenTick_thenWin() {
        // Arrange: Position snake head next to the sole pin of the level
        Position rightNextToPin = new Position(2, 1);

        // Act: Push and activate the last remaining pin
        GameState state = new GameState(level,
            new Snake(List.of(rightNextToPin)),
            level.pins(),
            GameState.Status.RUNNING,
            Direction.RIGHT).tick();

        // Assert: The level detects all pins are active and sets state to WON
        assertEquals(GameState.Status.WON, state.status());
    }
    // 10. falsche Pin-Richtung blockiert
    @Test
    void givenWrongPinDirection_thenBlocked() {
        Position rightNextToPin = new Position(2, 1);

        GameState state = new GameState(level,
            new Snake(List.of(rightNextToPin)),
            level.pins(), // Default pin is facing DOWN, we are pushing RIGHT
            GameState.Status.RUNNING,
            Direction.RIGHT).tick();

        // Snake head shouldn't move because the pin side is solid/blocked
        assertEquals(rightNextToPin, state.snake().head());
    }

    // 11. aktiver Pin blockiert
    @Test
    void givenActivePin_thenBlocked() {
        Position rightNextToPin = new Position(2, 1);
        Pin activePin = new Pin(pinPos, Pin.State.HIGH, Direction.DOWN);

        Level customLevel = new Level(level.width(), level.height(), level.cells(), List.of(activePin), start);

        GameState state = new GameState(customLevel,
            new Snake(List.of(rightNextToPin)),
            customLevel.pins(),
            GameState.Status.RUNNING,
            Direction.RIGHT).tick();

        // Already active pin blocks movement completely
        assertEquals(rightNextToPin, state.snake().head());
    }
}
