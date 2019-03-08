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

@Document(collection = "subscription")
public class Subscription {

  @Id
  private String id;
  private String slug;
  private String receiver;
  private String description;
  private NotificationCategory[] subscribedCategories;
  private String[] subscribedLabels;
  private Channel[] channels;
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

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getReceiver() {
    return receiver;
  }

  public void setReceiver(String receiver) {
    this.receiver = receiver;
  }

  public NotificationCategory[] getSubscribedCategories() {
    return subscribedCategories;
  }

  public void setSubscribedCategories(NotificationCategory[] subscribedCategories) {
    this.subscribedCategories = subscribedCategories;
  }

  public String[] getSubscribedLabels() {
    return subscribedLabels;
  }

  public void setSubscribedLabels(String[] subscribedLabels) {
    this.subscribedLabels = subscribedLabels;
  }

  public Channel[] getChannels() {
    return channels;
  }

  public void setChannels(Channel[] channels) {
    this.channels = channels;
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
    return "Subscription [slug=" + slug + ", receiver=" + receiver + ", subscribedCategories="
        + Arrays.toString(subscribedCategories) + ", subscribedLabels="
        + Arrays.toString(subscribedLabels) + ", channels=" + Arrays.toString(channels) + "]";
  }

}
