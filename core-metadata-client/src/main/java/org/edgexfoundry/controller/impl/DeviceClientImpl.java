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

import org.edgexfoundry.controller.DeviceClient;
import org.edgexfoundry.domain.meta.Device;
import org.edgexfoundry.exception.controller.DataValidationException;
import org.edgexfoundry.meta.client.ConsulDiscoveryClientTemplate;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DeviceClientImpl extends ConsulDiscoveryClientTemplate implements DeviceClient {

  @Value("${meta.db.device.url}")
  private String url;

  @Override
  public Device device(String id) {
    return getClient().device(id);
  }

  @Override
  public List<Device> devices() {
    return getClient().devices();
  }

  @Override
  public Device deviceForName(String name) {
    return getClient().deviceForName(name);
  }

  @Override
  public List<Device> devicesByLabel(String label) {
    return getClient().devicesByLabel(label);
  }

  @Override
  public List<Device> devicesForService(String serviceId) {
    return getClient().devicesForService(serviceId);
  }

  @Override
  public List<Device> devicesForServiceByName(String servicename) {
    return getClient().devicesForServiceByName(servicename);
  }

  @Override
  public List<Device> devicesForProfile(String profileId) {
    return getClient().devicesForProfile(profileId);
  }

  @Override
  public List<Device> devicesForProfileByName(String profilename) {
    return getClient().devicesForProfileByName(profilename);
  }

  @Override
  public List<Device> devicesForAddressable(String addressableId) {
    return getClient().devicesForAddressable(addressableId);
  }

  @Override
  public List<Device> devicesForAddressableByName(String addressablename) {
    return getClient().devicesForAddressableByName(addressablename);
  }

  @Override
  public String add(Device device) {
    return getClient().add(device);
  }

  @Override
  public boolean update(Device device) {
    return getClient().update(device);
  }

  @Override
  public boolean updateLastConnected(String id, long time) {
    return getClient().updateLastConnected(id, time);
  }

  @Override
  public boolean updateLastConnected(String id, long time, boolean notify) {
    return getClient().updateLastConnected(id, time, notify);
  }

  @Override
  public boolean updateLastConnectedByName(String name, long time) {
    return getClient().updateLastConnectedByName(name, time);
  }

  @Override
  public boolean updateLastConnectedByName(String name, long time, boolean notify) {
    return getClient().updateLastConnectedByName(name, time, notify);
  }

  @Override
  public boolean updateLastReported(String id, long time) {
    return getClient().updateLastReported(id, time);
  }

  @Override
  public boolean updateLastReported(String id, long time, boolean notify) {
    return getClient().updateLastReported(id, time, notify);
  }

  @Override
  public boolean updateLastReportedByName(String name, long time) {
    return getClient().updateLastReportedByName(name, time);
  }

  @Override
  public boolean updateLastReportedByName(String name, long time, boolean notify) {
    return getClient().updateLastReportedByName(name, time, notify);
  }

  @Override
  public boolean updateOpState(String id, String opState) {
    return getClient().updateOpState(id, opState);
  }

  @Override
  public boolean updateOpStateByName(String name, String opState) {
    return getClient().updateOpStateByName(name, opState);
  }

  @Override
  public boolean updateAdminState(String id, String adminState) {
    return getClient().updateAdminState(id, adminState);
  }

  @Override
  public boolean updateAdminStateByName(String name, String adminState) {
    return getClient().updateAdminStateByName(name, adminState);
  }

  @Override
  public boolean delete(String id) {
    return getClient().delete(id);
  }

  @Override
  public boolean deleteByName(String name) {
    return getClient().deleteByName(name);
  }

  private DeviceClient getClient() {
    ResteasyClient client = new ResteasyClientBuilder().build();
    ResteasyWebTarget target;

    String rootUrl = super.getRootUrl();
    if (rootUrl == null || rootUrl.isEmpty()) {
      target = client.target(url);
    } else {
      target = client.target(rootUrl + super.getPath());
    }

    return target.proxy(DeviceClient.class);
  }

  @Override
  protected String extractPath() {
    String result = "";
    try {
      URL urlObject = new URL(url);
      result = urlObject.getPath();
    } catch (MalformedURLException e) {
      throw new DataValidationException("the URL is malformed, meta.db.device.url: " + url);
    }
    return result;
  }

}
