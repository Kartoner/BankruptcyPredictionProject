package jo.BankruptcyPredictionProject.Values;

import jo.BankruptcyPredictionProject.Values.Interface.FormulaElement;

public class Literal implements FormulaElement {
    private Integer symbol;
    private String description;
    private Boolean isNegative;

    public Literal(Integer symbol, String description, Boolean isNegative) {
        this.symbol = symbol;
        this.description = description;
        this.isNegative = isNegative;
    }

    public Integer getSymbol() {
        return symbol;
    }

    public void setSymbol(Integer symbol) {
        this.symbol = symbol;
    }

    public String getDescription(){
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsNegative(){
        return isNegative;
    }

    public void setIsNegative(Boolean isNegative) {
        this.isNegative = isNegative;
    }

    @Override
    public String toString() {
        return this.symbol.toString();
    }

    @Override
    public String toExtString(){
        if (!isNegative){
            return description;
        } else {
            return "~" + description;
        }
    }
    
}