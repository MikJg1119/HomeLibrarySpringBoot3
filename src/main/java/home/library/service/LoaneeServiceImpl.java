package home.library.service;

import home.library.model.Book;
import home.library.model.Loanee;
import home.library.model.User;
import home.library.model.UsersLibrary;
import home.library.repository.LoaneeRepository;
import home.library.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.spec.InvalidParameterSpecException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LoaneeServiceImpl implements LoaneeService{


    @Autowired
    private LoaneeRepository loaneeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UsersLibraryService usersLibraryService;

    @Autowired
    private BookService bookService;


    @Override
    public void addLoanee(Loanee loanee) {
        loaneeRepository.save(loanee);
    }

    @Override
    public void removeLoanee(int id) {
        loaneeRepository.delete(loaneeRepository.getById(id));
    }

    @Override
    public Loanee getLoanee(int id) {
        return loaneeRepository.getById(id);
    }

    @Override
    public List<Loanee> getLoanees() {
        return loaneeRepository.findAll();
    }

    @Override
    public void loanBook(Book book, int loaneeId) {
        loaneeRepository.getById(loaneeId).addLoanedBook(book);
    }

    @Override
    public void loanBook(UsersLibrary usersLibrary, User user, int [] booksToBeLoanedIds, int loaneeId) throws InvalidParameterSpecException {
        List<Loanee> usersLoanees = usersLibrary.getLoanees();
        Map<Book, Loanee> booksLoanedOut = usersLibrary.getBooksLoanedToLoanees();
        Loanee loanee = getLoanee(loaneeId);

        List<Book> booksToBeLoaned = bookService.getBooksById(Arrays.stream(booksToBeLoanedIds).boxed().collect(Collectors.toList()));
        for (Book book : booksLoanedOut.keySet()){
            if (booksToBeLoaned.contains(book)){
                throw new InvalidParameterSpecException(book.getId()+" is already loaned out");
            }
        }
        for (Book book : booksToBeLoaned){
            booksLoanedOut.put(book, loanee);
        }

        if(!usersLoanees.contains(loanee)){
            usersLoanees.add(loanee);
        }
        loanee.addLoanedBook(booksToBeLoaned);
        usersLibrary.setBooksLoanedToLoanees(booksLoanedOut);
        loaneeRepository.save(loanee);
        usersLibraryService.save(usersLibrary);
    }

    @Override
    public void returnLoanedBook(Book book, int loaneeId) {
        loaneeRepository.getById(loaneeId).returnLoanedBook(book);
    }

    @Override
    public void returnLoanedBook(List<Book> booksToBeReturned, int loaneeId) {
        Loanee loanee = loaneeRepository.getById(loaneeId);
        loanee.setLoanedBooks(loanee.returnLoanedBook(booksToBeReturned));
        loaneeRepository.save(loanee);
//        loaneeRepository.getById(loaneeId).returnLoanedBook(booksToBeReturned);
    }

    @Override
    public List<Loanee> getAllLoaneesById(Iterable<Integer> ids) {
        return loaneeRepository.findAllById(ids);
    }

}
