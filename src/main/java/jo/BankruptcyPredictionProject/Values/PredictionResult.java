package jo.BankruptcyPredictionProject.Values;

public class PredictionResult {
    
    private Boolean predictionCorrect;

    private Boolean expected;

    private Boolean received;

    public PredictionResult(Boolean predictionCorrect, Boolean expected, Boolean received){
        this.predictionCorrect = predictionCorrect;
        this.expected = expected;
        this.received = received;
    }

    public Boolean isPredictionCorrect() {
        return predictionCorrect;
    }

    public void setPredictionCorrect(Boolean predictionCorrect) {
        this.predictionCorrect = predictionCorrect;
    }

    public Boolean getExpected() {
        return expected;
    }

    public void setExpected(Boolean expected) {
        this.expected = expected;
    }

    public Boolean getReceived() {
        return received;
    }

    public void setReceived(Boolean received) {
        this.received = received;
    }
}