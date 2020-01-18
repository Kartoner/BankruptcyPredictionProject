package jo.BankruptcyPredictionProject.Values;

import java.util.ArrayList;
import java.util.List;

import jo.BankruptcyPredictionProject.Values.Interface.FormulaElement;

public class Clause implements FormulaElement{
    private List<Literal> literals;
    
    public Clause(){
        this.literals = new ArrayList<>();
    }
    
    public List<Literal> getLiterals(){
        return this.literals;
    }
    
    public void attach(Literal literal){
        this.literals.add(literal);
    }

    @Override
    public String toString() {
        String result = "";
        
        for(Literal literal : this.literals){
            result += literal.toString() + " ";
        }
        
        result = result.substring(0, result.length() - 1);
        
        return result;
    }

    @Override
    public String toExtString(){
        String result = "";
        
        for(Literal literal : this.literals){
            result += literal.toExtString() + " ";
        }
        
        result = result.substring(0, result.length() - 1);
        
        return result;
    }
    
}