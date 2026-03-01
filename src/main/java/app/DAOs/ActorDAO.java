package app.DAOs;

import app.Main;
import app.entities.Actor;
import app.entities.Genre;
import app.entities.Movie;
import jakarta.persistence.EntityManager;

import java.util.HashSet;
import java.util.Set;

public class ActorDAO implements IDAO<Actor> {

    @Override
    public Actor create(Actor actor, EntityManager em) {
        em.persist(actor);
        return actor;
    }

    @Override
    public Actor update(Actor actor, EntityManager em) {
        return em.merge(actor);
    }

    @Override
    public int delete(Actor actor, EntityManager em) {
        Actor managed = em.find(Actor.class, actor.getId());
        if (managed == null) return -1;
        em.remove(managed);
        return managed.getId();
    }

    @Override
    public Actor getByID(int id, EntityManager em) {
        return em.find(Actor.class, id);
    }

    @Override
    public Set<Actor> getAll(EntityManager em) {
        return new HashSet<>(em.createQuery("SELECT a FROM Actor a", Actor.class).getResultList());
    }

    public Actor findByTmbdID(int tmdbID, EntityManager em) {
        Actor a = em.createQuery("SELECT a FROM Actor a WHERE a.tmdbID = :tmdbID", Actor.class)
                .setParameter("tmdbID", tmdbID).getSingleResultOrNull();
        return a;

    }
}
