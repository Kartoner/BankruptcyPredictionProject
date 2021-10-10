package jo.BankruptcyPredictionProject.Domain.Service;

import java.util.List;

import jo.BankruptcyPredictionProject.Domain.Entity.Clause;
import jo.BankruptcyPredictionProject.Domain.Entity.Formula;
import jo.BankruptcyPredictionProject.Domain.Entity.Literal;
import jo.BankruptcyPredictionProject.Domain.Enumeration.ClauseType;
import jo.BankruptcyPredictionProject.Domain.Enumeration.FormulaType;

public interface FormulaService {

    List<Formula> getFormulasByType(FormulaType type);
    
    boolean formulaExists(Formula formula);

    Formula createFormula(Formula formula);

    Clause createClause(Clause clause);

    List<Clause> getClausesByType(ClauseType type);

    Literal createLiteral(Literal literal);

    int loadFormulasFromFile(String formulasFilePath, FormulaType type);

    int loadFormulasFromArffFile(String filePath, Integer start, Integer end);
}