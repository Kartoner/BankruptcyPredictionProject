package jo.BankruptcyPredictionProject.Configuration;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "bppConfig")
public class BPPConfig {
    private static BPPConfig instance;

    private static final String configFilePath = "./src/main/resources/bppConfig.xml";

    private BPPConfig() throws JAXBException {
    }

    private BPPConfig(int clauseLength, int clauseNumber, int numberOfIterations, boolean fixedLength,
            double formulaToleranceThreshold, double elementToleranceThreshold, boolean fixedSize, int minLength,
            int minSize, int clauseFixAttempts, int testClauseFixAttempts) throws JAXBException {
        this.clauseLength = clauseLength;
        this.clauseNumber = clauseNumber;
        this.numberOfIterations = numberOfIterations;
        this.fixedLength = fixedLength;
        this.formulaToleranceThreshold = formulaToleranceThreshold;
        this.elementToleranceThreshold = elementToleranceThreshold;
        this.fixedSize = fixedSize;
        this.minLength = minLength;
        this.minSize = minSize;
        this.clauseFixAttempts = clauseFixAttempts;
        this.testClauseFixAttempts = testClauseFixAttempts;
    }

    public static BPPConfig getInstance() throws JAXBException {
        if (instance == null) {
            File file = new File(configFilePath);
            JAXBContext jaxbContext = JAXBContext.newInstance(BPPConfig.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            instance = (BPPConfig) unmarshaller.unmarshal(file);
        }

        return instance;
    }

    @XmlElement(name = "clauseLength")
    private int clauseLength;

    @XmlElement(name = "clauseNumber")
    private int clauseNumber;

    @XmlElement(name = "numberOfIterations")
    private int numberOfIterations;

    @XmlElement(name = "fixedLength")
    private boolean fixedLength;

    @XmlElement(name = "hardReset")
    private boolean hardReset;

    @XmlElement(name = "formulaToleranceThreshold")
    private double formulaToleranceThreshold;

    @XmlElement(name = "elementToleranceThreshold")
    private double elementToleranceThreshold;

    @XmlElement(name = "fixedSize")
    private boolean fixedSize;

    @XmlElement(name = "minLength")
    private int minLength;

    @XmlElement(name = "minSize")
    private int minSize;

    @XmlElement(name = "clauseFixAttempts")
    private int clauseFixAttempts;

    @XmlElement(name = "testClauseFixAttempts")
    private int testClauseFixAttempts;

    public int getClauseLength() {
        return this.clauseLength;
    }

    public int getClauseNumber() {
        return this.clauseNumber;
    }

    public int getNumberOfIterations() {
        return this.numberOfIterations;
    }

    public boolean isFixedLength() {
        return this.fixedLength;
    }

    public boolean isHardReset() {
        return this.hardReset;
    }

    public double getFormulaToleranceThreshold() {
        return this.formulaToleranceThreshold;
    }

    public double getElementToleranceThreshold() {
        return this.elementToleranceThreshold;
    }

    public boolean isFixedSize() {
        return this.fixedSize;
    }

    public int getMinLength() {
        return this.minLength;
    }

    public int getMinSize() {
        return this.minSize;
    }

    public int getClauseFixAttempts() {
        return this.clauseFixAttempts;
    }

    public int getTestClauseFixAttempts() {
        return this.testClauseFixAttempts;
    }

    @Override
    public String toString() {
        return "Length of a single clause: " + this.clauseLength + "\n" + "Number of clauses: " + this.clauseNumber
                + "\n" + "Number of iterations: " + this.numberOfIterations + "\n" + "Fixed length of clauses: "
                + this.fixedLength + "\n" + "Hard reset after failure: " + this.hardReset + "\n"
                + "Formula tolerance threshold: " + this.formulaToleranceThreshold + "\n"
                + "Element tolerance threshold: " + this.elementToleranceThreshold + "\n" + "Fixed size of formula: "
                + this.fixedSize + "\n" + "Minimal size of a single clause: " + this.minLength + "\n"
                + "Minimal size of a formula: " + this.minSize + "\n" + "Number of attempts for fixing a clause: "
                + this.clauseFixAttempts + "\n" + "Number of attempts for fixing a clause in tested formula: "
                + this.testClauseFixAttempts;
    }
}