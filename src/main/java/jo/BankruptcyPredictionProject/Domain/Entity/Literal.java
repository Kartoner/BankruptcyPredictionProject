package jo.BankruptcyPredictionProject.Domain.Entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "LITERALS")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Literal {
    
    @Id
    @Column(name = "LIT_ID")
    @GeneratedValue
    private Long id;

    @Column(name = "LIT_DESCRIPTION", nullable = false)
    private String description;

    @Column(name = "LIT_IS_NEGATIVE", nullable = false)
    private boolean isNegative = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LIT_ATS_ID")
    private AttributeScope attributeScope;

    @Column(name = "LIT_EXT_DESCRIPTION", nullable = false)
    private String extDescription;

    @Override
    public String toString() {
        if (!isNegative) {
            return Long.toString(this.id);
        } else {
            return "-" + this.id;
        }
    }

    public String toExtString() {
        if (!isNegative) {
            return this.description;
        } else {
            return "~" + this.description;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }

        Literal l = (Literal) obj;
        return this.toExtString().equals(l.toExtString());
    }
}