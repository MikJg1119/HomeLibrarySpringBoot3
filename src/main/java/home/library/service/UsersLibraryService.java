package home.library.service;


import home.library.model.*;

import java.util.List;
import java.util.Map;

public interface UsersLibraryService {

    void save(UsersLibrary usersLibrary);

    UsersLibrary getUsersLibraryByUser(User user);

    void setBooks(User user, Map<Book, String> books);

    void setLoanees(User user, List<Loanee> loanees);

    Map<Book, String> getBooksByUser(User user);

    List<Loanee> getLoaneesByUser(User user);

    List<Book> getBooksLoanedToLoaneeByUser(int userId, int loaneeId);

    void returnBook(Book book, User user);
}
