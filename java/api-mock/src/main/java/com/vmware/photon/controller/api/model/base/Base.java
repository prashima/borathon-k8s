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

package com.vmware.photon.controller.api.model.base;


import org.apache.commons.lang3.StringUtils;
import static com.google.common.base.Objects.ToStringHelper;

import java.util.Objects;

/**
 * All API representations backed by DB entities inherit from this class.
 */
public abstract class Base {

  private String id;

  private String selfLink;

  public abstract String getKind();

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getSelfLink() {
    return selfLink;
  }

  public void setSelfLink(String selfLink) {
    this.selfLink = selfLink;
  }

  @Override
  public String toString() {
    return toStringHelper().toString();
  }

  protected ToStringHelper toStringHelper() {
    ToStringHelper result = com.google.common.base.Objects.toStringHelper(this).add("id", id);
    if (StringUtils.isNotBlank(selfLink)) {
      result.add("selfLink", selfLink);
    }
    if (StringUtils.isNotBlank(getKind())) {
      result.add("Kind", getKind());
    }
    return result;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Base other = (Base) o;

    if (!this.getKind().equals(other.getKind())) {
      return false;
    }

    return Objects.equals(id, other.id) &&
        Objects.equals(selfLink, other.selfLink);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, selfLink);
  }
}
