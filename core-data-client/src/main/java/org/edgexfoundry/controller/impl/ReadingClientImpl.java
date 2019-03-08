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
 * @microservice: core-data-client
 * @author: Jim White, Dell
 * @version: 1.0.0
 *******************************************************************************/

package org.edgexfoundry.controller.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.edgexfoundry.controller.ReadingClient;
import org.edgexfoundry.core.client.ConsulDiscoveryClientTemplate;
import org.edgexfoundry.domain.core.Reading;
import org.edgexfoundry.exception.controller.DataValidationException;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ReadingClientImpl extends ConsulDiscoveryClientTemplate implements ReadingClient {

  @Value("${core.db.reading.url}")
  String url;

  @Override
  public Reading reading(String id) {
    return getClient().reading(id);
  }

  @Override
  public List<Reading> readings() {
    return getClient().readings();
  }

  @Override
  public List<Reading> readings(String deviceId, int limit) {
    return getClient().readings(deviceId, limit);
  }

  @Override
  public List<Reading> readingsByName(String name, int limit) {
    return getClient().readingsByName(name, limit);
  }

  @Override
  public List<Reading> readingsByNameAndDevice(String name, String device, int limit) {
    return getClient().readingsByNameAndDevice(name, device, limit);
  }

  @Override
  public List<Reading> readingsByUoMLabel(String uomLabel, int limit) {
    return getClient().readingsByUoMLabel(uomLabel, limit);
  }

  @Override
  public List<Reading> readingsByLabel(String label, int limit) {
    return getClient().readingsByLabel(label, limit);
  }

  @Override
  public List<Reading> readingsByType(String type, int limit) {
    return getClient().readingsByType(type, limit);
  }

  @Override
  public String add(Reading reading) {
    return getClient().add(reading);
  }

  @Override
  public boolean update(Reading reading) {
    return getClient().update(reading);
  }

  @Override
  public boolean delete(String id) {
    return getClient().delete(id);
  }

  private ReadingClient getClient() {
    ResteasyClient client = new ResteasyClientBuilder().build();
    ResteasyWebTarget target;

    String rootUrl = super.getRootUrl();
    if (rootUrl == null || rootUrl.isEmpty()) {
      target = client.target(url);
    } else {
      target = client.target(rootUrl + super.getPath());
    }

    return target.proxy(ReadingClient.class);
  }

  @Override
  protected String extractPath() {
    String result = "";
    try {
      URL urlObject = new URL(url);
      result = urlObject.getPath();
    } catch (MalformedURLException e) {
      throw new DataValidationException("the URL is malformed, core.db.reading.url: " + url);
    }
    return result;
  }

}
