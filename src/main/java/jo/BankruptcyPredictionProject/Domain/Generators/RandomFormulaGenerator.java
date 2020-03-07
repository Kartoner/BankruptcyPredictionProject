package jo.BankruptcyPredictionProject.Domain.Generators;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.xml.bind.JAXBException;

import jo.BankruptcyPredictionProject.Configuration.BPPConfig;
import jo.BankruptcyPredictionProject.Domain.Repositories.ArffRepo;
import jo.BankruptcyPredictionProject.Domain.Repositories.FormulaRepo;
import jo.BankruptcyPredictionProject.Values.Clause;
import jo.BankruptcyPredictionProject.Values.Formula;
import jo.BankruptcyPredictionProject.Values.Literal;
import jo.BankruptcyPredictionProject.Values.TestingResult;
import jo.BankruptcyPredictionProject.Values.Interface.FormulaElement;
import weka.core.Instance;
import weka.core.Instances;

public class RandomFormulaGenerator {
    private static RandomFormulaGenerator instance;

    private final BPPConfig bppConfig;
    private final FormulaRepo formulaRepo = FormulaRepo.getInstance();
    private final ArffRepo arffRepo = ArffRepo.getInstance();

    private Formula generatedFormula;
    private String testDataFilePath;

    private RandomFormulaGenerator() throws JAXBException {
        bppConfig = BPPConfig.getInstance();
        generatedFormula = new Formula();
    }

    public static RandomFormulaGenerator getInstance() throws JAXBException {
        if (instance == null) {
            instance = new RandomFormulaGenerator();
        }

        return instance;
    }

    public void tryGenerateSetOfRandomFormulas(int numOfFormulas) throws JAXBException {
        String oldPath = this.arffRepo.getFilePath();
        Instances oldData = this.arffRepo.getData();
        this.arffRepo.setFilePath(this.testDataFilePath);
        this.arffRepo.loadData(Boolean.TRUE);

        int generatedFormulaCounter = 0;
        for (int i = 0; i < numOfFormulas; i++){
            System.out.println("Formula no.: " + i);
            boolean generationResult = generateFormula();

            if (generationResult){
                generatedFormulaCounter++;
                this.formulaRepo.writeNewFormula(this.generatedFormula, null);
            }
            System.out.println("---------------------------");
        }

        System.out.println("Formulas generated: " + generatedFormulaCounter);

        this.arffRepo.setFilePath(oldPath);
        this.arffRepo.setData(oldData);
        this.formulaRepo.refreshAssessmentFormulas();
    }

    public boolean generateFormula() throws JAXBException {
        boolean isFormulaGenerated = Boolean.FALSE;
        int failCounter = 0;

        System.out.println("Generating...");
        System.out.println("Iteration no.: " + failCounter);
        generateNewFormula();

        TestingResult testingResult = testFormula();

        if (!testingResult.isValid()){
            failCounter++;

            while (failCounter < this.bppConfig.getNumberOfIterations()){
                System.out.println("Iteration no.: " + failCounter);

                if (this.bppConfig.isHardReset()){
                    generateNewFormula();
                } else {
                    fixFormula(testingResult.getFailingElement());
                }

                testingResult = this.testFormula();

                if (testingResult.isValid()){
                    return Boolean.TRUE;
                }

                failCounter++;
            }
        } else {
            isFormulaGenerated = Boolean.TRUE;
        }

        return isFormulaGenerated;
    }

    private void generateNewFormula() throws JAXBException {
        this.generatedFormula = new Formula();
        Random r = new Random();

        if (this.bppConfig.isFixedSize()){
            this.fillRandomFormula(this.bppConfig.getClauseNumber(), r);
        } else {
            int randomSize = r.nextInt(this.bppConfig.getClauseNumber() - 1) + 1;

            this.fillRandomFormula(randomSize, r);
        }
    }

