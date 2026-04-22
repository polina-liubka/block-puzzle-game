package sk.tuke.gamestudio.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.WebApplicationContext;
import sk.tuke.gamestudio.entity.Comment;
import sk.tuke.gamestudio.entity.Rating;
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

    @Autowired
    private ScoreService scoreService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private RatingService ratingService;

    private final String TILE_IMAGE = "/images/grey_square_glossy.png";

    @RequestMapping("/block-puzzle")
    public String blockPuzzle(
            @RequestParam(value = "command", required = false) String command,
            @RequestParam(value = "player", required = false) String player,
            @RequestParam(value = "commentText", required = false) String commentText,
            @RequestParam(value = "rating", required = false) Integer ratingValue,
            @RequestParam(value = "row", required = false) Integer row,
            @RequestParam(value = "column", required = false) Integer column,
            @RequestParam(value = "blockIndex", required = false) Integer blockIndex,
            Model model) {

        if ("select".equals(command) && blockIndex != null) {
            this.selectedBlock = blockIndex;
        }

        if (field.getBlocks() == null || field.getBlocks().isEmpty() || field.getBlocks().stream().allMatch(b -> b == null)) {
            field.generateBlocks();
        }

        if ("new".equals(command)) {
            field.reset();
        }

        if ("place".equals(command) && row != null && column != null) {
            List<Block> blocks = field.getBlocks();
            if (selectedBlock >= 0 && selectedBlock < blocks.size()) {
                Block block = blocks.get(selectedBlock);
                if(block != null && field.canPlace(block, row, column)) {
                    field.placeBlock(block, row, column);
                    blocks.set(selectedBlock, null);
                    field.removeLine();
                    field.gameOver();

                    if(blocks.stream().allMatch(b -> b == null)){
                        field.generateBlocks();
                        field.gameOver();
                    }
                }
            }
        }

        if ("exit".equals(command)) {
            if (player != null && !player.isBlank()) {
                if (field.getScore() > 0) {
                    scoreService.addScore(new sk.tuke.gamestudio.entity.Score("block-puzzle", player, field.getScore(), new Date()));
                }

                if (commentText != null && !commentText.isBlank()) {
                    commentService.addComment(new Comment("block-puzzle", player, commentText, new Date()));
                }

                if (ratingValue != null && ratingValue > 0) {
                    ratingService.setRating(new Rating("block-puzzle", player, ratingValue, new Date()));
                }
            }
            field.reset();
        }

        fillModel(model, player);
        return "block-puzzle";
    }

    private void fillModel(Model model, String player) {
        model.addAttribute("scores", scoreService.getTopScores("block-puzzle"));
        model.addAttribute("comments", commentService.getComments("block-puzzle"));
        model.addAttribute("score", field.getScore());
        model.addAttribute("gameOver", field.isOver());
        model.addAttribute("currentPlayer", player);

        try {
            model.addAttribute("avgRating", ratingService.getAverageRating("block-puzzle"));
        } catch (Exception e) {
            model.addAttribute("avgRating", 0);
        }
    }

    public String getHtmlField() {
        StringBuilder sb = new StringBuilder("<table class='game-table'>");
        for(int r = 0; r < 10; r++){
            sb.append("<tr>");
            for(int c = 0; c < 10; c++){
                Tile tile = field.getTile(r, c);
                sb.append("<td>");
                sb.append(String.format("<a href='/block-puzzle?command=place&row=%d&column=%d'>", r, c));
                if(tile.isOccupied()){
                    // Obsadena bunka
                    sb.append(String.format("<img src='%s' width='40' height='40'>", TILE_IMAGE));
                } else {
                    // Prazdna bunka
                    sb.append("<div style='width:40px; height:40px; background-color:#1a1a1a;'></div>");
                }

                sb.append("</a></td>");
            }
            sb.append("</tr>");
        }
        return sb.append("</table>").toString();
    }

    public String getHtmlBlocks() {
        StringBuilder sb = new StringBuilder("<div style='display:flex; justify-content:center; gap:20px; align-items: flex-end;'>");
        List<Block> blocks = field.getBlocks();

        for (int i = 0; i < blocks.size(); i++) {
            Block block = blocks.get(i);
            if (block == null) {
                sb.append("<div style='opacity:0.3; width:120px; text-align:center;'>used</div>");
                continue;
            }
            String selectionStyle = (i == selectedBlock) ? "outline: 3px solid blue; outline-offset: 5px;" : "";

            sb.append(String.format("<a href='/block-puzzle?command=select&blockIndex=%d' style='text-decoration:none; color:inherit;'>", i));
            sb.append(String.format("<div style='%s'>", selectionStyle));
            sb.append("<table style='border-collapse: collapse; border: none; background: none;'>");

            for (int row = 0; row < block.getHeight(); row++) {
                sb.append("<tr>");
                for (int col = 0; col < block.getWidth(); col++) {
                    sb.append("<td style='width:40px; height:40px; padding:0; border: none !important; line-height:0;'>");
                    if (block.getShape()[row][col] == 1) {
                        sb.append(String.format("<img src='%s' width='40' height='40' style='display:block;'>", TILE_IMAGE));
                    }
                    sb.append("</td>");
                }
                sb.append("</tr>");
            }
            sb.append("</table></div></a>");
        }
        sb.append("</div>");
        return sb.toString();
    }
}