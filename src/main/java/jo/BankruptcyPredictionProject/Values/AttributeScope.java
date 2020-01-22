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
        if (rangeFrom != null && value < rangeFrom){
            return Boolean.FALSE;
        }
        if (rangeTo != null && value > rangeTo){
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
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
    public String toString(){
        return getAttrName() + "Between" + getRangeFrom() + "And" + getRangeTo();
    }
}