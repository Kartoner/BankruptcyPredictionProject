package jo.BankruptcyPredictionProject.Domain.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jo.BankruptcyPredictionProject.Domain.Entity.Formula;

@Repository
public interface FormulaRepository extends JpaRepository<Formula, Long> {
    
}