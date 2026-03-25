package sk.tuke.gamestudio.entity;

import java.util.Date;

public class Rating {
    private String player;
    private String game;
    private int rating;
    private Date playedOn;

    public Rating(String game, String player, int rating, Date playedOn) {
        this.player = player;
        this.game = game;
        this.rating = rating;
        this.playedOn = playedOn;
    }

    public String getPlayer() {
        return player;
    }
    public void setPlayer(String player) {
        this.player = player;
    }

    public String getGame() {
        return game;
    }
    public void setGame(String game) {
        this.game = game;
    }

    public int getRating() {
        return rating;
    }
    public void setRating(int rating) {
        this.rating = rating; }

    public Date getPlayedOn() {
        return playedOn;
    }
    public void setPlayedOn(Date playedOn) {
        this.playedOn = playedOn;
    }
}