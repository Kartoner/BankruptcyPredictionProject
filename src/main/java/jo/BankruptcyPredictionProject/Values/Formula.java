package jo.BankruptcyPredictionProject.Values;

import java.util.LinkedList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jo.BankruptcyPredictionProject.Values.Interface.FormulaElement;

public class Formula {
    private List<FormulaElement> elements;

    private Map<String, Integer> uniqueVariables;
    
    public Formula(){
        this.elements = new LinkedList<>();
        this.uniqueVariables = new HashMap<>();
    }
    
    public List<FormulaElement> getElements(){
        return this.elements;
    }

    public Map<String, Integer> getUniqueVariables(){
        return this.uniqueVariables;
    }
    
    public void attach(FormulaElement formulaElement){
        if (!containsElement(formulaElement)){
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
    }

    private boolean containsElement(FormulaElement element){
        for (FormulaElement formulaElement: this.elements){
            if (formulaElement.toExtString().equals(element.toExtString())){
                return true;
            }
        }

        return false;
    }

    private void addIfUniqueVariable(Literal literal){
        if (!this.uniqueVariables.containsKey(literal.getDescription())){
            this.uniqueVariables.put(literal.getDescription(), literal.getSymbol());
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
        this.uniqueVariables.remove(literal.getDescription());
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