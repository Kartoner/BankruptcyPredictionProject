package jo.BankruptcyPredictionProject.Domain.Enumeration.Converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import jo.BankruptcyPredictionProject.Domain.Enumeration.ClauseType;

@Converter(autoApply = true)
public class ClauseTypeConverter implements AttributeConverter<ClauseType, String> {
    
    @Override
    public String convertToDatabaseColumn(ClauseType clauseType) {
        return (clauseType != null) ? clauseType.getAbbreviation() : null;
    }

    @Override
    public ClauseType convertToEntityAttribute(String dbData) {
        return (dbData != null) ? ClauseType.fromAbbreviation(dbData) : null;
    }
}