package sk.tuke.gamestudio.game.blockpuzzle.core;

import java.util.ArrayList;
import java.util.List;

public class Field {
    private final int rowCount;
    private final int columnCount;
    private final Tile[][] tiles;
    private GameState state = GameState.PLAYING;
    private int score = 0;
    private List<Block> blocks = new ArrayList<>();
    List<int[][]> allShapes = new ArrayList<>();
    private boolean hasBomb = false;

    public Field(int rowCount, int columnCount){
        this.rowCount = rowCount;
        this.columnCount = columnCount;
        this.tiles = new Tile[rowCount][columnCount];

        shapeOptions();

        for(int r = 0; r < rowCount; r++){
            for(int c = 0; c < columnCount; c++){
                tiles[r][c] = new Tile();
            }
        }
    }

    public int getRowCount() {
        return rowCount;
    }

    public int getColumnCount() {
        return columnCount;
    }

    public boolean isOver(){
        if(isMovesMode && movesLeft <= 0){
            return true;
        }
        return state == GameState.FAILED;
    }
    public int getScore(){
        return score;
    }
    public List<Block> getBlocks(){
        return blocks;
    }
    public Tile getTile(int row, int col) {
        return tiles[row][col];
    }

    public Tile[][] getTiles() {
        return tiles;
    }

    private int movesLeft;
    private boolean isMovesMode = false;
    public void setupMovesMode(int moves) {
        this.movesLeft = moves;
        this.isMovesMode = true;
    }

    public void decrementMoves() {
        if (isMovesMode && movesLeft > 0) {
            movesLeft--;
        }
    }

    public int getMovesLeft() { return movesLeft; }
    public boolean isMovesMode() { return isMovesMode; }

    public void printField() {
        System.out.print("    ");
        for(int c = 1; c <= columnCount; c++){
            System.out.print(c);
            if(c < 10){
                System.out.print(" ");
            }else{
                System.out.print("");
            }
        }
        System.out.println();

        for(int r = 0; r < rowCount; r++){
            int rowNum = r + 1;
            if(rowNum < 10){
                System.out.print(" " + rowNum + "  ");
            }else{
                System.out.print(rowNum + "  ");
            }

            for(int c = 0; c < columnCount; c++){
                if(!tiles[r][c].isOccupied()){
                    System.out.print("· ");
                }else{
                    System.out.print(tiles[r][c].getColor() + " ");
                }
            }
            if(r == 1){
                System.out.print("    SCORE: " + score);
            }
            if(r == 2 && hasBomb){
                System.out.print("    BOMB: type 'bomb' row col ");
            }

            System.out.println();
        }
    }

    private void shapeOptions(){
        allShapes.add(new int[][]{
                {1}
        });
        allShapes.add(new int[][]{
                {1, 1}
        });
        allShapes.add(new int[][]{
                {1},
                {1}
        });
        allShapes.add(new int[][]{
                {1, 1, 1}
        });
        allShapes.add(new int[][]{
                {1},
                {1},
                {1}
        });
        allShapes.add(new int[][]{
                {1, 1},
                {1, 0}
        });
        allShapes.add(new int[][]{
                {1, 1},
                {0, 1}
        });
        allShapes.add(new int[][]{
                {1, 0},
                {1, 1}
        });
        allShapes.add(new int[][]{
                {0, 1},
                {1, 1}
        });
        allShapes.add(new int[][]{
                {1, 1, 1, 1}
        });
        allShapes.add(new int[][]{
                {1},
                {1},
                {1},
                {1}
        });
        allShapes.add(new int[][]{
                {1, 1},
                {1, 1}
        });
        allShapes.add(new int[][]{
                {1, 1, 1},
                {1, 1, 1},
                {1, 1, 1}
        });
        allShapes.add(new int[][]{
                {1, 0, 0},
                {1, 0, 0},
                {1, 1, 1}
        });
        allShapes.add(new int[][]{
                {0, 0, 1},
                {0, 0, 1},
                {1, 1, 1}
        });
        allShapes.add(new int[][]{
                {1, 1, 1},
                {1, 0, 0},
                {1, 0, 0}
        });
        allShapes.add(new int[][]{
                {1, 1, 1},
                {0, 0, 1},
                {0, 0, 1}
        });
    }//String[] colors = {"#", "@", "&", "$", "%", "*", "O"};

