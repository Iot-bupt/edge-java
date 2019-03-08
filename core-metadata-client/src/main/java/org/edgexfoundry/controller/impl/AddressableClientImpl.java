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

import org.edgexfoundry.controller.AddressableClient;
import org.edgexfoundry.domain.meta.Addressable;
import org.edgexfoundry.exception.controller.DataValidationException;
import org.edgexfoundry.meta.client.ConsulDiscoveryClientTemplate;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AddressableClientImpl extends ConsulDiscoveryClientTemplate
    implements AddressableClient {

  @Value("${meta.db.addressable.url}")
  private String url;

  @Override
  public Addressable addressable(String id) {
    return getClient().addressable(id);
  }

  @Override
  public List<Addressable> addressables() {
    return getClient().addressables();
  }

  @Override
  public Addressable addressableForName(String name) {
    return getClient().addressableForName(name);
  }

  @Override
  public List<Addressable> addressablesByAddress(String address) {
    return getClient().addressablesByAddress(address);
  }

  @Override
  public List<Addressable> addressablesByPort(String port) {
    return getClient().addressablesByPort(port);
  }

  @Override
  public List<Addressable> addressablesByTopic(String topic) {
    return getClient().addressablesByTopic(topic);
  }

  @Override
  public List<Addressable> addressablesByPublisher(String publisher) {
    return getClient().addressablesByPublisher(publisher);
  }

  @Override
  public String add(Addressable addressable) {
    return getClient().add(addressable);
  }

  @Override
  public boolean update(Addressable addressable) {
    return getClient().update(addressable);
  }

  @Override
  public boolean delete(String id) {
    return getClient().delete(id);
  }

  @Override
  public boolean deleteByName(String name) {
    return getClient().deleteByName(name);
  }

  private AddressableClient getClient() {
    ResteasyClient client = new ResteasyClientBuilder().build();
    ResteasyWebTarget target;

    String rootUrl = super.getRootUrl();
    if (rootUrl == null || rootUrl.isEmpty()) {
      target = client.target(url);
    } else {
      target = client.target(rootUrl + super.getPath());
    }

    return target.proxy(AddressableClient.class);
  }

  @Override
  protected String extractPath() {
    String result = "";
    try {
      URL urlObject = new URL(url);
      result = urlObject.getPath();
    } catch (MalformedURLException e) {
      throw new DataValidationException("the URL is malformed, meta.db.addressable.url: " + url);
    }
    return result;
  }

}
