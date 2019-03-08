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
 * @microservice: support-logging
 * @author: Jude Hung, Dell
 * @version: 1.0.0
 *******************************************************************************/

package org.edgexfoundry.support.logging.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.edgexfoundry.exception.controller.LimitExceededException;
import org.edgexfoundry.exception.controller.ServiceException;
import org.edgexfoundry.support.domain.logging.LogEntry;
import org.edgexfoundry.support.logging.controller.impl.LoggingControllerImpl;
import org.edgexfoundry.support.logging.service.LoggingService;
import org.edgexfoundry.test.category.RequiresNone;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Category(RequiresNone.class)
public class LoggingControllerTest {

  private static final String[] TEST_LABELS = {"test", "entry2"};
  private static final Level TEST_LEVEL = Level.DEBUG;
  private static final String TEST_MSG = "now is the time for all good men";
  private static final String TEST_ORIGIN_SERVICE = "core-data";
  private static final String[] TEST_SERVICES = {TEST_ORIGIN_SERVICE};
  private static final String[] TEST_KEYWORDS = {"test"};
  private static final Level[] TEST_LEVELS = {Level.DEBUG, Level.ERROR};

  @InjectMocks
  private LoggingControllerImpl l;

  @Mock
  private LoggingService s;

  @Value("${read.max.limit:100}")
  private int maxLimit;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void testAddLogEntry() {
    LogEntry entry = buildLogEntry(TEST_ORIGIN_SERVICE, TEST_LEVEL, TEST_LABELS, TEST_MSG);
    ResponseEntity<?> entity = l.addLogEntry(entry);
    HttpStatus status = entity.getStatusCode();
    assertNotNull("HttpStatus is null.", status);
    assertTrue("HttpStatus is not 2xx.", status.is2xxSuccessful());
    Long accepted = (Long) entity.getBody();
    assertTrue("accepted timestamp is zero or less.", accepted > 0);
  }

  @Test(expected = ServiceException.class)
  public void testAddLogEntryNoService() {
    // create logging controller with out injected service
    l = new LoggingControllerImpl();
    LogEntry entry = buildLogEntry(TEST_ORIGIN_SERVICE, TEST_LEVEL, TEST_LABELS, TEST_MSG);
    l.addLogEntry(entry);
  }

  @Test
  public void testGetLogEntries() {
    List<LogEntry> entries = l.getLogEntries(maxLimit);
    assertEquals("Expect 0 but got " + entries.size() + " logEntries.", entries.size(), 0);
  }

  @Test(expected = LimitExceededException.class)
  public void testGetLogEntriesOverMax() {
    l.getLogEntries(maxLimit + 1);
  }

  @Test(expected = ServiceException.class)
  public void testGetLogEntriesNoService() {
    // create logging controller with out injected service
    l = new LoggingControllerImpl();
    l.getLogEntries(maxLimit);
  }

  @Test
  public void testGetLogEntriesByTime() {
    List<LogEntry> entries = l.getLogEntriesByTime(0, Long.MAX_VALUE, maxLimit);
    assertEquals("Expect 0 but got " + entries.size() + " logEntries.", entries.size(), 0);
  }

  @Test(expected = LimitExceededException.class)
  public void testGetLogEntriesByTimeOverMax() {
    l.getLogEntriesByTime(0, Long.MAX_VALUE, maxLimit + 1);
  }

  @Test(expected = ServiceException.class)
  public void testGetLogEntriesBytimeNoService() {
    // create logging controller with out injected service
    l = new LoggingControllerImpl();
    l.getLogEntriesByTime(0, Long.MAX_VALUE, maxLimit);
  }

  @Test
  public void testGetLogEntriesByLabels() {
    List<LogEntry> entries = l.getLogEntriesByLabels(TEST_LABELS, 0, Long.MAX_VALUE, maxLimit);
    assertEquals("Expect 0 but got " + entries.size() + " logEntries.", entries.size(), 0);
  }

