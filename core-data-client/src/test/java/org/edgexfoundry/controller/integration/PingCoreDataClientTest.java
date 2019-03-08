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
 * @microservice: core-data-client
 * @author: Jim White, Dell
 * @version: 1.0.0
 *******************************************************************************/

package org.edgexfoundry.controller.integration;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Field;

import org.edgexfoundry.controller.PingCoreDataClient;
import org.edgexfoundry.controller.impl.PingCoreDataClientImpl;
import org.edgexfoundry.test.category.RequiresCoreDataRunning;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category({RequiresCoreDataRunning.class})
public class PingCoreDataClientTest {

  private static final String ENDPT = "http://localhost:48080/api/v1/ping";

  private PingCoreDataClient client;

  // setup tests the add function
  @Before
  public void setup() throws Exception {
    client = new PingCoreDataClientImpl();
    setURL();
  }

  private void setURL() throws Exception {
    Class<?> clientClass = client.getClass();
    Field temp = clientClass.getDeclaredField("url");
    temp.setAccessible(true);
    temp.set(client, ENDPT);
  }

  @Test
  public void testPing() {
    assertEquals("Ping Core Data Micro Service failed", "pong", client.ping());
  }

}
