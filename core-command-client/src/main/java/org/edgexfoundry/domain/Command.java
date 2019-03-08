/*******************************************************************************
 * Copyright 2016-2017 Dell Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 * @microservice: core-command-client library
 * @author: Jim White, Dell
 * @version: 1.0.0
 *******************************************************************************/

package org.edgexfoundry.domain;

public class Command {

  private String id;
  private String name;
  private Get get;
  private Put put;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Get getGet() {
    return get;
  }

  public void setGet(Get get) {
    this.get = get;
  }

  public Put getPut() {
    return put;
  }

  public void setPut(Put put) {
    this.put = put;
  }

  @Override
  public String toString() {
    return "Command [name=" + name + ", get=" + get + ", put=" + put + ", " + super.toString()
        + "]";
  }

}
