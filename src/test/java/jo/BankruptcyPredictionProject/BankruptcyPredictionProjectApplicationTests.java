package jo.BankruptcyPredictionProject;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import jo.BankruptcyPredictionProject.Domain.ArffRepo;

@SpringBootTest
class BankruptcyPredictionProjectApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void givenFilePathWhenReadingDataThenCheckStatus(){
		ArffRepo arffRepo = ArffRepo.getInstance();
		arffRepo.setFilePath("E:\\Programowanie\\Magisterka\\Prediction Data\\3year.arff");
		arffRepo.readData(Boolean.TRUE);
	}

}
