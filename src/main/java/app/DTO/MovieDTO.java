package app.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)

public class MovieDTO {
    @JsonProperty("original_title")
    private String title;
    @JsonProperty("release_date")
    private LocalDate releaseDate;
    @JsonProperty("vote_average")
    private double voteAverage;
    @JsonProperty("popularity")
    private Double popularity;
    @JsonProperty("id")
    private int id;
    @JsonProperty("genre_ids")
    private Set<Integer> genreIds;
}
