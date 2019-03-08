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

package org.edgexfoundry.support.logging.client.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.edgexfoundry.exception.controller.DataValidationException;
import org.edgexfoundry.support.domain.logging.LogEntry;
import org.edgexfoundry.support.logging.client.ConsulDiscoveryClientTemplate;
import org.edgexfoundry.support.logging.client.LoggingClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LoggingClientImpl extends ConsulDiscoveryClientTemplate implements LoggingClient {

  @Value("${logging.remote.url}")
  String url;

  @Override
  public long addLogEntry(LogEntry entry) {
    return getClient().addLogEntry(entry);
  }

  @Override
  public List<LogEntry> getLogEntries(int limit) {
    return getClient().getLogEntries(limit);
  }

  @Override
  public List<LogEntry> getLogEntriesByTime(long start, long end, int limit) {
    return getClient().getLogEntriesByTime(start, end, limit);
  }

  @Override
  public List<LogEntry> getLogEntriesByLabels(String labels, long start, long end, int limit) {
    return getClient().getLogEntriesByLabels(labels, start, end, limit);
  }

  @Override
  public List<LogEntry> getLogEntriesByOriginServices(String originServices, long start, long end,
      int limit) {
    return getClient().getLogEntriesByOriginServices(originServices, start, end, limit);
  }

  @Override
  public List<LogEntry> getLogEntriesByKeywords(String keywords, long start, long end, int limit) {
    return getClient().getLogEntriesByKeywords(keywords, start, end, limit);
  }

  @Override
  public List<LogEntry> getLogEntriesByLogLevels(String logLevels, long start, long end,
      int limit) {
    return getClient().getLogEntriesByLogLevels(logLevels, start, end, limit);
  }

  @Override
  public List<LogEntry> getLogEntriesByLogLevelsAndOriginServices(String logLevels,
      String originServices, long start, long end, int limit) {
    return getClient().getLogEntriesByLogLevelsAndOriginServices(logLevels, originServices, start,
        end, limit);
  }

  @Override
  public List<LogEntry> getLogEntriesByLogLevelsAndOriginServicesAndLabels(String logLevels,
      String originServices, String labels, long start, long end, int limit) {
    return getClient().getLogEntriesByLogLevelsAndOriginServicesAndLabels(logLevels, originServices,
        labels, start, end, limit);
  }

  @Override
  public List<LogEntry> getLogEntriesByLogLevelsAndOriginServicesAndLabelsAndKeywords(
      String logLevels, String originServices, String labels, String keywords, long start, long end,
      int limit) {
    return getClient().getLogEntriesByLogLevelsAndOriginServicesAndLabelsAndKeywords(logLevels,
        originServices, labels, keywords, start, end, limit);
  }

  @Override
  public int deleteLogEntriesByTimePeriod(long start, long end) {
    return getClient().deleteLogEntriesByTimePeriod(start, end);
  }

  @Override
  public int deleteLogEntriesByKeywords(String keywords, long start, long end) {
    return getClient().deleteLogEntriesByKeywords(keywords, start, end);
  }

  @Override
  public int deleteLogEntriesByLabels(String labels, long start, long end) {
    return getClient().deleteLogEntriesByLabels(labels, start, end);
  }

  @Override
  public int deleteLogEntriesByOriginServices(String originServices, long start, long end) {
    return getClient().deleteLogEntriesByOriginServices(originServices, start, end);
  }

  @Override
  public int deleteLogEntriesByLogLevels(String logLevels, long start, long end) {
    return getClient().deleteLogEntriesByLogLevels(logLevels, start, end);
  }

  @Override
  public int deleteLogEntriesByLogLevelsAndOriginServices(String logLevels, String originServices,
      long start, long end) {
    return getClient().deleteLogEntriesByLogLevelsAndOriginServices(logLevels, originServices,
        start, end);
  }

  @Override
  public int deleteLogEntriesByLogLevelsAndOriginServicesAndLabels(String logLevels,
      String originServices, String labels, long start, long end) {
    return getClient().deleteLogEntriesByLogLevelsAndOriginServicesAndLabels(logLevels,
        originServices, labels, start, end);
  }

  @Override
  public int deleteLogEntriesByLogLevelsAndOriginServicesAndLabelsAndKeywords(String logLevels,
      String originServices, String labels, String keywords, long start, long end) {
    return getClient().deleteLogEntriesByLogLevelsAndOriginServicesAndLabelsAndKeywords(logLevels,
        originServices, labels, keywords, start, end);
  }

  private LoggingClient getClient() {
    ResteasyClient client = new ResteasyClientBuilder().build();
    ResteasyWebTarget target;

    String rootUrl = super.getRootUrl();
    if (rootUrl == null || rootUrl.isEmpty()) {
      target = client.target(url);
    } else {
      target = client.target(rootUrl + super.getPath());
    }

    return target.proxy(LoggingClient.class);
  }

  @Override
  protected String extractPath() {
    String result = "";
    try {
      URL urlObject = new URL(url);
      result = urlObject.getPath();
    } catch (MalformedURLException e) {
      throw new DataValidationException("the URL is malformed, logging.remote.url: " + url);
    }
    return result;
  }

}
