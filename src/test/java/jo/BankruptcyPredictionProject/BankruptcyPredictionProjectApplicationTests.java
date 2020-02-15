package jo.BankruptcyPredictionProject;

import javax.xml.bind.JAXBException;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import jo.BankruptcyPredictionProject.Configuration.BPPConfig;
import jo.BankruptcyPredictionProject.Domain.Parsers.ArffParser;
import jo.BankruptcyPredictionProject.Domain.Repositories.*;

@SpringBootTest
class BankruptcyPredictionProjectApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void givenFilePathWhenReadingDataThenCheckStatus() {
		ArffRepo arffRepo = ArffRepo.getInstance();
		arffRepo.setFilePath("E:\\Programowanie\\Magisterka\\Prediction Data\\3year.arff");
		arffRepo.loadData(Boolean.TRUE);
	}

	@Test
	void readingFormulasTest() {
		FormulaRepo formulaRepo = FormulaRepo.getInstance();
		formulaRepo.loadData();
	}

	@Test
	void readingScopesTest() {
		AttributeScopeRepo scopeRepo = AttributeScopeRepo.getInstance();
		scopeRepo.loadData();
	}

	@Test
	void readingBPPConfigTest() throws JAXBException {
		BPPConfig bppConfig = BPPConfig.getInstance();
		System.out.println(bppConfig);
	}

	/*@Test
	void readTestData(){
		ArffRepo arffRepo = ArffRepo.getInstance();
		arffRepo.setFilePath("E:\\Programowanie\\Magisterka\\Prediction Data\\sample.arff");
		arffRepo.loadData(Boolean.TRUE);
		FormulaRepo formulaRepo = FormulaRepo.getInstance();
		formulaRepo.loadData();
		ArffParser arffParser = ArffParser.getInstance();
		arffParser.processAllRecords(Boolean.TRUE);
	} */
}
