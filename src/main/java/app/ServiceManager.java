package app;

import app.DTO.*;
import app.Exceptions.ApiException;
import app.Exceptions.DatabaseException;
import app.Exceptions.NotFoundException;
import app.entities.Actor;
import app.entities.Director;
import app.entities.Genre;
import app.entities.Movie;
import app.DAOs.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;


import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ServiceManager {
    private static HttpClient client = HttpClient.newHttpClient();
    private static ObjectMapper om = new ObjectMapper().findAndRegisterModules();
    private static ActorDAO actorDAO = new ActorDAO();
    private static DirectorDAO directorDAO = new DirectorDAO();
    private static GenreDAO genreDAO = new GenreDAO();
    private static MovieDAO movieDAO = new MovieDAO();



    public static List<MovieDTO> getAPIdataLoopForMovieDTO(String url, int maxPages) {
        List<MovieDTO> all = new ArrayList<>();


            try {
                MovieDTOfilter first = fetchPageForMovieDTO(url, 1);
                all.addAll(first.getResults());
                int pagesToFetch = Math.min(first.getTotalPages(), maxPages);

                for (int page = 2; page <= pagesToFetch; page++) {
                    MovieDTOfilter next = fetchPageForMovieDTO(url, page);
                    all.addAll(next.getResults());
                }

                return all;
            } catch (URISyntaxException | IOException | InterruptedException e) {
                throw new ApiException(500,"TMDb API error");
            }

    }



    private static MovieDTOfilter fetchPageForMovieDTO(String baseUrl, int page) throws URISyntaxException, IOException, InterruptedException{

        String pagedUrl = baseUrl + "&page=" + page;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(pagedUrl))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new ApiException(response.statusCode(), "TMDb error " + response.statusCode() + ": "+ response.body());
        }

        return om.readValue(response.body(), MovieDTOfilter.class);
    }
    private static CreditsDTO fetchCredits(int movieId)
            throws URISyntaxException, IOException, InterruptedException {

        String url = "https://api.themoviedb.org/3/movie/"+movieId+"/credits?api_key=" + System.getenv("api_key");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(url))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new ApiException(response.statusCode(), "TMDb error " + response.statusCode() + ": "+ response.body());
        }

        return om.readValue(response.body(), CreditsDTO.class);
    }

    private static GenreDTOfilter fetchAllGenres() throws URISyntaxException, IOException, InterruptedException {
        String url = "https://api.themoviedb.org/3/genre/movie/list?language=en&api_key=" + System.getenv("api_key");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(url))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new ApiException(response.statusCode(), "TMDb error " + response.statusCode() + ": "+ response.body());
        }
        return om.readValue(response.body(), GenreDTOfilter.class);
    }

    public static void getAllDanishMoviesAndPrintToConsole() {
        EntityManager em = Main.emf.createEntityManager();
        try {
            Set<Movie> movies = movieDAO.getAll(em);
            if (movies.isEmpty()) {
                throw new NotFoundException("No movies found in the database.");
            }
            movies.forEach(m -> System.out.println(m.getTitle()));
            System.out.println(movies.size());
        } catch (RuntimeException e) {
            if (e instanceof NotFoundException) throw e;
            throw new DatabaseException("Error getting all danish movies: ", e);
        } finally {
            em.close();
        }

    }
    public static void getActorsAndDirectorForMovieAndPrintToConsole(String title) {
        EntityManager em = Main.emf.createEntityManager();
        try {
            Movie movie = movieDAO.findByTitle(title, em);
            if (movie != null) {
                System.out.println("Director:");
                System.out.println(movie.getDirector().getName());
                System.out.println("-------------------------------");
                System.out.println("Actors:");
                movie.getActors().forEach(a -> System.out.println(a.getName()));
            }
            else if (movie == null) {
                throw new NotFoundException("No movie found in the database.");
            }
        } catch (RuntimeException e) {
            if (e instanceof NotFoundException) throw e;
            throw new DatabaseException("Error getting actors and director for movie "+title+": ", e);
        } finally {
            em.close();
        }
    }

    public static void getAllGenresAndPrintToConsole() {
        EntityManager em = Main.emf.createEntityManager();
        try{
            genreDAO.getAll(em).forEach(g -> System.out.println(g.getName()));
        }  catch (RuntimeException e) {
            throw new DatabaseException("Error getting all genres: ", e);
        } finally {
            em.close();
        }
    }

    public static void getGenresForMovieAndPrintToConsole(String title) {
        EntityManager em = Main.emf.createEntityManager();

        try {
            Movie movie = movieDAO.findByTitle(title, em);
            if(movie == null) {
                throw new NotFoundException("Movie with title '" + title + "' was not found.");
            }
            genreDAO.findByMovie(movie, em).forEach(g -> System.out.println(g.getName()));
        } catch (RuntimeException e) {
            if (e instanceof NotFoundException) throw e;
            throw new DatabaseException("Error getting genres for movie"+title+ ": ", e);
        } finally {
            em.close();
        }
    }

    public static void getAllMoviesWithSpecificGenreAndPrintToConsole(String genreName){
        EntityManager em = Main.emf.createEntityManager();
        try {

            Genre genre = genreDAO.findByName(genreName, em);
            if (genre == null) {
                throw new NotFoundException("Genre with name '" + genreName + "' was not found.");
            }
            genre.getName();
            Set<Movie> movies = movieDAO.findByGenreName(genreName, em);

            if (!movies.isEmpty()) {
                movies.forEach(m -> System.out.println(m.getTitle()));
            } else if (movies.isEmpty()) {
                throw new NotFoundException("No movies found in the database.");
            }
        } catch (RuntimeException e) {
            if (e instanceof NotFoundException) throw e;
            throw new DatabaseException("Error getting movies with specific" + genreName +": ", e);
        } finally {
            em.close();
        }
    }

    public static void saveGenresToDatabase() {
        EntityManager em = Main.emf.createEntityManager();
        try {
            GenreDTOfilter list = fetchAllGenres();
            em.getTransaction().begin();
            for (GenreDTO genreDTO : list.getGenre()) {
                getOrCreateGenre(genreDTO, em);
            }
            em.getTransaction().commit();

        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw new RuntimeException(e);
        } finally {
            em.close();
        }
    }



    public static void saveAllDanishMoviesToDatabase(){
        EntityManager em = Main.emf.createEntityManager();
        try {
            em.getTransaction().begin();

            String url = "https://api.themoviedb.org/3/discover/movie?include_adult=false&include_video=false&language=en-US&primary_release_date.gte=2021-02-26&sort_by=popularity.desc&with_origin_country=DK&with_original_language=da&api_key=" + System.getenv("api_key");

            List<MovieDTO> movies = getAPIdataLoopForMovieDTO(url, 56); //samler alle id'er til at kunne finde cast og crew
            for (MovieDTO movieDTO : movies) {
                saveMovie(movieDTO, em);
            }
            em.getTransaction().commit();
        } catch (RuntimeException | URISyntaxException | IOException | InterruptedException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw new DatabaseException("Error saving movies to database: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    //------------------------------------------------------------------------------
    private static Actor getOrCreateActor(ActorDTO dto, EntityManager em) {
        Actor existing = actorDAO.findByTmbdID(dto.getTmdbID(), em);
        if (existing != null) return existing;

        Actor actor = new Actor();
        actor.setTmdbID(dto.getTmdbID());
        actor.setName(dto.getName());
        return actorDAO.create(actor, em);
    }

    private static Director getOrCreateDirector(DirectorDTO dto, EntityManager entityManager) {
        Director existing = directorDAO.findByTmbdID(dto.getTmdbID(), entityManager);
        if (existing != null) return existing;

        Director director = new Director();
        director.setTmdbID(dto.getTmdbID());
        director.setName(dto.getName());
        return directorDAO.create(director, entityManager);
    }

    private static Genre getOrCreateGenre(GenreDTO dto, EntityManager entityManager) {
        Genre existing = genreDAO.findByTmbdID(dto.getId(), entityManager);
        if (existing != null) return existing;

        Genre genre = new Genre();
        genre.setTmdbID(dto.getId());
        genre.setName(dto.getName());
        return genreDAO.create(genre, entityManager);
    }

    private static DirectorDTO extractDirector(CreditsDTO credits) {
        if (credits == null || credits.getCrew() == null) return null;

        return credits.getCrew().stream()
                .filter(c -> "Director".equalsIgnoreCase(c.getJob()))
                .findFirst()
                .orElse(null);
    }

    public static void createNewMovie(String title, LocalDate releaseDate, double rating, int tmdbID, double popularity) {
        EntityManager em = Main.emf.createEntityManager();
        em.getTransaction().begin();
        try {
            Movie movie = new Movie();
            movie.setTitle(title);
            movie.setReleaseDate(releaseDate);
            movie.setRating(rating);
            movie.setTmdbID(tmdbID);
            movie.setPopularity(popularity);
            movieDAO.create(movie, em);
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw new DatabaseException("Error creating movie: ", e);
        } finally {
            em.close();
        }

    }
    public static Movie updateMovieBasicInfo(String title, String newTitle, LocalDate newDate) {
        EntityManager em = Main.emf.createEntityManager();

        try {
            em.getTransaction().begin();
            Movie movie = movieDAO.findByTitle(title, em);

            if (movie == null) {
                throw new NotFoundException("Movie with title '" + title + "' was not found.");
            }
            movie.setTitle(newTitle);
            movie.setReleaseDate(newDate);
            Movie updated = movieDAO.update(movie, em);
            em.getTransaction().commit();
            return updated;
        } catch (RuntimeException e) {
            if (e instanceof NotFoundException) throw e;
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw new DatabaseException("Error updating movie: ", e);
        } finally {
            em.close();
        }
    }
    public static void deleteMovie(String title) {
        EntityManager em = Main.emf.createEntityManager();

        try {
            em.getTransaction().begin();
            Movie movie = movieDAO.findByTitle(title, em);

            if (movie != null) {
                movieDAO.delete(movie, em);
                System.out.println("Deleted movie: " + title);
            } else if (movie == null) {
                throw new NotFoundException("No movie found in the database.");
            }
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            if (e instanceof NotFoundException) throw e;
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw new DatabaseException("Error deleting movie: ", e);
        } finally {
            em.close();
        }
    }

    public static void getMoviesByKeywordTitleAndPrintToConsole(String keyword) {
        EntityManager em = Main.emf.createEntityManager();

        try {

            Set<Movie> movies = movieDAO.findByAllByKeyword(keyword, em);
            if (movies != null && !movies.isEmpty()) {
                movies.forEach(m -> System.out.println(m.getTitle()));
            }else {
                throw new NotFoundException("No movies found in the database.");
            }
        } catch (RuntimeException e) {
            if (e instanceof NotFoundException) throw e;
            throw new DatabaseException("Error getting movies by" + keyword+ " : ", e);
        } finally {
            em.close();
        }

    }

    public static void getTotalMovieRatingAverageAndPrintToConsole() {
        EntityManager em = Main.emf.createEntityManager();
        try {
            Double averageRating = movieDAO.calculateAverageRating(em);
            if (averageRating != null) {
                System.out.println("Average Movie Rating: " + averageRating);
            } else if (averageRating == null) {
                throw new NotFoundException("Can't calculate average rating - no movies found");
            }
        } catch (RuntimeException e) {
            if (e instanceof NotFoundException) throw e;
            throw new DatabaseException("Error getting total movie rating average: ", e);
        } finally {
        em.close();
    }
        }

    public static void getTop10HighestRatedMoviesAndPrintToConsole() {
        EntityManager em = Main.emf.createEntityManager();
        try {
            Set<Movie> topMovies = movieDAO.getTop10HighestRatedMovies(em);
            if (topMovies != null && !topMovies.isEmpty()) {
                topMovies.forEach(m -> System.out.println(m.getTitle() + " - Rating: " + m.getRating()));
            } else if (topMovies.isEmpty()) {
                throw new NotFoundException("No movies found in the database.");
            }
        } catch (RuntimeException e) {
            if (e instanceof NotFoundException) throw e;
            throw new DatabaseException("Error getting the top 10 highest rated movies:  ", e);
        } finally {
            em.close();
        }
    }
    public static void getTop10LowestRatedMoviesAndPrintToConsole() {
        EntityManager em = Main.emf.createEntityManager();
        try {
            Set<Movie> topMovies = movieDAO.getTop10LowestRatedMovies(em);
            if (topMovies != null && !topMovies.isEmpty()) {
                topMovies.forEach(m -> System.out.println(m.getTitle() + " - Rating: " + m.getRating()));
            } else if (topMovies.isEmpty()) {
                throw new NotFoundException("No movies found in the database.");
            }
        }catch (RuntimeException e) {
            if (e instanceof NotFoundException) throw e;
            throw new DatabaseException("Error fetching top 10 lowest rated movies: ", e);
        } finally {
            em.close();
        }
    }
    public static void getTop10MostPopularMoviesAndPrintToConsole() {
        EntityManager em = Main.emf.createEntityManager();
        try {
            Set<Movie> topMovies = movieDAO.getTop10MostPopularMovies(em);
            if (topMovies != null && !topMovies.isEmpty()) {
                topMovies.forEach(m -> System.out.println(m.getTitle() + " - Popularity: " + m.getPopularity()));
            } else {
                throw new NotFoundException("No movies found in the database.");
            }
        } catch (RuntimeException e) {
            if (e instanceof NotFoundException) throw e;
            throw new DatabaseException("Error fetching top 10 most popular movies: ", e);
        } finally {
            em.close();
        }

    }

    //------------------------------------------------------------------------------
    public static void saveMovie(MovieDTO movieDTO, EntityManager entityManager) throws URISyntaxException, IOException, InterruptedException {
        // 1) we check if the movie exists
        Movie existingMovie = movieDAO.findByTmbdID(movieDTO.getId(),entityManager);
        if (existingMovie != null){

            existingMovie.setPopularity(movieDTO.getPopularity());
            entityManager.merge(existingMovie);
            return;
        }


        // 2) here we create the movie entity
        Movie movie;
        System.out.println(movieDTO.getReleaseDate());

            movie = new Movie();
            movie.setTmdbID(movieDTO.getId());
            movie.setTitle(movieDTO.getTitle());
            movie.setRating(movieDTO.getVoteAverage());
            movie.setReleaseDate(movieDTO.getReleaseDate());
            movie.setPopularity(movieDTO.getPopularity());

            // Gets credits for the movie aka cast and crew
            CreditsDTO credits = fetchCredits(movieDTO.getId());

            // we pull the director from the credits and set it on the movie
            DirectorDTO directorDTO = extractDirector(credits);
            if (directorDTO != null) {
                Director director = getOrCreateDirector(directorDTO, entityManager);
                movie.setDirector(director);
            } else {
                movie.setDirector(null);
            }

            // here we pull the actors
            for (ActorDTO actorDTO : credits.getCast()) {
                Actor actor = getOrCreateActor(actorDTO, entityManager);
                movie.getActors().add(actor);
            }

            // here we match the genres for the movie
            for (Integer genreId : movieDTO.getGenreIds()) {
                Genre genre = genreDAO.findByTmbdID(genreId, entityManager);
                movie.getGenres().add(genre);
            }

            // And last we persist
            movieDAO.create(movie, entityManager);

    }


}
