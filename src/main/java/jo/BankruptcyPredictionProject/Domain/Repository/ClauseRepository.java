package jo.BankruptcyPredictionProject.Domain.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jo.BankruptcyPredictionProject.Domain.Entity.Clause;
import jo.BankruptcyPredictionProject.Domain.Enumeration.ClauseType;

@Repository
public interface ClauseRepository extends JpaRepository<Clause, Long> {
    
    List<Clause> findByClauseType(ClauseType type);
}