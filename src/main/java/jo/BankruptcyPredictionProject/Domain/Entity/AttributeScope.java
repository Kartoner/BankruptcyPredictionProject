package jo.BankruptcyPredictionProject.Domain.Entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ATTRIBUTE_SCOPES")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttributeScope {
    
    @Id
    @Column(name = "ATS_ID")
    @GeneratedValue
    private Long id;

    @Column(name = "ATS_ATTR_NAME", nullable = false)
    private String attrName;

    @Column(name = "ATS_RANGE_FROM")
    private Double rangeFrom;

    @Column(name = "ATS_RANGE_TO")
    private Double rangeTo;

    public Boolean isApplicable(Double value) {
        if (value == null) {
            return Boolean.FALSE;
        }
        if (this.rangeFrom != null && value < this.rangeFrom) {
            return Boolean.FALSE;
        }
        if (this.rangeTo != null && value > this.rangeTo) {
            return Boolean.FALSE;
        }

        return Boolean.TRUE;
    }

    public Boolean isApplicable(AttributeScope scope) {
        if (scope == null) {
            return Boolean.FALSE;
        }
        if (!this.attrName.equals(scope.attrName)) {
            return Boolean.FALSE;
        }
        if (this.equals(scope)) {
            return Boolean.TRUE;
        }
        if (isApplicable(scope.getRangeFrom()) || isApplicable(scope.getRangeTo())) {
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }

    @Override
    public boolean equals(Object obj) {
        AttributeScope scope = (AttributeScope) obj;
        return this.attrName.equals(scope.getAttrName()) && this.rangeFrom == scope.getRangeFrom()
                && this.rangeTo == scope.getRangeTo();
    }

    @Override
    public String toString() {
        if (this.rangeFrom != null && this.rangeTo == null) {
            return getAttrName() + "HigherThan" + getRangeFrom();
        } else if (this.rangeFrom == null && this.rangeTo != null) {
            return getAttrName() + "LowerThan" + getRangeTo();
        } else {
            return getAttrName() + "Between" + getRangeFrom() + "And" + getRangeTo();
        }
    }
}