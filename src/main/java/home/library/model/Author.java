package home.library.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "authors")
public class Author{

    @Column(name = "name")
    private String name;

    @Id
    @Column(columnDefinition = "serial")
    @Generated(GenerationTime.INSERT)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval=true, fetch=FetchType.LAZY)
//    @JoinColumn(name="book_id", referencedColumnName = "id")
    private List<Book> booksByAuthor;

    @Autowired
    public Author() {
//        this.booksByAuthor=new ArrayList<Book>();
    }

    public Author(String author) {
        this.name = author;
        this.booksByAuthor=new ArrayList<Book>();
    }


    @Override
    public String toString() {
        return this.name;
    }
}
