package jo.BankruptcyPredictionProject.Values;

public class AttributeScope {
    private String attrName;
    private Double rangeFrom;
    private Double rangeTo;

    public AttributeScope(String attrName, Double rangeFrom, Double rangeTo){
        this.attrName = attrName;
        this.rangeFrom = rangeFrom;
        this.rangeTo = rangeTo;
    }

    public Boolean isApplicable(Double value){
        if (value == null){
            return Boolean.FALSE;
        }
        if (this.rangeFrom != null && value < this.rangeFrom){
            return Boolean.FALSE;
        }
        if (this.rangeTo != null && value > this.rangeTo){
            return Boolean.FALSE;
        }

        return Boolean.TRUE;
    }

    public Boolean isApplicable(AttributeScope scope){
        if (scope == null){
            return Boolean.FALSE;
        }
        if (!this.attrName.equals(scope.attrName)){
            return Boolean.FALSE;
        }
        if (this.equals(scope)){
            return Boolean.TRUE;
        }
        if (isApplicable(scope.getRangeFrom()) || isApplicable(scope.getRangeTo())){
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }

    public String getAttrName(){
        return attrName;
    }

    public void setAttrName(String attrName){
        this.attrName = attrName;
    }

    public Double getRangeFrom(){
        return rangeFrom;
    }

    public void setRangeFrom(Double rangeFrom){
        this.rangeFrom = rangeFrom;
    }

    public Double getRangeTo(){
        return rangeTo;
    }

    public void setRangeTo(Double rangeTo){
        this.rangeTo = rangeTo;
    }

    @Override
    public boolean equals(Object obj) {
        AttributeScope scope = (AttributeScope) obj;
        return this.attrName.equals(scope.getAttrName())
         && this.rangeFrom == scope.getRangeFrom()
         && this.rangeTo == scope.getRangeTo();
    }

    @Override
    public String toString(){
        if (this.rangeFrom != null && this.rangeTo == null){
            return getAttrName() + "HigherThan" + getRangeFrom();
        } else if (this.rangeFrom == null && this.rangeTo != null){
            return getAttrName() + "LowerThan" + getRangeTo();
        } else {
            return getAttrName() + "Between" + getRangeFrom() + "And" + getRangeTo();
        }
    }
}