package jo.BankruptcyPredictionProject.Domain.Repositories;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FormulaRepo {
    private static FormulaRepo instance;

    private final String satFilePath = "./src/main/resources/satFormulas.txt";
    private final String unsatFilePath = "./src/main/resources/unsatFormulas.txt";

    private List<List<String>> satFormulas;
    private List<List<String>> unsatFormulas;

    private FormulaRepo() {
        this.satFormulas = new ArrayList<>();
        this.unsatFormulas = new ArrayList<>();
    }

    public static FormulaRepo getInstance() {
        if (instance == null) {
            return new FormulaRepo();
        }

        return instance;
    }

    public void loadData() {
        readFormulasFile(this.satFilePath);
        readFormulasFile(this.unsatFilePath);
    }

    public void writeNewFormula(String newFormula, boolean isSat) {
        String filePath;

        if (isSat) {
            filePath = this.satFilePath;
        } else {
            filePath = this.unsatFilePath;
        }

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true));
            bw.append("\n").append(newFormula).append("\n").append("---");
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

    public boolean formulaExists(String formula, boolean isSat) {
        for (List<String> existingFormula : getFormulas(isSat)) {
            String existingFormulaString = String.join("\n", existingFormula);
            if (formula.equals(existingFormulaString)) {
                return true;
            }
        }

        return false;
    }

    public List<List<String>> getFormulas(boolean isSat) {
        if (isSat) {
            return this.satFormulas;
        } else {
            return this.unsatFormulas;
        }
    }

    private void readFormulasFile(String formulasFilePath) {
        int loadedFormulas = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(formulasFilePath))) {
            String line = "";
            boolean assignToNewFormula = true;
            List<String> currentFormula = new ArrayList<String>();

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
                            currentFormula.clear();
                            assignToNewFormula = false;
                        }

                        currentFormula.add(prepareString(line));
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

    private String prepareString(String s) {
        return s.trim();
    }
}