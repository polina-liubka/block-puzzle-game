package sk.tuke.gamestudio.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.tuke.gamestudio.game.blockpuzzle.core.Block;
import sk.tuke.gamestudio.game.blockpuzzle.core.Field;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FieldTest {
    private Field field;


    @BeforeEach
    public void setUp() {
        field = new Field(10, 10);
    }
    @Test
    public void testGenerateBlocks() {
        field.generateBlocks();
        List<Block> blocks = field.getBlocks();

        assertNotNull(blocks, "The list of blocks must not be null");
        assertEquals(3, blocks.size(), "Exactly 3 blocks should be generated");
    }

    @Test
    public void testPlaceBlock() {
        int[][] shape = {{1}};
        Block block = new Block(shape, "#");

        int scoreBefore = field.getScore();
        field.placeBlock(block, 0, 0);

        assertTrue(field.getScore() > scoreBefore, "The score is expected to increase after the unit is installed");
    }

    @Test
    public void testRemoveLineClearsTiles() {
        int[][] longShape = {{1, 1, 1, 1, 1, 1, 1, 1, 1, 1}};
        Block longBlock = new Block(longShape, "@");

        field.placeBlock(longBlock, 0, 0);
        field.removeLine();

        assertFalse(field.getTile(0, 0).isOccupied(), "The line should be cleared after remove");
    }

    @Test
    public void testGameOverLogic() {
        assertFalse(field.isOver(), "The game doesn't have to be over right at the start");
    }
}