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

package org.edgexfoundry.support.logging.client.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.util.List;

import org.edgexfoundry.support.domain.logging.LogEntry;
import org.edgexfoundry.support.logging.client.LoggingClient;
import org.edgexfoundry.support.logging.client.impl.LoggingClientImpl;
import org.edgexfoundry.test.category.RequiresMongoDB;
import org.edgexfoundry.test.category.RequiresSupportLoggingRunning;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.event.Level;

@Category({RequiresMongoDB.class, RequiresSupportLoggingRunning.class})
public class LoggingClientTest {

  private static final String ENDPT = "http://localhost:48061/api/v1/logs";
  private static final String TEST_MSG = "Now is the time for all good men";
  private static final String[] TEST_LABELS = {"label1", "label2"};
  private static final Level TEST_LEVEL = Level.DEBUG;
  private static final String TEST_ORIGIN_SERVICE = "core-data";

  private static final String TEST_SERVICES_CSV = "core-data,foobar";
  private static final String TEST_LABELS_CSV = "label1,label2";
  private static final String TEST_LEVELS_CSV = "DEBUG,ERROR";
  private static final String TEST_KEYWORDS_CSV = "time,good";
  private static final int MAX_LIMIT = 100;

  private LoggingClient client;

  // setup tests the add function and get entries max
  @Before
  public void setup() throws Exception {
    client = new LoggingClientImpl();
    setURL();
    client.addLogEntry(buildLogEntry(TEST_ORIGIN_SERVICE, TEST_LEVEL, TEST_LABELS, TEST_MSG));
    assertEquals("Setup up did create the single log entry", 1,
        client.getLogEntries(MAX_LIMIT).size());
  }

  // cleanup tests the delete by time function and get entries max
  @After
  public void cleanup() {
    client.deleteLogEntriesByTimePeriod(0, Long.MAX_VALUE);
    assertEquals("Clean up did not remove all long enteries", 0,
        client.getLogEntries(MAX_LIMIT).size());
  }

  private void setURL() throws Exception {
    Class<?> clientClass = client.getClass();
    Field temp = clientClass.getDeclaredField("url");
    temp.setAccessible(true);
    temp.set(client, ENDPT);
  }

  @Test
  public void testGetLogEntriesByTime() {
    List<LogEntry> entries = client.getLogEntriesByTime(-1, Long.MAX_VALUE, MAX_LIMIT);
    assertTrue("Get log entries did not return the single log entry", entries.size() == 1);
  }

  @Test
  public void testGetLogEntriesByLabels() {
    List<LogEntry> entries =
        client.getLogEntriesByLabels(TEST_LABELS_CSV, -1, Long.MAX_VALUE, MAX_LIMIT);
    assertEquals("Get log entries did not return the single log entry", 1, entries.size());
  }

  @Test
  public void testGetLogEntriesByOriginServices() {
    List<LogEntry> entries =
        client.getLogEntriesByOriginServices(TEST_SERVICES_CSV, -1, Long.MAX_VALUE, MAX_LIMIT);
    assertEquals("Get log entries did not return the single log entry", 1, entries.size());
  }

  @Test
  public void testGetLogEntriesByKeywords() {
    List<LogEntry> entries =
        client.getLogEntriesByKeywords(TEST_KEYWORDS_CSV, -1, Long.MAX_VALUE, MAX_LIMIT);
    assertEquals("Get log entries did not return the single log entry", 1, entries.size());
  }

  @Test
  public void testGetLogEntriesByLogLevel() {
    List<LogEntry> entries =
        client.getLogEntriesByLogLevels(TEST_LEVELS_CSV, -1, Long.MAX_VALUE, MAX_LIMIT);
    assertEquals("Get log entries did not return the single log entry", 1, entries.size());
  }

  @Test
  public void testGetLogEntriesByLogLevelAndOriginServices() {
    List<LogEntry> entries = client.getLogEntriesByLogLevelsAndOriginServices(TEST_LEVELS_CSV,
        TEST_SERVICES_CSV, -1, Long.MAX_VALUE, MAX_LIMIT);
    assertEquals("Get log entries did not return the single log entry", 1, entries.size());
  }

