package app.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MovieDTOfilter {
    @JsonProperty("results")
    private List<MovieDTO> results;
    @JsonProperty("total_pages")
    private int totalPages;
}
