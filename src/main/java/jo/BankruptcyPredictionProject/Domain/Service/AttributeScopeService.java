package jo.BankruptcyPredictionProject.Domain.Service;

import java.util.List;

import jo.BankruptcyPredictionProject.Domain.Entity.AttributeScope;

public interface AttributeScopeService {
    
    public int loadScopesFromFile(String filePath);
    
    public List<AttributeScope> getAllApplicableScopes(String attrName, Double value);

    public boolean isScopeForAttribute(String attrName);

    public AttributeScope getScopeByDescription(String description);
}