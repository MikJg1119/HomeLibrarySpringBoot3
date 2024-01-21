package home.library.service;

import home.library.model.Author;
import home.library.model.Book;
import home.library.model.dto.AuthorDto;
import home.library.repository.AuthorRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthorServiceImpl implements AuthorService{

    private AuthorRepository authorRepository;


    @Autowired
    public void setAuthorRepository(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @Override
    public void addAuthor(Author author) {
    authorRepository.save(author);
    }

    @Override
    public void removeAuthor(int id) {

    authorRepository.deleteById(id);
    }

    @Override
    public Author getAuthor(int id) {
        return authorRepository.getById(id);
    }

    @Override
    public void updateAuthor(Author author) {
        Optional<Author> optionalAuthor = authorRepository.findById(author.getId());
        if (optionalAuthor.isPresent()){
            Author fromDb = optionalAuthor.get();
            fromDb.setId(author.getId());
            fromDb.setBooksByAuthor(author.getBooksByAuthor());
            fromDb.setName(author.getName());

            authorRepository.save(fromDb);
        }else {
            authorRepository.save(author);
        }
    }

    @Override
    public List<Author> getAuthors() {
        return authorRepository.findAll();
    }

    @Override
    public List<Author> getAuthorsById(Iterable<Integer> ids) {
        return authorRepository.findAllById(ids);
    }

    @Override
    public Optional<Author> getAuthorByName(String name) throws EntityNotFoundException {
        return authorRepository.findByName(name);
    }

    @Override
    public List<Author> getAuthorsByUser(Map<Book, String> usersbooks) {
        List<String> authorsNames = new ArrayList<String>();
        for (Book book : usersbooks.keySet()){
            authorsNames.add(book.getAuthor());
        }
        List<Author> authors=new ArrayList<Author>();
        for (String author :authorsNames){
            Optional <Author> search = getAuthorByName(author);
            search.ifPresent(authors::add);
        }
        return authors;
    }

    @Override
    public Author getAuthorFromDto(AuthorDto authorDto) {
        Author author = new Author();
        author.setName(authorDto.getName());
        author.setId(authorDto.getId());

        return author;
    }
}
