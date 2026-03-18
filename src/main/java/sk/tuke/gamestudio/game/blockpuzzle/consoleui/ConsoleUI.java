package sk.tuke.gamestudio.game.blockpuzzle.consoleui;

import sk.tuke.gamestudio.entity.Rating;
import sk.tuke.gamestudio.entity.Score;
import sk.tuke.gamestudio.game.blockpuzzle.core.Block;
import sk.tuke.gamestudio.game.blockpuzzle.core.Field;
import sk.tuke.gamestudio.service.*;

import java.util.List;
import java.util.Scanner;

public class ConsoleUI {
    private final Field field;
    private final Scanner scanner = new Scanner(System.in);
    private String playerName;
    private final ScoreService scoreService = new ScoreServiceJDBC();
    private final CommentService commentService = new CommentServiceJDBC();
    private final RatingService ratingService = new RatingServiceJDBC();

    public ConsoleUI(Field field) {
        this.field = field;
    }

    private void printTopScores() {
        try{
            List<Score> scores = scoreService.getTopScores("blockpuzzle");
            System.out.println("\n--- TOP 10 player scores ---");
            if(scores.isEmpty()){
                System.out.println("   No scores recorded yet.   ");
            }else{
                for (int i = 0; i < scores.size(); i++) {
                    Score s = scores.get(i);
                    System.out.printf("%d. %-15s %10d\n", i + 1, s.getPlayer(), s.getPoints());
                }
            }
            System.out.println("\n");
        } catch (Exception e) {
            System.out.println("Could not load scores from database.");
        }
    }

    public void play() {
        System.out.println("--- BLOCK PUZZLE ---");
        System.out.print("Enter your name: ");
        this.playerName = scanner.nextLine().trim();
        if (this.playerName.isEmpty()) this.playerName = "Guest";

        printTopScores();

        field.generateBlocks();
        while(!field.isOver()){
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

        scoreService.addScore(new Score("blockpuzzle", playerName, field.getScore(), new java.util.Date()));
    }

    private void handleInput() {
        System.out.print("Enter: block_number row column or 'exit': ");
        String line = scanner.nextLine().trim();

        if(line.equalsIgnoreCase("exit")){
            scoreService.addScore(new Score("blockpuzzle", playerName, field.getScore(), new java.util.Date()));
            System.out.println("Goodbye! I hope you'll come back!");
            System.exit(0);
        }

        String[] parts = line.split("\\s+");
        if(parts.length < 3){
            System.out.println("Invalid input!");
            return;
        }

        if(parts[0].equals("bomb")){
            if(!field.isHasBomb()){
                System.out.println("You don't have a bomb yet!");
                return;
            }
            int r = Integer.parseInt(parts[1]) - 1;
            int c = Integer.parseInt(parts[2]) - 1;

            if(r >= 0 && r < 10 && c >= 0 && c < 10){
                field.useBomb(r, c);
                System.out.println("BOOM!");
            }else{
                System.out.println("Invalid bomb coordinates!");
            }
            return;
        }

        int blockIdx = Integer.parseInt(parts[0]) - 1;
        int row = Integer.parseInt(parts[1]) - 1;
        int col = Integer.parseInt(parts[2]) - 1;

        if(blockIdx < 0 || blockIdx >= field.getBlocks().size()){
            System.out.println("Invalid block index!");
            return;
        }

        Block selectedBlock = field.getBlocks().get(blockIdx);
        if(selectedBlock == null){
            System.out.println("This block has already been used!");
            return;
        }

        if(field.canPlace(selectedBlock, row, col)){
            field.placeBlock(selectedBlock, row, col);

            field.removeLine();

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

            field.generateBomb();
            field.gameOver();
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