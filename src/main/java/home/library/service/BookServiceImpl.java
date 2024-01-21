package home.library.service;

import home.library.model.Author;
import home.library.model.Book;
import home.library.model.dto.BookDto;
import home.library.repository.BookRepository;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookServiceImpl implements BookService{

    private static final StringBuilder urlBuild = new StringBuilder("https://data.bn.org.pl/api/institutions/bibs.json?isbnIssn=");

    private BookRepository bookRepository;

    private AuthorService authorService;

    private UserService userService;

    public BookServiceImpl(BookRepository bookRepository, AuthorService authorService, UserService userService) {
        this.bookRepository = bookRepository;
        this.authorService = authorService;
        this.userService = userService;
    }

    public BookServiceImpl() {
    }

    @Autowired
    public void setBookRepository(home.library.repository.BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Autowired
    public void setAuthorService(home.library.service.AuthorService authorService) {
        this.authorService = authorService;
    }

    @Autowired
    public void setUserService(home.library.service.UserService userService) {
        this.userService = userService;
    }

    @Override
    public void addBook(Book book) {
        bookRepository.save(book);
    }

    @Override
    public void removeBook(int id) {
        bookRepository.deleteById(id);
    }

    @Override
    public Book getBook(int id) {
        Optional<Book> optional = bookRepository.findById(id);
        Book book = null;
        if (optional.isPresent()){
            book = optional.get();
        }else {
            throw new RuntimeException(" Book not found ");
        }
        return book;
    }

    @Override
    public void updateBook(Book book) {
        Optional<Book> optional = bookRepository.findById(book.getId());
        if (optional.isPresent()){
            Book dBEntity = optional.get();
            Optional.ofNullable(book.getId()).ifPresent(dBEntity::setId);
            Optional.ofNullable(book.getAuthor()).ifPresent(dBEntity::setAuthor);
            Optional.ofNullable(book.getIsbn()).ifPresent(dBEntity::setIsbn);
            Optional.ofNullable(book.getPublisher()).ifPresent(dBEntity::setPublisher);
            Optional.ofNullable(book.getPublishedYear()).ifPresent(dBEntity::setPublishedYear);
            Optional.ofNullable(book.getLanguage()).ifPresent(dBEntity::setLanguage);
            Optional.ofNullable(book.getSaga()).ifPresent(dBEntity::setSaga);
            Optional.ofNullable(book.getPublishingSeries()).ifPresent(dBEntity::setPublishingSeries);
            Optional.ofNullable(book.getCover()).ifPresent(dBEntity::setCover);
            bookRepository.save(dBEntity);
        }else {
            bookRepository.save(book);
        }
    }

    @Override
    public List<Book> getBooks() {
        return bookRepository.findAll();

    }

    @Override
    public List<Book> getBooksById(Iterable<Integer> ids) {
        return bookRepository.findAllById(ids);
    }

    @Override
    public Book getBookByName(String title) {
        return bookRepository.findByTitle(title);
    }

    @Override
    public List<Book> getBookByAuthor(String author) {
        return bookRepository.findByAuthor(author);
    }

    @Override
    public Book getBookByIsbn(String isbn) {
        String finalIsbn = isbn.replace("-","");
        Optional<Book> optional = bookRepository.findByIsbn(finalIsbn);
        Book book = null;
        if (optional.isPresent()){
            return  optional.get();
        }else {
            book = scrapeBookByIsbn(finalIsbn);
            bookRepository.save(book);
        }
        Author author;
        List<Book> authorsBooks;
        Optional<Author> authorOptional = authorService.getAuthorByName(book.getAuthor());
        if (authorOptional.isPresent()){
            author = authorOptional.get();
            authorsBooks = author.getBooksByAuthor();
        }else {
            author = new Author(book.getAuthor());
            authorsBooks = new ArrayList<Book>();
        }
        authorsBooks.add(book);
        author.setBooksByAuthor(authorsBooks);
        authorService.updateAuthor(author);
        return book;
    }

    @Override
    public Book toBook(BookDto bookDto) {
        Book book = new Book();
        book.setTitle(bookDto.getTitle());
        book.setAuthor(bookDto.getAuthor());
        book.setIsbn(bookDto.getIsbn());
        book.setPublisher(bookDto.getPublisher());
        book.setPublishedYear(bookDto.getPublishedYear());
        book.setLanguage(bookDto.getLanguage());
        book.setId(bookDto.getId());
        book.setSaga(bookDto.getSaga());
        book.setPublishingSeries(bookDto.getPublishingSeries());
        return book;
    }

    @Override
    public BookDto toBookDto(Book book) {
        BookDto bookDto = new BookDto();
        bookDto.setTitle(book.getTitle());
        bookDto.setAuthor(book.getAuthor());
        bookDto.setIsbn(book.getIsbn());
        bookDto.setPublisher(book.getPublisher());
        bookDto.setPublishedYear(book.getPublishedYear());
        bookDto.setLanguage(book.getLanguage());
        bookDto.setId(book.getId());
        bookDto.setSaga(book.getSaga());
        bookDto.setPublishingSeries(book.getPublishingSeries());
        return bookDto;
    }

    @Override
    public BookDto toBookDtoWithLocation(Book book, String location) {
        BookDto bookDto = new BookDto();
        bookDto.setTitle(book.getTitle());
        bookDto.setAuthor(book.getAuthor());
        bookDto.setIsbn(book.getIsbn());
        bookDto.setPublisher(book.getPublisher());
        bookDto.setPublishedYear(book.getPublishedYear());
        bookDto.setLanguage(book.getLanguage());
        bookDto.setId(book.getId());
        bookDto.setSaga(book.getSaga());
        bookDto.setPublishingSeries(book.getPublishingSeries());
        bookDto.setLocation(location);
        return bookDto;
    }

    @Override
    public List<BookDto> getAllBooksDto(List<Book> books) {
        List<BookDto> listBookDto = new ArrayList<BookDto>();
        for (Book book : books){
            listBookDto.add(toBookDto(book));
    }

        return listBookDto;
    }

    @Override
    public Book scrapeBookByIsbn(String isbn){
        String finalIsbn = isbn.replace("-","");
        Book book = new Book();
        book.setIsbn(finalIsbn);
        urlBuild.append(finalIsbn);
        try {
            URL url = new URL(urlBuild.toString());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent","mozilla/17.0");
            connection.connect();
            int responseCode = connection.getResponseCode();
            String response="";
            if (responseCode==HttpURLConnection.HTTP_OK){
                try {
                    Scanner scanner= new Scanner(url.openStream());
                    while (scanner.hasNext()){
                        response+=scanner.nextLine();
                    }
                    JSONParser parser = new JSONParser();
                    JSONObject obj = (JSONObject) parser.parse(response);
                    JSONArray jsonArray = (JSONArray) obj.get("bibs");
                    JSONObject jsonObject = (JSONObject) jsonArray.get(0);
                    book.setTitle((String) jsonObject.get("title"));
                    book.setPublisher ((String) jsonObject.get("publisher"));
                    String authorsString = (String) jsonObject.get("author");
                    ArrayList<String> authorsArray = (ArrayList<String>) Arrays.stream(authorsString.split("\\(")).toList();
                    book.setAuthor(authorsArray.get(0));
                    book.setPublishedYear((String) jsonObject.get("publicationYear"));
                    book.setLanguage((String) jsonObject.get("language"));
                    book.setSaga( "");
                    book.setPublishingSeries("");
                }catch (IndexOutOfBoundsException e){
                    book.setSaga("");
                    book.setPublishingSeries("");
                    book.setLanguage("");
                    book.setPublishedYear("");
                    book.setAuthor("");
                    book.setTitle("");
                    book.setPublisher("");

                }
            }
        } catch (IOException | ParseException e){
            e.printStackTrace();
        }

        return book;
    }

    @Override
    public List<BookDto> getAllBooksDtoWithLocation(Map<Book, String> books) {
        List<BookDto> listBookDto = new ArrayList<BookDto>();
        for (Book book : books.keySet()){
            listBookDto.add(toBookDtoWithLocation(book, books.get(book)));
        }

        return listBookDto;
    }
}
