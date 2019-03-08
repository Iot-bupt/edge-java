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
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.edgexfoundry.domain.meta.Response;

public interface ResponseData {

  static final String TEST_CODE = "200";
  static final String TEST_DESCRIPTION = "ok";
  static final String TEST_EXPECTED_VALUE1 = "temperature";
  static final String TEST_EXPECTED_VALUE2 = "humidity";

  static Response newTestInstance() {
    List<String> expected = new ArrayList<>();
    expected.add(TEST_EXPECTED_VALUE1);
    expected.add(TEST_EXPECTED_VALUE2);
    return new Response(TEST_CODE, TEST_DESCRIPTION, expected);
  }

  static void checkTestData(Response r) {
    assertEquals("Response code does not match expected", TEST_CODE, r.getCode());
    assertEquals("Response description does not match expected", TEST_DESCRIPTION,
        r.getDescription());
    assertTrue("Response expected values do not match expected",
        r.getExpectedValues().contains(TEST_EXPECTED_VALUE1));
    assertTrue("Response expected values do not match expected",
        r.getExpectedValues().contains(TEST_EXPECTED_VALUE2));
  }

}
