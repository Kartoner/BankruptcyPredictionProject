package jo.BankruptcyPredictionProject.Domain.Enumeration;

public enum ClauseType {
    STANDARD("S"),
    BUSINESS("B");

    private final String abbreviation;

    ClauseType(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public static ClauseType fromAbbreviation(String abbreviation) {
        if (abbreviation.equals(STANDARD.abbreviation)) {
            return STANDARD;
        } else if (abbreviation.equals(BUSINESS.abbreviation)) {
            return BUSINESS;
        } else {
            return null;
        }
    }

    public String getAbbreviation() {
        return abbreviation;
    }
}