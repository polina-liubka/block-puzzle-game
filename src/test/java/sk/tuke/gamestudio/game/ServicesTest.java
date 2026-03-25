package sk.tuke.gamestudio.game;

import org.junit.jupiter.api.Test;
import sk.tuke.gamestudio.entity.Comment;
import sk.tuke.gamestudio.entity.Rating;
import sk.tuke.gamestudio.entity.Score;
import sk.tuke.gamestudio.service.*;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ServicesTest {
    @Test
    public void testScoreService() {
        ScoreService scoreService = new ScoreServiceJDBC();
        scoreService.reset();

        Date now = new Date();
        scoreService.addScore(new Score("block puzzle", "Ivan", 100, now));
        scoreService.addScore(new Score("block puzzle", "Maria", 500, now));
        scoreService.addScore(new Score("block puzzle", "Peter", 300, now));

        List<Score> scores = scoreService.getTopScores("block puzzle");

        assertEquals(3, scores.size(), "There should be 3 records in the database");
        assertEquals(500, scores.get(0).getPoints(), "The first player should be the one with 500 points");
        assertEquals("Maria", scores.get(0).getPlayer());
        assertEquals(100, scores.get(2).getPoints(), "The last player should be the one with 100 points");
    }

    @Test
    public void testRatingService() {
        RatingService ratingService = new RatingServiceJDBC();
        ratingService.reset();

        ratingService.setRating(new Rating("block puzzle", "Player1", 5, new Date()));
        ratingService.setRating(new Rating("block puzzle", "Player2", 1, new Date()));

        assertEquals(3, ratingService.getAverageRating("block puzzle"), "The average rating should be 3");
        ratingService.setRating(new Rating("block puzzle", "Player1", 3, new Date()));
        assertEquals(2, ratingService.getAverageRating("block puzzle"), "After the update, the average rating should be 2");
    }

    @Test
    public void testCommentService() {
        CommentService commentService = new CommentServiceJDBC();
        commentService.reset();

        commentService.addComment(new Comment("block puzzle", "UserA", "Great game!", new Date()));
        commentService.addComment(new Comment("block puzzle", "UserB", "It was pretty hard, but cool", new Date()));

        List<Comment> comments = commentService.getComments("block puzzle");

        assertEquals(2, comments.size());
        assertEquals("UserA", comments.get(0).getPlayer());
        assertEquals("Great game!", comments.get(0).getComment());
    }
}
