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
 * @microservice: support-notifications-client library
 * @author: Cloud Tsai, Dell
 * @version: 1.0.0
 *******************************************************************************/

package org.edgexfoundry.support.notifications.client.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.edgexfoundry.controller.TransmissionClient;
import org.edgexfoundry.exception.controller.DataValidationException;
import org.edgexfoundry.support.domain.notifications.Transmission;
import org.edgexfoundry.support.notifications.client.ConsulDiscoveryClientTemplate;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TransmissionClientImpl extends ConsulDiscoveryClientTemplate
    implements TransmissionClient {

  @Value("${support.notifications.transmission.url}")
  String url;

  @Override
  public List<Transmission> findByNotificationSlug(String slug, int limit) {
    return getClient().findByNotificationSlug(slug, limit);
  }

  @Override
  public List<Transmission> findByCreatedDuration(long start, long end, int limit) {
    return getClient().findByCreatedDuration(start, end, limit);
  }

  @Override
  public List<Transmission> findByCreatedAfter(long start, int limit) {
    return getClient().findByCreatedAfter(start, limit);
  }

  @Override
  public List<Transmission> findByCreatedBefore(long end, int limit) {
    return getClient().findByCreatedBefore(end, limit);
  }

  @Override
  public List<Transmission> findEscalatedTransmissions(int limit) {
    return getClient().findEscalatedTransmissions(limit);
  }

  @Override
  public List<Transmission> findFailedTransmissions(int limit) {
    return getClient().findFailedTransmissions(limit);
  }

  @Override
  public boolean deleteOldSent(long age) {
    return getClient().deleteOldSent(age);
  }

  @Override
  public boolean deleteOldEscalated(long age) {
    return getClient().deleteOldEscalated(age);
  }

  @Override
  public boolean deleteOldAcknowledged(long age) {
    return getClient().deleteOldAcknowledged(age);
  }

  @Override
  public boolean deleteOldFailed(long age) {
    return getClient().deleteOldFailed(age);
  }

  private TransmissionClient getClient() {
    ResteasyClient client = new ResteasyClientBuilder().build();
    ResteasyWebTarget target;

    String rootUrl = super.getRootUrl();
    if (rootUrl == null || rootUrl.isEmpty()) {
      target = client.target(url);
    } else {
      target = client.target(rootUrl + super.getPath());
    }

    return target.proxy(TransmissionClient.class);
  }

  @Override
  protected String extractPath() {
    String result = "";
    try {
      URL urlObject = new URL(url);
      result = urlObject.getPath();
    } catch (MalformedURLException e) {
      throw new DataValidationException(
          "The URL is malformed, support.notifications.transmission.url: " + url);
    }
    return result;
  }

}
