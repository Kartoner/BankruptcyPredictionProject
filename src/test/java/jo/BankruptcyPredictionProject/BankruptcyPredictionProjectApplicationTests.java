package jo.BankruptcyPredictionProject;

import javax.xml.bind.JAXBException;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import jo.BankruptcyPredictionProject.Configuration.BPPConfig;
import jo.BankruptcyPredictionProject.Domain.Generators.RandomFormulaGenerator;
import jo.BankruptcyPredictionProject.Domain.Parsers.ArffParser;
import jo.BankruptcyPredictionProject.Domain.Repositories.*;
import jo.BankruptcyPredictionProject.Domain.Services.PredictionService;
import jo.BankruptcyPredictionProject.Utility.BPPLogger;

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

	/*
	 * @Test void readTestData(){ ArffRepo arffRepo = ArffRepo.getInstance();
	 * arffRepo.
	 * setFilePath("E:\\Programowanie\\Magisterka\\Prediction Data\\sample.arff");
	 * arffRepo.loadData(Boolean.TRUE); FormulaRepo formulaRepo =
	 * FormulaRepo.getInstance(); formulaRepo.loadData(); ArffParser arffParser =
	 * ArffParser.getInstance(); arffParser.processAllRecords(Boolean.TRUE); }
	 */

	@Test
	void testLog() {
		BPPLogger.log("Test\nTest");
		BPPLogger.log("Test2");
	}

	@Test
	void testClearLog() {
		BPPLogger.clear();
	}

	@Test
	void RFGSetGenerationTest() throws JAXBException {
		BPPLogger.clear();
		ArffRepo arffRepo = ArffRepo.getInstance();
		arffRepo.setFilePath("E:\\Programowanie\\Magisterka\\Prediction Data\\sample.arff");
		arffRepo.loadData(Boolean.TRUE);
		AttributeScopeRepo scopeRepo = AttributeScopeRepo.getInstance();
		scopeRepo.loadData();
		FormulaRepo formulaRepo = FormulaRepo.getInstance();
		formulaRepo.loadData();
		RandomFormulaGenerator RFG = RandomFormulaGenerator.getInstance();
		RFG.setTestDataFilePath("E:\\Programowanie\\Magisterka\\Prediction Data\\test.arff");
		RFG.tryGenerateSetOfRandomFormulas(200, Boolean.FALSE);
	}

	@Test
	void RFGSingleFormulaTest() throws JAXBException {
		BPPLogger.clear();
		ArffRepo arffRepo = ArffRepo.getInstance();
		arffRepo.setFilePath("E:\\Programowanie\\Magisterka\\Prediction Data\\sample.arff");
		arffRepo.loadData(Boolean.TRUE);
		AttributeScopeRepo scopeRepo = AttributeScopeRepo.getInstance();
		scopeRepo.loadData();
		FormulaRepo formulaRepo = FormulaRepo.getInstance();
		formulaRepo.loadData();
		RandomFormulaGenerator RFG = RandomFormulaGenerator.getInstance();
		RFG.setTestDataFilePath("E:\\Programowanie\\Magisterka\\Prediction Data\\test.arff");
		RFG.tryGenerateSetOfRandomFormulas(1, Boolean.TRUE);
	}

	@Test
	void PredictionTest() throws JAXBException {
		BPPLogger.clear();
		ArffRepo arffRepo = ArffRepo.getInstance();
		arffRepo.setFilePath("E:\\Programowanie\\Magisterka\\Prediction Data\\sample.arff");
		arffRepo.loadData(Boolean.TRUE);
		AttributeScopeRepo scopeRepo = AttributeScopeRepo.getInstance();
		scopeRepo.loadData();
		FormulaRepo formulaRepo = FormulaRepo.getInstance();
		formulaRepo.refreshAssessmentFormulas();
		PredictionService predictionService = PredictionService.getInstance();
		predictionService.setDataFilePath("E:\\Programowanie\\Magisterka\\Prediction Data\\5year.arff");
		predictionService.predict(true);
	}
}
