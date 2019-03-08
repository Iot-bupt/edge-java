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

import org.edgexfoundry.controller.DeviceProfileClient;
import org.edgexfoundry.domain.meta.DeviceProfile;
import org.edgexfoundry.exception.controller.DataValidationException;
import org.edgexfoundry.meta.client.ConsulDiscoveryClientTemplate;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DeviceProfileClientImpl extends ConsulDiscoveryClientTemplate
    implements DeviceProfileClient {

  @Value("${meta.db.deviceprofile.url}")
  private String url;

  @Override
  public DeviceProfile deviceProfile(String id) {
    return getClient().deviceProfile(id);
  }

  @Override
  public List<DeviceProfile> deviceProfiles() {
    return getClient().deviceProfiles();
  }

  @Override
  public DeviceProfile deviceProfileForName(String name) {
    return getClient().deviceProfileForName(name);
  }

  @Override
  public List<DeviceProfile> deviceProfilesByManufacturer(String manufacturer) {
    return getClient().deviceProfilesByManufacturer(manufacturer);
  }

  @Override
  public List<DeviceProfile> deviceProfilesByModel(String model) {
    return getClient().deviceProfilesByModel(model);
  }

  @Override
  public List<DeviceProfile> deviceProfilesByManufacturerOrModel(String manufacturer,
      String model) {
    return getClient().deviceProfilesByManufacturerOrModel(manufacturer, model);
  }

  @Override
  public List<DeviceProfile> deviceProfilesByLabel(String label) {
    return getClient().deviceProfilesByLabel(label);
  }

  @Override
  public String add(DeviceProfile deviceProfile) {
    return getClient().add(deviceProfile);
  }

  @Override
  public boolean update(DeviceProfile deviceProfile) {
    return getClient().update(deviceProfile);
  }

  @Override
  public boolean delete(String id) {
    return getClient().delete(id);
  }

  @Override
  public boolean deleteByName(String name) {
    return getClient().deleteByName(name);
  }

  private DeviceProfileClient getClient() {
    ResteasyClient client = new ResteasyClientBuilder().build();
    ResteasyWebTarget target;

    String rootUrl = super.getRootUrl();
    if (rootUrl == null || rootUrl.isEmpty()) {
      target = client.target(url);
    } else {
      target = client.target(rootUrl + super.getPath());
    }

    return target.proxy(DeviceProfileClient.class);
  }

  @Override
  protected String extractPath() {
    String result = "";
    try {
      URL urlObject = new URL(url);
      result = urlObject.getPath();
    } catch (MalformedURLException e) {
      throw new DataValidationException("the URL is malformed, meta.db.deviceprofile.url: " + url);
    }
    return result;
  }

}
