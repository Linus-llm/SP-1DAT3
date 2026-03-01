
import app.DAOs.GenreDAO;
import app.HibernateConfig.HibernateConfig;
import app.entities.Genre;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class GenreDAOTest {

    protected static EntityManagerFactory emf;
    private GenreDAO genreDAO = new GenreDAO();

    @BeforeAll
    static void boot() {
        HibernateConfig.setTest(true);
        emf = HibernateConfig.getEntityManagerFactoryForTest();
    }

    @Test
    void createAndFindByTmdbID() {
        EntityManager em = emf.createEntityManager();

            em.getTransaction().begin();

            Genre g = new Genre();
            g.setTmdbID(28);
            g.setName("Action");
            genreDAO.create(g, em);

            em.getTransaction().commit();

            Genre found = genreDAO.findByTmbdID(28, em);
            assertNotNull(found);
            assertEquals("Action", found.getName());

            em.close();

    }

    @Test
    void findByName() {
        EntityManager em = emf.createEntityManager();

            em.getTransaction().begin();

            Genre g = new Genre();
            g.setTmdbID(12);
            g.setName("Adventure");
            genreDAO.create(g, em);

            em.getTransaction().commit();

            Genre found = genreDAO.findByName("Adventure", em);
            assertNotNull(found);
            assertEquals(12, found.getTmdbID());

            em.close();

    }
}