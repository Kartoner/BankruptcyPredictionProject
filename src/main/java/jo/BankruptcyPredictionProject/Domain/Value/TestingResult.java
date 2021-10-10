package jo.BankruptcyPredictionProject.Domain.Value;

import jo.BankruptcyPredictionProject.Values.Formula;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestingResult {

    private boolean valid;

    private Integer failingElement;

    private Double successRatio;

    private Formula formula;

    private int failCounter;

    private Boolean addOrRemove;
}