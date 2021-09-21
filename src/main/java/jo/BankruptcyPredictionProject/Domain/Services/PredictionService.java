package jo.BankruptcyPredictionProject.Domain.Services;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.springframework.beans.factory.annotation.Autowired;

import jo.BankruptcyPredictionProject.Configuration.BPPConfig;
import jo.BankruptcyPredictionProject.Domain.Repositories.FormulaRepo;
import jo.BankruptcyPredictionProject.Domain.Value.PredictionResult;
import jo.BankruptcyPredictionProject.Utility.ArffLoader;
import jo.BankruptcyPredictionProject.Utility.BPPLogger;
import jo.BankruptcyPredictionProject.Values.Clause;
import jo.BankruptcyPredictionProject.Values.Formula;
import jo.BankruptcyPredictionProject.Values.Literal;
import jo.BankruptcyPredictionProject.Values.Interface.FormulaElement;
import weka.core.Instance;
import weka.core.Instances;

public class PredictionService {
    private static PredictionService instance;

    private final BPPConfig bppConfig;
    private final FormulaRepo formulaRepo = FormulaRepo.getInstance();

    @Autowired
    private ArffLoader arffRepo;

    private String dataFilePath;

    private PredictionService() throws JAXBException {
        bppConfig = BPPConfig.getInstance();
    }

    public static PredictionService getInstance() throws JAXBException {
        if (instance == null){
            instance = new PredictionService();
        }

        return instance;
    }

    public void predict(boolean isTest){
        String oldPath = this.arffRepo.getFilePath();
        Instances oldData = this.arffRepo.getData();
        this.arffRepo.setFilePath(this.dataFilePath);
        this.arffRepo.loadData(isTest);

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
        BPPLogger.log("Data size: " + this.arffRepo.getData().numInstances());

        for (int i = 0; i < this.arffRepo.getData().numInstances(); i++){
            BPPLogger.log("Record No.: " + i);

            PredictionResult predictionResult = getPredictionForRecord(this.arffRepo.getData().get(i), isTest);
            
            if (predictionResult.isPredictionCorrect() != null){
                if (predictionResult.isPredictionCorrect()){
                    correctPredictions++;

                    if (predictionResult.getExpected()){
                        bankruptCounter++;
                        bankruptCorrect++;
                    } else {
                        notBankruptCounter++;
                        notBankruptCorrect++;
                    }
                } else {
                    incorrectPredictions++;

                    if (predictionResult.getExpected()){
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

        if (isTest){
            int dataSize = this.arffRepo.getData().numInstances();
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

        this.arffRepo.setFilePath(oldPath);
        this.arffRepo.setData(oldData);
    }

    private PredictionResult getPredictionForRecord(Instance record, boolean isTest) {
        Boolean predictionCorrect = null;
        Boolean expected = null;
        Boolean received = null;

        boolean recordClass = true;
        if (isTest){
            recordClass = record.classValue() == 1.0 ? true : false;
        }

        List<Formula> assessmentFormulas = this.formulaRepo.getFormulas(null);
        List<Boolean> results = new ArrayList<Boolean>();

        for (Formula assessmentFormula : assessmentFormulas) {
            List<List<Boolean>> predictionMatrix = new LinkedList<>();
            for (int j = 0; j < assessmentFormula.getFormulaSize(); j++) {
                predictionMatrix.add(new LinkedList<Boolean>());
                FormulaElement formulaElement = assessmentFormula.getElements().get(j);
                if (formulaElement instanceof Literal) {
                    predictionMatrix.get(j).add(Boolean.FALSE);
                } else if (formulaElement instanceof Clause) {
                    Clause clause = (Clause) formulaElement;
                    for (int k = 0; k < clause.getLiterals().size(); k++) {
                        predictionMatrix.get(j).add(Boolean.FALSE);
                    }
                }
            }

            List<Boolean> result = new LinkedList<>();

            // Variables substitution
            for (int j = 0; j < record.numAttributes(); j++) {
                String attrName = record.attribute(j).name();
                Double value = record.value(j);

                for (int k = 0; k < assessmentFormula.getElements().size(); k++) {
                    FormulaElement formulaElement = assessmentFormula.getElements().get(k);

                    if (formulaElement instanceof Literal) {
                        Literal literal = (Literal) formulaElement;
                        if (literal.getScope() != null && literal.getScope().getAttrName().equals(attrName)
                                && literal.getScope().isApplicable(value)) {
                            predictionMatrix.get(k).set(0, Boolean.TRUE && !literal.getIsNegative());
                        }
                    } else if (formulaElement instanceof Clause) {
                        Clause clause = (Clause) formulaElement;

                        for (int l = 0; l < clause.getLiterals().size(); l++) {
                            Literal literal = clause.getLiterals().get(l);
                            if (literal.getScope() != null && literal.getScope().getAttrName().equals(attrName)
                                    && literal.getScope().isApplicable(value)) {
                                predictionMatrix.get(k).set(l, Boolean.TRUE && !literal.getIsNegative());
                            }
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

        //Calculate end result
        boolean endResult = true;
        int votesBankrupt = 0;
        int votesNotBankrupt = 0;
        for (Boolean result : results){
            if (result) {
                votesBankrupt++;
            } else {
                votesNotBankrupt++;
            }
        }

        if (votesBankrupt >= votesNotBankrupt){
            endResult = true;
            BPPLogger.log("Major risk of bankruptcy detected!");
        } else {
            endResult = false;
            BPPLogger.log("No major risk of bankruptcy detected!");
        }

        if (isTest){
            predictionCorrect = !(endResult ^ recordClass);
            expected = recordClass;
            received = endResult;
        }

        return new PredictionResult(predictionCorrect, expected, received);
    }

    public void setDataFilePath(String dataFilePath) {
        this.dataFilePath = dataFilePath;
    }
}