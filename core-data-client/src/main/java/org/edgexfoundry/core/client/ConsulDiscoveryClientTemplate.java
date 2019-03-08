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

package org.edgexfoundry.core.client;

import java.net.URI;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;

public abstract class ConsulDiscoveryClientTemplate {

  public static final String APP_ID = "edgex-core-data";
  private static boolean isCacheDiscoveryResult = false;

  @Value("${client.is-cache-discovery-result:false}")
  public static void setIsCacheDiscoveryResult(boolean flag) {
    isCacheDiscoveryResult = flag;
  }

  @Autowired
  private DiscoveryClient discoveryClient;

  private String rootUrl = "";
  private String path = "";

  @PostConstruct
  private void initClient() {
    rootUrl = retrieveUriFromDiscoveryClient();
    path = extractPath();
  }

  private String retrieveUriFromDiscoveryClient() {
    String result = "";
    if (discoveryClient == null) {
      return result;
    }

    List<ServiceInstance> list = discoveryClient.getInstances(APP_ID);
    if (list != null && !list.isEmpty()) {
      URI uri = list.get(0).getUri();
      if (uri != null) {
        result = uri.toString();
      }
    }
    return result;
  }

  protected abstract String extractPath();

  public String getRootUrl() {
    if (rootUrl == null || rootUrl.isEmpty() || !isCacheDiscoveryResult) {
      String retrievedUri = retrieveUriFromDiscoveryClient();
      if (retrievedUri != null && !retrievedUri.isEmpty()) {
        rootUrl = retrievedUri;
      }
    }
    return rootUrl;
  }

  public String getPath() {
    if (path == null || path.isEmpty()) {
      path = extractPath();
    }
    return path;
  }

}
