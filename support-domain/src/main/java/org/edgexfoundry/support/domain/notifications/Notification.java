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
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.http.MediaType;

@Document(collection = "notification")
public class Notification {

  @Id
  private String id;
  private String slug;
  private String sender;
  private NotificationCategory category;
  private NotificationSeverity severity = NotificationSeverity.NORMAL;
  private String content;
  private String description;
  private NotificationStatus status = NotificationStatus.NEW;
  private String[] labels;
  private String contentType = MediaType.TEXT_PLAIN_VALUE;
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

  public String getSlug() {
    return slug;
  }

  public void setSlug(String slug) {
    this.slug = slug;
  }

  public String getSender() {
    return sender;
  }

  public void setSender(String sender) {
    this.sender = sender;
  }

  public NotificationCategory getCategory() {
    return category;
  }

  public void setCategory(NotificationCategory category) {
    this.category = category;
  }

  public NotificationSeverity getSeverity() {
    return severity;
  }

  public void setSeverity(NotificationSeverity severity) {
    this.severity = severity;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public NotificationStatus getStatus() {
    return status;
  }

  public void setStatus(NotificationStatus status) {
    this.status = status;
  }

  public String[] getLabels() {
    return labels;
  }

  public void setLabels(String[] labels) {
    this.labels = labels;
  }

  public String getContentType() {
    return contentType;
  }

  public void setContentType(String contentType) {
    this.contentType = contentType;
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
    return "Notification [slug=" + slug + ", sender=" + sender + ", category=" + category
        + ", severity=" + severity + ", labels=" + Arrays.toString(labels) + "]";
  }

}
