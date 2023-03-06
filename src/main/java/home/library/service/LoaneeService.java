package home.library.service;


import home.library.model.Book;
import home.library.model.Loanee;
import home.library.model.User;
import home.library.model.UsersLibrary;

import java.security.spec.InvalidParameterSpecException;
import java.util.List;

public interface LoaneeService {
    void addLoanee(Loanee loanee);

    void removeLoanee(int id);

    Loanee getLoanee(int id);

    List<Loanee> getLoanees();

    void loanBook(Book book, int loaneeId);

    UsersLibrary loanBook(UsersLibrary usersLibrary, User user, int [] booksToBeLoaned, int loaneeId) throws InvalidParameterSpecException;

    void returnLoanedBook(Book book, int loaneeId);

    void returnLoanedBook(List<Book> booksToBeReturned, int loaneeId);

    List<Loanee> getAllLoaneesById(Iterable<Integer> ids);
}
