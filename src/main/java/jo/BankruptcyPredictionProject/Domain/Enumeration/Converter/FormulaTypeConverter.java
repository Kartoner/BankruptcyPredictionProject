package jo.BankruptcyPredictionProject.Domain.Enumeration.Converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import jo.BankruptcyPredictionProject.Domain.Enumeration.FormulaType;

@Converter(autoApply = true)
public class FormulaTypeConverter implements AttributeConverter<FormulaType, String> {
    
    @Override
    public String convertToDatabaseColumn(FormulaType formulaType) {
        return (formulaType != null) ? formulaType.getAbbreviation() : null;
    }

    @Override
    public FormulaType convertToEntityAttribute(String dbData) {
        return (dbData != null) ? FormulaType.fromAbbreviation(dbData) : null;
    }
}