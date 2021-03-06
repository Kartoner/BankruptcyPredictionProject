package jo.BankruptcyPredictionProject.Domain.Parsers;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jo.BankruptcyPredictionProject.Domain.Repositories.ArffRepo;
import jo.BankruptcyPredictionProject.Domain.Repositories.AttributeScopeRepo;
import jo.BankruptcyPredictionProject.Domain.Repositories.FormulaRepo;
import jo.BankruptcyPredictionProject.Utility.BPPLogger;
import jo.BankruptcyPredictionProject.Values.AttributeScope;
import jo.BankruptcyPredictionProject.Values.Formula;
import jo.BankruptcyPredictionProject.Values.Literal;
import weka.core.Instance;
import weka.core.Instances;

public class ArffParser {
    private static ArffParser instance;

    private final String outputFilePath = "./src/main/resources/outputFormula.txt";

    private final ArffRepo arffRepo = ArffRepo.getInstance();
    private final AttributeScopeRepo scopeRepo = AttributeScopeRepo.getInstance();
    private final FormulaRepo formulaRepo = FormulaRepo.getInstance();

    private List<Formula> parsedFormulas;

    private ArffParser() {
        this.parsedFormulas = new ArrayList<>();
        this.scopeRepo.loadData();
    }

    public static ArffParser getInstance() {
        if (instance == null) {
            instance = new ArffParser();
        }

        return instance;
    }

    private void clear() {
        this.parsedFormulas = new ArrayList<>();
    }

    public Boolean processRecord(Instance record) {
        Formula newFormula = new Formula();
        Double recordClass = null;
        if (record.classAttribute() != null) {
            recordClass = record.value(record.classIndex());
        }
        for (int i = 0; i < record.numAttributes(); i++) {
            String attrName = record.attribute(i).name();
            if (this.scopeRepo.isScopeForAttribute(attrName)) {
                Double value = record.value(i);
                Literal newLiteral = null;
                List<AttributeScope> scopes = this.scopeRepo.getAllApplicableScopes(attrName, value);
                if (!scopes.isEmpty()) {
                    for (AttributeScope scope : scopes) {
                        String description = scope.toString();
                        Integer existingVariableSymbol = this.formulaRepo.getLiteralSymbolIfExists(description);

                        if (existingVariableSymbol != null) {
                            newLiteral = new Literal(existingVariableSymbol, description, false, scope);
                        } else {
                            boolean additionSuccessful = this.formulaRepo.addNewVariable(description,
                                    this.formulaRepo.getCurrentSymbol());

                            if (additionSuccessful) {
                                newLiteral = new Literal(this.formulaRepo.getCurrentSymbol(), description, false,
                                        scope);
                                this.formulaRepo.incrementCurrentSymbol();
                            }
                        }

                        if (newLiteral != null) {
                            newFormula.attach(newLiteral);
                        }
                    }
                }
            }
        }

        if (!newFormula.getElements().isEmpty()) {
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

    public void processAllRecords(boolean flushToRepo) {
        clear();
        Instances records = this.arffRepo.getData();
        Boolean processingResult = null;
        int count = 0;

        for (int i = 0; i < records.numInstances(); i++) {
            Instance record = records.get(i);
            processingResult = processRecord(record);

            if (flushToRepo) {
                boolean result = false;

                if (processingResult != null) {
                    result = this.formulaRepo.writeNewFormula(this.parsedFormulas.get(this.parsedFormulas.size() - 1),
                            processingResult);
                }

                if (result) {
                    count++;
                }
            }
        }

        if (flushToRepo) {
            BPPLogger.log("Number of written formulas: " + count);
            this.formulaRepo.loadData();
        }
    }

    public void writeToOutput(Formula formula) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(this.outputFilePath, false));
            bw.append("p cnf " + formula.getUniqueVariablesCount() + " " + formula.getFormulaSize()).append("\n")
                    .append(formula.toString());
            bw.flush();
            bw.close();
        } catch (FileNotFoundException e) {
            BPPLogger.log("File: " + this.outputFilePath + " not found!");
            e.printStackTrace();
        } catch (IOException e) {
            BPPLogger.log("Writing to file: " + this.outputFilePath + " failed!");
            e.printStackTrace();
        }

        BPPLogger.log("Written new formula to file: " + this.outputFilePath);
    }

    public List<Formula> getParsedFormulas() {
        return this.parsedFormulas;
    }
}