// tag::copyright[]
/*******************************************************************************
 * Copyright (c) 2017, 2023 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
// end::copyright[]
package io.openliberty.guides.summer.model;

import java.util.List;

public class SummerList {

  private List<SystemData> systems;

  public SummerList(List<SystemData> systems) {
    this.systems = systems;
  }

  public List<SystemData> getSystems() {
    return systems;
  }

  public int getTotal() {
    return systems.size();
  }
}
