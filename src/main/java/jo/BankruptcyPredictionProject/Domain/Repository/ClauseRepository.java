package jo.BankruptcyPredictionProject.Domain.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jo.BankruptcyPredictionProject.Domain.Entity.Clause;

@Repository
public interface ClauseRepository extends JpaRepository<Clause, Long> {
    
}