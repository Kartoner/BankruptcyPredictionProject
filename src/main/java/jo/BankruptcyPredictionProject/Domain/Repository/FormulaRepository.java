package jo.BankruptcyPredictionProject.Domain.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jo.BankruptcyPredictionProject.Domain.Entity.Formula;
import jo.BankruptcyPredictionProject.Domain.Enumeration.FormulaType;

@Repository
public interface FormulaRepository extends JpaRepository<Formula, Long> {

    List<Formula> findByFormulaType(FormulaType type);
}