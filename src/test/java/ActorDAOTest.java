
import app.DAOs.ActorDAO;

import app.HibernateConfig.HibernateConfig;
import app.entities.Actor;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class ActorDAOTest {

    protected static EntityManagerFactory emf;
    private ActorDAO actorDAO = new ActorDAO();

    @BeforeAll
    static void boot() {
        HibernateConfig.setTest(true);
        emf = HibernateConfig.getEntityManagerFactoryForTest();
    }

    @Test
    void createAndFindByTmdbID() {
        EntityManager em = emf.createEntityManager();

        em.getTransaction().begin();

        Actor a = new Actor();
        a.setTmdbID(28);
        a.setName("Nolan");
        actorDAO.create(a, em);

        em.getTransaction().commit();

        Actor found = actorDAO.findByTmbdID(28, em);
        assertNotNull(found);
        assertEquals("Nolan", found.getName());

        em.close();

    }

}