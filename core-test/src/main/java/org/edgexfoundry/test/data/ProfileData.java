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
import java.util.ArrayList;
import java.util.List;
import org.edgexfoundry.domain.meta.DeviceObject;
import org.edgexfoundry.domain.meta.DeviceProfile;

public interface ProfileData {

  static final String TEST_PROFILE_NAME = "Test Profile.NAME";
  static final String TEST_MAUFACTURER = "Test Manufacturer";
  static final String TEST_MODEL = "Test Model";
  static final String[] TEST_LABELS = {"labe1", "label2"};
  static final String TEST_DESCRIPTION = "Test Description";
  static final String TEST_OBJ = "{key1:value1, key2:value2}";
  static final List<DeviceObject> TEST_DEVICE_RES = new ArrayList<DeviceObject>();

  static DeviceProfile newTestInstance() {
    DeviceProfile p = new DeviceProfile();
    p.setDescription(TEST_DESCRIPTION);
    p.setLabels(TEST_LABELS);
    p.setManufacturer(TEST_MAUFACTURER);
    p.setModel(TEST_MODEL);
    p.setName(TEST_PROFILE_NAME);
    p.setObjects(TEST_OBJ);
    p.setDeviceResources(TEST_DEVICE_RES);
    p.setOrigin(CommonData.TEST_ORIGIN);
    return p;
  }

  static void checkTestData(DeviceProfile p, String id) {
    assertEquals("Device profile id does not match expected", id, p.getId());
    assertEquals("Device profile name does not match expected", TEST_PROFILE_NAME, p.getName());
    assertEquals("Device profile origin does not match expected", CommonData.TEST_ORIGIN,
        p.getOrigin());
    assertEquals("Device profile commands list size does not match expected", 1,
        p.getCommands().size());
    assertEquals("Device profile description does not match expected", TEST_DESCRIPTION,
        p.getDescription());
    assertArrayEquals("Device profile labels does not match expected", TEST_LABELS, p.getLabels());
    assertEquals("Device profile manufacturer does not match expected", TEST_MAUFACTURER,
        p.getManufacturer());
    assertEquals("Device profile model does not match expected", TEST_MODEL, p.getModel());
    assertEquals("Device profile object does not match expected", TEST_OBJ, p.getObjects());
    assertEquals("Device provide device resources does not match expected", TEST_DEVICE_RES,
        p.getDeviceResources());
    assertNotNull("Device profile modified date is null", p.getModified());
    assertNotNull("Device profile created date is null", p.getCreated());
  }

}
