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

import static org.edgexfoundry.test.data.GetData.TEST_PATH;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.edgexfoundry.domain.meta.Command;
import org.edgexfoundry.domain.meta.Get;
import org.edgexfoundry.domain.meta.Put;

public interface CommandData {

  static final String TEST_CMD_NAME = "setTemp";

  static Command newTestInstance() {
    Command c = new Command();
    Get g = new Get();
    g.setPath(TEST_PATH);
    g.addResponse(ResponseData.newTestInstance());
    c.setGet(g);
    Put p = PutData.newTestInstance();
    p.addResponse(ResponseData.newTestInstance());
    c.setPut(p);
    c.setName(TEST_CMD_NAME);
    c.setOrigin(CommonData.TEST_ORIGIN);
    return c;
  }

  static void checkTestData(Command c, String id) {
    assertEquals("Command id does not match expected", id, c.getId());
    assertEquals("Command name does not match expected", TEST_CMD_NAME, c.getName());
    assertEquals("Command origin does not match expected", CommonData.TEST_ORIGIN, c.getOrigin());
    assertEquals("Command's Get path does not match expected", TEST_PATH, c.getGet().getPath());
    ResponseData.checkTestData(c.getGet().getResponses().get(0));
    PutData.checkTestData(c.getPut());
    ResponseData.checkTestData(c.getPut().getResponses().get(0));
    assertNotNull("Command modified date is null", c.getModified());
    assertNotNull("Command created date is null", c.getCreated());
  }
}
