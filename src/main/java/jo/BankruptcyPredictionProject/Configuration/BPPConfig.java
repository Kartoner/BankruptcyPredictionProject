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

    private BPPConfig() throws JAXBException {}

    private BPPConfig(int clauseLength, 
     int clauseNumber, 
     int numberOfIterations, 
     boolean tryNegation, 
     boolean fixedLength, 
     double toleranceThreshold, 
     boolean fixedSize, 
     int minSize)
     throws JAXBException{
        this.clauseLength = clauseLength;
        this.clauseNumber = clauseNumber;
        this.numberOfIterations = numberOfIterations;
        this.tryNegation = tryNegation;
        this.fixedLength = fixedLength;
        this.toleranceThreshold = toleranceThreshold;
        this.fixedSize = fixedSize;
        this.minSize = minSize;
    }

    public static BPPConfig getInstance() throws JAXBException {
        if (instance == null){
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

    @XmlElement(name = "tryNegation")
    private boolean tryNegation;

    @XmlElement(name = "fixedLength")
    private boolean fixedLength;

    @XmlElement(name = "hardReset")
    private boolean hardReset;

    @XmlElement(name = "toleranceThreshold")
    private double toleranceThreshold;

    @XmlElement(name = "fixedSize")
    private boolean fixedSize;

    @XmlElement(name = "minSize")
    private int minSize;

    public int getClauseLength(){
        return this.clauseLength;
    }

    public int getClauseNumber(){
        return this.clauseNumber;
    }

    public int getNumberOfIterations(){
        return this.numberOfIterations;
    }

    public boolean isTryNegation() {
        return this.tryNegation;
    }

    public boolean isFixedLength() {
        return this.fixedLength;
    }

    public boolean isHardReset() {
        return this.hardReset;
    }

    public double getToleranceThreshold() {
        return this.toleranceThreshold;
    }

    public boolean getFixedSize() {
        return this.fixedSize;
    }

    public int getMinSize() {
        return this.minSize;
    }

    @Override
    public String toString(){
        return "Length of a single clause: " + this.clauseLength + "\n"
        + "Number of clauses: " + this.clauseNumber + "\n"
        + "Number of iterations: " + this.numberOfIterations + "\n"
        + "Try negation: " + this.tryNegation + "\n"
        + "Fixed length of clauses: " + this.fixedLength + "\n"
        + "Hard reset after failure: " + this.hardReset + "\n"
        + "Tolerance threshold: " + this.toleranceThreshold + "\n"
        + "Fixed size of formula: " + this.fixedSize + "\n"
        + "Minimal size of a single clause: " + this.minSize;
    }
}