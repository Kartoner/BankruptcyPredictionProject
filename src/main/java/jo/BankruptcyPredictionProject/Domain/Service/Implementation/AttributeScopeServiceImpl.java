package jo.BankruptcyPredictionProject.Domain.Service.Implementation;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jo.BankruptcyPredictionProject.Domain.Entity.AttributeScope;
import jo.BankruptcyPredictionProject.Domain.Repository.AttributeScopeRepository;
import jo.BankruptcyPredictionProject.Domain.Service.AttributeScopeService;
import jo.BankruptcyPredictionProject.Utility.BPPLogger;

@Service
public class AttributeScopeServiceImpl implements AttributeScopeService {
    
    @Autowired
    private AttributeScopeRepository attributeScopeRepository;

    @Override
    @Transactional
    public int loadScopesFromFile(String filePath) {
        List<AttributeScope> scopes = this.attributeScopeRepository.findAll();

        int loadedScopes = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = "";

            AttributeScope attrScope;
            String attrName;
            Double rangeFrom;
            Double rangeTo;

            while (line != null) {
                line = br.readLine();

                if (line != null) {
                    String[] lineSplit = line.split(" ");

                    attrName = lineSplit[0];

                    if (lineSplit[1].equals("NA")) {
                        rangeFrom = null;
                    } else {
                        rangeFrom = Double.valueOf(lineSplit[1]);
                    }

                    if (lineSplit[2].equals("NA")) {
                        rangeTo = null;
                    } else {
                        rangeTo = Double.valueOf(lineSplit[2]);
                    }

                    attrScope = new AttributeScope();
                    attrScope.setAttrName(attrName);
                    attrScope.setRangeFrom(rangeFrom);
                    attrScope.setRangeTo(rangeTo);

                    final AttributeScope newScope = attrScope;

                    if (!scopes.stream().anyMatch(scope -> scope.toString().equals(newScope.toString()))) {
                        this.attributeScopeRepository.save(attrScope);
                        scopes.add(attrScope);

                        loadedScopes++;
                    }
                }
            }
        } catch (FileNotFoundException e) {
            BPPLogger.log("File: " + filePath + " not found!");
            e.printStackTrace();
        } catch (IOException e) {
            BPPLogger.log("Reading from file: " + filePath + " failed!");
            e.printStackTrace();
        }

        BPPLogger.log("Done reading from file: " + filePath + ". Loaded scopes: " + loadedScopes);
        return loadedScopes;
    }

    @Override
    @Transactional
    public List<AttributeScope> getAllApplicableScopes(String attrName, Double value) {
        if (value == null) {
            return new ArrayList<>();
        }

        List<AttributeScope> scopes = this.attributeScopeRepository.findByAttrName(attrName);

        return scopes.stream().filter(scope -> scope.isApplicable(value)).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public boolean isScopeForAttribute(String attrName) {
        List<AttributeScope> scopes = this.attributeScopeRepository.findByAttrName(attrName);

        return scopes.size() > 0;
    }

    @Override
    @Transactional
    public AttributeScope getScopeByDescription(String description) {
        List<AttributeScope> scopes = this.attributeScopeRepository.findAll();

        return scopes.stream().filter(scope -> scope.toString().equals(description)).collect(Collectors.toList()).get(0);
    }
}