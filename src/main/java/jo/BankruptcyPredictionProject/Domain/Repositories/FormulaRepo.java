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

import jo.BankruptcyPredictionProject.Values.Clause;
import jo.BankruptcyPredictionProject.Values.Formula;
import jo.BankruptcyPredictionProject.Values.Literal;
import jo.BankruptcyPredictionProject.Values.Interface.FormulaElement;

public class FormulaRepo {
    private static FormulaRepo instance;

    private final String satFilePath = "./src/main/resources/satFormulas.txt";
    private final String unsatFilePath = "./src/main/resources/unsatFormulas.txt";

    private List<Formula> satFormulas;
    private List<Formula> unsatFormulas;
    private Map<String, Integer> variables;

    private int currentSymbol;

    private FormulaRepo() {
        this.satFormulas = new ArrayList<>();
        this.unsatFormulas = new ArrayList<>();
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
        readFormulasFile(this.satFilePath);
        readFormulasFile(this.unsatFilePath);
    }

    public void writeNewFormula(Formula newFormula, boolean isSat) {
        String filePath;

        if (isSat) {
            filePath = this.satFilePath;
            this.satFormulas.add(newFormula);
        } else {
            filePath = this.unsatFilePath;
            this.unsatFormulas.add(newFormula);
        }

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true));
            bw.append("\n").append(newFormula.toExtString()).append("\n").append("---");
            bw.flush();
            bw.close();
        } catch (FileNotFoundException e) {
            System.out.println("File: " + filePath + " not found!");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Writing to file: " + filePath + " failed!");
            e.printStackTrace();
        }

        System.out.println("Written new formula to file: " + filePath);
    }

    public boolean formulaExists(Formula formula, boolean isSat) {
        for (Formula existingFormula : getFormulas(isSat)) {
            if (formula.equals(existingFormula)) {
                return true;
            }
        }

        return false;
    }

    public List<Formula> getFormulas(boolean isSat) {
        if (isSat) {
            return this.satFormulas;
        } else {
            return this.unsatFormulas;
        }
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
            System.out.println("File: " + formulasFilePath + " not found!");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Reading from file: " + formulasFilePath + " failed!");
            e.printStackTrace();
        }

        System.out.println("Done reading from file: " + formulasFilePath + ". Loaded formulas: " + loadedFormulas);
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

        return new Literal(symbol, description, isNegative);
    }

    private String prepareString(String s) {
        return s.trim();
    }
}