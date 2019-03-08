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

package org.edgexfoundry.support.notifications.client.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.util.List;

import org.edgexfoundry.controller.SubscriptionClient;
import org.edgexfoundry.support.domain.notifications.Subscription;
import org.edgexfoundry.support.notification.test.data.SubscriptionData;
import org.edgexfoundry.support.notifications.client.impl.SubscriptionClientImpl;
import org.edgexfoundry.test.category.RequiresMongoDB;
import org.edgexfoundry.test.category.RequiresSupportNotificationsRunning;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category({RequiresMongoDB.class, RequiresSupportNotificationsRunning.class})
public class SubscriptionClientTest {

  private static final String ENDPT = "http://localhost:48060/api/v1/subscription";

  private SubscriptionClient client;
  String id;

  @Before // also tests add subscription and find by slug
  public void setup() throws Exception {
    client = new SubscriptionClientImpl();
    setURL();
    Subscription subscription = SubscriptionData.newTestInstance();
    String slug = client.createSubscription(subscription);
    assertNotNull("Add of subscription did not return a slug", slug);
    assertEquals("Slug returned does not equal slug name provided", SubscriptionData.TEST_SLUG,
        slug);
    Subscription sub = client.findBySlug(slug);
    id = sub.getId();
    assertNotNull("Id of subscription is null", id);
  }

  @After // also tests delete subscription
  public void cleanup() {
    assertTrue("Delete of subscription did not happen",
        client.deleteBySlug(SubscriptionData.TEST_SLUG));
  }

  @Test
  public void testupdateSubscription() {
    final String newRecv = "newReceiver";
    Subscription sub = client.findBySlug(SubscriptionData.TEST_SLUG);
    sub.setReceiver(newRecv);
    assertTrue("Update of subscription did not happen", client.updateSubscription(sub));
    Subscription sub2 = client.findBySlug(SubscriptionData.TEST_SLUG);
    assertEquals("Update of receiver did not happen as expected", newRecv, sub2.getReceiver());
  }

  @Test
  public void testListAll() {
    List<Subscription> subs = client.listAll();
    assertEquals("Size of list of subscriptions returned by search was not expected", 1,
        subs.size());
    SubscriptionData.checkTestData(subs.get(0), id);
  }

  @Test
  public void testsearchByReceiver() {
    List<Subscription> subs = client.searchByReceiver(SubscriptionData.TEST_REC);
    assertEquals("Size of list of subscriptions returned by search was not expected", 1,
        subs.size());
    SubscriptionData.checkTestData(subs.get(0), id);
  }

  private void setURL() throws Exception {
    Class<?> clientClass = client.getClass();
    Field temp = clientClass.getDeclaredField("url");
    temp.setAccessible(true);
    temp.set(client, ENDPT);
  }

}
