package jo.BankruptcyPredictionProject.Domain.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jo.BankruptcyPredictionProject.Domain.Service.PredictionService;
import jo.BankruptcyPredictionProject.Request.LoadFromFileRequest;

import javax.validation.Valid;

@RequestMapping("prediction")
@RestController
public class PredictionController {
    
    @Autowired
    private PredictionService predictionService;

    @PostMapping("/predict")
    public String predict(@Valid @RequestBody LoadFromFileRequest request) {
        this.predictionService.predict(true, request.getFilePath());
        
        return "Prediction finished. Check the logs for details";
    }
}