package app.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ActorDTO {
    @JsonProperty("name")
    private String name;
    @JsonProperty("id")
    private int tmdbID;
}
