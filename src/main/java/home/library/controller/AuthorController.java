package home.library.controller;

import home.library.config.JwtTokenUtil;
import home.library.model.Author;
import home.library.model.Book;
import home.library.model.User;
import home.library.model.UsersLibrary;
import home.library.model.dto.AuthorDto;
import home.library.service.AuthorService;
import home.library.service.BookService;
import home.library.service.UserService;
import home.library.service.UsersLibraryService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
public class AuthorController {

    @Autowired
    AuthorService authorService;

    @Autowired
    UsersLibraryService usersLibraryService;

    @Autowired
    UserService userService;

    @Autowired
    BookService bookService;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @GetMapping("/authors")
    @ResponseBody
    public List<Author> authorsList(HttpServletRequest request){
        String username = jwtTokenUtil.returnUserFromRequest(request);
        User user = userService.getUserByEmail(username);
        return authorService.getAuthorsByUser(user);
    }


    @GetMapping("/showAuthorsBooks/{authorId}")
    public List<Book> showBooksByAuthor(@PathVariable(value = "authorId") Integer id,
                                        HttpServletRequest request){
        String username = jwtTokenUtil.returnUserFromRequest(request);
        User user = userService.getUserByEmail(username);
        List<Book> books;
        UsersLibrary usersLibrary = usersLibraryService.getUsersLibraryByUser(user);
        Author author = authorService.getAuthor(id);
        List<Book> authorsBooks=bookService.getBookByAuthor(author.getName());
        books=usersLibrary.getBooksAndLocation().keySet().stream().filter(authorsBooks::contains).collect(Collectors.toList());
        return books;
    }

    @PostMapping("/updateAuthor")
    public HttpStatus updateAuthor(@RequestBody AuthorDto authorDto){
        Author author = authorService.getAuthorFromDto(authorDto);
        authorService.updateAuthor(author);
        return HttpStatus.ACCEPTED;
    }


}
