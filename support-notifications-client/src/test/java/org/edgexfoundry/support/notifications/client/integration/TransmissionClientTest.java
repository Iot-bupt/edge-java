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
import org.edgexfoundry.controller.TransmissionClient;
import org.edgexfoundry.support.domain.notifications.Notification;
import org.edgexfoundry.support.domain.notifications.Transmission;
import org.edgexfoundry.support.notification.test.data.NotificationData;
import org.edgexfoundry.support.notifications.client.impl.NotificationClientImpl;
import org.edgexfoundry.support.notifications.client.impl.TransmissionClientImpl;
import org.edgexfoundry.test.category.RequiresMongoDB;
import org.edgexfoundry.test.category.RequiresSupportNotificationsRunning;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category({RequiresMongoDB.class, RequiresSupportNotificationsRunning.class})
public class TransmissionClientTest {

  private static final String NOTIFICATION_ENDPT = "http://localhost:48060/api/v1/notification";
  private static final String TRANSMISSION_ENDPT = "http://localhost:48060/api/v1/transmission";

  private static final int TEST_LIMIT = 10;
  private static final long TEST_START = 1469175494521L;
  private static final long TEST_END = 1472439915731L;

  private NotificationClient notificationClient;
  private TransmissionClient transmissionClient;
  private String slug;

  @Before
  public void setup() throws Exception {
    notificationClient = new NotificationClientImpl();
    transmissionClient = new TransmissionClientImpl();
    setURL();
    Notification notification = NotificationData.newTestInstance();
    slug = notificationClient.receiveNotification(notification);
    assertNotNull("Add of notification did not return a slug", slug);
    assertEquals("Slug returned does not equal slug name provided", NotificationData.TEST_SLUG,
        slug);
  }

  @After
  public void cleanup() {
    notificationClient.deleteBySlug(NotificationData.TEST_SLUG);
  }

  @Test
  public void testFindByNotificationSlug() {
    List<Transmission> searchResult =
        transmissionClient.findByNotificationSlug(NotificationData.TEST_SLUG, TEST_LIMIT);
    assertEquals("Size of list of transmissions returned by search was not expected", 0,
        searchResult.size());
  }

  @Test
  public void testFindByCreatedDuration() {
    List<Transmission> searchResult =
        transmissionClient.findByCreatedDuration(TEST_START, TEST_END, TEST_LIMIT);
    assertEquals("Size of list of transmissions returned by search was not expected", 0,
        searchResult.size());
  }

  @Test
  public void testFindByCreatedAfter() {
    List<Transmission> searchResult = transmissionClient.findByCreatedAfter(TEST_START, TEST_LIMIT);
    assertEquals("Size of list of transmissions returned by search was not expected", 0,
        searchResult.size());
  }

  @Test
  public void testFindByCreatedBefore() {
    List<Transmission> searchResult = transmissionClient.findByCreatedBefore(TEST_END, TEST_LIMIT);
    assertEquals("Size of list of transmissions returned by search was not expected", 0,
        searchResult.size());
  }

  @Test
  public void testFindEscalatedTransmissions() {
    List<Transmission> searchResult = transmissionClient.findEscalatedTransmissions(TEST_LIMIT);
    assertEquals("Size of list of transmissions returned by search was not expected", 0,
        searchResult.size());
  }

  @Test
  public void testFindFailedTransmissions() {
    List<Transmission> searchResult = transmissionClient.findFailedTransmissions(TEST_LIMIT);
    assertEquals("Size of list of transmissions returned by search was not expected", 0,
        searchResult.size());
  }

  @Test
  public void testDeleteOldSent() {
    assertTrue("Delete operation did not complete successfully",
        transmissionClient.deleteOldSent(Long.MAX_VALUE));
  }

  @Test
  public void testDeleteOldEscalated() {
    assertTrue("Delete operation did not complete successfully",
        transmissionClient.deleteOldEscalated(Long.MAX_VALUE));
  }

  @Test
  public void testDeleteOldAcknowledged() {
    assertTrue("Delete operation did not complete successfully",
        transmissionClient.deleteOldAcknowledged(Long.MAX_VALUE));
  }

  @Test
  public void testDeleteOldFailed() {
    assertTrue("Delete operation did not complete successfully",
        transmissionClient.deleteOldFailed(Long.MAX_VALUE));
  }

  private void setURL() throws Exception {
    Class<?> nClientClass = notificationClient.getClass();
    Field nURLField = nClientClass.getDeclaredField("url");
    nURLField.setAccessible(true);
    nURLField.set(notificationClient, NOTIFICATION_ENDPT);

    Class<?> tClientClass = transmissionClient.getClass();
    Field tURLField = tClientClass.getDeclaredField("url");
    tURLField.setAccessible(true);
    tURLField.set(transmissionClient, TRANSMISSION_ENDPT);
  }

}
