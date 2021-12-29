package jo.BankruptcyPredictionProject.Request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class AbstractFormulaGenerationRequest {

    @NotBlank(message = "inputFilePath field is required")
    private String inputFilePath;

    @NotBlank(message = "testDataFilePath field is required")
    private String testDataFilePath;
}
