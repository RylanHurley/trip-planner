package com.rylanhurley.tripplanner;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;


import java.math.BigDecimal;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CostCalculatorTest {

    @Test
    void splitCostIsDividedByPeople() {
        CostItem lodging = new CostItem();
        lodging.setAmount(new BigDecimal("1200.00"));
        lodging.setSplitType(SplitType.SPLIT);

        BigDecimal result = CostCalculator.perPerson(List.of(lodging), 6);

        assertEquals(new BigDecimal("200.00"), result);
    }
    @Test
    void eachCostIsNotDivided() {
        CostItem tickets = new CostItem();
        CostItem lodging = new CostItem();
        tickets.setAmount(new BigDecimal("90"));
        lodging.setAmount(new BigDecimal("1200.00"));
        lodging.setSplitType(SplitType.SPLIT);
        tickets.setSplitType(SplitType.EACH);
        BigDecimal result= CostCalculator.perPerson(List.of(lodging,tickets), 6);
        assertEquals(new BigDecimal("290.00"), result);
    }
    @Test
    void roundsToTwoDecimals() {
        CostItem lodging = new CostItem();
        lodging.setAmount(new BigDecimal("1000.00"));
        lodging.setSplitType(SplitType.SPLIT);
        BigDecimal result=CostCalculator.perPerson(List.of(lodging), 3);
        assertEquals(new BigDecimal("333.33"), result);
    }
    @Test
    void noCostsMeansZero() {
        BigDecimal result=CostCalculator.perPerson(List.of(),4);
        assertEquals(new BigDecimal("0.00"), result);

    }
    @Test
    void zeroPeopleIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> CostCalculator.perPerson(List.of(), 0));
    }
}