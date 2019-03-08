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

import org.edgexfoundry.domain.core.Reading;

public interface ReadingClient {

  @GET
  @Path("/{id}")
  Reading reading(@PathParam("id") String id);

  @GET
  List<Reading> readings();

  @GET
  @Path("/device/{deviceId}/{limit}")
  List<Reading> readings(@PathParam("deviceId") String deviceId, @PathParam("limit") int limit);

  @GET
  @Path("/name/{name:.+}/{limit}")
  List<Reading> readingsByName(@PathParam("name") String name, @PathParam("limit") int limit);

  @GET
  @Path("/name/{name:.+}/device/{device}/{limit}")
  List<Reading> readingsByNameAndDevice(@PathParam("name") String name,
      @PathParam("device") String device, @PathParam("limit") int limit);

  @GET
  @Path("/uomlabel/{uomLabel:.+}/{limit}")
  List<Reading> readingsByUoMLabel(@PathParam("uomLabel") String uomLabel,
      @PathParam("limit") int limit);

  @GET
  @Path("/label/{label:.+}/{limit}")
  List<Reading> readingsByLabel(@PathParam("label") String label, @PathParam("limit") int limit);

  @GET
  @Path("/type/{type:.+}/{limit}")
  List<Reading> readingsByType(@PathParam("type") String type, @PathParam("limit") int limit);

  @POST
  @Consumes("application/json")
  String add(Reading reading);

  @PUT
  @Consumes("application/json")
  boolean update(Reading reading);

  @DELETE
  @Path("/id/{id}")
  boolean delete(@PathParam("id") String id);

}
