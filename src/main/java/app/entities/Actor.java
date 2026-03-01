package app.entities;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.xml.namespace.QName;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name="actors")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Actor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false)
    private String name;
    @EqualsAndHashCode.Include
    @Column(nullable = false, unique = true)
    private int tmdbID;
    @ManyToMany(mappedBy = "actors")
    private Set<Movie> movies = new HashSet<>();

    public Actor(String name, Set<Movie> movies){
        this.name = name;
        this.movies = movies;
    }
}
