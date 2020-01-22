package jo.BankruptcyPredictionProject.Values;

import java.util.ArrayList;
import java.util.List;

import jo.BankruptcyPredictionProject.Values.Interface.FormulaElement;

public class Formula {
    private List<FormulaElement> elements;
    
    public Formula(){
        this.elements = new ArrayList<>();
    }
    
    public List<FormulaElement> getElements(){
        return this.elements;
    }
    
    public void attach(FormulaElement formulaElement){
        this.elements.add(formulaElement);
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        for (FormulaElement element : elements){
            sb.append(element.toString()).append('\n');
        }

        return sb.toString();
    }

    public String toExtString(){
        StringBuilder sb = new StringBuilder();
        for (FormulaElement element : elements){
            sb.append(element.toExtString()).append('\n');
        }

        return sb.toString();
    }
}