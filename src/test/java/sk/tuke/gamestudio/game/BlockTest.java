package sk.tuke.gamestudio.game;

import org.junit.jupiter.api.Test;
import sk.tuke.gamestudio.game.blockpuzzle.core.Block;

import static org.junit.jupiter.api.Assertions.*;

public class BlockTest {

    @Test
    public void testBlockDimensions() {
        int[][] shape = {
                {1, 0},
                {1, 0},
                {1, 1}
        };
        Block block = new Block(shape, "@");

        assertEquals(3, block.getHeight(), "The height of the block should be 3");
        assertEquals(2, block.getWidth(), "The block width should be 2");
    }

    @Test
    public void testBlockShapeIntegrity() {
        int[][] shape = {{1, 1}, {0, 1}};
        Block block = new Block(shape, "%");

        assertArrayEquals(shape, block.getShape(), "The array of the form must be identical to the original");
    }
}