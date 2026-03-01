package app.entities;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Setter
@Getter
@Table(name = "genres")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Genre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @EqualsAndHashCode.Include
    @Column(name="tmdb_id",nullable = false,unique = true)
    private int tmdbID;
    @Column(nullable = false)
    private String name;
}
