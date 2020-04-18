package jo.BankruptcyPredictionProject.Domain.Repositories;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import jo.BankruptcyPredictionProject.Utility.BPPLogger;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader.ArffReader;

public class ArffRepo {
    private static ArffRepo instance;

    private String filePath;
    private Instances data;

    private ArffRepo() {
    }

    public static ArffRepo getInstance() {
        if (instance == null) {
            instance = new ArffRepo();
        }

        return instance;
    }

    public void loadData(Boolean isTest) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File(this.filePath)));
            ArffReader arff;
            arff = new ArffReader(reader, 1000);
            this.data = arff.getStructure();

            if (isTest) {
                this.data.setClassIndex(data.numAttributes() - 1);
            }

            Instance inst;
            while ((inst = arff.readInstance(this.data)) != null) {
                this.data.add(inst);
            }

        } catch (Exception e) {
            BPPLogger.log("Reading from arff file: " + this.filePath + " failed!");
            e.printStackTrace();
        }

        BPPLogger.log("Data from arff file loaded successfully! Number of loaded instances: " + data.numInstances());
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Instances getData() {
        return data;
    }

    public void setData(Instances data) {
        this.data = data;
    }
}