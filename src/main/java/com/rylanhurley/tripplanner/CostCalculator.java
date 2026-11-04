package com.rylanhurley.tripplanner;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class CostCalculator {

    public static BigDecimal perPerson(List<CostItem> costs, int people) {
        // YOU write this part
        if ( people<=0 ) {
            throw new IllegalArgumentException("people must be at least 1");
        }
        BigDecimal splitTotal = BigDecimal.ZERO;// start at 0
        BigDecimal eachTotal = BigDecimal.ZERO;


        for (CostItem cost : costs) {                // look at each cost, one at a time
            if(cost.getSplitType()==SplitType.SPLIT){
            splitTotal = splitTotal.add(cost.getAmount());           // add this cost's amount to the total
        }
            else if(cost.getSplitType()==SplitType.EACH){
            eachTotal = eachTotal.add(cost.getAmount());}
        }
BigDecimal b=BigDecimal.valueOf(people);

        BigDecimal total=eachTotal.add(splitTotal.divide(b,2, RoundingMode.HALF_UP));
        return total;
    }
    }
