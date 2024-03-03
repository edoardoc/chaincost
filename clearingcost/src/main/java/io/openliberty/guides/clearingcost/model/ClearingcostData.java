package io.openliberty.guides.clearingcost.model;
import java.io.Serializable;
import java.math.BigDecimal;
import jakarta.json.bind.annotation.JsonbProperty;


public class ClearingcostData implements Serializable {
    
    @JsonbProperty
    private String country;

    @JsonbProperty
    private BigDecimal cost;

    public ClearingcostData() {
      this.country = null;
      this.cost = null;
    }
    public ClearingcostData(String country, BigDecimal cost) {
        this.country = country;
        this.cost = cost;
    }

    public String getCountry() {
      return country;
    }

    public void setCountry(String country) {
      this.country = country;
    }

    public BigDecimal getCost() {
      return cost;
    }

    public void setCost(BigDecimal cost) {
      this.cost = cost;
    }

    @Override
    public boolean equals(Object another) {
      if (another instanceof ClearingcostData) {
        return country.equals(((ClearingcostData) another).getCountry());
      }
      return false;
    }
}