    private void fixFormula(Integer failingElementIndex) throws JAXBException {
        Random r = new Random();

        FormulaElement failingElement = this.generatedFormula.getElements().get(failingElementIndex);

        if (failingElement instanceof Literal){
            
            this.generatedFormula.getElements().set(failingElementIndex, getValidElement(this.getRandomLiteral(r), r));
            
        } else if (failingElement instanceof Clause){
            Clause failingClause = (Clause) failingElement;
            int randomLiteralIndex = r.nextInt(failingClause.getLiterals().size());

            failingClause.getLiterals().set(randomLiteralIndex, this.getRandomLiteral(r));
            
            this.generatedFormula.getElements().set(failingElementIndex, getValidElement(failingClause, r));
        }
    }

    private void fillRandomFormula(int formulaSize, Random r) throws JAXBException {
        for (int i = 0; i < formulaSize; i++){
            if (this.bppConfig.isFixedLength()) {
                if (this.bppConfig.getClauseLength() == 1){
                    this.generatedFormula.attach(getValidElement(this.getRandomLiteral(r), r));
                } else {
                    Clause randomClause = fillRandomClause(this.bppConfig.getClauseLength(), r);

                    this.generatedFormula.attach(getValidElement(randomClause, r));
                }
            } else {
                int randomLength = r.nextInt(this.bppConfig.getClauseLength() - this.bppConfig.getMinLength() + 1) + this.bppConfig.getMinLength();

                if (randomLength == 1){
                    this.generatedFormula.attach(getValidElement(this.getRandomLiteral(r), r));
                } else {
                    Clause randomClause = fillRandomClause(randomLength, r);

                    this.generatedFormula.attach(getValidElement(randomClause, r));
                }
            }
        }
    }

    private Clause fillRandomClause(int clauseLength, Random r){
        Clause randomClause = new Clause();

        for (int i = 0; i < clauseLength; i++){
            Literal randomLiteral = this.getRandomLiteral(r);

            if (randomClause.literalAlreadyPresent(randomLiteral)){
                while (randomClause.literalAlreadyPresent(randomLiteral)){
                    randomLiteral = this.getRandomLiteral(r);
                }
            } 

            randomClause.attach(randomLiteral);
        }

        return randomClause;
    }

    private Literal getRandomLiteral(Random r){
        boolean isSat = r.nextBoolean();
        List<Formula> formulas = this.formulaRepo.getFormulas(isSat);
        Formula randomFormula = formulas.get(r.nextInt(formulas.size()));
        FormulaElement randomElement = randomFormula.getElements().get(r.nextInt(randomFormula.getFormulaSize()));

        if (randomElement instanceof Literal) {
            Literal randomLiteral = (Literal) randomElement;

            if (!isSat){
                randomLiteral.setIsNegative(!randomLiteral.getIsNegative());
            }

            return randomLiteral;
        } else if (randomElement instanceof Clause){
            Clause randomClause = (Clause) randomElement;
            Literal randomLiteral = randomClause.getLiterals().get(r.nextInt(randomClause.getLiterals().size()));

            if (!isSat){
                randomLiteral.setIsNegative(!randomLiteral.getIsNegative());
            }
            
            return randomLiteral;
        }

        return null;
    }

    private FormulaElement getValidElement(FormulaElement element, Random r) throws JAXBException {
        if (element instanceof Literal){
            if (!validateElement(element)){
                while (!validateElement(element)){
                    element = getRandomLiteral(r);
                }
            }
        } else if (element instanceof Clause){
            Clause clause = (Clause) element;
            if (!validateElement(clause)){
                int fixCounter = 1;
                while (!validateElement(clause)){
                    if (fixCounter < this.bppConfig.getClauseFixAttempts()){
                        int switchIndex = r.nextInt(clause.getLiterals().size());
                        clause.getLiterals().set(switchIndex, getRandomLiteral(r));
                        element = clause;
                        fixCounter++;
                    } else {
                        element = fillRandomClause(clause.getLiterals().size(), r);
                        fixCounter = 0;
                    }
                }
            }
        }

        return element;
    }

