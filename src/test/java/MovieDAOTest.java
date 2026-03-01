import app.HibernateConfig.HibernateConfig;
import app.DAOs.MovieDAO;
import app.entities.Director;
import app.entities.Genre;
import app.entities.Movie;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class MovieDAOTest {
    protected static EntityManagerFactory emf;
    private MovieDAO movieDAO = new MovieDAO();

    @BeforeAll
    static void boot() {
        HibernateConfig.setTest(true);
        emf = HibernateConfig.getEntityManagerFactoryForTest();
        // uses jdbc:tc postgres
    }


    @Test
    void createMovieAndFindByTitle(){
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Director d = new Director();
        d.setName("test director");
        d.setTmdbID(1);
        em.persist(d);

        Movie m = new Movie();
        m.setPopularity(6.5);
        m.setDirector(d);
        m.setTitle("test movie");
        m.setTmdbID(1);
        m.setReleaseDate(LocalDate.of(2020, 1, 1));
        m.setRating(8.0);
        movieDAO.create(m, em);
        em.getTransaction().commit();

        Movie foundMovie = movieDAO.findByTitle("test movie", em);
        assertNotNull(foundMovie);
        assertEquals(1, foundMovie.getTmdbID());
        assertEquals("test director", foundMovie.getDirector().getName());
        em.close();
    }
    @Test
    void updateChangesTitle() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        Movie m = new Movie();
        m.setTitle("Old");
        m.setTmdbID(1000);
        m.setRating(5.0);
        m.setPopularity(1.0);
        m.setReleaseDate(LocalDate.now());
        movieDAO.create(m, em);

        em.getTransaction().commit();

        em.getTransaction().begin();
        m.setTitle("New");
        Movie updated = movieDAO.update(m, em);
        em.getTransaction().commit();

        Movie found = movieDAO.findByTitle("New", em);
        assertNotNull(found);
        assertEquals("New", found.getTitle());
        em.close();
    }
    @Test
    void deleteRemovesMovie() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        Movie m = new Movie();
        m.setTitle("Delete Me");
        m.setTmdbID(2000);
        m.setRating(5.0);
        m.setPopularity(1.0);
        m.setReleaseDate(LocalDate.now());
        movieDAO.create(m, em);

        em.getTransaction().commit();

        em.getTransaction().begin();
        int deletedId = movieDAO.delete(m, em);
        em.getTransaction().commit();

        assertTrue(deletedId > 0);
        assertNull(movieDAO.getByID(deletedId, em));
        em.close();
    }
    @Test
    void findByGenreNameReturnsMovies() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        Genre g = new Genre();
        g.setName("Action");
        g.setTmdbID(28);
        em.persist(g);

        Movie m = new Movie();
        m.setTitle("Action Movie");
        m.setTmdbID(3000);
        m.setRating(6.0);
        m.setPopularity(1.0);
        m.setReleaseDate(LocalDate.now());
        m.getGenres().add(g);

        movieDAO.create(m, em);
        em.getTransaction().commit();

        Set<Movie> results = movieDAO.findByGenreName("Action", em);
        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(x -> x.getTitle().equals("Action Movie")));
        em.close();
    }
    @Test
    void calculateAverageRating_works() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        Movie m1 = new Movie();
        m1.setTitle("A");
        m1.setTmdbID(4001);
        m1.setRating(6.0);
        m1.setPopularity(1.0);
        m1.setReleaseDate(LocalDate.now());

        Movie m2 = new Movie();
        m2.setTitle("B");
        m2.setTmdbID(4002);
        m2.setRating(8.0);
        m2.setPopularity(1.0);
        m2.setReleaseDate(LocalDate.now());

        movieDAO.create(m1, em);
        movieDAO.create(m2, em);

        em.getTransaction().commit();

        double avg = movieDAO.calculateAverageRating(em);
        assertEquals(7, avg, 0.0001);
        em.close();
    }


}
