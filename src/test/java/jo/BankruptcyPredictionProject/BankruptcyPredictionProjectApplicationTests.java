package jo.BankruptcyPredictionProject;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import jo.BankruptcyPredictionProject.Domain.Repositories.*;

@SpringBootTest
class BankruptcyPredictionProjectApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void givenFilePathWhenReadingDataThenCheckStatus(){
		ArffRepo arffRepo = ArffRepo.getInstance();
		arffRepo.setFilePath("E:\\Programowanie\\Magisterka\\Prediction Data\\3year.arff");
		arffRepo.loadData(Boolean.TRUE);
	}

	@Test
	void readingFormulasTest(){
		FormulaRepo formulaRepo = FormulaRepo.getInstance();
		formulaRepo.loadData();
	}

}
