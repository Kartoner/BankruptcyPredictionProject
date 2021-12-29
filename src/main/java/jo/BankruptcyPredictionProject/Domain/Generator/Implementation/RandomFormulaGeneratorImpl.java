package jo.BankruptcyPredictionProject.Domain.Generator.Implementation;

import jo.BankruptcyPredictionProject.Configuration.BPPConfig;
import jo.BankruptcyPredictionProject.Domain.Entity.Clause;
import jo.BankruptcyPredictionProject.Domain.Entity.Formula;
import jo.BankruptcyPredictionProject.Domain.Entity.Literal;
import jo.BankruptcyPredictionProject.Domain.Enumeration.ClauseType;
import jo.BankruptcyPredictionProject.Domain.Enumeration.FormulaType;
import jo.BankruptcyPredictionProject.Domain.Generator.RandomFormulaGenerator;
import jo.BankruptcyPredictionProject.Domain.Service.FormulaService;
import jo.BankruptcyPredictionProject.Domain.Value.TestingResult;
import jo.BankruptcyPredictionProject.Utility.ArffLoader;
import jo.BankruptcyPredictionProject.Utility.BPPLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import weka.core.Instance;
import weka.core.Instances;

import javax.xml.bind.JAXBException;
import java.util.*;

@Component
public class RandomFormulaGeneratorImpl implements RandomFormulaGenerator {

    private final BPPConfig bppConfig;

    private final FormulaService formulaService;

    private final ArffLoader arffRepo;

    private Formula generatedFormula = new Formula();

    @Autowired
    public RandomFormulaGeneratorImpl(FormulaService formulaService, ArffLoader arffRepo) throws JAXBException {
        bppConfig = BPPConfig.getInstance();
        this.formulaService = formulaService;
        this.arffRepo = arffRepo;
    }

    public int tryGenerateSetOfRandomFormulas(int numOfFormulas, boolean readFromFile, String inputFilePath, String testDataFilePath) throws JAXBException {
        String oldPath = this.arffRepo.getFilePath();
        Instances oldData = this.arffRepo.getData();
        this.arffRepo.setFilePath(testDataFilePath);
        this.arffRepo.loadData(Boolean.TRUE);

        int generatedFormulaCounter = 0;
        for (int i = 0; i < numOfFormulas; i++) {
            BPPLogger.log("Formula no.: " + i);
            boolean generationResult = generateFormula(readFromFile, inputFilePath, i);

            if (generationResult) {
                generatedFormulaCounter++;
                this.generatedFormula.setFormulaType(FormulaType.ASSESSMENT);
                this.formulaService.createFormula(this.generatedFormula);
            }
            BPPLogger.log("---------------------------");
        }

        BPPLogger.log("Formulas generated: " + generatedFormulaCounter);

        this.arffRepo.setFilePath(oldPath);
        this.arffRepo.setData(oldData);
        return generatedFormulaCounter;
    }

    public boolean tryGenerateSingleFormulaFromFile(String inputFilePath, String testDataFilePath, Integer formulaNo) throws JAXBException {
        String oldPath = this.arffRepo.getFilePath();
        Instances oldData = this.arffRepo.getData();
        this.arffRepo.setFilePath(testDataFilePath);
        this.arffRepo.loadData(Boolean.TRUE);

        boolean generationResult = generateFormula(true, inputFilePath, formulaNo == null ? 0 : formulaNo);
        BPPLogger.log("Formula generated: " + generationResult);

        this.arffRepo.setFilePath(oldPath);
        this.arffRepo.setData(oldData);
        return generationResult;
    }

