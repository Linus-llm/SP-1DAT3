package app.entities;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "directors")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Director {
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false)
    private String name;
    @EqualsAndHashCode.Include
    @Column(nullable = false, unique = true)
    private int tmdbID;

}
