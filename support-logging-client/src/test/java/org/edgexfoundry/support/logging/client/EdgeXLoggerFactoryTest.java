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
 * @microservice: support-logging-client library
 * @author: Jude Hung, Dell
 * @version: 1.0.0
 *******************************************************************************/

package org.edgexfoundry.support.logging.client;

import static org.junit.Assert.assertTrue;

import org.edgexfoundry.test.category.RequiresNone;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category({RequiresNone.class})
public class EdgeXLoggerFactoryTest {

  @Test
  public void getEdgeXLogger() {
    EdgeXLogger logger = EdgeXLoggerFactory.getEdgeXLogger("foo");
    assertTrue("Logger is null after get", logger != null);
  }

  @Test
  public void getEdgeXLoggerAfterCreated() {
    EdgeXLogger logger = EdgeXLoggerFactory.getEdgeXLogger("foo");
    assertTrue("Logger is null after get", logger != null);
    logger = EdgeXLoggerFactory.getEdgeXLogger("foo");
    assertTrue("Logger is null after get", logger != null);
  }

  @Test
  public void testGetEdgeXLogger() {
    EdgeXLoggerFactory.getEdgeXLogger(EdgeXLoggerFactoryTest.class);
  }


}
