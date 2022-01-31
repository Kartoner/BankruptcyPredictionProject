package jo.BankruptcyPredictionProject.Domain.Repository;

import jo.BankruptcyPredictionProject.Domain.Entity.Literal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LiteralRepository extends JpaRepository<Literal, Long> {
    
    List<Literal> findByDescription(String description);

    List<Literal> findByExtDescription(String extDescription);
}