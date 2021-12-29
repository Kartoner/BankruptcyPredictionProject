package jo.BankruptcyPredictionProject.Request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SingleFormulaGenerationRequest extends AbstractFormulaGenerationRequest {

    private Integer formulaNo;
}
