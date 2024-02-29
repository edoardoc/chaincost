package io.openliberty.guides.clearingcost.model;

import java.util.List;

public class ClearingcostList {
  private List<ClearingcostData> costspercountry;

  public ClearingcostList(List<ClearingcostData> cost) {
    this.costspercountry = cost;
  }

  public List<ClearingcostData> getCostspercountry() {
    return costspercountry;
  }

  public int getTotal() {
    return costspercountry.size();
  }
}
