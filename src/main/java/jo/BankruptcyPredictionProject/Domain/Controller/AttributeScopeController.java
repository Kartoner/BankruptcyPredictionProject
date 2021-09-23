package jo.BankruptcyPredictionProject.Domain.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jo.BankruptcyPredictionProject.Domain.Service.AttributeScopeService;

@RequestMapping("scope")
@RestController
public class AttributeScopeController {
    
    @Autowired
    private AttributeScopeService attributeScopeService;

    @PostMapping("/load")
    public String loadFromFile(@RequestBody String filePath) {
        int loadedScopes = this.attributeScopeService.loadScopesFromFile(filePath);

        return "Done reading from file: " + filePath + ". Loaded scopes: " + loadedScopes;
    }
}