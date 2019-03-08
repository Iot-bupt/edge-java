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

import org.edgexfoundry.controller.NotificationClient;
import org.edgexfoundry.support.domain.notifications.Notification;
import org.edgexfoundry.support.notification.test.data.NotificationData;
import org.edgexfoundry.support.notifications.client.impl.NotificationClientImpl;
import org.edgexfoundry.test.category.RequiresMongoDB;
import org.edgexfoundry.test.category.RequiresSupportNotificationsRunning;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category({RequiresMongoDB.class, RequiresSupportNotificationsRunning.class})
public class NotificationsClientTest {

  private static final String ENDPT = "http://localhost:48060/api/v1/notification";
  private static final int MAX_LIMIT = 10;

  private NotificationClient client;
  private String slug;
  private String id;

  @Before // tests receive notification and find by slug
  public void setup() throws Exception {
    client = new NotificationClientImpl();
    setURL();
    Notification notification = NotificationData.newTestInstance();
    slug = client.receiveNotification(notification);
    assertNotNull("Add of notification did not return a slug", slug);
    assertEquals("Slug returned does not equal slug name provided", NotificationData.TEST_SLUG,
        slug);
    Notification note = client.findBySlug(slug);
    id = note.getId();
    assertNotNull("Id of notification is null", id);
  }

  @After // tests delete notification by slug
  public void cleanup() {
    assertTrue("Delete of notification did not happen",
        client.deleteBySlug(NotificationData.TEST_SLUG));
  }

  @Test
  public void testfindBySlug() {
    NotificationData.checkTestData(client.findBySlug(NotificationData.TEST_SLUG), id);
  }

  @Test
  public void testfindBySender() {
    List<Notification> notes = client.findBySender(NotificationData.TEST_SENDER, MAX_LIMIT);
    assertEquals("Size of list of notifications returned by search was not expected", 1,
        notes.size());
    NotificationData.checkTestData(notes.get(0), id);
  }

  @Test
  public void testfindByCreatedDuration() {
    List<Notification> notes = client.findByCreatedDuration(0, Long.MAX_VALUE, MAX_LIMIT);
    assertEquals("Size of list of notifications returned by search was not expected", 1,
        notes.size());
    NotificationData.checkTestData(notes.get(0), id);
  }

  @Test
  public void testfindByCreatedAfter() {
    List<Notification> notes = client.findByCreatedAfter(0, MAX_LIMIT);
    assertEquals("Size of list of notifications returned by search was not expected", 1,
        notes.size());
    NotificationData.checkTestData(notes.get(0), id);
  }

  @Test
  public void testfindByCreatedBefore() {
    List<Notification> notes = client.findByCreatedBefore(Long.MAX_VALUE, MAX_LIMIT);
    assertEquals("Size of list of notifications returned by search was not expected", 1,
        notes.size());
    NotificationData.checkTestData(notes.get(0), id);
  }

  @Test
  public void testfindNewNotifications() {
    List<Notification> notes = client.findNewNotifications(MAX_LIMIT);
    assertEquals("Size of list of notifications returned by search was not expected", 1,
        notes.size());
    NotificationData.checkTestData(notes.get(0), id);
  }

  @Test
  public void testDeleteOld() {
    assertTrue("Delete by method did not return expected result", client.deleteOld(100));
  }

  private void setURL() throws Exception {
    Class<?> clientClass = client.getClass();
    Field temp = clientClass.getDeclaredField("url");
    temp.setAccessible(true);
    temp.set(client, ENDPT);
  }

}
