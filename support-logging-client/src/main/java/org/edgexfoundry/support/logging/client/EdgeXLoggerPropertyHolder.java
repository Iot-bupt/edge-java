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
 * @microservice: support-logging-client library
 * @author: Jude Hung, Dell
 * @version: 1.0.0
 *******************************************************************************/

package org.edgexfoundry.support.logging.client;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EdgeXLoggerPropertyHolder {

  @Value("${spring.application.name:unknown}")
  private String appName;

  @Value("${logging.remote.enable:false}")
  private boolean enableRemoteLogging;

  @Value("${logging.remote.url:http://localhost:48061/api/v1/logs}")
  private String loggingServiceURL;

  private static String staticAppName;

  private static boolean staticEnableRemoteLogging;

  private static String staticLoggingServiceURL;

  public static void setStaticAppName(String name) {
    EdgeXLoggerPropertyHolder.staticAppName = name;
  }

  public static void setStaticEnableRemoteLogging(boolean enable) {
    EdgeXLoggerPropertyHolder.staticEnableRemoteLogging = enable;
  }

  public static void setStaticLoggingServiceURL(String url) {
    EdgeXLoggerPropertyHolder.staticLoggingServiceURL = url;
  }

  @PostConstruct
  private void setStaticProperties() {
    setStaticAppName(this.appName);
    setStaticEnableRemoteLogging(this.enableRemoteLogging);
    setStaticLoggingServiceURL(this.loggingServiceURL);
  }

  public static String getApplicationName() {
    return staticAppName;
  }

  public static boolean isRemoteLoggingEnabled() {
    return staticEnableRemoteLogging;
  }

  public static String getLoggingServiceURL() {
    return staticLoggingServiceURL;
  }

}
