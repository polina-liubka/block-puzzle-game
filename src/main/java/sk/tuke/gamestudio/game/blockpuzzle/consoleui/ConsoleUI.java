package sk.tuke.gamestudio.game.blockpuzzle.consoleui;

import sk.tuke.gamestudio.game.blockpuzzle.core.Block;
import sk.tuke.gamestudio.game.blockpuzzle.core.Field;
import sk.tuke.gamestudio.game.blockpuzzle.core.GameState;

import java.util.List;
import java.util.Scanner;

public class ConsoleUI {
    private final Field field;
    private final Scanner scanner = new Scanner(System.in);

    public ConsoleUI(Field field) {
        this.field = field;
    }

    public void play() {
        System.out.println("--- BLOCK PUZZLE ---");
        field.generateBlocks();
        while(field.getState() == GameState.PLAYING){
            field.printField();

            System.out.println("\nAVAILABLE BLOCKS(1 2 3)");
            printBlocks(field.getBlocks());
            System.out.println("-------------------------");
            handleInput();
        }

        field.printField();
        System.out.println("\nAVAILABLE BLOCKS(1 2 3)");
        printBlocks(field.getBlocks());
        System.out.println("-------------------------");
        System.out.println("***Game is over!*** Your score is: " + field.getScore());
    }

    private void handleInput() {
        System.out.print("Enter 'block_number' 'row' 'column': ");
        int blockIdx = scanner.nextInt() - 1;
        int row = scanner.nextInt() - 1;
        int col = scanner.nextInt() - 1;

        Block selectedBlock = field.getBlocks().get(blockIdx);
        if(selectedBlock == null){
            System.out.println("This block has already been used!");
            return;
        }

        if(field.canPlace(selectedBlock, row, col)){
            field.placeBlock(selectedBlock, row, col);

            field.getBlocks().set(blockIdx, null);

            boolean allNull = true;
            for(int i = 0; i < field.getBlocks().size(); i++){
                Block b = field.getBlocks().get(i);
                if(b != null){
                    allNull = false;
                    break;
                }
            }

            if(allNull){
                field.generateBlocks();
            }
        }else{
            System.out.println("You can't place a block here :(");
        }
    }

    private void printBlocks(List<Block> blocks) {
        if(blocks.isEmpty()){
            return;
        }

        for(int r = 0; r < 4; r++){
            for(int i = 0; i < blocks.size(); i++){
                Block curBlock = blocks.get(i);
                if(curBlock == null){
                    System.out.print("      ");
                    System.out.print("      ");
                    continue;
                }

                int[][] shape = curBlock.getShape();
                for(int c = 0; c < 4; c++){
                    if(r < shape.length && c < shape[r].length && shape[r][c] == 1){
                        System.out.print(curBlock.getColor() + " ");
                    }else{
                        System.out.print("  ");
                    }
                }
                System.out.print("      ");
            }
            System.out.println();
        }
    }
}