    private boolean generateFormula(boolean readFromFile, String filePath, int formulaNo) throws JAXBException {
        boolean isFormulaGenerated = Boolean.FALSE;
        int failCounter = 0;

        BPPLogger.log("Generating...");
        BPPLogger.log("Iteration no.: " + failCounter);
        if (readFromFile && filePath != null) {
            readFormulaFromFile(filePath, formulaNo);
        } else {
            generateNewFormula();
        }

        TestingResult testingResult = testFormula(null, null, null, 0, false);

        if (!testingResult.isValid()) {
            failCounter++;

            while (failCounter < this.bppConfig.getNumberOfIterations()) {
                BPPLogger.log("Iteration no.: " + failCounter);

                int clauseFailCounter = testingResult.getFailCounter();
                boolean clauseReplaced = false;

                Random r = new Random();

                if (this.bppConfig.isHardReset()) {
                    generateNewFormula();
                } else {
                    if (clauseFailCounter < this.bppConfig.getTestClauseFixAttempts() /**
                     * testingResult.getFormula().getElements().get(testingResult.getFailingElement()).getLength()
                     */
                    ) {
                        if (r.nextBoolean()) {
                            if (testingResult.getAddOrRemove() != null && testingResult.getAddOrRemove()) {
                                addLiteral(r, testingResult.getFailingElement());
                            } else {
                                if (this.generatedFormula.getClauses().get(testingResult.getFailingElement()).getLength() > BPPConfig.getInstance().getMinLength()) {
                                    boolean isClauseRemoved = removeLiteral(r, testingResult.getFailingElement());
                                    if (isClauseRemoved) {
                                        clauseFailCounter = 0;
                                        clauseReplaced = true;
                                    }
                                }
                            }
                        } else {
                            fixFormula(r, testingResult.getFailingElement());
                        }

                        if (r.nextBoolean()) {
                            if (testingResult.getAddOrRemove() != null && !testingResult.getAddOrRemove() && this.generatedFormula.getFormulaSize() > BPPConfig.getInstance().getMinSize()) {
                                int randomIndex = r.nextInt(this.generatedFormula.getFormulaSize());

                                this.generatedFormula.getClauses().remove(randomIndex);
                            } else {
                                int randomSize = r
                                        .nextInt(this.bppConfig.getClauseNumber() - this.bppConfig.getMinSize() + 1)
                                        + this.bppConfig.getMinSize();

                                this.fillRandomClause(randomSize, r);
                            }
                        }
                    } else {
                        if (r.nextBoolean() && this.generatedFormula.getFormulaSize() > BPPConfig.getInstance().getMinSize()) {
                            this.generatedFormula.getClauses().remove(testingResult.getFailingElement().intValue());
                        } else {
                            replaceElement(testingResult.getFailingElement());
                            clauseReplaced = true;
                        }
                        clauseFailCounter = 0;
                    }
                }

                testingResult = this.testFormula(testingResult.getSuccessRatio(), testingResult.getFormula(),
                        testingResult.getFailingElement(), clauseFailCounter, clauseReplaced);

                if (testingResult.isValid()) {
                    this.generatedFormula.setWeight(testingResult.getSuccessRatio());
                    return Boolean.TRUE;
                }

                failCounter++;
            }
        } else {
            this.generatedFormula.setWeight(testingResult.getSuccessRatio());
            isFormulaGenerated = Boolean.TRUE;
        }

        return isFormulaGenerated;
    }

    private void readFormulaFromFile(String filepath, int index) {
        List<Formula> formulas = this.formulaService.loadFormulasFromFile(filepath, FormulaType.ASSESSMENT);
        this.generatedFormula = formulas.get(index % formulas.size());
    }

    private void generateNewFormula() throws JAXBException {
        this.generatedFormula = new Formula();
        Random r = new Random();

        if (this.bppConfig.isFixedSize()) {
            this.fillRandomFormula(this.bppConfig.getClauseNumber(), r);
        } else {
            int randomSize = r.nextInt(this.bppConfig.getClauseNumber() - this.bppConfig.getMinSize() + 1)
                    + this.bppConfig.getMinSize();

            this.fillRandomFormula(randomSize, r);
        }

        for (Clause clause : this.formulaService.getClausesByType(ClauseType.BUSINESS)) {
            this.generatedFormula.attach(clause);
        }
    }

    private void fixFormula(Random r, Integer failingClauseIndex) throws JAXBException {
        Clause failingClause = this.generatedFormula.getClauses().get(failingClauseIndex);

        if (failingClause.getLength() == 1) {

            this.generatedFormula.getClauses().set(failingClauseIndex, getValidClause(this.getRandomLiteralClause(r), r));

        } else {
            int randomLiteralIndex = r.nextInt(failingClause.getLiterals().size());

            failingClause.getLiterals().set(randomLiteralIndex, this.getRandomLiteralClause(r).getLiterals().get(0));

            this.generatedFormula.getClauses().set(failingClauseIndex, getValidClause(failingClause, r));
        }

        for (Clause clause : this.formulaService.getClausesByType(ClauseType.BUSINESS)) {
            this.generatedFormula.attach(clause);
        }
    }

