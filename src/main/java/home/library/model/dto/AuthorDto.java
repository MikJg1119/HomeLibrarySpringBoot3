package home.library.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

public record AuthorDto (
      String name,
      int id
) implements Serializable {

}
