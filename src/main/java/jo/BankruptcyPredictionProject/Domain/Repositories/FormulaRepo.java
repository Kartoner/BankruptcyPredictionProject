package jo.BankruptcyPredictionProject.Domain.Repositories;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jo.BankruptcyPredictionProject.Utility.BPPLogger;
import jo.BankruptcyPredictionProject.Values.AttributeScope;
import jo.BankruptcyPredictionProject.Values.Clause;
import jo.BankruptcyPredictionProject.Values.Formula;
import jo.BankruptcyPredictionProject.Values.Literal;
import jo.BankruptcyPredictionProject.Values.Interface.FormulaElement;

public class FormulaRepo {
    private static FormulaRepo instance;

    private final String satFilePath = "./src/main/resources/satFormulas.txt";
    private final String unsatFilePath = "./src/main/resources/unsatFormulas.txt";
    private final String assessmentFilePath = "./src/main/resources/assessmentFormulas.txt";
    private final String randomMatchingRulesFilePath = "./src/main/resources/randomMatchingRules.txt";

    private List<Formula> satFormulas;
    private List<Formula> unsatFormulas;
    private List<Formula> assessmentFormulas;
    private List<FormulaElement> randomMatchingRules;
    private Map<String, Integer> variables;

    private int currentSymbol;

    private FormulaRepo() {
        this.satFormulas = new ArrayList<>();
        this.unsatFormulas = new ArrayList<>();
        this.assessmentFormulas = new ArrayList<>();
        this.randomMatchingRules = new ArrayList<>();
        this.variables = new HashMap<>();
        currentSymbol = 1;
    }

    public static FormulaRepo getInstance() {
        if (instance == null) {
            instance = new FormulaRepo();
        }

        return instance;
    }

    public void loadData() {
        clear();
        readFormulasFile(this.satFilePath);
        readFormulasFile(this.unsatFilePath);
        readFormulasFile(this.assessmentFilePath);
        readMatchingRulesFile();
    }

    public void refreshAssessmentFormulas(){
        this.assessmentFormulas.clear();
        readFormulasFile(this.assessmentFilePath);
    }

    public void refreshMatchingRules(){
        this.randomMatchingRules.clear();
        readMatchingRulesFile();
    }

    private void clear(){
        this.satFormulas.clear();
        this.unsatFormulas.clear();
        this.assessmentFormulas.clear();
        this.randomMatchingRules.clear();
        this.variables.clear();
    }

