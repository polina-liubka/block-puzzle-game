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

    public GameState getState(){
        return state;
    }
    public int getScore(){
        return score;
    }
    public List<Block> getBlocks(){
        return blocks;
    }

    public void printField() {
        for(int r = 0; r < rowCount; r++){
            for(int c = 0; c < columnCount; c++){
                if(!tiles[r][c].isOccupied()){
                    System.out.print("· ");
                }else{
                    System.out.print(tiles[r][c].getColor() + " ");
                }
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
    }

    private Block randomBlocks() {
        String[] colors = {"#", "@", "&", "$", "%", "*", "O"};

        int shapeIndex = (int) (Math.random() * allShapes.size());
        int colorIndex = (int) (Math.random() * colors.length);

        return new Block(allShapes.get(shapeIndex), colors[colorIndex]);
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
        for(int r = 0; r < block.getHeight(); r++){
            for(int c = 0; c < block.getWidth(); c++){
                if(block.getShape()[r][c] == 1){
                    int fieldRow = startRow + r;
                    int fieldCol = startCol + c;
                    tiles[fieldRow][fieldCol].setOccupied(Tile.State.OCCUPIED);
                    tiles[fieldRow][fieldCol].setColor(block.getColor());
                }
            }
        }
    }
}
