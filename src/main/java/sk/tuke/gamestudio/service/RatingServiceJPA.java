package sk.tuke.gamestudio.service;

import sk.tuke.gamestudio.entity.Rating;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@Transactional
public class RatingServiceJPA implements RatingService {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void setRating(Rating rating) throws RatingException {
        int count = ((Number) entityManager.createNativeQuery(
                        "SELECT count(*) FROM rating WHERE game = ?1 AND player = ?2")
                .setParameter(1, rating.getGame())
                .setParameter(2, rating.getPlayer())
                .getSingleResult()).intValue();

        if(count > 0){
            entityManager.createNativeQuery(
                            "UPDATE rating SET rating = ?1, playedon = ?2 WHERE game = ?3 AND player = ?4")
                    .setParameter(1, rating.getRating())
                    .setParameter(2, rating.getPlayedOn())
                    .setParameter(3, rating.getGame())
                    .setParameter(4, rating.getPlayer())
                    .executeUpdate();
        }else{
            entityManager.persist(rating);
        }
    }

    @Override
    public int getAverageRating(String game) throws RatingException {
        Object result = entityManager.createNamedQuery("Rating.getAverageRating")
                .setParameter("game", game)
                .getSingleResult();
        return result == null ? 0 : ((Double) result).intValue();
    }

    @Override
    public int getRating(String game, String player) throws RatingException {
        try {
            return (int) entityManager.createNamedQuery("Rating.getRating")
                    .setParameter("game", game)
                    .setParameter("player", player)
                    .getSingleResult();
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public void reset() throws RatingException {
        entityManager.createNamedQuery("Rating.resetRatings").executeUpdate();
    }
}