package io.openliberty.guides.iincache.model;
import java.io.Serializable;
import jakarta.json.bind.annotation.JsonbProperty;


public class IINCacheData implements Serializable {
    
    @JsonbProperty
    private String iin;

    @JsonbProperty
    private String alpha2;

    public IINCacheData() {
      this.iin = null;
      this.alpha2 = null;
    }
    public IINCacheData(String iin, String alpha2) {
        this.iin = iin;
        this.alpha2 = alpha2;
    }

    public String getIin() {
      return iin;
    }

    public void setIin(String iin) {
      this.iin = iin;
    }

    public String getAlpha2() {
      return alpha2;
    }

    public void setAlpha2(String alpha2) {
      this.alpha2 = alpha2;
    }

    @Override
    public boolean equals(Object another) {
      if (another instanceof IINCacheData) {
        return iin.equals(((IINCacheData) another).getIin());
      }
      return false;
    }
}
