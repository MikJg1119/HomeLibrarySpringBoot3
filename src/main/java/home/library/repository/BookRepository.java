package home.library.repository;

import home.library.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {
    @Query("select b from Book b where b.title = ?1")
    Book findByTitle(String title);
    @Query("select b from Book b where b.author = ?1")
    List<Book> findByAuthor(String author);
    Optional<Book> findByIsbn(String isbn);
}
