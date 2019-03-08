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

import java.util.Arrays;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "transmission")
public class Transmission {

  @Id
  private String id;
  @DBRef
  private Notification notification;
  private String receiver;
  private Channel channel;
  private TransmissionStatus status;
  private int resendCount;
  private TransmissionRecord[] records;
  @CreatedDate
  private long created;
  @LastModifiedDate
  private long modified;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Notification getNotification() {
    return notification;
  }

  public void setNotification(Notification notification) {
    this.notification = notification;
  }

  public String getReceiver() {
    return receiver;
  }

  public void setReceiver(String receiver) {
    this.receiver = receiver;
  }

  public Channel getChannel() {
    return channel;
  }

  public void setChannel(Channel channel) {
    this.channel = channel;
  }

  public TransmissionStatus getStatus() {
    return status;
  }

  public void setStatus(TransmissionStatus status) {
    this.status = status;
  }

  public int getResendCount() {
    return resendCount;
  }

  public void setResendCount(int resendCount) {
    this.resendCount = resendCount;
  }

  public void inreaseResendCount() {
    this.resendCount++;
  }

  public TransmissionRecord[] getRecords() {
    return records;
  }

  public void setRecords(TransmissionRecord[] records) {
    this.records = records;
  }

  public long getCreated() {
    return created;
  }

  public void setCreated(long created) {
    this.created = created;
  }

  public long getModified() {
    return modified;
  }

  public void setModified(long modified) {
    this.modified = modified;
  }

  @Override
  public String toString() {
    return "Transmission [notificationSlug="
        + (notification != null ? notification.getSlug() : "null") + ", receiver=" + receiver
        + ", channel=" + channel + ", status=" + status + ", resendCount=" + resendCount
        + ", records=" + Arrays.toString(records) + "]";
  }

}
