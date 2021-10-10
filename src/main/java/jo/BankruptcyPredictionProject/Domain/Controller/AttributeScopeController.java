package jo.BankruptcyPredictionProject.Domain.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jo.BankruptcyPredictionProject.Domain.Service.AttributeScopeService;
import jo.BankruptcyPredictionProject.Request.LoadFromFileRequest;

@RequestMapping("scope")
@RestController
public class AttributeScopeController {
    
    @Autowired
    private AttributeScopeService attributeScopeService;

    @PostMapping("/load")
    public String loadFromFile(@RequestBody LoadFromFileRequest request) {
        int loadedScopes = this.attributeScopeService.loadScopesFromFile(request.getFilePath());

        return "Done reading from file: " + request.getFilePath() + ". Loaded scopes: " + loadedScopes;
    }
}