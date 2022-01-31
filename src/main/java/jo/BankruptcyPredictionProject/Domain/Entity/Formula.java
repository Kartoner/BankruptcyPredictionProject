package jo.BankruptcyPredictionProject.Domain.Entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.Transient;

import jo.BankruptcyPredictionProject.Domain.Enumeration.FormulaType;
import jo.BankruptcyPredictionProject.Domain.Enumeration.Converter.FormulaTypeConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "FORMULAS")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Formula {
    
    @Id
    @Column(name = "FOR_ID")
    @GeneratedValue
    private Long id;

    @Column(name = "FOR_TYPE", nullable = false)
    @Convert(converter = FormulaTypeConverter.class)
    private FormulaType formulaType = FormulaType.ASSESSMENT;

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(name = "FORMULA_CLAUSE", joinColumns = @JoinColumn(name = "FOR_ID"), inverseJoinColumns = @JoinColumn(name = "CLA_ID"))
    private List<Clause> clauses = new ArrayList<>();

    @Column(name = "FOR_WEIGHT")
    private Double weight;

    @Transient
    private Set<String> uniqueVariables = new HashSet<>();

    @PostLoad
    public void fillUniqueVariables() {
        for (Clause clause : this.clauses) {
            for (Literal literal : clause.getLiterals()) {
                addIfUniqueVariable(literal);
            }
        }
    }

    public void attach(Clause clause) {
        if (!containsElement(clause)) {
            this.clauses.add(clause);
   
            for (Literal literal : clause.getLiterals()) {
                addIfUniqueVariable(literal);
            }
        }
    }

    private boolean containsElement(Clause newClause) {
        for (Clause clause : this.clauses) {
            if (clause.toExtString().equals(newClause.toExtString())) {
                return true;
            }
        }

        return false;
    }

    private void addIfUniqueVariable(Literal literal) {
        if (!this.uniqueVariables.contains(literal.getDescription())) {
            this.uniqueVariables.add(literal.getDescription());
        }
    }

    public void detach(int index) {
        Clause detachedClause = this.clauses.remove(index);

        for (Literal literal : detachedClause.getLiterals()) {
            removeUniqueVariable(literal);
        }
    }

    private void removeUniqueVariable(Literal literal) {
        for (Clause clause : this.clauses) {
            if (clause.containsVariable(literal.getDescription())) {
                return;
            }
        }

        this.uniqueVariables.remove(literal.getDescription());
    }

    public int getFormulaSize() {
        return this.clauses.size();
    }

    public int getUniqueVariablesCount() {
        return this.uniqueVariables.size();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Clause clause : this.clauses) {
            sb.append(clause.toString()).append(" 0").append('\n');
        }

        return sb.toString().trim();
    }

    public String toExtString() {
        StringBuilder sb = new StringBuilder();
        for (Clause clause : this.clauses) {
            sb.append(clause.toExtString()).append('\n');
        }

        return sb.toString().trim();
    }

    @Override
    public boolean equals(Object obj) {
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