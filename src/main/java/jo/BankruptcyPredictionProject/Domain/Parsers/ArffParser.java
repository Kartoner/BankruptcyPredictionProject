package jo.BankruptcyPredictionProject.Domain.Parsers;

import java.util.ArrayList;
import java.util.List;

import jo.BankruptcyPredictionProject.Domain.Repositories.ArffRepo;
import jo.BankruptcyPredictionProject.Domain.Repositories.AttributeScopeRepo;
import jo.BankruptcyPredictionProject.Domain.Repositories.FormulaRepo;
import jo.BankruptcyPredictionProject.Values.AttributeScope;
import jo.BankruptcyPredictionProject.Values.Formula;
import jo.BankruptcyPredictionProject.Values.Literal;
import weka.core.Instance;
import weka.core.Instances;

public class ArffParser{
    private static ArffParser instance;

    private final String outputFilePath = "./src/main/resources/outputFormula.txt";

    private final ArffRepo arffRepo = ArffRepo.getInstance();
    private final AttributeScopeRepo scopeRepo = AttributeScopeRepo.getInstance();
    private final FormulaRepo formulaRepo = FormulaRepo.getInstance();

    private List<Formula> parsedFormulas;

    private ArffParser(){
        this.parsedFormulas = new ArrayList<>();
        this.scopeRepo.loadData();
    }

    public static ArffParser getInstance(){
        if (instance == null){
            instance =  new ArffParser();
        }

        return instance;
    }

    private void clear(){
        this.parsedFormulas = new ArrayList<>();
    }

    public void processRecord(Instance record){
        Formula newFormula = new Formula();
        for (int i = 1; i < record.numAttributes(); i++){
            String attrName = record.attribute(i).name();
            if (this.scopeRepo.isScopeForAttribute(attrName)){
                Double value = record.value(i);
                Literal newLiteral = null;
                AttributeScope scope = this.scopeRepo.getApplicableScope(attrName, value);
                String description = scope.toString();
                Integer existingVariableSymbol = this.formulaRepo.getLiteralSymbolIfExists(description);

                if (existingVariableSymbol != null){
                    newLiteral = new Literal(existingVariableSymbol, description, false);
                } else {
                    boolean additionSuccessful = this.formulaRepo.addNewVariable(description, this.formulaRepo.getCurrentSymbol());

                    if (additionSuccessful){
                        newLiteral = new Literal(this.formulaRepo.getCurrentSymbol(), description, false);
                        this.formulaRepo.incrementCurrentSymbol();
                    }
                }

                if (newLiteral != null){
                    newFormula.attach(newLiteral);
                }
            }
        }

        if (!newFormula.getElements().isEmpty()){
            this.parsedFormulas.add(newFormula);
        }
    }

    public void processAllRecords(boolean flushToRepo){
        clear();
        Instances records = this.arffRepo.getData();

        for (int i = 0; i < records.numInstances(); i++){
            Instance record = records.get(i);
            processRecord(record);
        }
    }

    public List<Formula> getParsedFormulas(){
        return this.parsedFormulas;
    }
}