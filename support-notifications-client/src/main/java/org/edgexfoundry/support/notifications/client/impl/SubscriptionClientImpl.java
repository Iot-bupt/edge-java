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

import org.edgexfoundry.controller.SubscriptionClient;
import org.edgexfoundry.exception.controller.DataValidationException;
import org.edgexfoundry.support.domain.notifications.Subscription;
import org.edgexfoundry.support.notifications.client.ConsulDiscoveryClientTemplate;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionClientImpl extends ConsulDiscoveryClientTemplate
    implements SubscriptionClient {

  @Value("${support.notifications.subscription.url}")
  String url;

  @Override
  public String createSubscription(Subscription subscription) {
    return getClient().createSubscription(subscription);
  }

  @Override
  public boolean updateSubscription(Subscription subscription) {
    return getClient().updateSubscription(subscription);
  }

  @Override
  public List<Subscription> listAll() {
    return getClient().listAll();
  }

  @Override
  public Subscription findBySlug(String slug) {
    return getClient().findBySlug(slug);
  }

  @Override
  public List<Subscription> searchByReceiver(String receiver) {
    return getClient().searchByReceiver(receiver);
  }

  @Override
  public boolean deleteBySlug(String slug) {
    return getClient().deleteBySlug(slug);
  }

  private SubscriptionClient getClient() {
    ResteasyClient client = new ResteasyClientBuilder().build();
    ResteasyWebTarget target;

    String rootUrl = super.getRootUrl();
    if (rootUrl == null || rootUrl.isEmpty()) {
      target = client.target(url);
    } else {
      target = client.target(rootUrl + super.getPath());
    }

    return target.proxy(SubscriptionClient.class);
  }

  @Override
  protected String extractPath() {
    String result = "";
    try {
      URL urlObject = new URL(url);
      result = urlObject.getPath();
    } catch (MalformedURLException e) {
      throw new DataValidationException(
          "The URL is malformed, support.notifications.subscription.url: " + url);
    }
    return result;
  }

}
