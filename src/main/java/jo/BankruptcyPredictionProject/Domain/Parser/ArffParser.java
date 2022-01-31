package jo.BankruptcyPredictionProject.Domain.Parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jo.BankruptcyPredictionProject.Domain.Entity.AttributeScope;
import jo.BankruptcyPredictionProject.Domain.Entity.Clause;
import jo.BankruptcyPredictionProject.Domain.Entity.Formula;
import jo.BankruptcyPredictionProject.Domain.Entity.Literal;
import jo.BankruptcyPredictionProject.Domain.Enumeration.ClauseType;
import jo.BankruptcyPredictionProject.Domain.Enumeration.FormulaType;
import jo.BankruptcyPredictionProject.Domain.Repository.ClauseRepository;
import jo.BankruptcyPredictionProject.Domain.Repository.FormulaRepository;
import jo.BankruptcyPredictionProject.Domain.Repository.LiteralRepository;
import jo.BankruptcyPredictionProject.Domain.Service.AttributeScopeService;
import jo.BankruptcyPredictionProject.Utility.ArffLoader;
import jo.BankruptcyPredictionProject.Utility.BPPLogger;
import weka.core.Instance;
import weka.core.Instances;

@Component
public class ArffParser {

    @Autowired
    private ArffLoader arffLoader;

    @Autowired
    private FormulaRepository formulaRepository;

    @Autowired
    private ClauseRepository clauseRepository;

    @Autowired
    private LiteralRepository literalRepository;

    @Autowired
    private AttributeScopeService attributeScopeService;

    private List<Formula> parsedFormulas = new ArrayList<>();

    private List<Literal> literals = new ArrayList<>();

    private List<Clause> clauses = new ArrayList<>();

    private void clear() {
        this.parsedFormulas = new ArrayList<>();
        this.literals = new ArrayList<>();
        this.clauses = new ArrayList<>();
    }

    public Boolean processRecord(Instance record) {
        Formula newFormula = new Formula();
        Double recordClass = null;
        if (record.classAttribute() != null) {
            recordClass = record.value(record.classIndex());
        }
        for (int i = 0; i < record.numAttributes(); i++) {
            String attrName = record.attribute(i).name();
            if (this.attributeScopeService.isScopeForAttribute(attrName)) {
                Double value = record.value(i);
                List<AttributeScope> scopes = this.attributeScopeService.getAllApplicableScopes(attrName, value);
                if (!scopes.isEmpty()) {
                    for (AttributeScope scope : scopes) {
                        Literal literal = new Literal();
                        Clause clause = new Clause();
                        clause.setClauseType(ClauseType.STANDARD);

                        literal.setAttributeScope(scope);
                        literal.setDescription(scope.toString());
                        literal.setNegative(false);
                        literal.setExtDescription(literal.toExtString());

                        Optional<Literal> existingLiteral = this.literals.stream()
                                .filter(l -> l.getExtDescription().equals(literal.getExtDescription()))
                                .findFirst();

                        if (existingLiteral.isPresent()) {
                            clause.attach(existingLiteral.get());
                        } else {
                            this.literals.add(literal);
                            clause.attach(literal);
                        }
                        clause.setExtDescription(clause.toExtString());

                        Optional<Clause> existingClause = this.clauses.stream()
                                .filter(c -> c.getExtDescription().equals(clause.getExtDescription()))
                                .findFirst();

                        if (existingClause.isPresent()) {
                            newFormula.attach(existingClause.get());
                        } else {
                            this.clauses.add(clause);
                            newFormula.attach(clause);
                        }
                    }
                }
            }
        }

        if (!newFormula.getClauses().isEmpty()) {
            this.parsedFormulas.add(newFormula);
        }

        if (recordClass == null) {
            return null;
        } else if (recordClass == 1.0) {
            return true;
        } else {
            return false;
        }
    }

    public int processAllRecords(Integer start, Integer end) {
        clear();
        this.literals = this.literalRepository.findAll();
        this.clauses = this.clauseRepository.findByClauseType(ClauseType.STANDARD);
        this.arffLoader.loadData(true);
        Instances records = this.arffLoader.getData();
        Boolean processingResult = null;
        int count = 0;

        int startingIndex = start == null || start < 0 ? 0 : start;
        int endingIndex = end == null || end > records.numInstances() ? records.numInstances() : end;
        if (startingIndex > endingIndex) {
            startingIndex = 0;
        }
        int remaining = endingIndex - startingIndex;

        for (int i = startingIndex; i < endingIndex; i++) {
            Instance record = records.get(i);
            processingResult = processRecord(record);

                boolean result = false;

                if (processingResult != null) {
                    Formula newFormula = this.parsedFormulas.get(this.parsedFormulas.size() - 1);
                    newFormula.setFormulaType(processingResult ? FormulaType.BANKRUPT : FormulaType.NOT_BANKRUPT);

                    Formula createdFormula = this.formulaRepository.save(newFormula);
                    result = (createdFormula != null);
                }

                if (result) {
                    remaining--;
                    count++;
                    BPPLogger.log("Formula processed, " + remaining + " remaining");
                }
            
        }
        
        BPPLogger.log("Number of written formulas: " + count);
        return count;               
    }

    public void setDataSource(String filePath) {
        this.arffLoader.setFilePath(filePath);
    }
}