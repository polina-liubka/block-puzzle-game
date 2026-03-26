package sk.tuke.gamestudio.service;

import sk.tuke.gamestudio.entity.Rating;
import java.sql.*;

public class RatingServiceJDBC implements RatingService {
    public static final String URL = "jdbc:postgresql://localhost/gamestudio";
    public static final String USER = "postgres";
    public static final String PASSWORD = "yorik1325";
    public static final String SELECT_AVG = "SELECT AVG(rating) FROM rating WHERE game = ?";
    public static final String SELECT_SINGLE = "SELECT rating FROM rating WHERE game = ? AND player = ? ORDER BY playedOn DESC LIMIT 10";
    public static final String DELETE = "DELETE FROM rating";
    public static final String INSERT = "INSERT INTO rating (game, player, rating, playedOn) VALUES (?, ?, ?, ?)";

    @Override
    public void setRating(Rating rating) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String checkSql = "SELECT COUNT(*) FROM rating WHERE game = ? AND player = ?";
            boolean exists = false;

            try (PreparedStatement check = connection.prepareStatement(checkSql)) {
                check.setString(1, rating.getGame());
                check.setString(2, rating.getPlayer());
                try (ResultSet rs = check.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        exists = true;
                    }
                }
            }
            if(exists){
                String updateSql = "UPDATE rating SET rating = ?, playedOn = ? WHERE game = ? AND player = ?";
                try (PreparedStatement updateStmt = connection.prepareStatement(updateSql)) {
                    updateStmt.setInt(1, rating.getRating());
                    updateStmt.setTimestamp(2, new Timestamp(rating.getPlayedOn().getTime()));
                    updateStmt.setString(3, rating.getGame());
                    updateStmt.setString(4, rating.getPlayer());
                    updateStmt.executeUpdate();
                }
            } else {
                String insertSql = "INSERT INTO rating (game, player, rating, playedOn) VALUES (?, ?, ?, ?)";
                try (PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
                    insertStmt.setString(1, rating.getGame());
                    insertStmt.setString(2, rating.getPlayer());
                    insertStmt.setInt(3, rating.getRating());
                    insertStmt.setTimestamp(4, new Timestamp(rating.getPlayedOn().getTime()));
                    insertStmt.executeUpdate();
                }
            }

        } catch (SQLException e) {
            throw new RatingException("Problem setting rating", e);
        }
    }

    @Override
    public int getAverageRating(String game) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(SELECT_AVG)) {
            statement.setString(1, game);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
        } catch (SQLException e) {
            throw new RatingException("Problem getting average rating", e);
        }
    }

    @Override
    public int getRating(String game, String player) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(SELECT_SINGLE)) {
            statement.setString(1, game);
            statement.setString(2, player);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
        } catch (SQLException e) {
            throw new RatingException("Problem getting rating", e);
        }
    }

    @Override
    public void reset() {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(DELETE);
        } catch (SQLException e) {
            throw new RatingException("Problem deleting ratings", e);
        }
    }
}