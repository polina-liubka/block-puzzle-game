package sk.tuke.gamestudio.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import sk.tuke.gamestudio.entity.Comment;
import sk.tuke.gamestudio.entity.Rating;
import sk.tuke.gamestudio.entity.Score;
import sk.tuke.gamestudio.service.*;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ServicesTest {
    private ScoreService scoreService;
    private RatingService ratingService;
    private CommentService commentService;

    private final String GAME_NAME = "block puzzle";

    @BeforeEach
    public void setup() {
        scoreService = new ScoreServiceRestClient();
        commentService = new CommentServiceRestClient();
        ratingService = new RatingServiceRestClient();

        RestTemplate restTemplate = new RestTemplate();

        ReflectionTestUtils.setField(scoreService, "restTemplate", restTemplate);
        ReflectionTestUtils.setField(commentService, "restTemplate", restTemplate);
        ReflectionTestUtils.setField(ratingService, "restTemplate", restTemplate);
    }

    @Test
    public void testScoreService() {
        Date now = new Date();
        scoreService.addScore(new Score(GAME_NAME, "Ivan", 100, now));
        scoreService.addScore(new Score(GAME_NAME, "Maria", 500, now));
        scoreService.addScore(new Score(GAME_NAME, "Peter", 300, now));

        List<Score> scores = scoreService.getTopScores(GAME_NAME);

        assertTrue(scores.size() >= 3);
        assertEquals("Maria", scores.get(0).getPlayer());
        assertEquals(500, scores.get(0).getPoints());
    }

    @Test
    public void testRatingService() {
        ratingService.setRating(new Rating(GAME_NAME, "Player1", 5, new Date()));
        ratingService.setRating(new Rating(GAME_NAME, "Player2", 1, new Date()));

        assertEquals(3, ratingService.getAverageRating(GAME_NAME));

        ratingService.setRating(new Rating(GAME_NAME, "Player1", 3, new Date()));
        assertEquals(2, ratingService.getAverageRating(GAME_NAME));
    }

    @Test
    public void testCommentService() {
        commentService.addComment(new Comment(GAME_NAME, "UserA", "Great game!", new Date()));

        List<Comment> comments = commentService.getComments(GAME_NAME);

        assertTrue(comments.size() > 0);
        boolean found = comments.stream().anyMatch(c -> c.getPlayer().equals("UserA"));
        assertTrue(found, "Коментар від UserA має бути у списку");
    }
}