    public boolean writeNewFormula(Formula newFormula, Boolean isSat) {
        String filePath;

        if (isSat == null){
            filePath = this.assessmentFilePath;
            this.assessmentFormulas.add(newFormula);
        } else {
            if (isSat) {
                filePath = this.satFilePath;
                this.satFormulas.add(newFormula);
            } else {
                filePath = this.unsatFilePath;
                this.unsatFormulas.add(newFormula);
            }
        }

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true));
            bw.append(newFormula.toExtString()).append("\n").append("---").append("\n");
            bw.flush();
            bw.close();
        } catch (FileNotFoundException e) {
            BPPLogger.log("File: " + filePath + " not found!");
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            BPPLogger.log("Writing to file: " + filePath + " failed!");
            e.printStackTrace();
            return false;
        }

        BPPLogger.log("Written new formula to file: " + filePath);
        return true;
    }

    public boolean writeNewMatchingRule(FormulaElement element) {
        if (!matchingRuleExists(element)){
            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(this.randomMatchingRulesFilePath, true));
                bw.append(element.toExtString()).append("\n").append("---").append("\n");
                bw.flush();
                bw.close();
            } catch (FileNotFoundException e) {
                BPPLogger.log("File: " + this.randomMatchingRulesFilePath + " not found!");
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                BPPLogger.log("Writing to file: " + this.randomMatchingRulesFilePath + " failed!");
                e.printStackTrace();
                return false;
            }

            BPPLogger.log("Written new formula to file: " + this.randomMatchingRulesFilePath);
            return true;
        }

        return false;
    }

    public boolean matchingRuleExists(FormulaElement element) {
        for (FormulaElement formulaElement : this.randomMatchingRules){
            if (formulaElement.toExtString().equals(element.toExtString())){
                return true;
            }
        }

        return false;
    }

    public boolean formulaExists(Formula formula, Boolean isSat) {
        for (Formula existingFormula : this.getFormulas(isSat)) {
            if (formula.equals(existingFormula)) {
                return true;
            }
        }

        return false;
    }

    public List<Formula> getFormulas(Boolean isSat) {
        if (isSat == null){
            return this.assessmentFormulas;
        } else {
            if (isSat) {
                return this.satFormulas;
            } else {
                return this.unsatFormulas;
            }
        }
    }

    public List<FormulaElement> getMatchingRules(){
        return this.randomMatchingRules;
    }

    public Integer getLiteralSymbolIfExists(String literalDescription){
        if (this.variables.containsKey(literalDescription)){
            return this.variables.get(literalDescription);
        }

        return null;
    }

    public boolean addNewVariable(String description, Integer symbol){
        if (!this.variables.containsKey(description)){
            this.variables.put(description, symbol);
            return true;
        } else {
            return false;
        }
    }

    public int getCurrentSymbol(){
        return this.currentSymbol;
    }

    public void incrementCurrentSymbol(){
        this.currentSymbol++;
    }

    private void readFormulasFile(String formulasFilePath) {
        int loadedFormulas = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(formulasFilePath))) {
            String line = "";
            boolean assignToNewFormula = true;
            Formula currentFormula = new Formula();

            while (line != null) {
                line = br.readLine();

                if (line != null) {
                    if (line.equals("---")) {
                        assignToNewFormula = true;

                        if (formulasFilePath.equals(this.satFilePath)) {
                            this.satFormulas.add(currentFormula);
                        } else {
                            this.unsatFormulas.add(currentFormula);
                        }

                        loadedFormulas++;
                    } else {
                        if (assignToNewFormula) {
                            currentFormula = new Formula();
                            assignToNewFormula = false;
                        }

                        currentFormula.attach(processLine(prepareString(line)));
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

        BPPLogger.log("Done reading from file: " + formulasFilePath + ". Loaded formulas: " + loadedFormulas);
    }

    private void readMatchingRulesFile() {
        int loadedRules = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(this.randomMatchingRulesFilePath))) {
            String line = "";

            while (line != null) {
                line = br.readLine();

                if (line != null) {
                    if (line.equals("---")) {
                        continue;
                    } else {
                        this.randomMatchingRules.add(processLine(prepareString(line)));

                        loadedRules++;
                    }
                }
            }
        } catch (FileNotFoundException e) {
            BPPLogger.log("File: " + this.randomMatchingRulesFilePath + " not found!");
            e.printStackTrace();
        } catch (IOException e) {
            BPPLogger.log("Reading from file: " + this.randomMatchingRulesFilePath + " failed!");
            e.printStackTrace();
        }

        BPPLogger.log("Done reading from file: " + this.randomMatchingRulesFilePath + ". Loaded rules: " + loadedRules);
    }

    private FormulaElement processLine(String line){
        String[] lineSplit = line.split(" ");
        FormulaElement element;

        if (lineSplit.length == 1){
            element = createLiteralFromString(lineSplit[0]);
        } else {
            Clause clause = new Clause();

            for (int i = 0; i < lineSplit.length; i++){
                Literal literal = createLiteralFromString(lineSplit[i]);
                clause.attach(literal);
            }

            element = clause;
        }

        return element;
    }

    private Literal createLiteralFromString(String literalString){
        boolean additionSuccessful;

        int symbol;
        String description;
        boolean isNegative;

        if (literalString.charAt(0) == '~'){
            description = literalString.substring(1);
            isNegative = true;
        } else {
            description = literalString;
            isNegative = false;
        }

        additionSuccessful = addNewVariable(description, this.currentSymbol);

        if (additionSuccessful){
            symbol = currentSymbol;
            incrementCurrentSymbol();
        } else {
            symbol = getLiteralSymbolIfExists(description);
        }

        AttributeScope scope = AttributeScopeRepo.getInstance().getScopeByDescription(description);

        return new Literal(symbol, description, isNegative, scope);
    }

    private String prepareString(String s) {
        return s.trim();
    }

    public Map<String, Integer> getVariables(){
        return this.variables;
    }
}