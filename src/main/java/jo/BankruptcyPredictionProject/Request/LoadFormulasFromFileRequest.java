package jo.BankruptcyPredictionProject.Request;

import jo.BankruptcyPredictionProject.Domain.Enumeration.FormulaType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoadFormulasFromFileRequest extends LoadFromFileRequest {
    
    private FormulaType type;
}