package jo.BankruptcyPredictionProject.Request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoadFromFileRequest {

    @NotBlank(message = "filePath field is required")
    private String filePath;
}