    private void addLiteral(Random r, Integer failingClauseIndex) throws JAXBException {
        Clause failingClause = this.generatedFormula.getClauses().get(failingClauseIndex);
        Literal literal = getValidClause(this.getRandomLiteralClause(r), r).getLiterals().get(0);
        failingClause.attach(literal);

        this.generatedFormula.getClauses().set(failingClauseIndex, failingClause);
    }

    private boolean removeLiteral(Random r, Integer failingClauseIndex) {
        boolean isClauseRemoved = Boolean.FALSE;
        Clause failingClause = this.generatedFormula.getClauses().get(failingClauseIndex);

        if (failingClause.getLength() == 1) {

            this.generatedFormula.getClauses().remove(failingClauseIndex.intValue());
            isClauseRemoved = Boolean.TRUE;

        } else {
            int randomLiteralIndex = r.nextInt(failingClause.getLiterals().size());

            failingClause.getLiterals().remove(randomLiteralIndex);

            if (failingClause.getLiterals().isEmpty()) {
                this.generatedFormula.getClauses().remove(failingClauseIndex.intValue());
                isClauseRemoved = Boolean.TRUE;
            } else {
                this.generatedFormula.getClauses().set(failingClauseIndex, failingClause);
            }
        }

        return isClauseRemoved;
    }

    private void fillRandomFormula(int formulaSize, Random r) throws JAXBException {
        for (int i = 0; i < formulaSize; i++) {
            if (this.bppConfig.isFixedLength()) {
                if (this.bppConfig.getClauseLength() == 1) {
                    this.generatedFormula.attach(getValidClause(this.getRandomLiteralClause(r), r));
                } else {
                    Clause randomClause = fillRandomClause(this.bppConfig.getClauseLength(), r);

                    this.generatedFormula.attach(getValidClause(randomClause, r));
                }
            } else {
                int randomLength = r.nextInt(this.bppConfig.getClauseLength() - this.bppConfig.getMinLength() + 1)
                        + this.bppConfig.getMinLength();

                if (randomLength == 1) {
                    this.generatedFormula.attach(getValidClause(this.getRandomLiteralClause(r), r));
                } else {
                    Clause randomClause = fillRandomClause(randomLength, r);

                    this.generatedFormula.attach(getValidClause(randomClause, r));
                }
            }
        }
    }

    private void replaceElement(int index) throws JAXBException {
        Random r = new Random();

        if (this.bppConfig.isFixedLength()) {
            if (this.bppConfig.getClauseLength() == 1) {
                this.generatedFormula.getClauses().set(index, getValidClause(this.getRandomLiteralClause(r), r));
            } else {
                Clause randomClause = fillRandomClause(this.bppConfig.getClauseLength(), r);

                this.generatedFormula.getClauses().set(index, getValidClause(randomClause, r));
            }
        } else {
            int randomLength = r.nextInt(this.bppConfig.getClauseLength() - this.bppConfig.getMinLength() + 1)
                    + this.bppConfig.getMinLength();

            if (randomLength == 1) {
                this.generatedFormula.getClauses().set(index, getValidClause(this.getRandomLiteralClause(r), r));
            } else {
                Clause randomClause = fillRandomClause(randomLength, r);

                this.generatedFormula.getClauses().set(index, getValidClause(randomClause, r));
            }
        }
    }

    private Clause fillRandomClause(int clauseLength, Random r) {
        Clause randomClause = new Clause();

        for (int i = 0; i < clauseLength; i++) {
            Literal randomLiteral = this.getRandomLiteralClause(r).getLiterals().get(0);

            if (randomClause.literalAlreadyPresent(randomLiteral)) {
                while (randomClause.literalAlreadyPresent(randomLiteral)) {
                    randomLiteral = this.getRandomLiteralClause(r).getLiterals().get(0);
                }
            }

            randomClause.attach(randomLiteral);
        }
        randomClause.setExtDescription(randomClause.toExtString());

        return randomClause;
    }

