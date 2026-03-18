package sk.tuke.gamestudio.entity;

import java.util.Date;

public class Comment {
    private String player;
    private String game;
    private String comment;
    private Date playedOn;

    public Comment(String player, String game, String comment, Date playedOn) {
        this.player = player;
        this.game = game;
        this.comment = comment;
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

    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getPlayedOn() {
        return playedOn;
    }
    public void setPlayedOn(Date playedOn) {
        this.playedOn = playedOn;
    }
}