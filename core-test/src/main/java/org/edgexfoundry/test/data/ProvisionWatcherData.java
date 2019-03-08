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

import org.edgexfoundry.domain.meta.ProvisionWatcher;

public interface ProvisionWatcherData {

  static final String NAME = "TestWatcher.NAME";
  static final String KEY1 = "MAC";
  static final String KEY2 = "HTTP";
  static final String VAL1 = "00-05-1B-A1-99-99";
  static final String VAL2 = "10.0.1.1";

  static ProvisionWatcher newTestInstance() {
    ProvisionWatcher watcher = new ProvisionWatcher(NAME);
    watcher.addIdentifier(KEY1, VAL1);
    watcher.addIdentifier(KEY2, VAL2);
    watcher.setOrigin(CommonData.TEST_ORIGIN);
    return watcher;
  }

  static void checkTestData(ProvisionWatcher watcher, String id) {
    assertEquals("Watcher's id does not macth expected value", id, watcher.getId());
    assertEquals("Watcher's name does not macth expected name", NAME, watcher.getName());
    assertEquals("Watcher's origin does not macth expected origin", CommonData.TEST_ORIGIN,
        watcher.getOrigin());
    assertEquals("Watcher's identifiers size does not match expected size", 2,
        watcher.getIdentifiers().size());
    assertEquals("A watcher identifier does not match expected", VAL1,
        watcher.getIdentifiers().get(KEY1));
    assertEquals("A watcher identifier does not match expected", VAL2,
        watcher.getIdentifiers().get(KEY2));
    assertNotNull("Watcher's modified date is null", watcher.getModified());
    assertNotNull("Watcher's created date is null", watcher.getCreated());
  }

}
