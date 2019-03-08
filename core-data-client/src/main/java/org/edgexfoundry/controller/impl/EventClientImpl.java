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

import org.edgexfoundry.controller.EventClient;
import org.edgexfoundry.core.client.ConsulDiscoveryClientTemplate;
import org.edgexfoundry.domain.core.Event;
import org.edgexfoundry.domain.core.Reading;
import org.edgexfoundry.exception.controller.DataValidationException;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EventClientImpl extends ConsulDiscoveryClientTemplate implements EventClient {

  @Value("${core.db.event.url}")
  String url;

  @Override
  public Event event(String id) {
    return getClient().event(id);
  }

  @Override
  public List<Event> events() {
    return getClient().events();
  }

  @Override
  public List<Event> events(long start, long end, int limit) {
    return getClient().events(start, end, limit);
  }

  @Override
  public List<Event> eventsForDevice(String deviceId, int limit) {
    return getClient().eventsForDevice(deviceId, limit);
  }

  @Override
  public List<Reading> readingsForDeviceAndValueDescriptor(String deviceId, String valuedescriptor,
      int limit) {
    return getClient().readingsForDeviceAndValueDescriptor(deviceId, valuedescriptor, limit);
  }

  @Override
  public String add(Event event) {
    return getClient().add(event);
  }

  @Override
  public boolean markedPushed(String id) {
    return getClient().markedPushed(id);
  }

  @Override
  public boolean update(Event event) {
    return getClient().update(event);
  }

  @Override
  public boolean delete(String id) {
    return getClient().delete(id);
  }

  @Override
  public int deleteByDevice(String deviceId) {
    return getClient().deleteByDevice(deviceId);
  }

  @Override
  public long scrubPushedEvents() {
    return getClient().scrubPushedEvents();
  }

  @Override
  public long scrubOldEvents(long age) {
    return getClient().scrubOldEvents(age);
  }

  private EventClient getClient() {
    ResteasyClient client = new ResteasyClientBuilder().build();
    ResteasyWebTarget target;

    String rootUrl = super.getRootUrl();
    if (rootUrl == null || rootUrl.isEmpty()) {
      target = client.target(url);
    } else {
      target = client.target(rootUrl + super.getPath());
    }

    return target.proxy(EventClient.class);
  }

  @Override
  protected String extractPath() {
    String result = "";
    try {
      URL urlObject = new URL(url);
      result = urlObject.getPath();
    } catch (MalformedURLException e) {
      throw new DataValidationException("the URL is malformed, core.db.event.url: " + url);
    }
    return result;
  }

}
