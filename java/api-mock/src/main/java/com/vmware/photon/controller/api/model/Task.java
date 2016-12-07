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

import com.vmware.photon.controller.api.model.base.Model;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Task API representation.
 */
public class Task extends Model {

  public static final String KIND = "task";

  private Entity entity;

  private String state;

  private List<Step> steps = new ArrayList<>();

  private String operation;

  private Date startedTime;

  private Date queuedTime;

  private Date endTime;

  private Object resourceProperties;

  public String getKind() {
    return KIND;
  }

  public Entity getEntity() {
    return entity;
  }

  public void setEntity(Entity entity) {
    this.entity = entity;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public List<Step> getSteps() {
    return steps;
  }

  public void setSteps(List<Step> steps) {
    this.steps = steps;
  }

  public String getOperation() {
    return operation;
  }

  public void setOperation(String operation) {
    this.operation = operation;
  }

  public Date getStartedTime() {
    return startedTime;
  }

  public void setStartedTime(Date startedTime) {
    this.startedTime = startedTime;
  }

  public Date getQueuedTime() {
    return queuedTime;
  }

  public void setQueuedTime(Date queuedTime) {
    this.queuedTime = queuedTime;
  }

  public Date getEndTime() {
    return endTime;
  }

  public void setEndTime(Date endTime) {
    this.endTime = endTime;
  }

  public Object getResourceProperties() {
    return resourceProperties;
  }

  public void setResourceProperties(Object resourceProperties) {
    this.resourceProperties = resourceProperties;
  }

  /**
   * Entity associated with task.
   */
  public static class Entity {

    private String kind;

    private String id;

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }

    public String getKind() {
      return kind;
    }

    public void setKind(String kind) {
      this.kind = kind;
    }
  }
}
