package jo.BankruptcyPredictionProject.Values;

public class TestingResult {

    private boolean valid;

    private Integer failingElement;

    public TestingResult(boolean valid, Integer failingElement){
        this.valid = valid;
        this.failingElement = failingElement;
    }

    public boolean isValid(){
        return this.valid;
    }

    public void setValid(boolean valid){
        this.valid = valid;
    }

    public Integer getFailingElement(){
        return this.failingElement;
    }

    public void setResult(Integer failingElement){
        this.failingElement = failingElement;
    }
}