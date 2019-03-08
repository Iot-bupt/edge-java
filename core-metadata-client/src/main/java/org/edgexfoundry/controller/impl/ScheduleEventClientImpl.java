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
 * @microservice: core-metadata-client
 * @author: Jim White, Dell
 * @version: 1.0.0
 *******************************************************************************/

package org.edgexfoundry.controller.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.edgexfoundry.controller.ScheduleEventClient;
import org.edgexfoundry.domain.meta.ScheduleEvent;
import org.edgexfoundry.exception.controller.DataValidationException;
import org.edgexfoundry.meta.client.ConsulDiscoveryClientTemplate;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ScheduleEventClientImpl extends ConsulDiscoveryClientTemplate
    implements ScheduleEventClient {

  @Value("${meta.db.event.url}")
  private String url;

  @Override
  public ScheduleEvent scheduleEvent(String id) {
    return getClient().scheduleEvent(id);
  }

  @Override
  public List<ScheduleEvent> scheduleEvents() {
    return getClient().scheduleEvents();
  }

  @Override
  public List<ScheduleEvent> scheduleEventsForAddressable(String addressableId) {
    return getClient().scheduleEventsForAddressable(addressableId);
  }

  @Override
  public List<ScheduleEvent> scheduleEventsForAddressableByName(String addressablename) {
    return getClient().scheduleEventsForAddressableByName(addressablename);
  }

  @Override
  public List<ScheduleEvent> scheduleEventsForServiceByName(String servicename) {
    return getClient().scheduleEventsForServiceByName(servicename);
  }

  @Override
  public ScheduleEvent scheduleEventForName(String name) {
    return getClient().scheduleEventForName(name);
  }

  @Override
  public String add(ScheduleEvent scheduleEvent) {
    return getClient().add(scheduleEvent);
  }

  @Override
  public boolean update(ScheduleEvent scheduleEvent) {
    return getClient().update(scheduleEvent);
  }

  @Override
  public boolean delete(String id) {
    return getClient().delete(id);
  }

  @Override
  public boolean deleteByName(String name) {
    return getClient().deleteByName(name);
  }

  private ScheduleEventClient getClient() {
    ResteasyClient client = new ResteasyClientBuilder().build();
    ResteasyWebTarget target;

    String rootUrl = super.getRootUrl();
    if (rootUrl == null || rootUrl.isEmpty()) {
      target = client.target(url);
    } else {
      target = client.target(rootUrl + super.getPath());
    }

    return target.proxy(ScheduleEventClient.class);
  }

  @Override
  protected String extractPath() {
    String result = "";
    try {
      URL urlObject = new URL(url);
      result = urlObject.getPath();
    } catch (MalformedURLException e) {
      throw new DataValidationException("the URL is malformed, meta.db.event.url: " + url);
    }
    return result;
  }

}
