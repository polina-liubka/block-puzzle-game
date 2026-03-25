package sk.tuke.gamestudio.game.blockpuzzle.consoleui;

import sk.tuke.gamestudio.entity.Rating;
import sk.tuke.gamestudio.entity.Comment;
import sk.tuke.gamestudio.entity.Score;
import sk.tuke.gamestudio.service.*;

import java.util.List;
import java.util.Scanner;

public class FeedbackUI {
    private final ScoreService scoreService = new ScoreServiceJDBC();
    private final CommentService commentService = new CommentServiceJDBC();
    private final RatingService ratingService = new RatingServiceJDBC();
    private final Scanner scanner = new Scanner(System.in);

    public void handleFeedback(String playerName) {
        printRating();
        handleRatingInput(playerName);
        handleCommentInput(playerName);
        showCommentsMenu();
    }

    public void printTopScores() {
        List<Score> scores = scoreService.getTopScores("blockpuzzle");
        System.out.println("\n--- TOP 10 player scores ---");
        if(scores.isEmpty()){
            System.out.println("No scores recorded yet.");
        }else{
            for(int i = 0; i < scores.size(); i++){
                Score s = scores.get(i);
                System.out.printf("%d. %-15s %10d\n", i + 1, s.getPlayer(), s.getPoints());
            }
        }
        System.out.println("\n");
    }

    private void printRating() {
        int avg = ratingService.getAverageRating("blockpuzzle");
        if(avg > 0){
            System.out.println("^o^ Rating: " + avg + "/5 ^o^");
        }else{
            System.out.println("╮（╯＿╰）╭ There are no ratings yet — be the first!");
        }
    }

    private void handleRatingInput(String playerName) {
        System.out.println("Did you like it? Rate the games(1-5):");
        String input = scanner.nextLine().trim();

        if(!input.isEmpty()){
            int stars = Integer.parseInt(input);
            if(stars >= 1 && stars <= 5){
                Rating rating = new Rating("blockpuzzle", playerName, stars, new java.util.Date());
                ratingService.setRating(rating);
                System.out.println("Thank you for your feedback!");
            }else{
                System.out.println("It should be between 1 and 5");
            }
        }
    }

    private void showCommentsMenu() {
        System.out.print("Would you like to see comments from other players? (y/n): ");
        String choice = scanner.nextLine().trim().toLowerCase();

        if(choice.equals("y")){
            List<Comment> comments = commentService.getComments("blockpuzzle");
            System.out.println("\n^_^ Comments: ");
            if(comments.isEmpty()){
                System.out.println("╮（╯＿╰）╭ There are no comments yet — be the first!");
            }else{
                int count = Math.min(comments.size(), 5);
                for(int i = 0; i < count; i++){
                    var c = comments.get(i);
                    System.out.printf("<%s>: %s\n", c.getPlayer(), c.getComment());
                }
            }
            System.out.println("-----------------------\n");
        }
    }

    private void handleCommentInput(String playerName) {
        System.out.print("Would you like to leave a comment about the game? (y/n): ");
        String choice = scanner.nextLine().trim().toLowerCase();

        if(choice.equals("y")){
            System.out.print("Your comment on the game: ");
            String text = scanner.nextLine().trim();
            if(!text.isEmpty()){
                commentService.addComment(new Comment("blockpuzzle", playerName, text, new java.util.Date()));
                System.out.println("Thank you for your feedback!");
            }
        }
    }
}
