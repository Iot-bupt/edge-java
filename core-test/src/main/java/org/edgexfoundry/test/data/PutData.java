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

import java.util.ArrayList;
import java.util.List;

import org.edgexfoundry.domain.meta.Put;

public class PutData {

  static final String TEST_PATH = "/api/v1/device/{deviceId}/temp";
  static final String TEST_PARAM1 = "Temperature";
  static final String TEST_PARAM2 = "Humidity";

  static Put newTestInstance() {
    Put p = new Put();
    p.setPath(TEST_PATH);
    List<String> params = new ArrayList<>();
    params.add(TEST_PARAM1);
    params.add(TEST_PARAM2);
    p.setParameterNames(params);
    return p;
  }

  static void checkTestData(Put put) {
    assertEquals("Put's path does not match expected", TEST_PATH, put.getPath());
    assertEquals("Put's param do not match expected", TEST_PARAM1, put.getParameterNames().get(0));
    assertEquals("Put's param do not match expected", TEST_PARAM2, put.getParameterNames().get(1));
  }

}
