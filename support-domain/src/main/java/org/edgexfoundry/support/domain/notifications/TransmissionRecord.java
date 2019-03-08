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

package org.edgexfoundry.support.domain.notifications;

public class TransmissionRecord {

  private TransmissionStatus status;
  private String response;
  private long sent;

  public TransmissionStatus getStatus() {
    return status;
  }

  public void setStatus(TransmissionStatus status) {
    this.status = status;
  }

  public String getResponse() {
    return response;
  }

  public void setResponse(String response) {
    this.response = response;
  }

  public long getSent() {
    return sent;
  }

  public void setSent(long sent) {
    this.sent = sent;
  }

  @Override
  public String toString() {
    return "TransmissionRecord [status=" + status + ", response=" + response + ", sent=" + sent
        + "]";
  }

}
