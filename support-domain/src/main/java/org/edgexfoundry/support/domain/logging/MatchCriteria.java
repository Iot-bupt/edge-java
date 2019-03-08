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

import org.slf4j.event.Level;

public class MatchCriteria {

  private long start;
  private long end;
  private Level[] logLevels;
  private String[] labels;
  private String[] originServices;
  private String[] messageKeywords;

  public long getStart() {
    return start;
  }

  public void setStart(long start) {
    this.start = start;
  }

  public long getEnd() {
    return end;
  }

  public void setEnd(long end) {
    this.end = end;
  }

  public Level[] getLogLevels() {
    return logLevels;
  }

  public void setLogLevels(Level[] logLevels) {
    this.logLevels = logLevels;
  }

  public String[] getLabels() {
    return labels;
  }

  public void setLabels(String[] labels) {
    this.labels = labels;
  }

  public String[] getOriginServices() {
    return originServices;
  }

  public void setOriginServices(String[] originServices) {
    this.originServices = originServices;
  }

  public String[] getMessageKeywords() {
    return messageKeywords;
  }

  public void setMessageKeywords(String[] messageKeywords) {
    this.messageKeywords = messageKeywords;
  }

}
