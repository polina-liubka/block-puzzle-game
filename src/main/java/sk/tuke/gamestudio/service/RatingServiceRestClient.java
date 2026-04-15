package sk.tuke.gamestudio.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;
import sk.tuke.gamestudio.entity.Rating;

public class RatingServiceRestClient implements RatingService {
    private final String url = "http://localhost:8080/api/rating";

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void setRating(Rating rating) {
        restTemplate.postForEntity(url, rating, Rating.class);
    }

    @Override
    public int getRating(String gameName, String playerName) {
        return restTemplate.getForObject(url + "/" + gameName + "/" + playerName, Integer.class);
    }

    @Override
    public int getAverageRating(String gameName) {
        return restTemplate.getForObject(url + "/" + gameName, Integer.class);
    }

    @Override
    public void reset() {
        throw new UnsupportedOperationException("Not supported via web service");
    }
}