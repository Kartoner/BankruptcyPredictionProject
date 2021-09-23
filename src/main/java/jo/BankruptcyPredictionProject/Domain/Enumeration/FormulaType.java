package jo.BankruptcyPredictionProject.Domain.Enumeration;

public enum FormulaType {
    ASSESSMENT("A"),
    BANKRUPT("B"),
    NOT_BANKRUPT("N");

    private final String abbreviation;

    FormulaType(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public static FormulaType fromAbbreviation(String abbreviation) {
        if (abbreviation.equals(ASSESSMENT.abbreviation)) {
            return ASSESSMENT;
        } else if (abbreviation.equals(BANKRUPT.abbreviation)) {
            return BANKRUPT;
        } else if (abbreviation.equals(NOT_BANKRUPT.abbreviation)) {
            return NOT_BANKRUPT;
        } else {
            return null;
        }
    }

    public String getAbbreviation() {
        return abbreviation;
    }
}