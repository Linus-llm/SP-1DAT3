package app;
import app.HibernateConfig.HibernateConfig;
import app.entities.Movie;
import jakarta.persistence.EntityManagerFactory;

import java.time.LocalDate;

public class Main {
    public static EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();

    public static void main(String[] args) {
        emf.createEntityManager();
        //ServiceManager.saveGenresToDatabase();
        //ServiceManager.saveAllDanishMoviesToDatabase();
        //ServiceManager.getAllDanishMoviesAndPrintToConsole();
        //ServiceManager.getActorsAndDirectorForMovieAndPrintToConsole("Den sidste");
        //ServiceManager.getAllGenresAndPrintToConsole();
        //System.out.println("-----------------------------");
        //ServiceManager.getGenresForMovieAndPrintToConsole("Den sidste viking");
        //System.out.println("----------------------------");
        //ServiceManager.getAllMoviesWithSpecificGenreAndPrintToConsole("Comedy");
        //ServiceManager.createNewMovie("Blinkende lygter", LocalDate.of(2000, 11, 03), 7.1,10180, 6.7);
        //Movie movieTest = ServiceManager.updateMovieBasicInfo("Blinkende lygter", "Flikkende Lygter", LocalDate.of(2001, 11, 03));
        //System.out.println(movieTest.getTitle());
        //ServiceManager.deleteMovie("Flikkende Lygter");
        //ServiceManager.getMoviesByKeywordTitleAndPrintToConsole("Viking");
        //ServiceManager.getTotalMovieRatingAverageAndPrintToConsole();
        //ServiceManager.getTop10HighestRatedMoviesAndPrintToConsole();
        //ServiceManager.getTop10LowestRatedMoviesAndPrintToConsole();
        //ServiceManager.getTop10MostPopularMoviesAndPrintToConsole();

    }
}