package jo.BankruptcyPredictionProject.Domain.Enumeration;

public enum FormulaType {
    ASSESSMENT("A"),
    TEST_BANKRUPT("B"),
    TEST_NOT_BANKRUPT("N");

    private final String abbreviation;

    FormulaType(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public static FormulaType fromAbbreviation(String abbreviation) {
        if (abbreviation.equals(ASSESSMENT.abbreviation)) {
            return ASSESSMENT;
        } else if (abbreviation.equals(TEST_BANKRUPT.abbreviation)) {
            return TEST_BANKRUPT;
        } else if (abbreviation.equals(TEST_NOT_BANKRUPT.abbreviation)) {
            return TEST_NOT_BANKRUPT;
        } else {
            return null;
        }
    }

    public String getAbbreviation() {
        return abbreviation;
    }
}