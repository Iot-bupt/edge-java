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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.edgexfoundry.test.category.RequiresNone;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Marker;
import org.slf4j.helpers.BasicMarkerFactory;
import org.springframework.beans.factory.annotation.Value;

@Category({RequiresNone.class})
public class EdgeXLoggerTest {

  private static final String TEST_MSG = "Now is the time for all good men...";
  private static final String[] TEST_LABELS = {"label1", "label2"};
  private static final String LOGGER_NAME = "NOP";
  private static final Object TEST_OBJ = new Object();
  private static final String TEST_MARKER_NAME = "foo";

  private EdgeXLogger logger;
  private BasicMarkerFactory factory;
  private Marker marker;

  @Value("${logging.remote.enable:true}")
  private int remoteLogging;

  @Before
  public void setup() throws Exception {
    logger = EdgeXLoggerFactory.getEdgeXLogger(EdgeXLoggerTest.class);
    factory = new BasicMarkerFactory();
    marker = factory.getMarker(TEST_MARKER_NAME);
  }

  @Test
  public void testIsRemoteLoggingEnabled() {
    assertFalse("Is remote logging not returning correctly", logger.isRemoteLoggingEnabled());
  }

  @Test
  public void testDebug() {
    logger.debug(TEST_MSG);
  }

  @Test
  public void testDebugWithLabels() {
    logger.debug(TEST_MSG, TEST_LABELS);
  }

  @Test
  public void testDebugWithObject() {
    logger.debug(TEST_MSG, TEST_OBJ);
  }

  @Test
  public void testDebugWith2Object() {
    logger.debug(TEST_MSG, TEST_OBJ, TEST_OBJ);
  }

  @Test
  public void testDebugWithNObject() {
    logger.debug(TEST_MSG, TEST_OBJ, TEST_OBJ, TEST_OBJ);
  }

  @Test
  public void testDebugWithMarkerString() {
    logger.debug(marker, TEST_MSG);
  }

  @Test
  public void testDebugWithMarkerStringObj() {
    logger.debug(marker, TEST_MSG, TEST_OBJ);
  }

  @Test
  public void testDebugWithMarkerString2Obj() {
    logger.debug(marker, TEST_MSG, TEST_OBJ, TEST_OBJ);
  }

  @Test
  public void testDebugWithMarkerStringNObj() {
    logger.debug(marker, TEST_MSG, TEST_OBJ, TEST_OBJ, TEST_OBJ);
  }

  @Test
  public void testDebugWithStringThrowable() {
    logger.debug(TEST_MSG, new Exception(TEST_MSG));
  }

  @Test
  public void testDebugWithMarkerStringThrowable() {
    logger.debug(marker, TEST_MSG, new Exception(TEST_MSG));
  }

  @Test
  public void testError() {
    logger.error(TEST_MSG);
  }

  @Test
  public void testErrorWithLabels() {
    logger.error(TEST_MSG, TEST_LABELS);
  }

  @Test
  public void testErrorWithObject() {
    logger.error(TEST_MSG, TEST_OBJ);
  }

  @Test
  public void testErrorWith2Object() {
    logger.error(TEST_MSG, TEST_OBJ, TEST_OBJ);
  }

  @Test
  public void testErrorWithNObject() {
    logger.error(TEST_MSG, TEST_OBJ, TEST_OBJ, TEST_OBJ);
  }

  @Test
  public void testErrorWithMarkerString() {
    logger.error(marker, TEST_MSG);
  }

  @Test
  public void testErrorWithMarkerStringObj() {
    logger.error(marker, TEST_MSG, TEST_OBJ);
  }

  @Test
  public void testErrorWithMarkerString2Obj() {
    logger.error(marker, TEST_MSG, TEST_OBJ, TEST_OBJ);
  }

  @Test
  public void testErrorWithMarkerStringNObj() {
    logger.error(marker, TEST_MSG, TEST_OBJ, TEST_OBJ, TEST_OBJ);
  }

  @Test
  public void testErrorWithStringThrowable() {
    logger.error(TEST_MSG, new Exception(TEST_MSG));
  }

  @Test
  public void testErrorWithMarkerStringThrowable() {
    logger.error(marker, TEST_MSG, new Exception(TEST_MSG));
  }

  @Test
  public void testInfo() {
    logger.info(TEST_MSG);
  }

  @Test
  public void testInfoWithLabels() {
    logger.info(TEST_MSG, TEST_LABELS);
  }

  @Test
  public void testInfoWithObject() {
    logger.info(TEST_MSG, TEST_OBJ);
  }

  @Test
  public void testInfoWith2Object() {
    logger.info(TEST_MSG, TEST_OBJ, TEST_OBJ);
  }

  @Test
  public void testInfoWithNObject() {
    logger.info(TEST_MSG, TEST_OBJ, TEST_OBJ, TEST_OBJ);
  }

  @Test
  public void testInfoWithMarkerString() {
    logger.info(marker, TEST_MSG);
  }

  @Test
  public void testInfoWithMarkerStringObj() {
    logger.info(marker, TEST_MSG, TEST_OBJ);
  }

  @Test
  public void testInfoWithMarkerString2Obj() {
    logger.info(marker, TEST_MSG, TEST_OBJ, TEST_OBJ);
  }

