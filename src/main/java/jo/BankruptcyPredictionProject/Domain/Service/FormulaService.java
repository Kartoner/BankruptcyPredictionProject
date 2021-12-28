package jo.BankruptcyPredictionProject.Domain.Service;

import jo.BankruptcyPredictionProject.Domain.Entity.Clause;
import jo.BankruptcyPredictionProject.Domain.Entity.Formula;
import jo.BankruptcyPredictionProject.Domain.Entity.Literal;
import jo.BankruptcyPredictionProject.Domain.Enumeration.ClauseType;
import jo.BankruptcyPredictionProject.Domain.Enumeration.FormulaType;

import java.util.List;

public interface FormulaService {

    List<Formula> getFormulasByType(FormulaType type);
    
    boolean formulaExists(Formula formula);

    Formula createFormula(Formula formula);

    Clause createClause(Clause clause);

    List<Clause> getClausesByType(ClauseType type);

    Literal getLiteralByExtDescription(String extDescription);

    Literal createLiteral(Literal literal);

    int loadFormulasBatch(String formulasFilePath, FormulaType type);

    List<Formula> loadFormulasFromFile(String formulasFilePath, FormulaType type);

    int loadFormulasFromArffFile(String filePath, Integer start, Integer end);
}