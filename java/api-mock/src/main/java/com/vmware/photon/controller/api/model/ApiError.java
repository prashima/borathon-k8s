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


import java.util.Map;
import java.util.Objects;

/**
 * Helper API error representation.
 */
public class ApiError {

  private String code;

  private String message;

  private Map<String, String> data;

  public ApiError() {
  }

  public ApiError(String code, String message, Map<String, String> data) {
    this.code = code;
    this.message = message;
    this.data = data;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public Map<String, String> getData() {
    return data;
  }

  public void setData(Map<String, String> data) {
    this.data = data;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    ApiError other = (ApiError) o;

    return Objects.equals(this.code, other.code) &&
        Objects.equals(this.message, other.message) &&
        Objects.equals(this.data, other.data);
  }

  @Override
  public int hashCode() {
    return Objects.hash(code, message, data);
  }
}
