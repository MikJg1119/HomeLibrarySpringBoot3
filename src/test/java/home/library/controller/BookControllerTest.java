package home.library.controller;


import home.library.config.JwtTokenUtil;
import home.library.model.Role;
import home.library.model.User;
import home.library.model.UsersLibrary;
import home.library.repository.AuthorRepository;
import home.library.repository.BookRepository;
import home.library.repository.UserRepository;
import home.library.repository.UsersLibraryRepository;
import home.library.service.*;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

class BookControllerTest {

    private BookRepository bookRepository;
    private UserRepository userRepository;
    private UsersLibraryRepository usersLibraryRepository;
    private AuthorRepository authorRepository;

    private BookServiceImpl bookService;
    private UserServiceImpl userService;
    private AuthorServiceImpl authorService;
    private UsersLibraryServiceImpl usersLibraryService;

    private JwtTokenUtil jwtTokenUtil;
    private BookController bookController;

    @BeforeEach
    void setUp() {
        //mocks for classes doing db interaction
        this.bookRepository = Mockito.mock(BookRepository.class);
        this.userRepository = Mockito.mock(UserRepository.class);
        this.usersLibraryRepository = Mockito.mock(UsersLibraryRepository.class);
        this.authorRepository = Mockito.mock(AuthorRepository.class);

        //setting-up BookController's dependencies
        this.jwtTokenUtil = new JwtTokenUtil();
        this.userService = new UserServiceImpl();
        this.authorService = new AuthorServiceImpl();
        authorService.setAuthorRepository(authorRepository);
        userService.setUserRepository(userRepository);
        this.bookService = new BookServiceImpl(bookRepository,authorService, userService);
        this.usersLibraryService = new UsersLibraryServiceImpl(usersLibraryRepository, userService, null, bookService);






        this.bookController = new BookController(bookService,userService, usersLibraryService, jwtTokenUtil);


    }

    @Test
    void booksList() {
        HttpServletRequest httpServletRequest = new MockHttpServletRequest();
        UserDetails userDetails = userService.loadUserByUsername("name");
        bookController.booksList(httpServletRequest);

    }

    private void mockDbData(){

        User user = new User("name", "name", "name", List.of(new Role("user")));
        Mockito.when(userRepository.findByName(Mockito.anyString())).thenReturn(user);

        UsersLibrary usersLibrary = new UsersLibrary(user);

        Mockito.when(usersLibraryRepository.findById(user.getId())).thenReturn(Optional.of(usersLibraryService.toDbEntity(usersLibrary)));

    }

}