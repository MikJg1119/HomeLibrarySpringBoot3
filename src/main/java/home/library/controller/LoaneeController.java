package home.library.controller;

import home.library.config.JwtTokenUtil;
import home.library.model.Book;
import home.library.model.Loanee;
import home.library.model.User;
import home.library.model.UsersLibrary;
import home.library.model.dto.BookDto;
import home.library.service.BookService;
import home.library.service.LoaneeService;
import home.library.service.UserService;
import home.library.service.UsersLibraryService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.spec.InvalidParameterSpecException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
public class LoaneeController {

    @Autowired
    private LoaneeService loaneeService;

    @Autowired
    private BookService bookService;

    @Autowired
    private UserService userService;

    @Autowired
    private UsersLibraryService usersLibraryService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;


    public LoaneeController() {
    }

    public LoaneeController(LoaneeService loaneeService, BookService bookService, UserService userService, UsersLibraryService usersLibraryService) {
        this.loaneeService = loaneeService;
        this.bookService = bookService;
        this.userService = userService;
        this.usersLibraryService = usersLibraryService;
    }

    @PostMapping("/loanBooksToLoanee/{loaneeId}")
    public HttpStatus loanBooksToLoanee(@PathVariable(value = "loaneeId") Integer loaneeId,
                                        HttpServletRequest request,
                                        @RequestBody int [] booksToBeLoanedIds){

        String username = jwtTokenUtil.returnUserFromRequest(request);
        User user = userService.getUserByEmail(username);
        UsersLibrary usersLibrary = usersLibraryService.getUsersLibraryByUser(user);
        Loanee loanee = loaneeService.getLoanee(loaneeId);
        try {
            loaneeService.loanBook(usersLibrary, user,booksToBeLoanedIds,loaneeId);
        } catch (InvalidParameterSpecException e) {
            e.printStackTrace();
            return HttpStatus.UNPROCESSABLE_ENTITY;
        }
        return HttpStatus.OK;
    }

    @PostMapping("/saveLoaneeAndLoanBooks")
    public HttpStatus saveLoaneeAndLoanBooks(@RequestBody Loanee loanee,
                                             HttpServletRequest request,
                                         @RequestBody int [] booksToBeLoanedId){

        if (!loaneeService.getLoanees().contains(loanee)){
            loaneeService.addLoanee(loanee);
        }

        List<Book> booksToBeLoaned = new ArrayList<>();
        for (int id : booksToBeLoanedId){
            booksToBeLoaned.add(bookService.getBook(id));
        }

        String username = jwtTokenUtil.returnUserFromRequest(request);
        User user = userService.getUserByEmail(username);
        UsersLibrary usersLibrary = usersLibraryService.getUsersLibraryByUser(user);
        try {
            loaneeService.loanBook(usersLibrary, user, booksToBeLoanedId,loanee.getId());
        } catch (InvalidParameterSpecException e) {
            e.printStackTrace();
            return HttpStatus.UNPROCESSABLE_ENTITY;
        }

        return HttpStatus.OK;
    }

    @PostMapping("/loanedBooksListByLoanee/{id}")
    public List<BookDto> listOfBooksLoanedByLoanee(@PathVariable(value = "id") Integer id,
                                                   HttpServletRequest request){
        String username = jwtTokenUtil.returnUserFromRequest(request);
        User user = userService.getUserByEmail(username);
        Map<Book,String> usersBooks= usersLibraryService.getBooksByUser(user);
        List<BookDto> bookDtos = bookService.getAllBooksDtoWithLocation(usersBooks);
        List<BookDto> booksLoanedByLoanee = new ArrayList<>();
//        return  loaneeService.getLoanee(id).getLoanedBooks().stream().filter(e.getId() -> bookDtos.contains()).collect(Collectors.toList());
        UsersLibrary usersLibrary = usersLibraryService.getUsersLibraryByUser(user);
        for (Book book : usersLibrary.getBooksLoanedToLoanees().keySet()){
            if (usersLibrary.getBooksLoanedToLoanees().get(book).getId() == id){
                booksLoanedByLoanee.add(bookService.toBookDto(book));
            }
        }
        return booksLoanedByLoanee;
    }

    @PostMapping("/returnBook/{id}")
    public HttpStatus returnBookToLibrary(@PathVariable(value = "id") Integer bookId,
                                          HttpServletRequest request){
        Book book = bookService.getBook(bookId);
        String username = jwtTokenUtil.returnUserFromRequest(request);
        User user = userService.getUserByEmail(username);
        usersLibraryService.returnBook(book, user);
        return HttpStatus.ACCEPTED;
    }

    @GetMapping ("/loanees")
    public List<Loanee> getLoaneesList(HttpServletRequest request){
        String username = jwtTokenUtil.returnUserFromRequest(request);
        User user = userService.getUserByEmail(username);
        return usersLibraryService.getUsersLibraryByUser(user).getLoanees();
    }

}
