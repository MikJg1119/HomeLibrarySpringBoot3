package home.library.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class AuthorDto implements Serializable {
    private String name;
    private int id;
}
