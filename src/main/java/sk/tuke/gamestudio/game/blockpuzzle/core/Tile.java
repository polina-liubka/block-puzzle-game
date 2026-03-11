package sk.tuke.gamestudio.game.blockpuzzle.core;

public class Tile {
    public enum State {
        EMPTY,
        OCCUPIED
    }

    private State state;
    private String color;

    public Tile() {
        this.state = State.EMPTY;
        this.color = "";
    }

    public boolean isOccupied() {
        return this.state == State.OCCUPIED;
    }

    public void setOccupied(State state) {
        this.state = state;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}