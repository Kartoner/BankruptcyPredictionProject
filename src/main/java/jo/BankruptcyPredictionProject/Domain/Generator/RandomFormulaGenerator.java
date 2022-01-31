package jo.BankruptcyPredictionProject.Domain.Generator;

import javax.xml.bind.JAXBException;

public interface RandomFormulaGenerator {

    int tryGenerateSetOfRandomFormulas(int numOfFormulas, boolean readFromFile, String inputFilePath, String testDataFilePath) throws JAXBException;

    boolean tryGenerateSingleFormulaFromFile(String inputFilePath, String testDataFilePath, Integer formulaNo) throws JAXBException;
}
