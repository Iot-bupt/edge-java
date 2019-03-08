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

import static org.edgexfoundry.test.data.AddressableData.TEST_ADDR_NAME;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.edgexfoundry.domain.meta.AdminState;
import org.edgexfoundry.domain.meta.Device;
import org.edgexfoundry.domain.meta.OperatingState;

public interface DeviceData {

  static final String TEST_NAME = "TEST_DEVICE.NAME";
  static final String TEST_DESCRIPTION = "TEST_DESCRIPTION";
  static final AdminState TEST_ADMIN = AdminState.UNLOCKED;
  static final OperatingState TEST_OP = OperatingState.ENABLED;
  static final long TEST_LAST_CONNECTED = 1000000;
  static final long TEST_LAST_REPORTED = 1000000;
  static final String[] TEST_LABELS = {"MODBUS", "TEMP"};
  static final String TEST_LOCATION = "{40lat;45long}";

  static Device newTestInstance() {
    Device d = new Device();
    d.setAdminState(TEST_ADMIN);
    d.setDescription(TEST_DESCRIPTION);
    d.setLabels(TEST_LABELS);
    d.setLastConnected(TEST_LAST_CONNECTED);
    d.setLastReported(TEST_LAST_REPORTED);
    d.setLocation(TEST_LOCATION);
    d.setName(TEST_NAME);
    d.setOperatingState(TEST_OP);
    d.setOrigin(CommonData.TEST_ORIGIN);
    return d;
  }

  static void checkTestData(Device d, String id) {
    assertEquals("Device id does not match expected", id, d.getId());
    assertEquals("Device name does not match expected", TEST_NAME, d.getName());
    assertEquals("Device origin does not match expected", CommonData.TEST_ORIGIN, d.getOrigin());
    assertEquals("Device addressable does not match expected", TEST_ADDR_NAME,
        d.getAddressable().getName());
    assertEquals("Device admin state does not match expected", TEST_ADMIN, d.getAdminState());
    assertEquals("Device description does not match expected", TEST_DESCRIPTION,
        d.getDescription());
    assertArrayEquals("Device labels does not match expected", TEST_LABELS, d.getLabels());
    assertEquals("Device last connected does not match expected", TEST_LAST_CONNECTED,
        d.getLastConnected());
    assertEquals("Device last reported does not match expected", TEST_LAST_REPORTED,
        d.getLastReported());
    assertEquals("Device location does not match expected", TEST_LOCATION, d.getLocation());
    assertEquals("Device operating state does not match expected", TEST_OP, d.getOperatingState());
    assertEquals("Device profile does not match expected", ProfileData.TEST_PROFILE_NAME,
        d.getProfile().getName());
    assertEquals("Device service does not match expected", ServiceData.TEST_SERVICE_NAME,
        d.getService().getName());
    assertNotNull("Device modified date is null", d.getModified());
    assertNotNull("Device created date is null", d.getCreated());
  }

}
