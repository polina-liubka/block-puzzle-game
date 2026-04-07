package sk.tuke.gamestudio.service;

import sk.tuke.gamestudio.entity.Rating;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.persistence.NoResultException;

@Transactional
public class RatingServiceJPA implements RatingService {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void setRating(Rating rating) throws RatingException {
        try{
            Rating existingRating = (Rating) entityManager.createNamedQuery("Rating.getRatingObject")
                    .setParameter("game", rating.getGame())
                    .setParameter("player", rating.getPlayer())
                    .getSingleResult();
            existingRating.setRating(rating.getRating());
            existingRating.setPlayedOn(rating.getPlayedOn());
        }catch (NoResultException e){
            entityManager.merge(rating);
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
        try{
            return (int) entityManager.createNamedQuery("Rating.getRating")
                    .setParameter("game", game)
                    .setParameter("player", player)
                    .getSingleResult();
        }catch (NoResultException e){
            return 0;
        }
    }

    @Override
    public void reset() throws RatingException {
        entityManager.createNamedQuery("Rating.resetRatings").executeUpdate();
    }
}