    private boolean validateElement(FormulaElement randomElement) throws JAXBException {
        int successCounter = 0;

        if (randomElement instanceof Literal){
            successCounter += validateLiteral((Literal) randomElement);
        } else if (randomElement instanceof Clause){
            successCounter += validateClause((Clause) randomElement);
        }
        System.out.println(successCounter);

        double successPercentage = (successCounter * 1.0 / (this.formulaRepo.getFormulas(true).size() + this.formulaRepo.getFormulas(false).size()) * 1.0) * 1.0;
        System.out.println(successPercentage);

        
        return successPercentage >= BPPConfig.getInstance().getElementToleranceThreshold();
    }

    private int validateLiteral(Literal randomLiteral){
        int successCounter = 0;

        successCounter += validateLiteralWithSet(randomLiteral, this.formulaRepo.getFormulas(true), true);
        successCounter += validateLiteralWithSet(randomLiteral, this.formulaRepo.getFormulas(false), false);

        return successCounter;
    }

    private int validateLiteralWithSet(Literal randomLiteral, List<Formula> formulas, boolean isSat){
        int successCounter = 0;

        for (Formula formula : formulas){
            for (FormulaElement element : formula.getElements()){
                if (element instanceof Literal){
                    Literal literal = (Literal) element;

                    if (literal.getDescription().equals(randomLiteral.getDescription())
                     && ((literal.getIsNegative() == randomLiteral.getIsNegative()) ^ isSat)){
                        successCounter++;
                        break;
                    }
                } else if (element instanceof Clause){
                    Clause clause = (Clause) element;
                    boolean isValid = false;

                    for (Literal literal : clause.getLiterals()){
                        if (literal.getDescription().equals(randomLiteral.getDescription())
                         && ((literal.getIsNegative() == randomLiteral.getIsNegative()) ^ isSat)){
                            successCounter++;
                            isValid = true;
                            break;
                        }
                    }
                    if (isValid){
                        break;
                    }
                }
            }
        }

        return successCounter;
    }

    private int validateClause(Clause randomClause){
        int successCounter = 0;

        successCounter += validateClauseWithSet(randomClause, this.formulaRepo.getFormulas(true), true);
        successCounter += validateClauseWithSet(randomClause, this.formulaRepo.getFormulas(false), false);

        return successCounter;
    }

    private int validateClauseWithSet(Clause randomClause, List<Formula> formulas, boolean isSat){
        int successCounter = 0;

        for (Formula formula : formulas){
            for (FormulaElement element : formula.getElements()){
                boolean isValid = false;
                for (Literal randomLiteral : randomClause.getLiterals()){
                    if (element instanceof Literal){
                        Literal literal = (Literal) element;

                        if (literal.getDescription().equals(randomLiteral.getDescription())
                         && ((literal.getIsNegative() == randomLiteral.getIsNegative()) ^ isSat)){
                            successCounter++;
                            isValid = true;
                            break;
                        }
                    } else if (element instanceof Clause){
                        Clause clause = (Clause) element;

                        for (Literal literal : clause.getLiterals()){
                            if (literal.getDescription().equals(randomLiteral.getDescription())
                             && ((literal.getIsNegative() == randomLiteral.getIsNegative()) ^ isSat)){
                                successCounter++;
                                isValid = true;
                                break;
                            }
                        }
                        if (isValid){
                            break;
                        }
                    }
                }
                if (isValid){
                    break;
                }
            }
        }

        return successCounter;
    }

