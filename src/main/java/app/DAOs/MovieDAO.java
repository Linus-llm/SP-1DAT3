package app.DAOs;

import app.Main;
import app.entities.Director;
import app.entities.Movie;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.HashSet;
import java.util.Set;

public class MovieDAO implements IDAO<Movie>{

    @Override
    public Movie create(Movie movie, EntityManager em) {
        em.persist(movie);
        return movie;
    }
    @Override
    public Movie update(Movie movie, EntityManager em) {
        return em.merge(movie);
    }
    @Override
    public int delete(Movie movie, EntityManager em) {
        Movie managed = em.find(Movie.class, movie.getId());
        if (managed == null) return -1; // or throw NotFoundException in Service
        em.remove(managed);
        return managed.getId();
    }
    @Override
    public Movie getByID(int id, EntityManager em) {
        return em.find(Movie.class, id);
    }
    @Override
    public Set<Movie> getAll(EntityManager em) {
        return new HashSet<>(em.createQuery("SELECT m FROM Movie m", Movie.class).getResultList());
    }
    public Movie findByTmbdID(int tmdbID, EntityManager entityManager) {

            Movie m = entityManager.createQuery("SELECT m FROM Movie m WHERE m.tmdbID = :tmdbID", Movie.class)
                    .setParameter("tmdbID", tmdbID).getSingleResultOrNull();
            return m;

    }
    public Movie findByTitle(String title, EntityManager entityManager) {

        Movie m = entityManager.createQuery("SELECT m FROM Movie m WHERE m.title = :title", Movie.class)
                .setParameter("title", title).getSingleResultOrNull();
        return m;

    }

    public Set<Movie> findByGenreName (String genreName, EntityManager entityManager){
        Set<Movie> movies = new HashSet<>(entityManager.createQuery("SELECT m from Movie m JOIN m.genres g WHERE g.name = :genreName").setParameter("genreName", genreName).getResultList());
    return movies;
    }

    public Set<Movie> findByAllByKeyword(String keyword, EntityManager entityManager) {
        Set<Movie> movies = new HashSet<>(entityManager.createQuery("Select m from Movie m where m.title ILIKE :keyword ").setParameter("keyword", "%"+keyword+"%").getResultList());
        return movies;
    }

    public double calculateAverageRating(EntityManager entityManager) {
        Double averageRating = entityManager.createQuery("SELECT AVG(m.rating) FROM Movie m WHERE m.rating != 0.0", Double.class).getSingleResultOrNull();
        return averageRating;
    }

    public Set<Movie> getTop10HighestRatedMovies(EntityManager entityManager) {
        Set<Movie> movies = new HashSet<>(entityManager.createQuery("SELECT m FROM Movie m WHERE m.rating != 10.0 ORDER BY m.rating DESC", Movie.class).setMaxResults(10).getResultList());
        return movies;
    }
    public Set<Movie> getTop10LowestRatedMovies(EntityManager entityManager) {
        Set<Movie> movies = new HashSet<>(entityManager.createQuery("SELECT m FROM Movie m WHERE m.rating != 0.0 ORDER BY m.rating ASC ", Movie.class).setMaxResults(10).getResultList());
        return movies;
    }
    public Set<Movie> getTop10MostPopularMovies(EntityManager entityManager){
        Set<Movie> movies = new HashSet<>(entityManager.createQuery("SELECT m FROM Movie m ORDER BY m.popularity DESC", Movie.class).setMaxResults(10).getResultList());
        return movies;
    }


}
