package jo.BankruptcyPredictionProject.Values;

import jo.BankruptcyPredictionProject.Values.Interface.FormulaElement;

public class Literal implements FormulaElement {
    private int symbol;
    private String description;
    private boolean isNegative;
    private AttributeScope scope;

    public Literal(int symbol, String description, boolean isNegative, AttributeScope scope) {
        this.symbol = symbol;
        this.description = description;
        this.isNegative = isNegative;
        this.scope = scope;
    }

    public int getSymbol() {
        return symbol;
    }

    public void setSymbol(int symbol) {
        this.symbol = symbol;
    }

    public String getDescription(){
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean getIsNegative(){
        return isNegative;
    }

    public void setIsNegative(boolean isNegative) {
        this.isNegative = isNegative;
    }

    public AttributeScope getScope(){
        return scope;
    }

    public void setScope(AttributeScope scope){
        this.scope = scope;
    }

    @Override
    public String toString() {
        if (!isNegative){
            return Integer.toString(this.symbol);
        } else {
            return "-" + this.symbol;
        }
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