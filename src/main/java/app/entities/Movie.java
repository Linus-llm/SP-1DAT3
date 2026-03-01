package app.entities;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
@Entity
@NoArgsConstructor
@Setter
@Getter
@Table(name = "movies")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Movie {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false)
    private String title;
    private double rating;
    private LocalDate releaseDate;
    @ManyToOne
    @JoinColumn(name = "director_id")
    private Director director;
    @ManyToMany
    @JoinTable(name="movie_actors", joinColumns = @JoinColumn(name="movie_id"), inverseJoinColumns = @JoinColumn(name="actor_id"))
    private Set<Actor> actors = new HashSet<>();
    @ManyToMany
    @JoinTable(name="movie_genres", joinColumns = @JoinColumn(name="movie_id"), inverseJoinColumns = @JoinColumn(name="genre_id"))
    private Set<Genre> genres = new HashSet<>();
    @EqualsAndHashCode.Include
    @Column(nullable = false, unique = true)
    private int tmdbID;
    @Column(nullable = true)
    private Double popularity;


}
