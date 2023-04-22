package home.library.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import home.library.model.*;
import home.library.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UsersLibraryServiceImpl implements UsersLibraryService {

    private UsersLibraryRepository usersLibraryRepository;

    private UserService userService;

    private LoaneeService loaneeService;

    private BookService bookService;

    private ObjectMapper objectMapper = new ObjectMapper();

    public UsersLibraryServiceImpl(UsersLibraryRepository usersLibraryRepository, UserService userService, LoaneeService loaneeService, BookService bookService) {
        this.usersLibraryRepository = usersLibraryRepository;
        this.userService = userService;
        this.loaneeService = loaneeService;
        this.bookService = bookService;
    }

    @Autowired
    public void setUsersLibraryRepository(UsersLibraryRepository usersLibraryRepository) {
        this.usersLibraryRepository = usersLibraryRepository;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setLoaneeService(LoaneeService loaneeService) {
        this.loaneeService = loaneeService;
    }

    @Autowired
    public void setBookService(BookService bookService) {
        this.bookService = bookService;
    }

    @Override
    public UsersLibrary getUsersLibraryByUser(User user) {
        Optional<UsersLibraryDbEntity> dbRecord = usersLibraryRepository.findById(user.getId());
        return dbRecord.map(this::toFinalEntity).orElse(null);
    }

    @Override
    public void save(UsersLibrary usersLibrary) {
        UsersLibraryDbEntity usersLibraryDbEntity = toDbEntity(usersLibrary);
        usersLibraryRepository.save(usersLibraryDbEntity);
    }

    @Override
    public void setBooks(User user, Map<Book, String> books) {
        Optional<UsersLibraryDbEntity> userDb = usersLibraryRepository.findById(user.getId());
        if (userDb.isPresent()){
            UsersLibraryDbEntity usersLibraryDbEntity = userDb.get();
            Map<Integer, String> bookIds = null;
            try {
                bookIds = objectMapper.readValue(userDb.get().getPersonalBookshelfIdsAndLocationJson(), Map.class);
                for(Book book : books.keySet()){
                    if (!bookIds.keySet().contains(book.getId())){
                        bookIds.put(book.getId(), books.get(book));
                    }
                }
                usersLibraryDbEntity.setPersonalBookshelfIdsAndLocationJson(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(bookIds));
                usersLibraryRepository.save(usersLibraryDbEntity);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

        }

    }

    @Override
    public void setLoanees(User user, List<Loanee> loanees) {
        Optional<UsersLibraryDbEntity> userDb = usersLibraryRepository.findById(user.getId());
        if (userDb.isPresent()){
            UsersLibraryDbEntity usersLibraryDbEntity = userDb.get();
            List<Integer> loaneeIds = null;
            try {
                loaneeIds = objectMapper.readValue(userDb.get().getPersonalLoaneesIdsJson(), List.class);
                for(Loanee loanee : loanees){
                    if (!loaneeIds.contains(loanee.getId())){
                        loaneeIds.add(loanee.getId());
                    }
                }
                usersLibraryDbEntity.setPersonalLoaneesIdsJson(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(loaneeIds));
                usersLibraryRepository.save(usersLibraryDbEntity);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public Map<Book, String> getBooksByUser(User user) {

        Optional<UsersLibraryDbEntity> dbRecord = usersLibraryRepository.findById(user.getId());
        return dbRecord.map(usersLibraryDbEntity -> toFinalEntity(usersLibraryDbEntity).getBooksAndLocation()).orElse(null);
    }

    @Override
    public List<Loanee> getLoaneesByUser(User user) {
        Optional<UsersLibraryDbEntity> dbRecord = usersLibraryRepository.findById(user.getId());
        return dbRecord.map(usersLibraryDbEntity -> toFinalEntity(usersLibraryDbEntity).getLoanees()).orElse(null);
    }

    @Override
    public List<Book> getBooksLoanedToLoaneeByUser(int userId, int loaneeId) {
        Optional<UsersLibraryDbEntity> dbRecord = usersLibraryRepository.findById(userId);
        List<Book> booksLoanedToLoanee = new ArrayList<>();
        if (dbRecord.isPresent()){
            UsersLibrary usersLibrary = toFinalEntity(dbRecord.get());
            for (Book book : usersLibrary.getBooksLoanedToLoanees().keySet()){
                if ((usersLibrary.getBooksLoanedToLoanees().get(book).getId() == loaneeId)) {
                    booksLoanedToLoanee.add(book);
                }

            }
        }
        return booksLoanedToLoanee;
    }

    @Override
    public void returnBook(Book book, User user){
        UsersLibrary usersLibrary = getUsersLibraryByUser(user);
        Loanee loanee = usersLibrary.checkIfBookIsLoaned(book);
        loanee.returnLoanedBook(book);
        Map<Book, Loanee> booksLoanedToLoanees = usersLibrary.getBooksLoanedToLoanees();
        if (booksLoanedToLoanees.containsKey(book)){
            booksLoanedToLoanees.remove(book);
        }
        usersLibrary.setBooksLoanedToLoanees(booksLoanedToLoanees);
        List<Loanee> loaneesList = usersLibrary.getLoanees();
         if (!booksLoanedToLoanees.containsValue(loanee)){
            loaneesList.remove(loanee);
         }

        save(usersLibrary);

    }

    public UsersLibraryDbEntity toDbEntity(UsersLibrary usersLibrary){


        UsersLibraryDbEntity usersLibraryDbEntity = new UsersLibraryDbEntity();
        usersLibraryDbEntity.setUserId(usersLibrary.getUser().getId());
        Map<Integer, Integer> bookIdsToLoaneeIds = new HashMap<>();
        Map<Integer, String> bookshelfMapIds = new HashMap<>();
        for (Book book : usersLibrary.getBooksAndLocation().keySet()){
            bookshelfMapIds.put(book.getId() , usersLibrary.getBooksAndLocation().get(book));
            if (usersLibrary.getBooksLoanedToLoanees().containsKey(book)){
                bookIdsToLoaneeIds.put(book.getId(), usersLibrary.getBooksLoanedToLoanees().get(book).getId());
            }
        }

        List<Integer> loaneeIds = new ArrayList<>();
        for (Loanee loanee : usersLibrary.getLoanees()){
            loaneeIds.add(loanee.getId());
        }

        try {
            usersLibraryDbEntity.setPersonalBookshelfIdsAndLocationJson(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(bookshelfMapIds));
            usersLibraryDbEntity.setPersonalLoaneesIdsJson(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(loaneeIds));
            usersLibraryDbEntity.setPersonalMapBookIdToLoaneeIdJson(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(bookIdsToLoaneeIds));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return usersLibraryDbEntity;
    }

    public UsersLibrary toFinalEntity (UsersLibraryDbEntity usersLibraryDbEntity)  {
        UsersLibrary usersLibrary = new UsersLibrary();
        usersLibrary.setUser(userService.getUserById(usersLibraryDbEntity.getUserId()));
        Map<Integer, String> booksIds;
        List<Integer> loaneeIds;
        Map<Integer, Integer> booksToLoaneesIds;
        try {
            booksIds = objectMapper.readValue(usersLibraryDbEntity.getPersonalBookshelfIdsAndLocationJson(), Map.class);
            loaneeIds = objectMapper.readValue(usersLibraryDbEntity.getPersonalLoaneesIdsJson(), List.class);
            booksToLoaneesIds = objectMapper.readValue(usersLibraryDbEntity.getPersonalMapBookIdToLoaneeIdJson(), Map.class);
            Map <Book, String> bookshelf = new HashMap<>();
            List<Book> books = bookService.getBooksById(booksIds.keySet());
            for (Book book : books){
                bookshelf.put(book, booksIds.get(book.getId()));
            }
            usersLibrary.setBooksAndLocation(bookshelf);
            usersLibrary.setLoanees(loaneeService.getAllLoaneesById(loaneeIds));
            Map<Book, Loanee> booksLoanedToLoanee = new HashMap<>();
            List <Book> loanedBooks = bookService.getBooksById(booksToLoaneesIds.keySet());
            for (Book book : loanedBooks){
                Loanee loanee = loaneeService.getLoanee(booksToLoaneesIds.get(book.getId()));
                booksLoanedToLoanee.put(book, loanee);
            }
            usersLibrary.setBooksLoanedToLoanees(booksLoanedToLoanee);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return usersLibrary;

    }
}
