/*
 * Copyright 2015 VMware, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, without warranties or
 * conditions of any kind, EITHER EXPRESS OR IMPLIED.  See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.vmware.photon.controller.api.model;

import com.vmware.photon.controller.api.model.base.Flavorful;
import com.vmware.photon.controller.api.model.base.Named;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * A VM is created using a JSON payload that maps to this class. From a JSON
 * perspective, this looks like a subset of the {@link Vm} class.
 */
public class VmCreateSpec implements Flavorful, Named {

  public static final String KIND = "vm";
  private String name;
  private String flavor;
  private Set<String> tags = new HashSet<>();
  private String sourceImageId;
  private List<AttachedDiskCreateSpec> attachedDisks = new ArrayList<>();
  private Map<String, String> environment = new HashMap<>();
  //private List<LocalitySpec> affinities = new ArrayList<>();
  private List<String> subnets;

  public String getKind() {
    return KIND;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getFlavor() {
    return flavor;
  }

  public void setFlavor(String flavor) {
    this.flavor = flavor;
  }

  public Set<String> getTags() {
    return tags;
  }

  public void setTags(Set<String> tags) {
    this.tags = tags;
  }

  public String getSourceImageId() {
    return this.sourceImageId;
  }

  public void setSourceImageId(String sourceImageId) {
    this.sourceImageId = sourceImageId;
  }

  public List<AttachedDiskCreateSpec> getAttachedDisks() {
    return attachedDisks;
  }

  public void setAttachedDisks(List<AttachedDiskCreateSpec> attachedDisks) {
    this.attachedDisks = attachedDisks;
  }

  public void addDisk(AttachedDiskCreateSpec disk) {
    attachedDisks.add(disk);
  }

  public Map<String, String> getEnvironment() {
    return environment;
  }

  public void setEnvironment(Map<String, String> environment) {
    this.environment = environment;
  }

  /*public List<LocalitySpec> getAffinities() {
    return affinities;
  }

  public void setAffinities(List<LocalitySpec> affinities) {
    this.affinities = affinities;
  }*/

  public List<String> getSubnets() {
    return subnets;
  }

  public void setSubnets(List<String> subnets) {
    this.subnets = subnets;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    VmCreateSpec other = (VmCreateSpec) o;

    return Objects.equals(name, other.name) &&
        Objects.equals(flavor, other.flavor) &&
        Objects.equals(sourceImageId, other.sourceImageId) &&
        Objects.equals(tags, other.tags) &&
        Objects.equals(attachedDisks, other.attachedDisks) &&
        Objects.equals(subnets, other.subnets);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, flavor, sourceImageId, tags, attachedDisks, subnets);
  }

  @Override
  public String toString() {
    return String.format("%s, %s", name, flavor);
  }

}
