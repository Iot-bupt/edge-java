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

package org.edgexfoundry.controller;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.edgexfoundry.domain.core.Event;
import org.edgexfoundry.domain.core.Reading;

public interface EventClient {

  @GET
  @Path("/{id}")
  Event event(@PathParam("id") String id);

  @GET
  List<Event> events();

  @GET
  @Path("/{start}/{end}/{limit}")
  List<Event> events(@PathParam("start") long start, @PathParam("end") long end,
      @PathParam("limit") int limit);

  @GET
  @Path("/device/{deviceId:.+}/{limit}")
  List<Event> eventsForDevice(@PathParam("deviceId") String deviceId,
      @PathParam("limit") int limit);

  @GET
  @Path("/device/{deviceId:.+}/valuedescriptor/{valuedescriptor:.+}/{limit}")
  List<Reading> readingsForDeviceAndValueDescriptor(@PathParam("deviceId") String deviceId,
      @PathParam("valuedescriptor") String valuedescriptor, @PathParam("limit") int limit);

  @POST
  @Consumes("application/json")
  String add(Event event);

  @PUT
  @Path("/id/{id}")
  boolean markedPushed(@PathParam("id") String id);

  @PUT
  @Consumes("application/json")
  boolean update(Event event);

  @DELETE
  @Path("/id/{id}")
  boolean delete(@PathParam("id") String id);

  @DELETE
  @Path("/device/{deviceId:.+}")
  int deleteByDevice(@PathParam("deviceId") String deviceId);

  @DELETE
  @Path("/scrub")
  long scrubPushedEvents();

  @DELETE
  @Path("/removeold/age/{age}")
  long scrubOldEvents(@PathParam("age") long age);

}
