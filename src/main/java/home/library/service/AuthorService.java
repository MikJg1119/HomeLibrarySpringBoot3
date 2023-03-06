package home.library.service;


import home.library.model.Author;
import home.library.model.dto.AuthorDto;

import java.util.List;
import java.util.Optional;
import java.util.Map;

public interface AuthorService {
    void addAuthor(Author author);

    void removeAuthor(int id);

    Author getAuthor(int id);

    void updateAuthor(Author author);

    List<Author> getAuthors();

    List<Author> getAuthorsById(Iterable<Integer> ids);

    Optional<Author> getAuthorByName(String name);

    List<Author> getAuthorsByUser(Map<home.library.model.Book, String> usersbooks);

    Author getAuthorFromDto(AuthorDto authorDto);
}
