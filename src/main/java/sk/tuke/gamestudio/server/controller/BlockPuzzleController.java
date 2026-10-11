package sk.tuke.gamestudio.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.WebApplicationContext;
import sk.tuke.gamestudio.entity.Score;
import sk.tuke.gamestudio.game.blockpuzzle.core.Block;
import sk.tuke.gamestudio.game.blockpuzzle.core.Field;
import sk.tuke.gamestudio.game.blockpuzzle.core.Tile;
import sk.tuke.gamestudio.service.CommentService;
import sk.tuke.gamestudio.service.RatingService;
import sk.tuke.gamestudio.service.ScoreService;

import java.util.Date;
import java.util.List;

@Controller
@Scope(WebApplicationContext.SCOPE_SESSION)
public class BlockPuzzleController {
    private Field field = new Field(10, 10);
    private int selectedBlock = 0;
    private boolean bombMode = false;

    @Autowired
    private ScoreService scoreService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private RatingService ratingService;
    @Autowired
    private UserController userController;

    @RequestMapping("/")
    public String index() {
        return "redirect:/block-puzzle-home";
    }

    @RequestMapping("/block-puzzle/feedback")
    public String addFeedback(
            @RequestParam(value = "commentText", required = false) String commentText,
            @RequestParam(value = "rating", required = false) Integer ratingValue,
            Model model) {

        if (userController.isLogged()) {
            String playerName = userController.getLoggedUser().getLogin();

            if (ratingValue != null && ratingValue >= 1 && ratingValue <= 5) {
                ratingService.setRating(new sk.tuke.gamestudio.entity.Rating(
                        "block-puzzle", playerName, ratingValue, new Date()
                ));
            }

            if (commentText != null && !commentText.trim().isEmpty()) {
                commentService.addComment(new sk.tuke.gamestudio.entity.Comment(
                        "block-puzzle", playerName, commentText, new Date()
                ));
            }
        }
        return "redirect:/block-puzzle/stats";
    }

    @RequestMapping("/block-puzzle/content")
    public String blockPuzzleContent(
            @RequestParam(value = "command", required = false) String command,
            @RequestParam(value = "row", required = false) Integer row,
            @RequestParam(value = "column", required = false) Integer column,
            @RequestParam(value = "blockIndex", required = false) Integer blockIndex,
            Model model) {
        return blockPuzzle(null, null, command, null, null, null, row, column, blockIndex, model);
    }

    @RequestMapping("/block-puzzle/stats")
    public String statistics(Model model) {
        model.addAttribute("scores", scoreService.getTopScores("block-puzzle"));
        model.addAttribute("comments", commentService.getComments("block-puzzle"));
        try {
            model.addAttribute("avgRating", ratingService.getAverageRating("block-puzzle"));
        } catch (Exception e) {
            model.addAttribute("avgRating", 0);
        }
        return "stats";
    }

    @RequestMapping("/block-puzzle-home")
    public String blockPuzzleHome(Model model) {
        boolean gameInProgress = (field.getScore() > 0 || !isFieldEmpty());
        model.addAttribute("gameInProgress", gameInProgress);
        return "block-puzzle-home";
    }

    public boolean isGameInProgress() {
        return field.getScore() > 0 || !isFieldEmpty();
    }

    private boolean isFieldEmpty() {
        for (int r = 0; r < field.getRowCount(); r++) {
            for (int c = 0; c < field.getColumnCount(); c++) {
                if (field.getTile(r, c).isOccupied()) return false;
            }
        }
        return true;
    }

    @RequestMapping("/block-puzzle/logout")
    public String logoutAndSave() {
        if (userController.isLogged() && !field.isOver()) {
            scoreService.addScore(new Score(
                    "block-puzzle",
                    userController.getLoggedUser().getLogin(),
                    field.getScore(),
                    new Date()
            ));
        }
        this.field = new Field(10, 10);
        userController.logout();

        return "redirect:/block-puzzle-home";
    }

