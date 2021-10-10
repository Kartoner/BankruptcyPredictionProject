package jo.BankruptcyPredictionProject.Domain.Service.implementation;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jo.BankruptcyPredictionProject.Domain.Entity.Clause;
import jo.BankruptcyPredictionProject.Domain.Entity.Formula;
import jo.BankruptcyPredictionProject.Domain.Entity.Literal;
import jo.BankruptcyPredictionProject.Domain.Enumeration.FormulaType;
import jo.BankruptcyPredictionProject.Domain.Service.FormulaService;
import jo.BankruptcyPredictionProject.Domain.Service.PredictionService;
import jo.BankruptcyPredictionProject.Domain.Value.PredictionResult;
import jo.BankruptcyPredictionProject.Utility.ArffLoader;
import jo.BankruptcyPredictionProject.Utility.BPPLogger;
import weka.core.Instance;
import weka.core.Instances;

@Service
public class PredictionServiceImpl implements PredictionService {

    @Autowired
    private FormulaService formulaService;

    @Autowired
    private ArffLoader arffLoader;

    public void predict(boolean isTest, String dataFilePath) {
        String oldPath = this.arffLoader.getFilePath();
        Instances oldData = this.arffLoader.getData();
        this.arffLoader.setFilePath(dataFilePath);
        this.arffLoader.loadData(isTest);

        int correctPredictions = 0;
        int incorrectPredictions = 0;
        int bankruptCounter = 0;
        int notBankruptCounter = 0;
        int bankruptCorrect = 0;
        int bankruptIncorrect = 0;
        int notBankruptCorrect = 0;
        int notBankruptIncorrect = 0;

        BPPLogger.log("---------------------------");
        BPPLogger.log("Prediction started");
        BPPLogger.log("Data size: " + this.arffLoader.getData().numInstances());

        for (int i = 0; i < this.arffLoader.getData().numInstances(); i++) {
            BPPLogger.log("Record No.: " + i);

            PredictionResult predictionResult = getPredictionForRecord(this.arffLoader.getData().get(i), isTest);

            if (predictionResult.getPredictionCorrect() != null) {
                if (predictionResult.getPredictionCorrect()) {
                    correctPredictions++;

                    if (predictionResult.getExpected()) {
                        bankruptCounter++;
                        bankruptCorrect++;
                    } else {
                        notBankruptCounter++;
                        notBankruptCorrect++;
                    }
                } else {
                    incorrectPredictions++;

                    if (predictionResult.getExpected()) {
                        bankruptCounter++;
                        bankruptIncorrect++;
                    } else {
                        notBankruptCounter++;
                        notBankruptIncorrect++;
                    }
                }
            }
        }

        BPPLogger.log("Finished!");

        if (isTest) {
            int dataSize = this.arffLoader.getData().numInstances();
            double correctnessRatio = (correctPredictions * 1.0 / dataSize * 1.0) * 1.0;
            double bankruptCorrectnessRatio = (bankruptCorrect * 1.0 / bankruptCounter * 1.0) * 1.0;
            double notBankruptCorrectnessRatio = (notBankruptCorrect * 1.0 / notBankruptCounter * 1.0) * 1.0;
            BPPLogger.log("Risk of bankruptcy:");
            BPPLogger.log("Correct predictions: " + bankruptCorrect + " / " + bankruptCounter);
            BPPLogger.log("Incorrect predictions: " + bankruptIncorrect + " / " + bankruptCounter);
            BPPLogger.log("Correctness ratio: " + bankruptCorrectnessRatio);
            BPPLogger.log("No risk of bankruptcy:");
            BPPLogger.log("Correct predictions: " + notBankruptCorrect + " / " + notBankruptCounter);
            BPPLogger.log("Incorrect predictions: " + notBankruptIncorrect + " / " + notBankruptCounter);
            BPPLogger.log("Correctness ratio: " + notBankruptCorrectnessRatio);
            BPPLogger.log("Overall:");
            BPPLogger.log("Correct predictions: " + correctPredictions + " / " + dataSize);
            BPPLogger.log("Incorrect predictions: " + incorrectPredictions + " / " + dataSize);
            BPPLogger.log("Correctness ratio: " + correctnessRatio);
        }

        BPPLogger.log("---------------------------");

        this.arffLoader.setFilePath(oldPath);
        this.arffLoader.setData(oldData);
    }

    private PredictionResult getPredictionForRecord(Instance record, boolean isTest) {
        Boolean predictionCorrect = null;
        Boolean expected = null;
        Boolean received = null;

        boolean recordClass = true;
        if (isTest) {
            recordClass = record.classValue() == 1.0 ? true : false;
        }

        List<Formula> assessmentFormulas = this.formulaService.getFormulasByType(FormulaType.ASSESSMENT);
        List<Boolean> results = new ArrayList<Boolean>();

        for (Formula assessmentFormula : assessmentFormulas) {
            List<List<Boolean>> predictionMatrix = new LinkedList<>();
            for (int j = 0; j < assessmentFormula.getFormulaSize(); j++) {
                predictionMatrix.add(new LinkedList<Boolean>());
                
                Clause clause = assessmentFormula.getClauses().get(j);
                for (int k = 0; k < clause.getLiterals().size(); k++) {
                    predictionMatrix.get(j).add(Boolean.FALSE);
                }
            }

            List<Boolean> result = new LinkedList<>();

            // Variables substitution
            for (int j = 0; j < record.numAttributes(); j++) {
                String attrName = record.attribute(j).name();
                Double value = record.value(j);

                for (int k = 0; k < assessmentFormula.getFormulaSize(); k++) {
                    Clause clause = assessmentFormula.getClauses().get(k);

                    for (int l = 0; l < clause.getLiterals().size(); l++) {
                        Literal literal = clause.getLiterals().get(l);
                        if (literal.getAttributeScope() != null
                                && literal.getAttributeScope().getAttrName().equals(attrName)
                                && literal.getAttributeScope().isApplicable(value)) {
                            predictionMatrix.get(k).set(l, Boolean.TRUE && !literal.isNegative());
                        }
                    }

                }
            }

            for (int j = 0; j < predictionMatrix.size(); j++) {
                boolean evalSubResult = Boolean.FALSE;

                for (int k = 0; k < predictionMatrix.get(j).size(); k++) {
                    evalSubResult = evalSubResult || predictionMatrix.get(j).get(k);
                }

                result.add(evalSubResult);
            }

            boolean evalResult = Boolean.TRUE;

            for (int j = 0; j < result.size(); j++) {
                evalResult = evalResult && result.get(j);
            }

            results.add(evalResult);
        }

        // Calculate end result
        boolean endResult = true;
        int votesBankrupt = 0;
        int votesNotBankrupt = 0;
        for (Boolean result : results) {
            if (result) {
                votesBankrupt++;
            } else {
                votesNotBankrupt++;
            }
        }

        if (votesBankrupt >= votesNotBankrupt) {
            endResult = true;
            BPPLogger.log("Major risk of bankruptcy detected!");
        } else {
            endResult = false;
            BPPLogger.log("No major risk of bankruptcy detected!");
        }

        if (isTest) {
            predictionCorrect = !(endResult ^ recordClass);
            expected = recordClass;
            received = endResult;
        }

        return new PredictionResult(predictionCorrect, expected, received);
    }
}