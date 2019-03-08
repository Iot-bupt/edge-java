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

import org.edgexfoundry.domain.core.Reading;

public interface ReadingData {

  static final String TEST_NAME = "Temperature";
  static final String TEST_VALUE = "45";

  // NOTE: Not setting Device automatically because it will often ten
  static Reading newTestInstance() {
    Reading reading = new Reading(ReadingData.TEST_NAME, ReadingData.TEST_VALUE);
    reading.setOrigin(CommonData.TEST_ORIGIN);
    reading.setDevice(EventData.TEST_DEVICE_ID);
    return reading;
  }

  static void checkTestData(Reading reading, String readingId) {
    checkTestData(reading, readingId, ReadingData.TEST_NAME, ReadingData.TEST_VALUE,
        CommonData.DEFAULT_PUSHED, EventData.TEST_DEVICE_ID, CommonData.TEST_ORIGIN,
        CommonData.MATCHING_TIMESTAMPS);
  }

  static void checkTestData(Reading reading, String readingId, String name, String value,
      long pushed, String device, long origin, boolean matchingTimestamps) {
    assertEquals(
        "ID doesn't match expected reading id.  Expected " + readingId + " got " + reading.getId(),
        readingId, reading.getId());
    assertEquals(
        "Name doesn't match expected reading name. Expected " + name + " got " + reading.getName(),
        name, reading.getName());
    assertEquals("Value doesn't match expected reading value. Expected " + value + " got "
        + reading.getValue(), value, reading.getValue());
    assertEquals("Reading origin does not match saved origin. Expected " + origin + " got "
        + reading.getOrigin(), origin, reading.getOrigin());
    assertEquals(
        "Pushed does not match saved push. Expected " + pushed + " got " + reading.getPushed(),
        pushed, reading.getPushed());
    assertEquals("Reading device name does not match expected value.  Expected " + device + " got "
        + reading.getDevice(), device, reading.getDevice());
    assertNotNull("Reading modified date is null", reading.getModified());
    assertNotNull("Reading create date is null", reading.getCreated());
    if (matchingTimestamps == true) {
      assertTrue(reading.getModified() == reading.getCreated());
    } else {
      assertTrue(reading.getModified() != reading.getCreated());
    }
  }

}
