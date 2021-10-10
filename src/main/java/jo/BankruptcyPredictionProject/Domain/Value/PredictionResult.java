package jo.BankruptcyPredictionProject.Domain.Value;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PredictionResult {
    
    private Boolean predictionCorrect;

    private Boolean expected;

    private Boolean received;
}