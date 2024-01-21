package home.library.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Loanee {

    @Id
    @Column(columnDefinition = "serial")
    @Generated(GenerationTime.INSERT)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "loanees_books",
            joinColumns = @JoinColumn(
                    name = "loanee_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "book_id", referencedColumnName = "id"))
    private List<Book> loanedBooks;

    public Loanee(String name) {
        this.name = name;
        loanedBooks = new ArrayList<Book>();
    }

    public void addLoanedBook(Book book){
        loanedBooks.add(book);
    }

    public void addLoanedBook(List<Book> booksToBeLoaned){
        loanedBooks.addAll(booksToBeLoaned);
    }

    public void returnLoanedBook(Book book){
        loanedBooks.remove(book);
    }
    public List<Book> returnLoanedBook(List<Book> booksToBeReturned){

        loanedBooks.removeAll(booksToBeReturned);
        return loanedBooks;

    }
}
