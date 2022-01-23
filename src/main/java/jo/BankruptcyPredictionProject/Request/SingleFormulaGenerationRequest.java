package jo.BankruptcyPredictionProject.Request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SingleFormulaGenerationRequest extends AbstractFormulaGenerationRequest {

    @NotBlank(message = "inputFilePath field is required")
    private String inputFilePath;

    private Integer formulaNo;
}
