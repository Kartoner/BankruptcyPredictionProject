package jo.BankruptcyPredictionProject.Request;

import jo.BankruptcyPredictionProject.Domain.Enumeration.FormulaType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoadFormulasFromFileRequest extends LoadFromFileRequest {

    @NotNull(message = "type field is required")
    private FormulaType type;
}