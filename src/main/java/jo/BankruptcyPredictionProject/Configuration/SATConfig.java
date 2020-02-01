package jo.BankruptcyPredictionProject.Configuration;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "satConfig")
public class SATConfig {
    private static SATConfig instance;

    private static final String configFilePath = "./src/main/resources/satConfig.xml";

    private SATConfig() {}

    private SATConfig(int clauseLength, int clauseNumber, int numberOfIterations){
        this.clauseLength = clauseLength;
        this.clauseNumber = clauseNumber;
        this.numberOfIterations = numberOfIterations;
    }

    public static SATConfig getInstance() throws JAXBException {
        if (instance == null){
            File file = new File(configFilePath);
            JAXBContext jaxbContext = JAXBContext.newInstance(SATConfig.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            instance = (SATConfig) unmarshaller.unmarshal(file);
        }

        return instance;
    }

    @XmlElement(name = "clauseLength")
    private int clauseLength;

    @XmlElement(name = "clauseNumber")
    private int clauseNumber;

    @XmlElement(name = "numberOfIterations")
    private int numberOfIterations;

    public int getClauseLength(){
        return this.clauseLength;
    }

    public int getClauseNumber(){
        return this.clauseNumber;
    }

    public int getNumberOfIterations(){
        return this.numberOfIterations;
    }

    @Override
    public String toString(){
        return "Length of a single clause: " + this.clauseLength + "\n"
        + "Number of clauses: " + this.clauseNumber + "\n"
        + "Number of iterations: " + this.numberOfIterations;
    }
}