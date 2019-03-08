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

import org.edgexfoundry.domain.meta.Addressable;
import org.edgexfoundry.domain.meta.Protocol;

public interface AddressableData {

  static final String TEST_ADDR_NAME = "TEST_ADDR.NAME";
  static final Protocol TEST_PROTOCOL = Protocol.HTTP;
  static final String TEST_ADDRESS = "localhost";
  static final int TEST_PORT = 48089;
  static final String TEST_PATH = "/api/v1/device";
  static final String TEST_PUBLISHER = "TEST_PUB";
  static final String TEST_USER = "edgexer";
  static final String TEST_PASSWORD = "password";
  static final String TEST_TOPIC = "device_topic";

  static Addressable newTestInstance() {
    Addressable a =
        new Addressable(TEST_ADDR_NAME, TEST_PROTOCOL, TEST_ADDRESS, TEST_PATH, TEST_PORT);
    // ordinarily, an Addressable would have TCP or message q info, but for
    // testing it has both
    a.setPublisher(TEST_PUBLISHER);
    a.setTopic(TEST_TOPIC);
    a.setUser(TEST_USER);
    a.setPassword(TEST_PASSWORD);
    a.setOrigin(CommonData.TEST_ORIGIN);
    return a;
  }

  static void checkTestData(Addressable a, String addressableId) {
    assertEquals("Addressable id does not match expected", addressableId, a.getId());
    assertEquals("Addressable name does not match expected", TEST_ADDR_NAME, a.getName());
    assertEquals("Addressable address does not match expected", TEST_ADDRESS, a.getAddress());
    assertEquals("Addressable origin does not match expected", CommonData.TEST_ORIGIN,
        a.getOrigin());
    assertEquals("Addressable password does not match expected", TEST_PASSWORD, a.getPassword());
    assertEquals("Addressable path does not match expected", TEST_PATH, a.getPath());
    assertEquals("Addressable port does not match expected", TEST_PORT, a.getPort());
    assertEquals("Addressable protocol does not match expected", TEST_PROTOCOL, a.getProtocol());
    assertEquals("Addressable publisher does not match expected", TEST_PUBLISHER, a.getPublisher());
    assertEquals("Addressable topic does not match expected", TEST_TOPIC, a.getTopic());
    assertEquals("Addressable user does not match expected", TEST_USER, a.getUser());
    assertNotNull("Addressable modified date is null", a.getModified());
    assertNotNull("Addressable created date is null", a.getCreated());
  }
}
