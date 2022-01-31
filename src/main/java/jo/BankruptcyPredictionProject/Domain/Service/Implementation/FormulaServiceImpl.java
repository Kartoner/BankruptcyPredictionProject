package jo.BankruptcyPredictionProject.Domain.Service.Implementation;

import jo.BankruptcyPredictionProject.Domain.Entity.AttributeScope;
import jo.BankruptcyPredictionProject.Domain.Entity.Clause;
import jo.BankruptcyPredictionProject.Domain.Entity.Formula;
import jo.BankruptcyPredictionProject.Domain.Entity.Literal;
import jo.BankruptcyPredictionProject.Domain.Enumeration.ClauseType;
import jo.BankruptcyPredictionProject.Domain.Enumeration.FormulaType;
import jo.BankruptcyPredictionProject.Domain.Parser.ArffParser;
import jo.BankruptcyPredictionProject.Domain.Repository.ClauseRepository;
import jo.BankruptcyPredictionProject.Domain.Repository.FormulaRepository;
import jo.BankruptcyPredictionProject.Domain.Repository.LiteralRepository;
import jo.BankruptcyPredictionProject.Domain.Service.AttributeScopeService;
import jo.BankruptcyPredictionProject.Domain.Service.FormulaService;
import jo.BankruptcyPredictionProject.Utility.BPPLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FormulaServiceImpl implements FormulaService {
    
    @Autowired
    private FormulaRepository formulaRepository;

    @Autowired
    private ClauseRepository clauseRepository;

    @Autowired
    private LiteralRepository literalRepository;

    @Autowired
    private AttributeScopeService attributeScopeService;

    @Autowired
    private ArffParser arffParser;

    private List<Formula> formulas = new ArrayList<>();

    private List<Literal> literals = new ArrayList<>();

    private List<Clause> clauses = new ArrayList<>();

    private void prepareListsForLoading() {
        this.formulas = this.formulaRepository.findAll();
        this.literals = this.literalRepository.findAll();
        this.clauses = this.clauseRepository.findAll();
    }

    private void clear() {
        this.formulas = new ArrayList<>();
        this.literals = new ArrayList<>();
        this.clauses = new ArrayList<>();
    }

    @Override
    @Transactional
    public List<Formula> getFormulasByType(FormulaType type) {
        return this.formulaRepository.findByFormulaType(type);
    }

    @Override
    @Transactional
    public boolean formulaExists(Formula formula) {
        for (Formula existingFormula : this.getFormulasByType(formula.getFormulaType())) {
            if (formula.equals(existingFormula)) {
                return true;
            }
        }

        return false;
    }

    private boolean formulaExistsForLoading(Formula formula) {
        for (Formula existingFormula : this.formulas.stream().filter(f -> f.getFormulaType().equals(formula.getFormulaType())).collect(Collectors.toList())) {
            if (formula.equals(existingFormula)) {
                return true;
            }
        }

        return false;
    }

    @Override
    @Transactional
    public Formula createFormula(Formula formula) {
        if (!this.formulaExists(formula)) {
            return this.formulaRepository.save(formula);
        } else {
            return null;
        }
    }

    @Override
    @Transactional
    public Clause createClause(Clause clause) {
        if (!this.clauseExists(clause)) {
            return this.clauseRepository.save(clause);
        } else {
            return null;
        }
    }

    private boolean clauseExists(Clause clause) {
        for (Clause existingClause : this.getClausesByType(clause.getClauseType())) {
            if (clause.equals(existingClause)) {
                return true;
            }
        }

        return false;
    }

    @Override
    @Transactional
    public List<Clause> getClausesByType(ClauseType type) {
        return this.clauseRepository.findByClauseType(type);
    }

    @Override
    @Transactional
    public List<Literal> getLiterals() {
        return this.literalRepository.findAll();
    }

    @Override
    @Transactional
    public Literal getLiteralByExtDescription(String extDescription) {
        return this.literalRepository.findByExtDescription(extDescription).stream().findFirst().orElse(null);
    }

    @Override
    @Transactional
    public Literal createLiteral(Literal literal) {
        if (!this.literalExists(literal)) {
            return this.literalRepository.save(literal);
        } else {
            return null;
        }
    }

    private boolean literalExists(Literal literal) {
        for (Literal existingLiteral : this.literalRepository.findAll()) {
            if (literal.equals(existingLiteral)) {
                return true;
            }
        }

        return false;
    }

    @Override
    @Transactional
    public int loadFormulasBatch(String formulasFilePath, FormulaType type) {
        List<Formula> formulas = this.loadFormulasFromFile(formulasFilePath, type);
        this.formulaRepository.saveAll(formulas);
        return formulas.size();
    }

    @Override
    @Transactional
    public List<Formula> loadFormulasFromFile(String formulasFilePath, FormulaType type) {
        int loadedFormulasCount = 0;
        List<Formula> loadedFormulas = new ArrayList<>();

        prepareListsForLoading();
        BPPLogger.log("Data loaded");

        try (BufferedReader br = new BufferedReader(new FileReader(formulasFilePath))) {
            String line = "";
            boolean assignToNewFormula = true;
            Formula currentFormula = new Formula();

            while (line != null) {
                line = br.readLine();

                if (line != null) {
                    if (line.equals("---")) {
                        assignToNewFormula = true;
                        currentFormula.setFormulaType(type);
                        if (currentFormula.getWeight() == null) {
                            currentFormula.setWeight(0.75);
                        }

                        if (!this.formulaExistsForLoading(currentFormula)) {
                            loadedFormulas.add(currentFormula);
                            this.formulas.add(currentFormula);
                            loadedFormulasCount++;
                            BPPLogger.log("Formula no. " + loadedFormulasCount + " processed");
                        }
                    } else {
                        if (assignToNewFormula) {
                            currentFormula = new Formula();
                            assignToNewFormula = false;
                        }

                        String[] lineSplit = prepareString(line).split(" ");

                        if (lineSplit[0].equals("W:")) {
                            currentFormula.setWeight(Double.parseDouble(lineSplit[1]));
                        } else {
                            BPPLogger.log("Processing line: " + line);
                            currentFormula.attach(createClauseFromLine(lineSplit));
                            BPPLogger.log("Line processed");
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            BPPLogger.log("File: " + formulasFilePath + " not found!");
            e.printStackTrace();
        } catch (IOException e) {
            BPPLogger.log("Reading from file: " + formulasFilePath + " failed!");
            e.printStackTrace();
        }

        BPPLogger.log("Done reading from file: " + formulasFilePath + ". Loaded formulas: " + loadedFormulasCount);
        clear();
        return loadedFormulas;
    }

    private Clause createClauseFromLine(String[] lineSplit){
        Clause clause = new Clause();

        for (int i = 0; i < lineSplit.length; i++){
                Literal literal = createLiteralFromString(lineSplit[i]);
                clause.attach(literal);
        }

        clause.setClauseType(ClauseType.STANDARD);
        clause.setExtDescription(clause.toExtString());

        return this.replaceWithExistingIfClauseExists(clause);
    }

    private Clause replaceWithExistingIfClauseExists(Clause clause) {
        Optional<Clause> existingClause = this.clauses.stream()
                .filter(c -> c.getExtDescription().equals(clause.getExtDescription()))
                .findFirst();
        if (existingClause.isPresent()) {
            return existingClause.get();
        }

        this.clauses.add(clause);
        return clause;
    }

    private Literal createLiteralFromString(String literalString){
        Literal literal = new Literal();

        String description;
        boolean isNegative;

        if (literalString.charAt(0) == '~'){
            description = literalString.substring(1);
            isNegative = true;
        } else {
            description = literalString;
            isNegative = false;
        }

        AttributeScope scope = this.attributeScopeService.getScopeByDescription(description);

        literal.setDescription(description);
        literal.setNegative(isNegative);
        literal.setAttributeScope(scope);
        literal.setExtDescription(literal.toExtString());

        return this.replaceWithExistingIfLiteralExists(literal);
    }

    private Literal replaceWithExistingIfLiteralExists(Literal literal) {
        Optional<Literal> existingLiteral = this.literals.stream()
                .filter(l -> l.getExtDescription().equals(literal.getExtDescription()))
                .findFirst();
        if (existingLiteral.isPresent()) {
            return existingLiteral.get();
        }

        this.literals.add(literal);
        return literal;
    }

    private String prepareString(String s) {
        return s.trim();
    }

    @Override
    @Transactional
    public int loadFormulasFromArffFile(String filePath, Integer start, Integer end) {
        this.arffParser.setDataSource(filePath);
        return this.arffParser.processAllRecords(start, end);
    }
}