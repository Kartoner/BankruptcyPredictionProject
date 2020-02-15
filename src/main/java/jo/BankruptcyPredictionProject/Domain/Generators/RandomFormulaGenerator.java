package jo.BankruptcyPredictionProject.Domain.Generators;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.xml.bind.JAXBException;

import jo.BankruptcyPredictionProject.Configuration.BPPConfig;
import jo.BankruptcyPredictionProject.Domain.Repositories.ArffRepo;
import jo.BankruptcyPredictionProject.Domain.Repositories.FormulaRepo;
import jo.BankruptcyPredictionProject.Values.Clause;
import jo.BankruptcyPredictionProject.Values.Formula;
import jo.BankruptcyPredictionProject.Values.Literal;
import jo.BankruptcyPredictionProject.Values.TestingResult;
import jo.BankruptcyPredictionProject.Values.Interface.FormulaElement;
import weka.core.Instance;
import weka.core.Instances;

public class RandomFormulaGenerator {
    private static RandomFormulaGenerator instance;

    private final BPPConfig bppConfig;
    private final FormulaRepo formulaRepo = FormulaRepo.getInstance();
    private final ArffRepo arffRepo = ArffRepo.getInstance();

    private Formula generatedFormula;
    private String testDataFilePath;

    private RandomFormulaGenerator() throws JAXBException {
        bppConfig = BPPConfig.getInstance();
        generatedFormula = new Formula();
    }

    public static RandomFormulaGenerator getInstance() throws JAXBException {
        if (instance == null) {
            instance = new RandomFormulaGenerator();
        }

        return instance;
    }

    public void tryGenerateSetOfRandomFormulas(int numOfFormulas){
        int generatedFormulaCounter = 0;
        for (int i = 0; i < numOfFormulas; i++){
            boolean generationResult = generateFormula();

            if (generationResult){
                generatedFormulaCounter++;
            }
        }

        System.out.println("Formulas generated: " + generatedFormulaCounter);
    }

    public boolean generateFormula(){
        boolean isFormulaGenerated = Boolean.FALSE;

        return isFormulaGenerated;
    }

    private TestingResult testFormula(){
        TestingResult testingResult;

        boolean isValid = false;
        Integer failingElement = null;
        int successCounter = 0;

        Map<Integer, Integer> failingElements = new HashMap<>();

        for (int i = 0; i < this.generatedFormula.getFormulaSize(); i++){
            failingElements.put(i, 0);
        }

        String oldPath = arffRepo.getFilePath();
        Instances oldData = arffRepo.getData();
        arffRepo.setFilePath(this.testDataFilePath);
        arffRepo.loadData(Boolean.TRUE);

        Instances data = arffRepo.getData();

        for (int i = 0; i < data.size(); i++) {
            List<List<Boolean>> subResult = new LinkedList<>();
            for (int j = 0; j < this.generatedFormula.getFormulaSize(); j++){
                subResult.add(new LinkedList<Boolean>());
                FormulaElement formulaElement = this.generatedFormula.getElements().get(i);
                if (formulaElement instanceof Literal){
                    subResult.get(j).add(Boolean.FALSE);
                } else if (formulaElement instanceof Clause){
                    Clause clause = (Clause) formulaElement;
                    for (int k = 0; k < clause.getLiterals().size(); k++){
                        subResult.get(j).add(Boolean.FALSE);
                    }
                }
            }

            List<Boolean> result = new LinkedList<>();

            Instance record = data.get(i);
            for (int j = 1; j < record.numAttributes(); j++){
                String attrName = record.attribute(j).name();
                Double value = record.value(j);

                for (int k = 0; k < generatedFormula.getElements().size(); k++){
                    FormulaElement formulaElement = generatedFormula.getElements().get(k);

                    if (formulaElement instanceof Literal){
                        Literal literal = (Literal) formulaElement;
                        if (literal.getScope().getAttrName().equals(attrName) && literal.getScope().isApplicable(value)){
                            subResult.get(k).set(0, Boolean.TRUE);
                        }
                    } else if (formulaElement instanceof Clause){
                        Clause clause = (Clause) formulaElement;

                        for (int l = 0; l < clause.getLiterals().size(); l++){
                            Literal literal = clause.getLiterals().get(l);
                            if (literal.getScope().getAttrName().equals(attrName) && literal.getScope().isApplicable(value)){
                                subResult.get(k).set(l, Boolean.TRUE);
                            }
                        }
                    }
                }
            }

            for (int j = 0; j < subResult.size(); j++){
                boolean evalSubResult = Boolean.FALSE;

                for (int k = 0; k < subResult.get(j).size(); k++){
                    evalSubResult = evalSubResult || subResult.get(j).get(k);
                }

                result.add(evalSubResult);

                if (!evalSubResult){
                    failingElements.put(result.size() - 1, failingElements.get(result.size() - 1) + 1);
                }
            }

            boolean evalResult = Boolean.TRUE;

            for (int j = 0; j < result.size(); j++){
                evalResult = evalResult && result.get(j);
            }

            if (evalResult) {
                successCounter++;
            }
            
        }

        if ((successCounter / data.size()) * 1.0 > this.bppConfig.getToleranceThreshold()){
            isValid = true;

            if (!isValid){
                Map.Entry<Integer, Integer> maxEntry = null;

                for (Map.Entry<Integer, Integer> entry : failingElements.entrySet()){
                    if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0){
                        maxEntry = entry;
                    }
                }

                failingElement = maxEntry.getKey();
            }
        } 

        arffRepo.setFilePath(oldPath);
        arffRepo.setData(oldData);
        testingResult = new TestingResult(isValid, failingElement);
        return testingResult;
    }

    public void setTestDataFilePath(String filePath){
        this.testDataFilePath = filePath;
    }

}