  @Test(expected = LimitExceededException.class)
  public void testGetLogEntriesByLabelsOverMax() {
    l.getLogEntriesByLabels(TEST_LABELS, 0, Long.MAX_VALUE, maxLimit + 1);
  }

  @Test(expected = ServiceException.class)
  public void testGetLogEntriesByLabelsNoService() {
    // create logging controller with out injected service
    l = new LoggingControllerImpl();
    l.getLogEntriesByLabels(TEST_LABELS, 0, Long.MAX_VALUE, maxLimit);
  }

  @Test
  public void testGetLogEntriesByOriginServices() {
    List<LogEntry> entries =
        l.getLogEntriesByOriginServices(TEST_SERVICES, 0, Long.MAX_VALUE, maxLimit);
    assertEquals("Expect 0 but got " + entries.size() + " logEntries.", entries.size(), 0);
  }

  @Test(expected = LimitExceededException.class)
  public void testGetLogEntriesByOriginServicesOverMax() {
    l.getLogEntriesByOriginServices(TEST_SERVICES, 0, Long.MAX_VALUE, maxLimit + 1);
  }

  @Test(expected = ServiceException.class)
  public void testGetLogEntriesByOriginServicesNoService() {
    // create logging controller with out injected service
    l = new LoggingControllerImpl();
    l.getLogEntriesByOriginServices(TEST_SERVICES, 0, Long.MAX_VALUE, maxLimit);
  }

  @Test
  public void testGetLogEntriesByKeywords() {
    List<LogEntry> entries = l.getLogEntriesByKeywords(TEST_KEYWORDS, 0, Long.MAX_VALUE, maxLimit);
    assertEquals("Expect 0 but got " + entries.size() + " logEntries.", entries.size(), 0);
  }

  @Test(expected = LimitExceededException.class)
  public void testGetLogEntriesByKeywordsOverMax() {
    l.getLogEntriesByKeywords(TEST_KEYWORDS, 0, Long.MAX_VALUE, maxLimit + 1);
  }

  @Test(expected = ServiceException.class)
  public void testGetLogEntriesByKeywordsNoService() {
    // create logging controller with out injected service
    l = new LoggingControllerImpl();
    l.getLogEntriesByKeywords(TEST_KEYWORDS, 0, Long.MAX_VALUE, maxLimit);
  }

  @Test
  public void testGetLogEntriesByLogLevels() {
    List<LogEntry> entries = l.getLogEntriesByLogLevels(TEST_LEVELS, 0, Long.MAX_VALUE, maxLimit);
    assertEquals("Expect 0 but got " + entries.size() + " logEntries.", entries.size(), 0);
  }

  @Test(expected = LimitExceededException.class)
  public void testGetLogEntriesByLogLevelsOverMax() {
    l.getLogEntriesByLogLevels(TEST_LEVELS, 0, Long.MAX_VALUE, maxLimit + 1);
  }

  @Test(expected = ServiceException.class)
  public void testGetLogEntriesByLogLevelsNoService() {
    // create logging controller with out injected service
    l = new LoggingControllerImpl();
    l.getLogEntriesByLogLevels(TEST_LEVELS, 0, Long.MAX_VALUE, maxLimit);
  }

  @Test
  public void testGetLogEntriesByLogLevelsAndOriginServices() {
    List<LogEntry> entries = l.getLogEntriesByLogLevelsAndOriginServices(TEST_LEVELS, TEST_SERVICES,
        0, Long.MAX_VALUE, maxLimit);
    assertEquals("Expect 0 but got " + entries.size() + " logEntries.", entries.size(), 0);
  }

  @Test(expected = LimitExceededException.class)
  public void testGetLogEntriesByLogLevelsAndOriginServicesOverMax() {
    l.getLogEntriesByLogLevelsAndOriginServices(TEST_LEVELS, TEST_SERVICES, 0, Long.MAX_VALUE,
        maxLimit + 1);
  }

