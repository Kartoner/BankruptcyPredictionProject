package jo.BankruptcyPredictionProject.Values;

public class TestingResult {

    private boolean valid;

    private Integer failingElement;

    private Double successRatio;

    private Formula formula;

    public TestingResult(boolean valid, Integer failingElement, Double successRatio, Formula formula){
        this.valid = valid;
        this.failingElement = failingElement;
        this.successRatio = successRatio;
        this.formula = formula;
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

    public Double getSuccessRatio(){
        return this.successRatio;
    }

    public void setSuccessRatio(Double successRatio) {
        this.successRatio = successRatio;
    }

    public Formula getFormula() {
        return this.formula;
    }

    public void setFormula(Formula formula) {
        this.formula = formula;
    }
}