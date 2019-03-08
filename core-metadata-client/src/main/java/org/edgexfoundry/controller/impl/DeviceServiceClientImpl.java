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
import java.util.Set;

import org.edgexfoundry.controller.DeviceServiceClient;
import org.edgexfoundry.domain.meta.Addressable;
import org.edgexfoundry.domain.meta.DeviceService;
import org.edgexfoundry.exception.controller.DataValidationException;
import org.edgexfoundry.meta.client.ConsulDiscoveryClientTemplate;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DeviceServiceClientImpl extends ConsulDiscoveryClientTemplate
    implements DeviceServiceClient {

  @Value("${meta.db.deviceservice.url}")
  private String url;

  @Override
  public DeviceService deviceService(String id) {
    return getClient().deviceService(id);
  }

  @Override
  public List<DeviceService> deviceServices() {
    return getClient().deviceServices();
  }

  @Override
  public DeviceService deviceServiceForName(String name) {
    return getClient().deviceServiceForName(name);
  }

  @Override
  public List<DeviceService> deviceServicesForAddressable(String addressableId) {
    return getClient().deviceServicesForAddressable(addressableId);
  }

  @Override
  public List<DeviceService> deviceServicesForAddressableByName(String addressablename) {
    return getClient().deviceServicesForAddressableByName(addressablename);
  }

  @Override
  public List<DeviceService> deviceServicesByLabel(String label) {
    return getClient().deviceServicesByLabel(label);
  }

  @Override
  public Set<Addressable> addressablesForAssociatedDevices(String id) {
    return getClient().addressablesForAssociatedDevices(id);
  }

  @Override
  public Set<Addressable> addressablesForAssociatedDevicesByName(String name) {
    return getClient().addressablesForAssociatedDevicesByName(name);
  }

  @Override
  public String add(DeviceService deviceService) {
    return getClient().add(deviceService);
  }

  @Override
  public boolean update(DeviceService deviceService) {
    return getClient().update(deviceService);
  }

  @Override
  public boolean updateLastConnected(String id, long time) {
    return getClient().updateLastConnected(id, time);
  }

  @Override
  public boolean updateLastConnectedByName(String name, long time) {
    return getClient().updateLastConnectedByName(name, time);
  }

  @Override
  public boolean updateLastReported(String id, long time) {
    return getClient().updateLastReported(id, time);
  }

  @Override
  public boolean updateLastReportedByName(String name, long time) {
    return getClient().updateLastReportedByName(name, time);
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

  private DeviceServiceClient getClient() {
    ResteasyClient client = new ResteasyClientBuilder().build();
    ResteasyWebTarget target;

    String rootUrl = super.getRootUrl();
    if (rootUrl == null || rootUrl.isEmpty()) {
      target = client.target(url);
    } else {
      target = client.target(rootUrl + super.getPath());
    }

    return target.proxy(DeviceServiceClient.class);
  }

  @Override
  protected String extractPath() {
    String result = "";
    try {
      URL urlObject = new URL(url);
      result = urlObject.getPath();
    } catch (MalformedURLException e) {
      throw new DataValidationException("the URL is malformed, meta.db.deviceservice.url: " + url);
    }
    return result;
  }

}
