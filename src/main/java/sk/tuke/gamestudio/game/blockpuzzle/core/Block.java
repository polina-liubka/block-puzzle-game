package sk.tuke.gamestudio.game.blockpuzzle.core;

public class Block {
    private final int[][] shape;
    private final String color;

    public Block(int[][] shape, String color){
        this.shape = shape;
        this.color = color;
    }

    public int[][] getShape(){
        return shape;
    }

    public String getColor(){
        return color;
    }

    public int getHeight(){
        return shape.length;
    }

    public int getWidth(){
        return shape[0].length;
    }
}
