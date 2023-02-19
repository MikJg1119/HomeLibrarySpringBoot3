package home.library.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class UsersLibrary {


    private User user;


    private Map<Book, String> booksAndLocation = new HashMap<>();


    private List<Loanee> loanees = new ArrayList<>();


    private Map<Book,Loanee> booksLoanedToLoanees =new HashMap<>();


    public UsersLibrary() {
//        this.books=new ArrayList<Book>();
//        this.loanees=new ArrayList<Loanee>();
    }

    public UsersLibrary(User user) {
        this.user = user;
//        this.books=new ArrayList<Book>();
//        this.loanees=new ArrayList<Loanee>();
    }

    public Loanee checkIfBookIsLoaned(Book book){
//        for (Loanee loanee : loanees){
//            if (loanee.getLoanedBooks().contains(book)){
//                return loanee;
//            }
//        }
        return booksLoanedToLoanees.get(book);
    }
}