  @Test(expected = ServiceException.class)
  public void testGetLogEntriesByLogLevelsAndOriginServicesNoService() {
    // create logging controller with out injected service
    l = new LoggingControllerImpl();
    l.getLogEntriesByLogLevelsAndOriginServices(TEST_LEVELS, TEST_SERVICES, 0, Long.MAX_VALUE,
        maxLimit);
  }

  @Test
  public void testGetLogEntriesByLogLevelsAndOriginServicesAndLabels() {
    List<LogEntry> entries = l.getLogEntriesByLogLevelsAndOriginServicesAndLabels(TEST_LEVELS,
        TEST_SERVICES, TEST_LABELS, 0, Long.MAX_VALUE, maxLimit);
    assertEquals("Expect 0 but got " + entries.size() + " logEntries.", entries.size(), 0);
  }

  @Test(expected = LimitExceededException.class)
  public void testGetLogEntriesByLogLevelsAndOriginServicesAndLabelsOverMax() {
    l.getLogEntriesByLogLevelsAndOriginServicesAndLabels(TEST_LEVELS, TEST_SERVICES, TEST_LABELS, 0,
        Long.MAX_VALUE, maxLimit + 1);
  }

  @Test(expected = ServiceException.class)
  public void testGetLogEntriesByLogLevelsAndOriginServicesAndLabelsNoService() {
    // create logging controller with out injected service
    l = new LoggingControllerImpl();
    l.getLogEntriesByLogLevelsAndOriginServicesAndLabels(TEST_LEVELS, TEST_SERVICES, TEST_LABELS, 0,
        Long.MAX_VALUE, maxLimit);
  }

  @Test
  public void testGetLogEntriesByLogLevelsAndOriginServicesAndLabelsAndKeywords() {
    List<LogEntry> entries = l.getLogEntriesByLogLevelsAndOriginServicesAndLabelsAndKeywords(
        TEST_LEVELS, TEST_SERVICES, TEST_LABELS, TEST_KEYWORDS, 0, Long.MAX_VALUE, maxLimit);
    assertEquals("Expect 0 but got " + entries.size() + " logEntries.", entries.size(), 0);
  }

  @Test(expected = LimitExceededException.class)
  public void testGetLogEntriesByLogLevelsAndOriginServicesAndLabelsAndKeywordsOverMax() {
    l.getLogEntriesByLogLevelsAndOriginServicesAndLabelsAndKeywords(TEST_LEVELS, TEST_SERVICES,
        TEST_LABELS, TEST_KEYWORDS, 0, Long.MAX_VALUE, maxLimit + 1);
  }

  @Test(expected = ServiceException.class)
  public void testGetLogEntriesByLogLevelsAndOriginServicesAndLabelsAndKeywordsNoService() {
    // create logging controller with out injected service
    l = new LoggingControllerImpl();
    l.getLogEntriesByLogLevelsAndOriginServicesAndLabelsAndKeywords(TEST_LEVELS, TEST_SERVICES,
        TEST_LABELS, TEST_KEYWORDS, 0, Long.MAX_VALUE, maxLimit);
  }

  @Test
  public void testDeleteLogEntriesByTimePeriod() {
    assertEquals("Expected no entries to be deleted",
        l.deleteLogEntriesByTimePeriod(0, Long.MAX_VALUE), 0);
  }

  @Test(expected = ServiceException.class)
  public void testDeleteLogEntriesByTimePeriodNoService() {
    // create logging controller with out injected service
    l = new LoggingControllerImpl();
    l.deleteLogEntriesByTimePeriod(0, Long.MAX_VALUE);
  }

  @Test
  public void testDeleteLogEntriesByKeywords() {
    assertEquals("Expected no entries to be deleted",
        l.deleteLogEntriesByKeywords(TEST_KEYWORDS, 0, Long.MAX_VALUE), 0);
  }

  @Test(expected = ServiceException.class)
  public void testDeleteLogEntriesByKeywordsNoService() {
    // create logging controller with out injected service
    l = new LoggingControllerImpl();
    l.deleteLogEntriesByKeywords(TEST_KEYWORDS, 0, Long.MAX_VALUE);
  }

