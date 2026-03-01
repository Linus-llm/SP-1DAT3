package app.DAOs;

import app.Main;
import app.entities.Actor;
import app.entities.Director;
import app.entities.Genre;
import app.entities.Movie;
import jakarta.persistence.EntityManager;

import java.util.HashSet;
import java.util.Set;

public class DirectorDAO implements IDAO<Director>{
    @Override
    public Director create(Director director, EntityManager em) {
        em.persist(director);
        return director;
    }
    @Override
    public Director update(Director director, EntityManager em) {
        return em.merge(director);
    }
    @Override
    public int delete(Director director, EntityManager em) {
        Director managed = em.find(Director.class, director.getId());
        if (managed == null) return -1;
        em.remove(managed);
        return managed.getId();
    }
    @Override
    public Director getByID(int id, EntityManager em) {
        return em.find(Director.class, id);
    }
    @Override
    public Set<Director> getAll(EntityManager em) {
        return new HashSet<>(em.createQuery("SELECT d FROM Director d", Director.class).getResultList());
    }
    public Director findByTmbdID(int tmdbID, EntityManager em) {
            Director d = em.createQuery("SELECT d FROM Director d WHERE d.tmdbID = :tmdbID", Director.class)
                    .setParameter("tmdbID", tmdbID).getSingleResultOrNull();
            return d;
    }
}
