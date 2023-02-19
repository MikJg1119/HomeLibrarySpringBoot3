package home.library.service;


import home.library.model.Book;
import home.library.model.dto.BookDto;

import java.util.List;
import java.util.Map;

public interface BookService {

    void addBook(Book book);

    void removeBook(int id);

    Book getBook(int id);

    void updateBook(Book book);

    List<Book> getBooks();

    List<Book> getBooksById(Iterable<Integer> ids);

    Book getBookByName(String title);

    List<Book> getBookByAuthor(String author);

    Book getBookByIsbn(String isbn);

    Book toBook(BookDto bookDto);

    BookDto toBookDto(Book book);

    List<BookDto> getAllBooksDto(List<Book> books);

    Book scrapeBookByIsbn(String isbn);

    BookDto toBookDtoWithLocation(Book book, String location);

    List<BookDto> getAllBooksDtoWithLocation(Map<Book, String> books);
}
