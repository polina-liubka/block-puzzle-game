package sk.tuke.gamestudio.game.blockpuzzle;

import sk.tuke.gamestudio.game.blockpuzzle.consoleui.ConsoleUI;
import sk.tuke.gamestudio.game.blockpuzzle.core.Field;

public class BlockPuzzle {
    public static void main(String[] args) {
        Field field = new Field(10, 10);

        field.generateBlocks();

        ConsoleUI ui = new ConsoleUI(field);
        ui.play();
    }
}