  @Test
  public void testGetLogEntriesByLogLevelAndOriginServicesAndLabels() {
    List<LogEntry> entries = client.getLogEntriesByLogLevelsAndOriginServicesAndLabels(
        TEST_LEVELS_CSV, TEST_SERVICES_CSV, TEST_LABELS_CSV, -1, Long.MAX_VALUE, MAX_LIMIT);
    assertEquals("Get log entries did not return the single log entry", 1, entries.size());
  }

  @Test
  public void testGetLogEntriesByLogLevelAndOriginServicesAndLabelsAndKeywords() {
    List<LogEntry> entries =
        client.getLogEntriesByLogLevelsAndOriginServicesAndLabelsAndKeywords(TEST_LEVELS_CSV,
            TEST_SERVICES_CSV, TEST_LABELS_CSV, TEST_KEYWORDS_CSV, -1, Long.MAX_VALUE, MAX_LIMIT);
    assertEquals("Get log entries did not return the single log entry", 1, entries.size());
  }

  @Test
  public void testDeleteLogEntriesByTimePeriod() {
    assertEquals("Delete did not remove log entry", 1,
        client.deleteLogEntriesByTimePeriod(-1, Long.MAX_VALUE));
  }

  @Test
  public void testDeleteLogEntriesByKeywords() {
    assertEquals("Delete did not remove log entry", 1,
        client.deleteLogEntriesByKeywords(TEST_KEYWORDS_CSV, -1, Long.MAX_VALUE));
  }

  @Test
  public void testDeleteLogEntriesByLabels() {
    assertEquals("Delete did not remove log entry", 1,
        client.deleteLogEntriesByLabels(TEST_LABELS_CSV, -1, Long.MAX_VALUE));
  }

  @Test
  public void testDeleteLogEntriesByOriginServices() {
    assertEquals("Delete did not remove log entry", 1,
        client.deleteLogEntriesByOriginServices(TEST_SERVICES_CSV, -1, Long.MAX_VALUE));
  }

  @Test
  public void testDeleteLogEntriesByLogLevels() {
    assertEquals("Delete did not remove log entry", 1,
        client.deleteLogEntriesByLogLevels(TEST_LEVELS_CSV, -1, Long.MAX_VALUE));
  }

  @Test
  public void testDeleteLogEntriesByLogLevelsAndOriginServices() {
    assertEquals("Delete did not remove log entry", 1,
        client.deleteLogEntriesByLogLevelsAndOriginServices(TEST_LEVELS_CSV, TEST_SERVICES_CSV, -1,
            Long.MAX_VALUE));
  }

  @Test
  public void testDeleteLogEntriesByLogLevelsAndOriginServicesAndLabels() {
    assertEquals("Delete did not remove log entry", 1,
        client.deleteLogEntriesByLogLevelsAndOriginServicesAndLabels(TEST_LEVELS_CSV,
            TEST_SERVICES_CSV, TEST_LABELS_CSV, -1, Long.MAX_VALUE));
  }

  @Test
  public void testDeleteLogEntriesByLogLevelsAndOriginServicesAndLabelsAndKeywords() {
    assertEquals("Delete did not remove log entry", 1,
        client.deleteLogEntriesByLogLevelsAndOriginServicesAndLabelsAndKeywords(TEST_LEVELS_CSV,
            TEST_SERVICES_CSV, TEST_LABELS_CSV, TEST_KEYWORDS_CSV, -1, Long.MAX_VALUE));
  }


  private LogEntry buildLogEntry(String originService, Level LogLevel, String[] labels,
      String message) {
    LogEntry entry = new LogEntry();
    entry.setOriginService(originService);
    entry.setLabels(labels);
    entry.setLogLevel(LogLevel);
    entry.setMessage(message);
    return entry;
  }

}