    private Clause getRandomLiteralClause(Random r) {
        boolean isSat = r.nextBoolean();
        List<Formula> formulas = this.formulaService.getFormulasByType(isSat ? FormulaType.BANKRUPT : FormulaType.NOT_BANKRUPT);
        Formula randomFormula = formulas.get(r.nextInt(formulas.size()));
        Clause randomClause = randomFormula.getClauses().get(r.nextInt(randomFormula.getFormulaSize()));
        Clause resultClause = new Clause();
        Literal randomLiteral;

        if (randomClause.getLength() == 1) {
            randomLiteral = randomClause.getLiterals().get(0);
        } else {
            randomLiteral = randomClause.getLiterals().get(r.nextInt(randomClause.getLiterals().size()));
        }

        fillRandomLiteralData(isSat, randomLiteral);

        resultClause.attach(randomLiteral);

        return resultClause;
    }

    private void fillRandomLiteralData(boolean isSat, Literal randomLiteral) {
        if (!isSat) {
            randomLiteral.setNegative(!randomLiteral.isNegative());
        }
        randomLiteral.setExtDescription(randomLiteral.toExtString());

        Literal existingLiteral = this.formulaService.getLiteralByExtDescription(randomLiteral.getExtDescription());
        if (existingLiteral == null) {
            randomLiteral.setId(null);
        } else {
            randomLiteral.setId(existingLiteral.getId());
        }
    }

    private Clause getValidClause(Clause clause, Random r) throws JAXBException {
        if (clause.getLength() == 1) {
            if (!validateClause(clause)) {
                while (!validateClause(clause)) {
                    clause = getRandomLiteralClause(r);
                }
            }
        } else {
            if (!validateClause(clause)) {
                int fixCounter = 1;
                while (!validateClause(clause)) {
                    if (fixCounter < this.bppConfig.getClauseFixAttempts()) {
                        int switchIndex = r.nextInt(clause.getLiterals().size());
                        clause.getLiterals().set(switchIndex, getRandomLiteralClause(r).getLiterals().get(0));
                        fixCounter++;
                    } else {
                        clause = fillRandomClause(clause.getLiterals().size(), r);
                        fixCounter = 0;
                    }
                }
            }
        }

        return clause;
    }

    private boolean validateClause(Clause randomClause) throws JAXBException {
        int successCounter = 0;
        List<Formula> formulas = this.formulaService.getFormulasByType(FormulaType.BANKRUPT);

        successCounter += validateClauseWithSet(randomClause, formulas);

        double successPercentage = (successCounter * 1.0 / formulas.size());

        return successPercentage >= BPPConfig.getInstance().getElementValidationThreshold();
    }

    private int validateClauseWithSet(Clause randomClause, List<Formula> formulas) {
        int successCounter = 0;

        for (Formula formula : formulas) {
            for (Clause clause : formula.getClauses()) {
                boolean isValid = false;
                for (Literal randomLiteral : randomClause.getLiterals()) {
                    for (Literal literal : clause.getLiterals()) {
                        if (randomLiteral.getAttributeScope().isApplicable(literal.getAttributeScope())
                                && ((literal.isNegative() == randomLiteral.isNegative()))) {
                            successCounter++;
                            isValid = true;
                            break;
                        }
                    }
                    if (isValid) {
                        break;
                    }
                }
                if (isValid) {
                    break;
                }
            }
        }

        return successCounter;
    }