  @Test
  public void testInfoWithMarkerStringNObj() {
    logger.info(marker, TEST_MSG, TEST_OBJ, TEST_OBJ, TEST_OBJ);
  }

  @Test
  public void testInfoWithStringThrowable() {
    logger.info(TEST_MSG, new Exception(TEST_MSG));
  }

  @Test
  public void testInfoWithMarkerStringThrowable() {
    logger.info(marker, TEST_MSG, new Exception(TEST_MSG));
  }

  @Test
  public void testWarn() {
    logger.warn(TEST_MSG);
  }

  @Test
  public void testWarnWithLabels() {
    logger.warn(TEST_MSG, TEST_LABELS);
  }

  @Test
  public void testWarnWithObject() {
    logger.warn(TEST_MSG, TEST_OBJ);
  }

  @Test
  public void testWarnWith2Object() {
    logger.warn(TEST_MSG, TEST_OBJ, TEST_OBJ);
  }

  @Test
  public void testWarnWithNObject() {
    logger.warn(TEST_MSG, TEST_OBJ, TEST_OBJ, TEST_OBJ);
  }

  @Test
  public void testWarnWithMarkerString() {
    logger.warn(marker, TEST_MSG);
  }

  @Test
  public void testWithMarkerStringObj() {
    logger.warn(marker, TEST_MSG, TEST_OBJ);
  }

  @Test
  public void testWarnWithMarkerString2Obj() {
    logger.warn(marker, TEST_MSG, TEST_OBJ, TEST_OBJ);
  }

  @Test
  public void testWarnWithMarkerStringNObj() {
    logger.warn(marker, TEST_MSG, TEST_OBJ, TEST_OBJ, TEST_OBJ);
  }

  @Test
  public void testWarnWithStringThrowable() {
    logger.warn(TEST_MSG, new Exception(TEST_MSG));
  }

  @Test
  public void testWarnWithMarkerStringThrowable() {
    logger.warn(marker, TEST_MSG, new Exception(TEST_MSG));
  }

  @Test
  public void testTrace() {
    logger.trace(TEST_MSG);
  }

  @Test
  public void testTraceWithLabels() {
    logger.trace(TEST_MSG, TEST_LABELS);
  }

  @Test
  public void testTraceWithObject() {
    logger.trace(TEST_MSG, TEST_OBJ);
  }

  @Test
  public void testTraceWith2Object() {
    logger.trace(TEST_MSG, TEST_OBJ, TEST_OBJ);
  }

  @Test
  public void testTraceWithNObject() {
    logger.trace(TEST_MSG, TEST_OBJ, TEST_OBJ, TEST_OBJ);
  }

  @Test
  public void testTraceWithMarkerString() {
    logger.trace(marker, TEST_MSG);
  }

  @Test
  public void testTraceWithMarkerStringObj() {
    logger.trace(marker, TEST_MSG, TEST_OBJ);
  }

  @Test
  public void testTraceWithMarkerString2Obj() {
    logger.trace(marker, TEST_MSG, TEST_OBJ, TEST_OBJ);
  }

  @Test
  public void testTraceWithMarkerStringNObj() {
    logger.trace(marker, TEST_MSG, TEST_OBJ, TEST_OBJ, TEST_OBJ);
  }

  @Test
  public void testTraceWithStringThrowable() {
    logger.trace(TEST_MSG, new Exception(TEST_MSG));
  }

  @Test
  public void testTraceWithMarkerStringThrowable() {
    logger.trace(marker, TEST_MSG, new Exception(TEST_MSG));
  }

  @Test
  public void testGetName() {
    assertTrue("Logger name of " + logger.getName() + " not a match",
        LOGGER_NAME.equals(logger.getName()));
  }

  @Test
  public void testIsTraceEnabled() {
    assertFalse("Logger trace is enabled and should not be by default", logger.isTraceEnabled());
  }

  @Test
  public void testIsTraceEnabledWithMarker() {
    assertFalse("Logger trace is enabled and should not be by default",
        logger.isTraceEnabled(marker));
  }

  @Test
  public void testIsDebugEnabled() {
    assertFalse("Logger debug is enabled and should not be by default", logger.isDebugEnabled());
  }

  @Test
  public void testIsDebugEnabledWithMarker() {
    assertFalse("Logger debug is enabled and should not be by default",
        logger.isDebugEnabled(marker));
  }

  @Test
  public void testIsInfoEnabled() {
    assertFalse("Logger info is enabled and should not be by default", logger.isInfoEnabled());
  }

  @Test
  public void testIsInfoEnabledWithMarker() {
    assertFalse("Logger info is enabled and should not be by default",
        logger.isInfoEnabled(marker));
  }

  @Test
  public void testIsWarnEnabled() {
    assertFalse("Logger warn is enabled and should not be by default", logger.isWarnEnabled());
  }

  @Test
  public void testIsWarnEnabledWithMarker() {
    assertFalse("Logger warn is enabled and should not be by default",
        logger.isWarnEnabled(marker));
  }

  @Test
  public void testIsErrorEnabled() {
    assertFalse("Logger error is enabled and should not be by default", logger.isErrorEnabled());
  }

  @Test
  public void testIsErrorEnabledWithMarker() {
    assertFalse("Logger error is enabled and should not be by default",
        logger.isErrorEnabled(marker));
  }

}
