package jo.BankruptcyPredictionProject.Domain.Service.implementation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jo.BankruptcyPredictionProject.Domain.Repository.FormulaRepository;
import jo.BankruptcyPredictionProject.Domain.Service.FormulaService;

@Service
public class FormulaServiceImpl implements FormulaService {
    
    @Autowired
    private FormulaRepository formulaRepository;
}