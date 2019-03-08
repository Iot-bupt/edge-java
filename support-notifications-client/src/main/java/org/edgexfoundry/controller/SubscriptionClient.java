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
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.edgexfoundry.support.domain.notifications.Subscription;

// TODO - put in org-edgexfoundry-support-notifications-client package
public interface SubscriptionClient {

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  String createSubscription(Subscription subscription);

  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  boolean updateSubscription(Subscription subscription);

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  List<Subscription> listAll();

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/slug/{slug:.+}")
  Subscription findBySlug(@PathParam("slug") String slug);

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/receiver/{receiver:.+}")
  List<Subscription> searchByReceiver(@PathParam("receiver") String receiver);

  @DELETE
  @Path("/slug/{slug:.+}")
  boolean deleteBySlug(@PathParam("slug") String slug);

}
