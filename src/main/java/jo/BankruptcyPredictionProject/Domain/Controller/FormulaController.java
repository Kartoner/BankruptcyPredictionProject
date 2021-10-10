package jo.BankruptcyPredictionProject.Domain.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jo.BankruptcyPredictionProject.Domain.Service.FormulaService;
import jo.BankruptcyPredictionProject.Request.LoadFormulasFromFileRequest;
import jo.BankruptcyPredictionProject.Request.LoadFromFileWithScopeRequest;

@RequestMapping("formula")
@RestController
public class FormulaController {
    
    @Autowired
    private FormulaService formulaService;

    @PostMapping("/load")
    public String loadFromFile(@RequestBody LoadFormulasFromFileRequest request) {
        int loadedFormulas = this.formulaService.loadFormulasFromFile(request.getFilePath(), request.getType());

        return "Done reading from file: " + request.getFilePath() + ". Loaded formulas: " + loadedFormulas;
    }

    @PostMapping("/load/arff")
    public String loadFromArffFile(@RequestBody LoadFromFileWithScopeRequest request) {
        int loadedFormulas = this.formulaService.loadFormulasFromArffFile(request.getFilePath(), request.getStart(), request.getEnd());

        return "Done reading from file: " + request.getFilePath() + ". Loaded formulas: " + loadedFormulas;
    }
}