package jo.BankruptcyPredictionProject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import jo.BankruptcyPredictionProject.Utility.BPPLogger;

@SpringBootApplication
public class BankruptcyPredictionProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankruptcyPredictionProjectApplication.class, args);
        BPPLogger.clear();
    }

}
