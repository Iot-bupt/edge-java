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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.edgexfoundry.domain.meta.AdminState;
import org.edgexfoundry.domain.meta.DeviceService;
import org.edgexfoundry.domain.meta.OperatingState;

public interface ServiceData {

  static final String TEST_SERVICE_NAME = "test service";
  static final String TEST_DESCRIPTION = "TEST_DESCRIPTION";
  static final AdminState TEST_ADMIN = AdminState.UNLOCKED;
  static final OperatingState TEST_OP = OperatingState.ENABLED;
  static final long TEST_LAST_CONNECTED = 1000000;
  static final long TEST_LAST_REPORTED = 1000000;
  static final String[] TEST_LABELS = {"MODBUS", "TEMP"};

  static DeviceService newTestInstance() {
    DeviceService s = new DeviceService();
    s.setAdminState(TEST_ADMIN);
    s.setDescription(TEST_DESCRIPTION);
    s.setLabels(TEST_LABELS);
    s.setLastConnected(TEST_LAST_CONNECTED);
    s.setLastReported(TEST_LAST_REPORTED);
    s.setName(TEST_SERVICE_NAME);
    s.setOperatingState(TEST_OP);
    s.setOrigin(CommonData.TEST_ORIGIN);
    return s;
  }

  static void checkTestData(DeviceService s, String id) {
    assertEquals("Device service id does not match expected", id, s.getId());
    assertEquals("Device service name does not match expected", TEST_SERVICE_NAME, s.getName());
    assertEquals("Device service origin does not match expected", CommonData.TEST_ORIGIN,
        s.getOrigin());
    assertEquals("Device service admin state does not match expected", TEST_ADMIN,
        s.getAdminState());
    assertEquals("Device service description does not match expected", TEST_DESCRIPTION,
        s.getDescription());
    assertEquals("Device service last connected does not match expected", TEST_LAST_CONNECTED,
        s.getLastConnected());
    assertEquals("Device service last reported does not match expected", TEST_LAST_REPORTED,
        s.getLastReported());
    assertEquals("Device service operating state does not match expected", TEST_OP,
        s.getOperatingState());
    assertArrayEquals("Device service labels does not match expected", TEST_LABELS, s.getLabels());
    assertNotNull("Device service modified date is null", s.getModified());
    assertNotNull("Device service created date is null", s.getCreated());
  }
}
