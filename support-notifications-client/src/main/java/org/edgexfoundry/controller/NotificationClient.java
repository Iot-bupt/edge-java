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
 * @microservice: support-notifications-client library
 * @author: Cloud Tsai, Dell
 * @version: 1.0.0
 *******************************************************************************/

package org.edgexfoundry.controller;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.edgexfoundry.support.domain.notifications.Notification;

// TODO - put in org-edgexfoundry-support-notifications-client package
public interface NotificationClient {

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  String receiveNotification(Notification notification);

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/slug/{slug:.+}")
  Notification findBySlug(@PathParam("slug") String slug);

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/sender/{sender:.+}/{limit}")
  List<Notification> findBySender(@PathParam("sender") String sender,
      @PathParam("limit") int limit);

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/{start}/{end}/{limit}")
  List<Notification> findByCreatedDuration(@PathParam("start") long start,
      @PathParam("end") long end, @PathParam("limit") int limit);

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/start/{start}/{limit}")
  List<Notification> findByCreatedAfter(@PathParam("start") long start,
      @PathParam("limit") int limit);

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/end/{end}/{limit}")
  List<Notification> findByCreatedBefore(@PathParam("end") long end, @PathParam("limit") int limit);

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/new/{limit}")
  List<Notification> findNewNotifications(@PathParam("limit") int limit);

  @DELETE
  @Path("/slug/{slug:.+}")
  boolean deleteBySlug(@PathParam("slug") String slug);

  @DELETE
  @Path("/age/{age}")
  boolean deleteOld(@PathParam("age") long age);

}
