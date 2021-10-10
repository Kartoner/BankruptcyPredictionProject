package jo.BankruptcyPredictionProject.Domain.Service;

public interface PredictionService {
    
    void predict(boolean isTest, String dataFilePath);
}