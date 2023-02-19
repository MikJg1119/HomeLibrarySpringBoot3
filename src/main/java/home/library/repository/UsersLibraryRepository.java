package home.library.repository;

import home.library.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersLibraryRepository extends JpaRepository<UsersLibraryDbEntity,Integer> {

}
