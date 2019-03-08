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

import static org.edgexfoundry.test.data.ScheduleData.TEST_SCHEDULE_NAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.edgexfoundry.domain.meta.Addressable;
import org.edgexfoundry.domain.meta.ScheduleEvent;

public interface ScheduleEventData {

  static final String TEST_SERVICE_NAME = "test service";
  static final String TEST_SCHEDULE_EVENT_NAME = "test schedule event";
  static final String TEST_PARAMS = "{key1:value1}";

  static ScheduleEvent newTestInstance() {
    Addressable a = AddressableData.newTestInstance();
    ScheduleEvent event = new ScheduleEvent(TEST_SCHEDULE_EVENT_NAME, a, TEST_PARAMS,
        TEST_SCHEDULE_NAME, TEST_SERVICE_NAME);
    event.setOrigin(CommonData.TEST_ORIGIN);
    return event;
  }

  static void checkTestData(ScheduleEvent event, String id) {
    assertEquals("Schedule event id does not match expected", id, event.getId());
    assertEquals("Schedule event name does not match expected", TEST_SCHEDULE_EVENT_NAME,
        event.getName());
    assertEquals("Schedule event origin does not match expected", CommonData.TEST_ORIGIN,
        event.getOrigin());
    assertEquals("Schedule event addressable name does not match expected",
        AddressableData.TEST_ADDR_NAME, event.getAddressable().getName());
    assertEquals("Schedule event service does not match expected", TEST_SERVICE_NAME,
        event.getService());
    assertEquals("Schedule event params does not match expected", TEST_PARAMS,
        event.getParameters());
    assertEquals("Schedule event associated schedule does not match expected", TEST_SCHEDULE_NAME,
        event.getSchedule());
    assertNotNull("Schedule event modified date is null", event.getModified());
    assertNotNull("Schedule event created date is null", event.getCreated());
  }
}
