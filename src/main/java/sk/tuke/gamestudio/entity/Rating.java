package sk.tuke.gamestudio.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import java.util.Date;

@Entity
@NamedQuery(name = "Rating.getAverageRating",
        query = "SELECT AVG(r.rating) FROM Rating r WHERE r.game=:game")
@NamedQuery(name = "Rating.getRating",
        query = "SELECT r.rating FROM Rating r WHERE r.game=:game AND r.player=:player")
@NamedQuery(name = "Rating.resetRatings",
        query = "DELETE FROM Rating")
public class Rating {
    @Id
    @GeneratedValue
    private String player;
    private String game;
    private int rating;
    private Date playedOn;

    public Rating() {}

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