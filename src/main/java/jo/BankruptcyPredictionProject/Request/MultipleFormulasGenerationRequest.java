package jo.BankruptcyPredictionProject.Request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MultipleFormulasGenerationRequest extends AbstractFormulaGenerationRequest {

    private String inputFilePath;

    @NotNull(message = "numOfFormulas field is required")
    @Min(value = 1, message = "Number of formulas should be at least 1")
    private Integer numOfFormulas;

    @NotNull(message = "readFromFile field is required")
    private Boolean readFromFile;
}
