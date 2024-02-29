package io.openliberty.guides.clearingcost;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.openliberty.guides.clearingcost.model.ClearingcostData;
import io.openliberty.guides.clearingcost.model.ClearingcostList;
import jakarta.enterprise.context.ApplicationScoped;


// tag::ApplicationScoped[]
@ApplicationScoped
// end::ApplicationScoped[]
public class ClearingcostManager {

  private List<ClearingcostData> clearingcosts = Collections.synchronizedList(new ArrayList<>());

  public void add(ClearingcostData clearingcost) {
    if (!clearingcosts.contains(clearingcost)) {
      clearingcosts.add(clearingcost);
    }
  }

  public void reset() {
    clearingcosts.clear();
  }

  public ClearingcostList list() {
    return new ClearingcostList(clearingcosts);
  }
}
