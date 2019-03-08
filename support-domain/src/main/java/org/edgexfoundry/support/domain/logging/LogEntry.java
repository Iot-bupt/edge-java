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
 * @microservice: support-domain
 * @author: Cloud Tsai and Jude Hung, Dell
 * @version: 1.0.0
 *******************************************************************************/

package org.edgexfoundry.support.domain.logging;

import java.util.Arrays;

import org.slf4j.event.Level;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class LogEntry {

  @Id
  private String id;

  private Level logLevel;
  private String[] labels;
  private String originService;
  private String message;
  private long created;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Level getLogLevel() {
    return logLevel;
  }

  public void setLogLevel(Level logLevel) {
    this.logLevel = logLevel;
  }

  public String[] getLabels() {
    return labels;
  }

  public void setLabels(String[] labels) {
    this.labels = labels;
  }

  public String getOriginService() {
    return originService;
  }

  public void setOriginService(String originService) {
    this.originService = originService;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public long getCreated() {
    return created;
  }

  public void setCreated(long created) {
    this.created = created;
  }

  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(this.created).append(" [")
        .append(null == this.originService ? "" : this.originService).append("] ")
        .append(null == this.labels ? "[]" : Arrays.toString(this.labels)).append(" ")
        .append(String.format("%1$-5s", this.logLevel.name().toUpperCase())).append(" - ")
        .append(this.message);
    return builder.toString();
  }
}
