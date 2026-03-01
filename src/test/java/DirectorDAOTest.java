
import app.DAOs.DirectorDAO;
import app.DAOs.GenreDAO;
import app.HibernateConfig.HibernateConfig;
import app.entities.Director;
import app.entities.Genre;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class DirectorDAOTest {

    protected static EntityManagerFactory emf;
    private final DirectorDAO directorDAO = new DirectorDAO();

    @BeforeAll
    static void boot() {
        HibernateConfig.setTest(true);
        emf = HibernateConfig.getEntityManagerFactoryForTest();
    }

    @Test
    void createAndFindByTmdbID() {
        EntityManager em = emf.createEntityManager();

        em.getTransaction().begin();

        Director d = new Director();
        d.setTmdbID(28);
        d.setName("Nolan");
        directorDAO.create(d, em);

        em.getTransaction().commit();

        Director found = directorDAO.findByTmbdID(28, em);
        assertNotNull(found);
        assertEquals("Nolan", found.getName());

        em.close();

    }

}