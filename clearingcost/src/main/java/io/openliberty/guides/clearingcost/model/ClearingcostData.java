package io.openliberty.guides.clearingcost.model;

import java.math.BigDecimal;

public class ClearingcostData {

    private final String country;
    private final BigDecimal cost;

    public ClearingcostData(String country, BigDecimal cost) {
      this.country = country;
      this.cost = cost;
    }

    public String getCountry() {
      return country;
    }

    public BigDecimal getCost() {
      return cost;
    }

    @Override
    public boolean equals(Object another) {
      if (another instanceof ClearingcostData) {
        return country.equals(((ClearingcostData) another).getCountry());
      }
      return false;
    }
}
