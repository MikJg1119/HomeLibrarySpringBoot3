package home.library.repository;


import home.library.model.Loanee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoaneeRepository extends JpaRepository<Loanee,Integer> {


}