  @Test
  public void testDeleteLogEntriesByLabels() {
    assertEquals("Expected no entries to be deleted",
        l.deleteLogEntriesByLabels(TEST_LABELS, 0, Long.MAX_VALUE), 0);
  }

  @Test(expected = ServiceException.class)
  public void testDeleteLogEntriesByLabelsNoService() {
    // create logging controller with out injected service
    l = new LoggingControllerImpl();
    l.deleteLogEntriesByLabels(TEST_LABELS, 0, Long.MAX_VALUE);
  }

  @Test
  public void testDeleteLogEntriesByOriginServices() {
    assertEquals("Expected no entries to be deleted",
        l.deleteLogEntriesByOriginServices(TEST_SERVICES, 0, Long.MAX_VALUE), 0);
  }

  @Test(expected = ServiceException.class)
  public void testDeleteLogEntriesByOriginServicesNoService() {
    // create logging controller with out injected service
    l = new LoggingControllerImpl();
    l.deleteLogEntriesByOriginServices(TEST_SERVICES, 0, Long.MAX_VALUE);
  }

  @Test
  public void testDeleteLogEntriesByLogLevels() {
    assertEquals("Expected no entries to be deleted",
        l.deleteLogEntriesByLogLevels(TEST_LEVELS, 0, Long.MAX_VALUE), 0);
  }

  @Test(expected = ServiceException.class)
  public void testDeleteLogEntriesByLogLevelsNoService() {
    // create logging controller with out injected service
    l = new LoggingControllerImpl();
    l.deleteLogEntriesByLogLevels(TEST_LEVELS, 0, Long.MAX_VALUE);
  }

  @Test
  public void testDeleteLogEntriesByLogLevelsAndOriginServices() {
    assertEquals("Expected no entries to be deleted",
        l.deleteLogEntriesByLogLevelsAndOriginServices(TEST_LEVELS, TEST_SERVICES, 0,
            Long.MAX_VALUE),
        0);
  }

  @Test(expected = ServiceException.class)
  public void testDeleteLogEntriesByLogLevelsAndOriginServicesNoService() {
    // create logging controller with out injected service
    l = new LoggingControllerImpl();
    l.deleteLogEntriesByLogLevelsAndOriginServices(TEST_LEVELS, TEST_SERVICES, 0, Long.MAX_VALUE);
  }

  @Test
  public void testDeleteLogEntriesByLogLevelsAndOriginServicesAndLabels() {
    assertEquals("Expected no entries to be deleted",
        l.deleteLogEntriesByLogLevelsAndOriginServicesAndLabels(TEST_LEVELS, TEST_SERVICES,
            TEST_LABELS, 0, Long.MAX_VALUE),
        0);
  }

  @Test(expected = ServiceException.class)
  public void testDeleteLogEntriesByLogLevelsAndOriginServicesAndLabelsNoService() {
    // create logging controller with out injected service
    l = new LoggingControllerImpl();
    l.deleteLogEntriesByLogLevelsAndOriginServicesAndLabels(TEST_LEVELS, TEST_SERVICES, TEST_LABELS,
        0, Long.MAX_VALUE);
  }

  @Test
  public void testDeleteLogEntriesByLogLevelsAndOriginServicesAndLabelsAndKeywords() {
    assertEquals("Expected no entries to be deleted",
        l.deleteLogEntriesByLogLevelsAndOriginServicesAndLabelsAndKeywords(TEST_LEVELS,
            TEST_SERVICES, TEST_LABELS, TEST_KEYWORDS, 0, Long.MAX_VALUE),
        0);
  }

  @Test(expected = ServiceException.class)
  public void testDeleteLogEntriesByLogLevelsAndOriginServicesAndLabelsAndKeywordsNoService() {
    // create logging controller with out injected service
    l = new LoggingControllerImpl();
    l.deleteLogEntriesByLogLevelsAndOriginServicesAndLabelsAndKeywords(TEST_LEVELS, TEST_SERVICES,
        TEST_LABELS, TEST_KEYWORDS, 0, Long.MAX_VALUE);
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
