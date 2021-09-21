package jo.BankruptcyPredictionProject.Domain.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jo.BankruptcyPredictionProject.Domain.Entity.AttributeScope;

@Repository
public interface AttributeScopeRepository extends JpaRepository<AttributeScope, Long> {
    
}