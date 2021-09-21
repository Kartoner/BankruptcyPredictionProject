package jo.BankruptcyPredictionProject.Domain.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jo.BankruptcyPredictionProject.Domain.Entity.Literal;

@Repository
public interface LiteralRepository extends JpaRepository<Literal, Long> {
    
}