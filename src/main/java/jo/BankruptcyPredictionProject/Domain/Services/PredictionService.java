package jo.BankruptcyPredictionProject.Domain.Services;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXBException;

import jo.BankruptcyPredictionProject.Configuration.BPPConfig;
import jo.BankruptcyPredictionProject.Domain.Repositories.ArffRepo;
import jo.BankruptcyPredictionProject.Domain.Repositories.FormulaRepo;
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
    private final ArffRepo arffRepo = ArffRepo.getInstance();

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

        BPPLogger.log("---------------------------");
        BPPLogger.log("Prediction started");
        BPPLogger.log("Data size: " + this.arffRepo.getData().numInstances());

        for (int i = 0; i < this.arffRepo.getData().numInstances(); i++){
            Boolean predictionCorrect = getPredictionForRecord(this.arffRepo.getData().get(i), isTest);
            
            if (predictionCorrect != null){
                if (predictionCorrect){
                    correctPredictions++;
                } else {
                    incorrectPredictions++;
                }
            }
        }

        BPPLogger.log("Finished!");

        if (isTest){
            BPPLogger.log("Correct predictions: " + correctPredictions + " / " + this.arffRepo.getData().numInstances());
            BPPLogger.log("Incorrect predictions: " + incorrectPredictions + " / " + this.arffRepo.getData().numInstances());
        }

        BPPLogger.log("---------------------------");

        this.arffRepo.setFilePath(oldPath);
        this.arffRepo.setData(oldData);
    }

    private Boolean getPredictionForRecord(Instance record, boolean isTest){
        Boolean predictionCorrect = null;
        boolean recordClass = record.classValue() == 1.0 ? true : false;

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

        return predictionCorrect;
    }

    public void setDataFilePath(String dataFilePath) {
        this.dataFilePath = dataFilePath;
    }
}