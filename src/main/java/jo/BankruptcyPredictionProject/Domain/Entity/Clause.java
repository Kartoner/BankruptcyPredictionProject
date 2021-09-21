package jo.BankruptcyPredictionProject.Domain.Entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import jo.BankruptcyPredictionProject.Domain.Enumeration.ClauseType;
import jo.BankruptcyPredictionProject.Domain.Enumeration.Converter.ClauseTypeConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "CLAUSES")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Clause {
    
    @Id
    @Column(name = "CLA_ID")
    @GeneratedValue
    private Long id;

    @Column(name = "CLA_TYPE", nullable = false)
    @Convert(converter = ClauseTypeConverter.class)
    private ClauseType clauseType = ClauseType.STANDARD;

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(name = "CLAUSE_LITERAL", joinColumns = @JoinColumn(name = "CLA_ID"), inverseJoinColumns = @JoinColumn(name = "LIT_ID"))
    private List<Literal> literals = new ArrayList<>();

    public void attach(Literal literal) {
        this.literals.add(literal);
    }

    public boolean literalAlreadyPresent(Literal newLiteral) {
        for (Literal literal : this.literals) {
            if (literal.toExtString().equals(newLiteral.toExtString())) {
                return true;
            }
        }

        return false;
    }

    public boolean containsVariable(String description) {
        for (Literal literal : this.literals) {
            if (literal.getDescription().equals(description)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String toString() {
        String result = "";

        for (Literal literal : this.literals) {
            result += literal.toString() + " ";
        }

        result = result.substring(0, result.length() - 1);

        return result;
    }

    public String toExtString() {
        String result = "";

        for (Literal literal : this.literals) {
            result += literal.toExtString() + " ";
        }

        result = result.substring(0, result.length() - 1);

        return result;
    }

    public int getLength() {
        return this.literals.size();
    }
}