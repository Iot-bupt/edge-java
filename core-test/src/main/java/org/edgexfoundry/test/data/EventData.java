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
 * @microservice: core-test library
 * @author: Jim White, Dell
 * @version: 1.0.0
 *******************************************************************************/
package org.edgexfoundry.test.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.edgexfoundry.domain.core.Event;

public interface EventData {

  static final String TEST_DEVICE_ID = "test machine";

  static Event newTestInstance() {
    Event event = new Event(EventData.TEST_DEVICE_ID, null);
    event.setOrigin(CommonData.TEST_ORIGIN);
    return event;
  }

  static void checkTestData(Event event, String id) {
    checkTestData(event, id, EventData.TEST_DEVICE_ID, CommonData.DEFAULT_PUSHED,
        CommonData.TEST_ORIGIN, CommonData.MATCHING_TIMESTAMPS);
  }

  static void checkTestData(Event event, String id, String deviceId, long pushed, long origin,
      boolean matchingTimestamps) {
    assertEquals("ID doesn't match expected event id. Expected " + id + " got " + event.getId(), id,
        event.getId());
    assertEquals("Event device does not match saved device. Expected " + deviceId + " got "
        + event.getDevice(), deviceId, event.getDevice());
    assertEquals("Event pushed does not match saved pushed. Expected " + pushed + " got "
        + event.getPushed(), pushed, event.getPushed());
    assertEquals(
        "Event readings list size is bigger than one which does not match saved list. Expected 1 got "
            + event.getReadings().size(),
        1, event.getReadings().size());
    assertEquals("Event origin does not match saved origin. Expected " + origin + " got "
        + event.getOrigin(), origin, event.getOrigin());
    assertNotNull("Event modified date is null", event.getModified());
    assertNotNull("Event create date is null", event.getCreated());
    if (matchingTimestamps == true) {
      assertTrue(event.getModified() == event.getCreated());
    } else {
      assertTrue(event.getModified() != event.getCreated());
    }
  }

  static void checkTestDataWithoutReadings(Event event, String id) {
    assertEquals("ID doesn't match expected event id. Expected " + id + " got " + event.getId(), id,
        event.getId());
    assertEquals("Event device does not match saved device. Expected " + EventData.TEST_DEVICE_ID + " got "
        + event.getDevice(), EventData.TEST_DEVICE_ID, event.getDevice());
    assertEquals("Event origin does not match saved origin. Expected " + CommonData.TEST_ORIGIN + " got "
        + event.getOrigin(), CommonData.TEST_ORIGIN, event.getOrigin());
    assertNotNull("Event modified date is null", event.getModified());
    assertNotNull("Event create date is null", event.getCreated());
  }
}