    private Block randomBlocks() {
        //String[] colors = {"#", "@", "&", "$", "%", "*", "O"};
        String[] blockColors = {"blue", "green", "skyblue", "beige", "red", "yellow", "black", "orange", "brown"};

        int shapeIndex = (int) (Math.random() * allShapes.size());
        int colorIndex = (int) (Math.random() * blockColors.length);

        return new Block(allShapes.get(shapeIndex), blockColors[colorIndex]);
    }

    public void generateBlocks() {
        blocks.clear();
        for(int i = 0; i < 3; i++){
            Block randomBlock = randomBlocks();
            blocks.add(randomBlock);
        }
    }

    public boolean canPlace(Block block, int startRow, int startCol) {
        for(int r = 0; r < block.getHeight(); r++){
            for(int c = 0; c < block.getWidth(); c++){
                if(block.getShape()[r][c] == 1){
                    int fieldRow = startRow + r;
                    int fieldCol = startCol + c;

                    if(fieldRow < 0 || fieldRow >= rowCount || fieldCol < 0 || fieldCol >= columnCount){
                        return false;
                    }
                    if(tiles[fieldRow][fieldCol].isOccupied()){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public void placeBlock(Block block, int startRow, int startCol) {
        int scoreCount = 0;
        for(int r = 0; r < block.getHeight(); r++){
            for(int c = 0; c < block.getWidth(); c++){
                if(block.getShape()[r][c] == 1){
                    int fieldRow = startRow + r;
                    int fieldCol = startCol + c;
                    tiles[fieldRow][fieldCol].setOccupied(Tile.State.OCCUPIED);
                    tiles[fieldRow][fieldCol].setColor(block.getColor());
                    scoreCount++;
                }
            }
        }
        score += scoreCount;
        generateBomb();
    }

    public void removeLine() {
        List<Integer> rowsClear = new ArrayList<>();
        List<Integer> colsClear = new ArrayList<>();

        for(int r = 0; r < rowCount; r++){
            boolean full = true;
            for(int c = 0; c < columnCount; c++){
                if(!tiles[r][c].isOccupied()){
                    full = false;
                    break;
                }
            }
            if(full){
                rowsClear.add(r);
            }
        }

        for(int c = 0; c < columnCount; c++){
            boolean full = true;
            for(int r = 0; r < rowCount; r++){
                if(!tiles[r][c].isOccupied()){
                    full = false;
                    break;
                }
            }
            if(full){
                colsClear.add(c);
            }
        }

        for(int i = 0; i < rowsClear.size(); i++){
            int r = rowsClear.get(i);
            for(int c = 0; c < columnCount; c++){
                tiles[r][c].setOccupied(Tile.State.EMPTY);
            }
        }

        for(int i = 0; i < colsClear.size(); i++){
            int c = colsClear.get(i);
            for(int r = 0; r < rowCount; r++){
                tiles[r][c].setOccupied(Tile.State.EMPTY);
            }
        }

        int totalLines = rowsClear.size() + colsClear.size();
        if(totalLines > 0){
            int linePoints = 20;
            for(int i = 1; i < totalLines; i++){
                linePoints = linePoints * 2;
            }
            score += linePoints;

            if(totalLines > 1){
                System.out.println("COMBO x" + totalLines + "! +" + linePoints+ " points!");
            }
        }
    }

    public boolean isHasBomb() {
        return hasBomb;
    }

    public void generateBomb() {
        if(!hasBomb && Math.random() < 0.2){
            hasBomb = true;
        }
    }

    public void useBomb(int r, int c) {
        if(hasBomb){
            for(int i = 0; i < columnCount; i++){
                tiles[r][i].setOccupied(Tile.State.EMPTY);
            }

            for(int i = 0; i < rowCount; i++){
                tiles[i][c].setOccupied(Tile.State.EMPTY);
            }

            hasBomb = false;
            score += 100;
        }
    }

    public void reset() {
        this.state = GameState.PLAYING;
        this.score = 0;
        this.hasBomb = false;
        this.isMovesMode = false;
        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < columnCount; c++) {
                tiles[r][c].setOccupied(Tile.State.EMPTY);
            }
        }
        generateBlocks();
    }

    public void gameOver() {
        if(blocks == null || blocks.isEmpty() || blocks.stream().allMatch(b -> b == null)){
            return;
        }

        for(int i = 0; i < blocks.size(); i++){
            Block b = blocks.get(i);
            if(b != null){
                for(int r = 0; r < rowCount; r++){
                    for(int c = 0; c < columnCount; c++){
                        if(canPlace(b, r, c)){
                            this.state = GameState.PLAYING;
                            return;
                        }
                    }
                }
            }
        }
        this.state = GameState.FAILED;
        System.out.println("GAME OVER TRIGGERED");
    }
}
