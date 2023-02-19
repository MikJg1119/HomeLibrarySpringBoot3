package home.library.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class BookDto implements Serializable {

    private String title;
    private String author;
    private String isbn;
    private String publisher;
    private String publishedYear;
    private String language;
    private int id;
    private String saga;
    private String publishingSeries;
    private String location;

}
