package app.DAOs;

import app.Main;
import app.entities.Actor;
import app.entities.Director;
import app.entities.Genre;
import app.entities.Movie;
import jakarta.persistence.EntityManager;

import java.util.HashSet;
import java.util.Set;

public class GenreDAO implements IDAO<Genre> {
    @Override
    public Genre create(Genre genre, EntityManager em) {
        em.persist(genre);
        return genre;
    }
    @Override
    public Genre update(Genre genre, EntityManager em) {
        return em.merge(genre);
    }
    @Override
    public int delete(Genre genre, EntityManager em) {
        Genre managed = em.find(Genre.class, genre.getId());
        if (managed == null) return -1;
        em.remove(managed);
        return managed.getId();
    }
    @Override
    public Genre getByID(int id, EntityManager em) {
        return em.find(Genre.class, id);
    }
    @Override
    public Set<Genre> getAll(EntityManager em) {
        return new HashSet<>(em.createQuery("SELECT g FROM Genre g", Genre.class).getResultList());
    }
    public Genre findByTmbdID(int tmdbID, EntityManager entityManager) {
            Genre g = entityManager.createQuery("SELECT g FROM Genre g WHERE g.tmdbID = :tmdbID", Genre.class)
                    .setParameter("tmdbID", tmdbID).getSingleResultOrNull();
            return g;
        }

        public Set<Genre> findByMovie(Movie movie, EntityManager entityManager){
            Set<Genre> genres = new HashSet<>(entityManager.createQuery("SELECT g FROM Movie m JOIN m.genres g WHERE m.id = :movieId", Genre.class)
                    .setParameter("movieId", movie.getId()).getResultList());
            return genres;
        }

        public Genre findByName(String name, EntityManager entityManager){
        Genre g = entityManager.createQuery("SELECT g FROM Genre g WHERE g.name = :name", Genre.class).setParameter("name",name).getSingleResultOrNull();
        return g;
    }

}