    private TestingResult testFormula(Double lastRatio, Formula lastFormula, Integer lastFailingElement,
                                      int lastFailCounter, boolean clauseReplaced) {
        TestingResult testingResult;

        boolean isValid = false;
        Integer failingClauseIndex = null;
        int successCounter = 0;
        int bankrupt = 0;
        int bankruptMatched = 0;
        int notBankrupt = 0;
        int notBankruptMatched = 0;

        Map<Integer, Integer> failingElements = new HashMap<>();
        Map<Integer, Integer> matchingElements = new HashMap<>();

        for (int i = 0; i < this.generatedFormula.getFormulaSize(); i++) {
            failingElements.put(i, 0);
            matchingElements.put(i, 0);
        }

        Instances data = arffRepo.getData();

        for (int i = 0; i < data.size(); i++) {
            List<List<Boolean>> subResult = new LinkedList<>();
            for (int j = 0; j < this.generatedFormula.getFormulaSize(); j++) {
                subResult.add(new LinkedList<Boolean>());
                Clause clause = this.generatedFormula.getClauses().get(j);
                for (int k = 0; k < clause.getLiterals().size(); k++) {
                    subResult.get(j).add(Boolean.FALSE);
                }
            }

            List<Boolean> result = new LinkedList<>();

            Instance record = data.get(i);
            boolean recordClass = record.classValue() == 1.0;

            if (recordClass) {
                bankrupt++;
            } else {
                notBankrupt++;
            }

            for (int j = 0; j < record.numAttributes(); j++) {
                String attrName = record.attribute(j).name();
                Double value = record.value(j);

                for (int k = 0; k < generatedFormula.getClauses().size(); k++) {
                    Clause clause = generatedFormula.getClauses().get(k);

                    for (int l = 0; l < clause.getLiterals().size(); l++) {
                        Literal literal = clause.getLiterals().get(l);
                        if (literal.getAttributeScope() != null && literal.getAttributeScope().getAttrName().equals(attrName)
                                && literal.getAttributeScope().isApplicable(value)) {
                            subResult.get(k).set(l, Boolean.TRUE && !literal.isNegative());
                        }
                    }
                }
            }

            for (int j = 0; j < subResult.size(); j++) {
                boolean evalSubResult = Boolean.FALSE;

                for (int k = 0; k < subResult.get(j).size(); k++) {
                    evalSubResult = evalSubResult || subResult.get(j).get(k);
                }

                result.add(evalSubResult);

                if (evalSubResult ^ recordClass) {
                    failingElements.put(result.size() - 1, failingElements.get(result.size() - 1) + 1);
                } else {
                    matchingElements.put(result.size() - 1, failingElements.get(result.size() - 1) + 1);
                }
            }

            boolean evalResult = Boolean.TRUE;

            for (int j = 0; j < result.size(); j++) {
                evalResult = evalResult && result.get(j);
            }

            evalResult = !(evalResult ^ recordClass);

            if (evalResult) {
                successCounter++;
                if (recordClass) {
                    bankruptMatched++;
                } else {
                    notBankruptMatched++;
                }
            }

        }

        double successRatio = (successCounter * 1.0 / data.size());

        BPPLogger.log("Formula: ");
        BPPLogger.log(this.generatedFormula.toExtString());
        BPPLogger.log("Formula length: " + this.generatedFormula.getFormulaSize());
        BPPLogger.log("Data size: " + data.size());
        BPPLogger.log("Matched records: " + successCounter);
        BPPLogger.log("Bankrupt matched: " + bankruptMatched + " / " + bankrupt);
        BPPLogger.log("Not bankrupt matched: " + notBankruptMatched + " / " + notBankrupt);
        BPPLogger.log("Success ratio: " + successRatio);

        if (successRatio >= this.bppConfig.getFormulaToleranceThreshold()) {
            isValid = true;
        }

        Boolean addOrRemove = null;

        if (!isValid) {
            Map.Entry<Integer, Integer> maxEntry = null;

            for (Map.Entry<Integer, Integer> entry : failingElements.entrySet()) {
                if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0) {
                    maxEntry = entry;
                }
            }

            failingClauseIndex = maxEntry.getKey();
            double bankruptMatchedRatio = ((bankruptMatched * 1.0) / (bankrupt * 1.0));
            double notBankruptMatchedRatio = ((notBankruptMatched * 1.0) / (notBankrupt * 1.0));

            addOrRemove = (bankruptMatchedRatio < notBankruptMatchedRatio);
        }

        for (Map.Entry<Integer, Integer> entry : matchingElements.entrySet()) {
            double matchingRatio = ((entry.getValue() * 1.0) / (data.size() * 1.0));

            if (matchingRatio >= this.bppConfig.getElementToleranceThreshold()) {
                Clause businessRule = this.generatedFormula.getClauses().get(entry.getKey());
                businessRule.setId(null);
                businessRule.setClauseType(ClauseType.BUSINESS);
                this.formulaService.createClause(businessRule);
            }
        }

        if (!this.bppConfig.isHardReset() && lastRatio != null && lastFormula != null && lastFailingElement != null
                && !clauseReplaced && lastRatio > successRatio) {
            successRatio = lastRatio;
            this.generatedFormula = lastFormula;
            failingClauseIndex = lastFailingElement;
        }

        int failCounter = 0;

        if (lastFailingElement != null && lastFailingElement.equals(failingClauseIndex)) {
            failCounter = lastFailCounter + 1;
        }

        testingResult = new TestingResult(isValid, failingClauseIndex, successRatio, this.generatedFormula, failCounter,
                addOrRemove);
        return testingResult;
    }
}