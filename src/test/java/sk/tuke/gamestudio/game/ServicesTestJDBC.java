package sk.tuke.gamestudio.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.tuke.gamestudio.entity.Comment;
import sk.tuke.gamestudio.entity.Rating;
import sk.tuke.gamestudio.entity.Score;
import sk.tuke.gamestudio.service.*;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ServicesTestJDBC {
    private ScoreService scoreService;
    private CommentService commentService;
    private RatingService ratingService;

    private final String GAME_NAME = "block puzzle";

    @BeforeEach
    public void setup() {
        scoreService = new ScoreServiceJDBC();
        commentService = new CommentServiceJDBC();
        ratingService = new RatingServiceJDBC();

        scoreService.reset();
        commentService.reset();
        ratingService.reset();
    }

    @Test
    public void testScoreServiceJDBC() {
        Date now = new Date();
        scoreService.addScore(new Score(GAME_NAME, "JDBC_User1", 100, now));
        scoreService.addScore(new Score(GAME_NAME, "JDBC_User2", 500, now));

        List<Score> scores = scoreService.getTopScores(GAME_NAME);

        assertEquals(2, scores.size());
        assertEquals("JDBC_User2", scores.get(0).getPlayer());
        assertEquals(500, scores.get(0).getPoints());
    }

    @Test
    public void testCommentServiceJDBC() {
        commentService.addComment(new Comment(GAME_NAME, "Critic", "Old school JDBC works!", new Date()));

        List<Comment> comments = commentService.getComments(GAME_NAME);

        assertEquals(1, comments.size());
        assertEquals("Critic", comments.get(0).getPlayer());
    }

    @Test
    public void testRatingServiceJDBC() {
        ratingService.setRating(new Rating(GAME_NAME, "Player1", 5, new Date()));
        ratingService.setRating(new Rating(GAME_NAME, "Player2", 3, new Date()));

        assertEquals(4, ratingService.getAverageRating(GAME_NAME));

        ratingService.setRating(new Rating(GAME_NAME, "Player1", 1, new Date()));
        assertEquals(2, ratingService.getAverageRating(GAME_NAME));
    }
}