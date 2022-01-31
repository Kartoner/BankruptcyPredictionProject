package jo.BankruptcyPredictionProject.Request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoadFromFileWithScopeRequest extends LoadFromFileRequest {

    private Integer start;

    private Integer end;
}