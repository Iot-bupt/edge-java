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

package org.edgexfoundry.support.logging.client;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.edgexfoundry.support.domain.logging.LogEntry;

public interface LoggingClient {
  
  @POST
  @Consumes("application/json")
  long addLogEntry(LogEntry entry);

  @GET
  @Path("/{limit}")
  List<LogEntry> getLogEntries(@PathParam("limit") int limit);

  @GET
  @Path("/{start}/{end}/{limit}")
  List<LogEntry> getLogEntriesByTime(@PathParam("start") long start, @PathParam("end") long end,
      @PathParam("limit") int limit);

  @GET
  @Path("/labels/{labels}/{start}/{end}/{limit}")
  List<LogEntry> getLogEntriesByLabels(@PathParam("labels") String labels,
      @PathParam("start") long start, @PathParam("end") long end, @PathParam("limit") int limit);

  @GET
  @Path("/originServices/{originServices}/{start}/{end}/{limit}")
  List<LogEntry> getLogEntriesByOriginServices(@PathParam("originServices") String originServices,
      @PathParam("start") long start, @PathParam("end") long end, @PathParam("limit") int limit);

  @GET
  @Path("/keywords/{keywords}/{start}/{end}/{limit}")
  List<LogEntry> getLogEntriesByKeywords(@PathParam("keywords") String keywords,
      @PathParam("start") long start, @PathParam("end") long end, @PathParam("limit") int limit);

  @GET
  @Path("/logLevels/{logLevels}/{start}/{end}/{limit}")
  List<LogEntry> getLogEntriesByLogLevels(@PathParam("logLevels") String logLevels,
      @PathParam("start") long start, @PathParam("end") long end, @PathParam("limit") int limit);

  @GET
  @Path("/logLevels/{logLevels}/originServices/{originServices}/{start}/{end}/{limit}")
  List<LogEntry> getLogEntriesByLogLevelsAndOriginServices(
      @PathParam("logLevels") String logLevels,
      @PathParam("originServices") String originServices, @PathParam("start") long start,
      @PathParam("end") long end, @PathParam("limit") int limit);

  @GET
  @Path("/logLevels/{logLevels}/originServices/{originServices}/labels/{labels}/{start}/{end}/{limit}")
  List<LogEntry> getLogEntriesByLogLevelsAndOriginServicesAndLabels(
      @PathParam("logLevels") String logLevels,
      @PathParam("originServices") String originServices, @PathParam("labels") String labels,
      @PathParam("start") long start, @PathParam("end") long end, @PathParam("limit") int limit);

  @GET
  @Path("/logLevels/{logLevels}/originServices/{originServices}/labels/{labels}/keywords/{keywords}/{start}/{end}/{limit}")
  List<LogEntry> getLogEntriesByLogLevelsAndOriginServicesAndLabelsAndKeywords(
      @PathParam("logLevels") String logLevels,
      @PathParam("originServices") String originServices, @PathParam("labels") String labels,
      @PathParam("keywords") String keywords, @PathParam("start") long start,
      @PathParam("end") long end, @PathParam("limit") int limit);

  @DELETE
  @Path("/{start}/{end}")
  int deleteLogEntriesByTimePeriod(@PathParam("start") long start, @PathParam("end") long end);

  @DELETE
  @Path("/keywords/{keywords}/{start}/{end}")
  int deleteLogEntriesByKeywords(@PathParam("keywords") String keywords,
      @PathParam("start") long start, @PathParam("end") long end);

  @DELETE
  @Path("/labels/{labels}/{start}/{end}")
  int deleteLogEntriesByLabels(@PathParam("labels") String labels, @PathParam("start") long start,
      @PathParam("end") long end);

  @DELETE
  @Path("/originServices/{originServices}/{start}/{end}")
  int deleteLogEntriesByOriginServices(@PathParam("originServices") String originServices,
      @PathParam("start") long start, @PathParam("end") long end);

  @DELETE
  @Path("/logLevels/{logLevels}/{start}/{end}")
  int deleteLogEntriesByLogLevels(@PathParam("logLevels") String logLevels,
      @PathParam("start") long start, @PathParam("end") long end);

  @DELETE
  @Path("/logLevels/{logLevels}/originServices/{originServices}/{start}/{end}")
  int deleteLogEntriesByLogLevelsAndOriginServices(@PathParam("logLevels") String logLevels,
      @PathParam("originServices") String originServices, @PathParam("start") long start,
      @PathParam("end") long end);

  @DELETE
  @Path("/logLevels/{logLevels}/originServices/{originServices}/labels/{labels}/{start}/{end}")
  int deleteLogEntriesByLogLevelsAndOriginServicesAndLabels(
      @PathParam("logLevels") String logLevels,
      @PathParam("originServices") String originServices, @PathParam("labels") String labels,
      @PathParam("start") long start, @PathParam("end") long end);

  @DELETE
  @Path("/logLevels/{logLevels}/originServices/{originServices}/labels/{labels}/keywords/{keywords}/{start}/{end}")
  int deleteLogEntriesByLogLevelsAndOriginServicesAndLabelsAndKeywords(
      @PathParam("logLevels") String logLevels,
      @PathParam("originServices") String originServices, @PathParam("labels") String labels,
      @PathParam("keywords") String keywords, @PathParam("start") long start,
      @PathParam("end") long end);

}