    @RequestMapping("/block-puzzle")
    public String blockPuzzle(
            @RequestParam(value = "mode", required = false) String mode,
            @RequestParam(value = "moves", required = false) Integer moves,
            @RequestParam(value = "command", required = false) String command,
            @RequestParam(value = "player", required = false) String player,
            @RequestParam(value = "commentText", required = false) String commentText,
            @RequestParam(value = "rating", required = false) Integer ratingValue,
            @RequestParam(value = "row", required = false) Integer row,
            @RequestParam(value = "column", required = false) Integer column,
            @RequestParam(value = "blockIndex", required = false) Integer blockIndex,
            Model model) {

        if("startMoves".equals(mode) && moves != null){
            field.reset();
            field.setupMovesMode(moves);
            return "redirect:/block-puzzle";
        }

        if("select".equals(command) && blockIndex != null){
            this.selectedBlock = blockIndex;
        }

        if("new".equals(command)){
            field.reset();
            return "redirect:/block-puzzle";
        }

        if("activateBomb".equals(command)){
            this.bombMode = true;
        }

        if ("place".equals(command) && row != null && column != null) {
            if (bombMode) {
                field.useBomb(row, column);
                bombMode = false;
                field.removeLine();
                field.gameOver();
            } else {
                if (blockIndex != null) this.selectedBlock = blockIndex;
                List<Block> blocks = field.getBlocks();
                if (selectedBlock >= 0 && selectedBlock < blocks.size()) {
                    Block block = blocks.get(selectedBlock);
                    if (block != null && field.canPlace(block, row, column)) {
                        field.placeBlock(block, row, column);

                        field.decrementMoves();

                        blocks.set(selectedBlock, null);
                        field.removeLine();

                        if (blocks.stream().allMatch(b -> b == null)) {
                            field.generateBlocks();
                        }
                        field.gameOver();
                        if(field.isOver()){
                            if(userController.isLogged()){
                                scoreService.addScore(new sk.tuke.gamestudio.entity.Score(
                                        "block-puzzle",
                                        userController.getLoggedUser().getLogin(),
                                        field.getScore(),
                                        new Date()
                                ));
                            }
                        }
                    }
                }
            }
        }

        if (field.getBlocks().isEmpty() || field.getBlocks().stream().allMatch(b -> b == null)) {
            field.generateBlocks();
        }

        if ("exit".equals(command)) {
            field.reset();
            return "redirect:/block-puzzle-home";
        }
        fillModel(model, player);
        return "block-puzzle";
    }

    private void fillModel(Model model, String player) {
        model.addAttribute("score", field.getScore());
        model.addAttribute("gameOver", field.isOver());
        model.addAttribute("hasBomb", field.isHasBomb());
        model.addAttribute("bombMode", bombMode);

        model.addAttribute("movesLeft", field.getMovesLeft());
        model.addAttribute("isMovesMode", field.isMovesMode());

        try {
            model.addAttribute("avgRating", ratingService.getAverageRating("block-puzzle"));
        } catch (Exception e) {
            model.addAttribute("avgRating", 0);
        }
    }

    public String getHtmlField() {
        StringBuilder sb = new StringBuilder("<table class='game-table'>");
        for (int r = 0; r < 10; r++) {
            sb.append("<tr>");
            for (int c = 0; c < 10; c++) {
                Tile tile = field.getTile(r, c);
                sb.append("<td>");
                sb.append(String.format("<a class='ajax-link' href='/block-puzzle/content?command=place&row=%d&column=%d'>", r, c));
                if (tile.isOccupied()) {
                    String imageName = "/images/" + tile.getColor() + "_ore.png";
                    sb.append(String.format("<img src='%s' width='30' height='30' style='display:block;'>", imageName));
                } else {
                    sb.append("<img src='/images/coarse_dirt.png' width='40' height='40' style='display:block;'>");
                }
                sb.append("</a></td>");
            }
            sb.append("</tr>");
        }
        return sb.append("</table>").toString();
    }

    public String getHtmlBlocks() {
        StringBuilder sb = new StringBuilder("<div class='blocks-container'>");
        List<Block> blocks = field.getBlocks();

        for (int i = 0; i < blocks.size(); i++) {
            Block block = blocks.get(i);
            sb.append("<div class='block-slot'>");
            if (block != null) {
                String selectedClass = (i == selectedBlock) ? "selected-block-wrapper" : "";
                sb.append(String.format("<a class='ajax-link block-link' href='/block-puzzle/content?command=select&blockIndex=%d'>", i));
                sb.append(String.format("<div class='%s'>", selectedClass));
                sb.append("<table class='game-table'>");
                for (int row = 0; row < block.getHeight(); row++) {
                    sb.append("<tr>");
                    for (int col = 0; col < block.getWidth(); col++) {
                        sb.append("<td>");
                        if (block.getShape()[row][col] == 1) {
                            String imageName = "/images/" + block.getColor() + "_ore.png";
                            sb.append(String.format("<img src='%s'>", imageName));
                        }
                        sb.append("</td>");
                    }
                    sb.append("</tr>");
                }
                sb.append("</table></div></a>");
            }
            sb.append("</div>");
        }
        return sb.append("</div>").toString();
    }
}