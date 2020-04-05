package jo.BankruptcyPredictionProject.Domain.Repositories;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jo.BankruptcyPredictionProject.Values.AttributeScope;

public class AttributeScopeRepo {
    private static AttributeScopeRepo instance;

    private final String scopesFilePath = "./src/main/resources/attributeScopes.txt";

    private Map<String, List<AttributeScope>> scopes;

    private AttributeScopeRepo(){
        this.scopes = new HashMap<>();
    }

    public static AttributeScopeRepo getInstance(){
        if (instance == null){
            instance = new AttributeScopeRepo();
        }

        return instance;
    }

    public void loadData(){
        int loadedScopes = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(scopesFilePath))) {
            String line = "";

            AttributeScope attrScope;
            String attrName;
            Double rangeFrom;
            Double rangeTo;

            while (line != null){
                line = br.readLine();

                if (line != null){
                    String[] lineSplit = line.split(" ");

                    attrName = lineSplit[0];

                    if (lineSplit[1].equals("NA")){
                        rangeFrom = null;
                    } else {
                        rangeFrom = Double.valueOf(lineSplit[1]);
                    }

                    if (lineSplit[2].equals("NA")){
                        rangeTo = null;
                    } else {
                        rangeTo = Double.valueOf(lineSplit[2]);                        
                    }

                    attrScope = new AttributeScope(attrName, rangeFrom, rangeTo);

                    if (!this.scopes.containsKey(attrName)){
                        List<AttributeScope> scopeList = new ArrayList<>();
                        scopeList.add(attrScope);
                        this.scopes.put(attrName, scopeList);
                    } else {
                        List<AttributeScope> scopeList = this.scopes.get(attrName);
                        scopeList.add(attrScope);
                        this.scopes.put(attrName, scopeList);
                    }

                    loadedScopes++;
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("File: " + scopesFilePath + " not found!");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Reading from file: " + scopesFilePath + " failed!");
            e.printStackTrace();
        }

        System.out.println("Done reading from file: " + scopesFilePath + ". Loaded scopes: " + loadedScopes);
    }

    public List<AttributeScope> getAllApplicableScopes(String attrName, Double value){
        List<AttributeScope> applicableScopes = new ArrayList<>();
        if (value == null){
            return applicableScopes;
        }
        if (this.scopes.containsKey(attrName)){
            List<AttributeScope> scopesList = this.scopes.get(attrName);

            for (AttributeScope scope : scopesList){
                if (scope.isApplicable(value)){
                    applicableScopes.add(scope);
                }
            }
        }

        return applicableScopes;
    }

    public boolean isScopeForAttribute(String attrName){
        return this.scopes.containsKey(attrName);
    }

    public AttributeScope getScopeByDescription(String description){
        for (Map.Entry<String, List<AttributeScope>> entry : this.scopes.entrySet()){
            for (AttributeScope scope : entry.getValue()){
                if (scope.toString().equals(description)){
                    return scope;
                }
            }
        }

        return null;
    }
}