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

import static org.edgexfoundry.test.data.ScheduleEventData.TEST_SCHEDULE_EVENT_NAME;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.edgexfoundry.domain.meta.DeviceReport;

public interface ReportData {

  static final String TEST_RPT_NAME = "Test Report.NAME";
  static final String[] TEST_EXPECTED = {"vD1", "vD2"};

  static DeviceReport newTestInstance() {
    DeviceReport r = new DeviceReport(TEST_RPT_NAME, DeviceData.TEST_NAME,
        ScheduleEventData.TEST_SCHEDULE_EVENT_NAME, TEST_EXPECTED);
    r.setOrigin(CommonData.TEST_ORIGIN);
    return r;
  }

  static void checkTestData(DeviceReport r, String id) {
    assertEquals("Device report id does not match expected", id, r.getId());
    assertEquals("Device report name does not match expected", TEST_RPT_NAME, r.getName());
    assertEquals("Device report origin does not match expected", CommonData.TEST_ORIGIN,
        r.getOrigin());
    assertEquals("Device report device does not match expected", DeviceData.TEST_NAME,
        r.getDevice());
    assertEquals("Device report schedule event does not match expected", TEST_SCHEDULE_EVENT_NAME,
        r.getEvent());
    assertArrayEquals("Device report expected does not match expected", TEST_EXPECTED,
        r.getExpected());
    assertNotNull("Device report modified date is null", r.getModified());
    assertNotNull("Device report created date is null", r.getCreated());
  }

}
