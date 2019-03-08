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

package org.edgexfoundry.controller;

import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.edgexfoundry.domain.meta.Addressable;
import org.edgexfoundry.domain.meta.DeviceService;

public interface DeviceServiceClient {
  @GET
  @Path("/{id}")
  DeviceService deviceService(@PathParam("id") String id);

  @GET
  List<DeviceService> deviceServices();

  @GET
  @Path("/name/{name:.+}")
  DeviceService deviceServiceForName(@PathParam("name") String name);

  @GET
  @Path("/addressable/{addressableId:.+}")
  List<DeviceService> deviceServicesForAddressable(
      @PathParam("addressableId") String addressableId);

  @GET
  @Path("/addressablename/{addressablename:.+}")
  List<DeviceService> deviceServicesForAddressableByName(
      @PathParam("addressablename") String addressablename);

  @GET
  @Path("/label/{label:.+}")
  List<DeviceService> deviceServicesByLabel(@PathParam("label") String label);

  @GET
  @Path("/deviceaddressables/{id}")
  Set<Addressable> addressablesForAssociatedDevices(@PathParam("id") String id);

  @GET
  @Path("/deviceaddressablesbyname/{name:.+}")
  Set<Addressable> addressablesForAssociatedDevicesByName(@PathParam("name") String name);

  @POST
  @Consumes("application/json")
  String add(DeviceService deviceService);

  @PUT
  @Consumes("application/json")
  boolean update(DeviceService deviceService);

  @PUT
  @Consumes("application/json")
  @Path("/{id}/lastconnected/{time}")
  boolean updateLastConnected(@PathParam("id") String id, @PathParam("time") long time);

  @PUT
  @Consumes("application/json")
  @Path("/name/{name:.+}/lastconnected/{time}")
  boolean updateLastConnectedByName(@PathParam("name") String name, @PathParam("time") long time);

  @PUT
  @Consumes("application/json")
  @Path("/{id}/lastreported/{time}")
  boolean updateLastReported(@PathParam("id") String id, @PathParam("time") long time);

  @PUT
  @Consumes("application/json")
  @Path("/name/{name:.+}/lastreported/{time}")
  boolean updateLastReportedByName(@PathParam("name") String name, @PathParam("time") long time);

  @PUT
  @Consumes("application/json")
  @Path("/{id}/opstate/{opState}")
  boolean updateOpState(@PathParam("id") String id, @PathParam("opState") String opState);

  @PUT
  @Consumes("application/json")
  @Path("/name/{name:.+}/opstate/{opState}")
  boolean updateOpStateByName(@PathParam("name") String name, @PathParam("opState") String opState);

  @PUT
  @Consumes("application/json")
  @Path("/{id}/adminstate/{adminState}")
  boolean updateAdminState(@PathParam("id") String id, @PathParam("adminState") String adminState);

  @PUT
  @Consumes("application/json")
  @Path("/name/{name:.+}/adminstate/{adminState}")
  boolean updateAdminStateByName(@PathParam("name") String name,
      @PathParam("adminState") String adminState);


  @DELETE
  @Path("/id/{id}")
  boolean delete(@PathParam("id") String id);

  @DELETE
  @Path("/name/{name:.+}")
  boolean deleteByName(@PathParam("name") String name);

}
