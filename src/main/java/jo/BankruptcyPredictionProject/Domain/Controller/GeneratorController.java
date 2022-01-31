package jo.BankruptcyPredictionProject.Domain.Controller;

import jo.BankruptcyPredictionProject.Domain.Generator.RandomFormulaGenerator;
import jo.BankruptcyPredictionProject.Request.MultipleFormulasGenerationRequest;
import jo.BankruptcyPredictionProject.Request.SingleFormulaGenerationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.xml.bind.JAXBException;

@RequestMapping("generator")
@RestController
public class GeneratorController {

    @Autowired
    private RandomFormulaGenerator randomFormulaGenerator;

    @PostMapping("/generateSingle")
    public String generateSingleFormula(@Valid @RequestBody SingleFormulaGenerationRequest request) throws JAXBException {
        boolean result = this.randomFormulaGenerator.tryGenerateSingleFormulaFromFile(request.getInputFilePath(),
                request.getTestDataFilePath(),
                request.getFormulaNo());

        return "Formula " + (result ? "" : "not ") + "generated";
    }

    @PostMapping("/generateMultiple")
    public String generateMultipleFormulas(@Valid @RequestBody MultipleFormulasGenerationRequest request) throws JAXBException {
        int result = this.randomFormulaGenerator.tryGenerateSetOfRandomFormulas(request.getNumOfFormulas(),
                request.getReadFromFile(),
                request.getInputFilePath(),
                request.getTestDataFilePath());

        return "Formulas generated: " + result;
    }
}
