package home.library.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.springframework.beans.factory.annotation.Autowired;


@Getter
@Setter
@Entity
@Table(name = "books")
public class Book  {

    @Column(name = "title")
    private String title;
    @Column(name = "author")
    private String author;
    @Column(name = "_isbn")
    private String isbn;
    @Column(name = "publisher")
    private String publisher;
    @Column(name = "publishedyear")
    private String publishedYear;
    @Column(name = "language")
    private String language;
    @Id
    @Column(columnDefinition = "serial")
    @Generated(GenerationTime.INSERT)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
//    @ManyToOne
//    @JoinColumn(name = "authorid", insertable = false, updatable = false)
//    private Author authorEntity;  //entity needed to get authorid out of authors table
//    @Column(name ="authorid")
//    private int authorId; //=authorEntity.getId();

    @Column(name = "saga")
    private String saga;
    @Column(name = "publishingseries")
    private String publishingSeries;

    @Column(name = "cover", nullable = true)
    @Lob
    private byte[] cover;


    @Autowired
    public Book() {

    }


    @Override
    public boolean equals(Object obj) {
        return this.isbn.equals(((Book) obj).getIsbn());
    }




}