    private TestingResult testFormula(){
        TestingResult testingResult;

        boolean isValid = false;
        Integer failingElement = null;
        int successCounter = 0;
        int bankrupt = 0;
        int bankruptMatched = 0;
        int notBankrupt = 0;
        int notBankruptMatched = 0;

        Map<Integer, Integer> failingElements = new HashMap<>();

        for (int i = 0; i < this.generatedFormula.getFormulaSize(); i++){
            failingElements.put(i, 0);
        }

        Instances data = arffRepo.getData();

        for (int i = 0; i < data.size(); i++) {
            List<List<Boolean>> subResult = new LinkedList<>();
            for (int j = 0; j < this.generatedFormula.getFormulaSize(); j++){
                subResult.add(new LinkedList<Boolean>());
                FormulaElement formulaElement = this.generatedFormula.getElements().get(j);
                if (formulaElement instanceof Literal){
                    subResult.get(j).add(Boolean.FALSE);
                } else if (formulaElement instanceof Clause){
                    Clause clause = (Clause) formulaElement;
                    for (int k = 0; k < clause.getLiterals().size(); k++){
                        subResult.get(j).add(Boolean.FALSE);
                    }
                }
            }

            List<Boolean> result = new LinkedList<>();

            Instance record = data.get(i);
            boolean recordClass = record.classValue() == 1.0 ? true : false;

            if (recordClass){
                bankrupt++;
            } else {
                notBankrupt++;
            }

            for (int j = 1; j < record.numAttributes(); j++){
                String attrName = record.attribute(j).name();
                Double value = record.value(j);

                for (int k = 0; k < generatedFormula.getElements().size(); k++){
                    FormulaElement formulaElement = generatedFormula.getElements().get(k);

                    if (formulaElement instanceof Literal){
                        Literal literal = (Literal) formulaElement;
                        if (literal.getScope() != null && literal.getScope().getAttrName().equals(attrName) && literal.getScope().isApplicable(value)){
                            subResult.get(k).set(0, Boolean.TRUE && !literal.getIsNegative());
                        }
                    } else if (formulaElement instanceof Clause){
                        Clause clause = (Clause) formulaElement;

                        for (int l = 0; l < clause.getLiterals().size(); l++){
                            Literal literal = clause.getLiterals().get(l);
                            if (literal.getScope() != null && literal.getScope().getAttrName().equals(attrName) && literal.getScope().isApplicable(value)){
                                subResult.get(k).set(l, Boolean.TRUE && !literal.getIsNegative());
                            }
                        }
                    }
                }
            }

            for (int j = 0; j < subResult.size(); j++){
                boolean evalSubResult = Boolean.FALSE;

                for (int k = 0; k < subResult.get(j).size(); k++){
                    evalSubResult = evalSubResult || subResult.get(j).get(k);
                }

                result.add(evalSubResult);

                if (evalSubResult ^ recordClass){
                    failingElements.put(result.size() - 1, failingElements.get(result.size() - 1) + 1);
                }
            }

            boolean evalResult = Boolean.TRUE;

            for (int j = 0; j < result.size(); j++){
                evalResult = evalResult && result.get(j);
            }

            evalResult = !(evalResult ^ recordClass);

            if (evalResult) {
                successCounter++;
                if (recordClass){
                    bankruptMatched++;
                } else {
                    notBankruptMatched++;
                }
            }
            
        }

        System.out.println("Formula: ");
        System.out.println(this.generatedFormula.toExtString());
        System.out.println("Data size: " + data.size());
        System.out.println("Matched records: " + successCounter);
        System.out.println("Bankrupt matched: " + bankruptMatched + " / " + bankrupt);
        System.out.println("Not bankrupt matched: " + notBankruptMatched + " / " + notBankrupt);
        System.out.println("Success ratio: " + (successCounter * 1.0 / data.size() * 1.0) * 1.0);

        if ((successCounter * 1.0 / data.size() * 1.0) * 1.0 >= this.bppConfig.getFormulaToleranceThreshold()){
            isValid = true;
        }

        if (!isValid){
            Map.Entry<Integer, Integer> maxEntry = null;

            for (Map.Entry<Integer, Integer> entry : failingElements.entrySet()){
                if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0){
                    maxEntry = entry;
                }
            }

            failingElement = maxEntry.getKey();
        }

        testingResult = new TestingResult(isValid, failingElement);
        return testingResult;
    }

    public void setTestDataFilePath(String filePath){
        this.testDataFilePath = filePath;
    }

}