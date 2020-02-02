package jo.BankruptcyPredictionProject.Values;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jo.BankruptcyPredictionProject.Values.Interface.FormulaElement;

public class Formula {
    private List<FormulaElement> elements;

    private Set<Literal> uniqueVariables;
    
    public Formula(){
        this.elements = new ArrayList<>();
        this.uniqueVariables = new HashSet<>();
    }
    
    public List<FormulaElement> getElements(){
        return this.elements;
    }

    public Set<Literal> getUniqueVariables(){
        return this.uniqueVariables;
    }
    
    public void attach(FormulaElement formulaElement){
        this.elements.add(formulaElement);

        if (formulaElement instanceof Literal){
            Literal newLiteral = (Literal) formulaElement;
            
            addIfUniqueVariable(newLiteral);
        } else if (formulaElement instanceof Clause){
            Clause clause = (Clause) formulaElement;

            for (Literal literal : clause.getLiterals()){
                addIfUniqueVariable(literal);
            }
        }
    }

    private void addIfUniqueVariable(Literal literal){
        if (!this.uniqueVariables.contains(literal)){
            this.uniqueVariables.add(literal);
        }
    }

    public void detach(int index){
        FormulaElement detachedElement = this.elements.remove(index);

        if (detachedElement instanceof Literal){
            Literal literal = (Literal) detachedElement;

            removeUniqueVariable(literal);
        } else if (detachedElement instanceof Clause){
            Clause clause = (Clause) detachedElement;

            for (Literal literal : clause.getLiterals()){
                removeUniqueVariable(literal);
            }
        }
    }

    private void removeUniqueVariable(Literal literal){
        this.uniqueVariables.remove(literal);
    }

    public int getFormulaSize(){
        return this.elements.size();
    }

    public int getUniqueVariablesCount(){
        return this.uniqueVariables.size();
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        for (FormulaElement element : elements){
            sb.append(element.toString()).append(" 0").append('\n');
        }

        return sb.toString().trim();
    }

    public String toExtString(){
        StringBuilder sb = new StringBuilder();
        for (FormulaElement element : elements){
            sb.append(element.toExtString()).append('\n');
        }

        return sb.toString().trim();
    }

    @Override
    public boolean equals(Object obj){
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }

        Formula f = (Formula) obj;
        return this.toExtString().equals(f.toExtString());
    }
}