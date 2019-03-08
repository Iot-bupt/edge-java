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

package org.edgexfoundry.support.logging.controller.integration;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Field;
import java.util.List;

import org.edgexfoundry.EdgeXSupportLoggingApplication;
import org.edgexfoundry.exception.controller.LimitExceededException;
import org.edgexfoundry.exception.controller.ServiceException;
import org.edgexfoundry.support.domain.logging.LogEntry;
import org.edgexfoundry.support.logging.controller.impl.LoggingControllerImpl;
import org.edgexfoundry.support.logging.dao.LogEntryDAO;
import org.edgexfoundry.support.logging.service.LoggingService;
import org.edgexfoundry.test.category.RequiresMongoDB;
import org.edgexfoundry.test.category.RequiresSpring;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = EdgeXSupportLoggingApplication.class)
@TestPropertySource(locations = "classpath:test-mongodb.properties")
@Category({RequiresMongoDB.class, RequiresSpring.class})
public class LoggingControllerTest {

  private static final String[] TEST_LABELS = {"test", "entry2"};
  private static final Level TEST_LEVEL = Level.DEBUG;
  private static final String TEST_MSG = "now is the time for all good men";
  private static final String TEST_ORIGIN_SERVICE = "core-data";
  private static final String[] TEST_SERVICES = {TEST_ORIGIN_SERVICE};
  private static final String[] TEST_KEYWORDS = {"time"};
  private static final Level[] TEST_LEVELS = {Level.DEBUG, Level.ERROR};
  private static final String[] BAD_ARRAY = {"foo"};
  private static final int MAX_LIMIT = 10;

  @Autowired
  private LoggingControllerImpl controller;

  @Autowired
  private LoggingService service;

  @Autowired
  private MongoTemplate mongoTemplate;

  @Autowired
  protected LogEntryDAO logEntryDAO;


  public void cleanPersistence() {
    mongoTemplate.remove(new Query(), "logEntry");
  }

  public void addEntry() {
    LogEntry debug = buildLogEntry(TEST_ORIGIN_SERVICE, TEST_LEVEL, TEST_LABELS, TEST_MSG);
    logEntryDAO.save(debug);
  }

  @Before
  public void setUp() throws InterruptedException {
    cleanPersistence();
    addEntry();
  }

  @After
  public void cleanup() {
    cleanPersistence();
    changeControllerService(true);
  }

  @Test(expected = ServiceException.class)
  public void testAddLogEntryException() {
    LogEntry entry = buildLogEntry(TEST_ORIGIN_SERVICE, TEST_LEVEL, TEST_LABELS, TEST_MSG);
    changeControllerService(false);
    controller.addLogEntry(entry);
  }

  @Test
  public void testGetLogEntries() {
    List<LogEntry> entries = controller.getLogEntries(MAX_LIMIT);
    assertEquals("Number of log entries returned by query should be 1", 1, entries.size());
  }

  @Test(expected = ServiceException.class)
  public void testGetLogEntriesException() throws Exception {
    changeControllerService(false);
    controller.getLogEntries(MAX_LIMIT);
  }

  @Test(expected = LimitExceededException.class)
  public void testGetLogEntriesMaxException() throws Exception {
    controller.getLogEntries(Integer.MAX_VALUE);
  }

  @Test
  public void testGetLogEntriesByTime() {
    assertEquals("Number of log entries returned by query should be 1", 1,
        controller.getLogEntriesByTime(-1, Long.MAX_VALUE, MAX_LIMIT).size());
  }

  @Test
  public void testGetLogEntriesByTimeOutOfRange() {
    assertEquals("Number of log entries returned by query should be 0", 0,
        controller.getLogEntriesByTime(Long.MAX_VALUE, Long.MAX_VALUE, MAX_LIMIT).size());
  }

  @Test(expected = ServiceException.class)
  public void testGetLogEntriesByTimeException() throws Exception {
    changeControllerService(false);
    controller.getLogEntriesByTime(-1, Long.MAX_VALUE, MAX_LIMIT);
  }

  @Test(expected = LimitExceededException.class)
  public void testGetLogEntriesByTimeMaxException() throws Exception {
    controller.getLogEntriesByTime(-1, Long.MAX_VALUE, Integer.MAX_VALUE);
  }

  @Test
  public void testGetLogEntriesByLabel() {
    assertEquals("Number of log entries returned by query should be 1", 1,
        controller.getLogEntriesByLabels(TEST_LABELS, -1, Long.MAX_VALUE, MAX_LIMIT).size());
  }

  @Test
  public void testGetLogEntriesByLabelsNoMatch() {
    assertEquals("Number of log entries returned by query should be 0", 0, controller
        .getLogEntriesByLabels(TEST_LABELS, Long.MAX_VALUE, Long.MAX_VALUE, MAX_LIMIT).size());
  }

  @Test(expected = ServiceException.class)
  public void testGetLogEntriesByLabelsException() throws Exception {
    changeControllerService(false);
    controller.getLogEntriesByLabels(TEST_LABELS, -1, Long.MAX_VALUE, MAX_LIMIT);
  }

  @Test(expected = LimitExceededException.class)
  public void testGetLogEntriesByLabelsMaxException() throws Exception {
    controller.getLogEntriesByLabels(TEST_LABELS, -1, Long.MAX_VALUE, Integer.MAX_VALUE);
  }

  @Test
  public void testGetLogEntriesByOriginService() {
    assertEquals("Number of log entries returned by query should be 1", 1, controller
        .getLogEntriesByOriginServices(TEST_SERVICES, -1, Long.MAX_VALUE, MAX_LIMIT).size());
  }

  @Test
  public void testGetLogEntriesByOriginServiceNoMatch() {
    assertEquals("Number of log entries returned by query should be 0", 0,
        controller
            .getLogEntriesByOriginServices(TEST_SERVICES, Long.MAX_VALUE, Long.MAX_VALUE, MAX_LIMIT)
            .size());
  }

  @Test(expected = ServiceException.class)
  public void testGetLogEntriesByOriginServiceException() throws Exception {
    changeControllerService(false);
    controller.getLogEntriesByOriginServices(TEST_SERVICES, -1, Long.MAX_VALUE, MAX_LIMIT);
  }

  @Test(expected = LimitExceededException.class)
  public void testGetLogEntriesByOriginServiceMaxException() throws Exception {
    controller.getLogEntriesByOriginServices(TEST_SERVICES, -1, Long.MAX_VALUE, Integer.MAX_VALUE);
  }

  @Test
  public void testGetLogEntriesByKeywords() {
    assertEquals("Number of log entries returned by query should be 1", 1,
        controller.getLogEntriesByKeywords(TEST_KEYWORDS, -1, Long.MAX_VALUE, MAX_LIMIT).size());
  }

  @Test
  public void testGetLogEntriesByKeywordsNoMatch() {
    assertEquals("Number of log entries returned by query should be 0", 0, controller
        .getLogEntriesByKeywords(TEST_KEYWORDS, Long.MAX_VALUE, Long.MAX_VALUE, MAX_LIMIT).size());
  }

  @Test(expected = ServiceException.class)
  public void testGetLogEntriesByKeywordsException() throws Exception {
    changeControllerService(false);
    controller.getLogEntriesByKeywords(TEST_KEYWORDS, -1, Long.MAX_VALUE, MAX_LIMIT);
  }

  @Test(expected = LimitExceededException.class)
  public void testGetLogEntriesByKeywordsMaxException() throws Exception {
    controller.getLogEntriesByKeywords(TEST_KEYWORDS, -1, Long.MAX_VALUE, Integer.MAX_VALUE);
  }

  @Test
  public void testGetLogEntriesByLevels() {
    assertEquals("Number of log entries returned by query should be 1", 1,
        controller.getLogEntriesByLogLevels(TEST_LEVELS, -1, Long.MAX_VALUE, MAX_LIMIT).size());
  }

  @Test
  public void testGetLogEntriesByLevelsNoMatch() {
    assertEquals("Number of log entries returned by query should be 0", 0,
        controller.getLogEntriesByLogLevels(new Level[] {Level.INFO}, Long.MAX_VALUE,
            Long.MAX_VALUE, MAX_LIMIT).size());
  }

  @Test(expected = ServiceException.class)
  public void testGetLogEntriesByLevelsException() throws Exception {
    changeControllerService(false);
    controller.getLogEntriesByLogLevels(TEST_LEVELS, -1, Long.MAX_VALUE, MAX_LIMIT);
  }

  @Test(expected = LimitExceededException.class)
  public void testGetLogEntriesByLevelsMaxException() throws Exception {
    controller.getLogEntriesByLogLevels(TEST_LEVELS, -1, Long.MAX_VALUE, Integer.MAX_VALUE);
  }

  @Test
  public void testGetLogEntriesByLevelsOriginService() {
    assertEquals("Number of log entries returned by query should be 1", 1,
        controller.getLogEntriesByLogLevelsAndOriginServices(TEST_LEVELS, TEST_SERVICES, -1,
            Long.MAX_VALUE, MAX_LIMIT).size());
  }

  @Test
  public void testGetLogEntriesByLevelsOriginServiceNoMatch() {
    assertEquals("Number of log entries returned by query should be 0", 0,
        controller.getLogEntriesByLogLevelsAndOriginServices(TEST_LEVELS, BAD_ARRAY, Long.MAX_VALUE,
            Long.MAX_VALUE, MAX_LIMIT).size());
  }

  @Test(expected = ServiceException.class)
  public void testGetLogEntriesByLevelsOriginServiceException() throws Exception {
    changeControllerService(false);
    controller.getLogEntriesByLogLevelsAndOriginServices(TEST_LEVELS, TEST_SERVICES, -1,
        Long.MAX_VALUE, MAX_LIMIT);
  }

  @Test(expected = LimitExceededException.class)
  public void testGetLogEntriesByLevelsOriginServiceMaxException() throws Exception {
    controller.getLogEntriesByLogLevelsAndOriginServices(TEST_LEVELS, TEST_SERVICES, -1,
        Long.MAX_VALUE, Integer.MAX_VALUE);
  }

  @Test
  public void testGetLogEntriesByLevelsOriginServiceLabels() {
    assertEquals("Number of log entries returned by query should be 1", 1,
        controller.getLogEntriesByLogLevelsAndOriginServicesAndLabels(TEST_LEVELS, TEST_SERVICES,
            TEST_LABELS, -1, Long.MAX_VALUE, MAX_LIMIT).size());
  }

  @Test
  public void testGetLogEntriesByLevelsOriginServiceLabelsNoMatch() {
    assertEquals("Number of log entries returned by query should be 0", 0,
        controller.getLogEntriesByLogLevelsAndOriginServicesAndLabels(TEST_LEVELS, TEST_SERVICES,
            BAD_ARRAY, Long.MAX_VALUE, Long.MAX_VALUE, MAX_LIMIT).size());
  }

  @Test(expected = ServiceException.class)
  public void testGetLogEntriesByLevelsOriginServiceLabelsException() throws Exception {
    changeControllerService(false);
    controller.getLogEntriesByLogLevelsAndOriginServicesAndLabels(TEST_LEVELS, TEST_SERVICES,
        TEST_LABELS, -1, Long.MAX_VALUE, MAX_LIMIT);
  }

  @Test(expected = LimitExceededException.class)
  public void testGetLogEntriesByLevelsOriginServiceLabelsMaxException() throws Exception {
    controller.getLogEntriesByLogLevelsAndOriginServicesAndLabels(TEST_LEVELS, TEST_SERVICES,
        TEST_LABELS, -1, Long.MAX_VALUE, Integer.MAX_VALUE);
  }

  @Test
  public void testGetLogEntriesByLevelsOriginServiceLabelsKeywords() {
    assertEquals("Number of log entries returned by query should be 1", 1,
        controller.getLogEntriesByLogLevelsAndOriginServicesAndLabelsAndKeywords(TEST_LEVELS,
            TEST_SERVICES, TEST_LABELS, TEST_KEYWORDS, -1, Long.MAX_VALUE, MAX_LIMIT).size());
  }

  @Test
  public void testGetLogEntriesByLevelsOriginServiceLabelsKeywordsNoMatch() {
    assertEquals("Number of log entries returned by query should be 0", 0,
        controller
            .getLogEntriesByLogLevelsAndOriginServicesAndLabelsAndKeywords(TEST_LEVELS,
                TEST_SERVICES, BAD_ARRAY, TEST_KEYWORDS, Long.MAX_VALUE, Long.MAX_VALUE, MAX_LIMIT)
            .size());
  }

  @Test(expected = ServiceException.class)
  public void testGetLogEntriesByLevelsOriginServiceLabelsKeywordsException() throws Exception {
    changeControllerService(false);
    controller.getLogEntriesByLogLevelsAndOriginServicesAndLabelsAndKeywords(TEST_LEVELS,
        TEST_SERVICES, TEST_LABELS, TEST_KEYWORDS, -1, Long.MAX_VALUE, MAX_LIMIT);
  }

  @Test(expected = LimitExceededException.class)
  public void testGetLogEntriesByLevelsOriginServiceLabelsKeywordsMaxException() throws Exception {
    controller.getLogEntriesByLogLevelsAndOriginServicesAndLabelsAndKeywords(TEST_LEVELS,
        TEST_SERVICES, TEST_LABELS, TEST_KEYWORDS, -1, Long.MAX_VALUE, Integer.MAX_VALUE);
  }

  @Test
  public void testDeleteLogEntriesByTimePeriod() {
    assertEquals("Number of records removed was not 1", 1,
        controller.deleteLogEntriesByTimePeriod(-1, Long.MAX_VALUE));
  }

  @Test
  public void testDeleteLogEntriesByTimePeriodNoMatches() {
    assertEquals("Number of records removed was not 0", 0,
        controller.deleteLogEntriesByTimePeriod(Long.MAX_VALUE, Long.MAX_VALUE));
  }

  @Test
  public void testDeleteLogEntriesByKeywords() {
    assertEquals("Number of records removed was not 1", 1,
        controller.deleteLogEntriesByKeywords(TEST_KEYWORDS, -1, Long.MAX_VALUE));
  }

  @Test
  public void testDeleteLogEntriesByKeywordsNoMatches() {
    assertEquals("Number of records removed was not 0", 0,
        controller.deleteLogEntriesByKeywords(BAD_ARRAY, Long.MAX_VALUE, Long.MAX_VALUE));
  }

  @Test
  public void testDeleteLogEntriesByLabels() {
    assertEquals("Number of records removed was not 0", 0,
        controller.deleteLogEntriesByLabels(TEST_LABELS, Long.MAX_VALUE, Long.MAX_VALUE));
  }

  @Test
  public void testDeleteLogEntriesByLabelsNoMatches() {
    assertEquals("Number of records removed was not 0", 0,
        controller.deleteLogEntriesByLabels(BAD_ARRAY, Long.MAX_VALUE, Long.MAX_VALUE));
  }

  @Test
  public void testDeleteLogEntriesByOriginServices() {
    assertEquals("Number of records removed was not 0", 0,
        controller.deleteLogEntriesByOriginServices(TEST_SERVICES, Long.MAX_VALUE, Long.MAX_VALUE));
  }

  @Test
  public void testDeleteLogEntriesByOriginServicesNoMatches() {
    assertEquals("Number of records removed was not 0", 0,
        controller.deleteLogEntriesByOriginServices(BAD_ARRAY, Long.MAX_VALUE, Long.MAX_VALUE));
  }

  @Test
  public void testDeleteLogEntriesByLogLevels() {
    assertEquals("Number of records removed was not 0", 0,
        controller.deleteLogEntriesByLogLevels(TEST_LEVELS, Long.MAX_VALUE, Long.MAX_VALUE));
  }

  @Test
  public void testDeleteLogEntriesByLogLevelsNoMatches() {
    assertEquals("Number of records removed was not 0", 0, controller
        .deleteLogEntriesByLogLevels(new Level[] {Level.INFO}, Long.MAX_VALUE, Long.MAX_VALUE));
  }

  @Test
  public void testDeleteLogEntriesByLogLevelsAndOriginServices() {
    assertEquals("Number of records removed was not 0", 0,
        controller.deleteLogEntriesByLogLevelsAndOriginServices(TEST_LEVELS, TEST_SERVICES,
            Long.MAX_VALUE, Long.MAX_VALUE));
  }

  @Test
  public void testDeleteLogEntriesByLogLevelsAndOriginServicesNoMatches() {
    assertEquals("Number of records removed was not 0", 0,
        controller.deleteLogEntriesByLogLevelsAndOriginServices(new Level[] {Level.INFO}, BAD_ARRAY,
            Long.MAX_VALUE, Long.MAX_VALUE));
  }

  @Test
  public void testDeleteLogEntriesByLogLevelsAndOriginServicesAndLabels() {
    assertEquals("Number of records removed was not 0", 0,
        controller.deleteLogEntriesByLogLevelsAndOriginServicesAndLabels(TEST_LEVELS, TEST_SERVICES,
            TEST_LABELS, Long.MAX_VALUE, Long.MAX_VALUE));
  }

  @Test
  public void testDeleteLogEntriesByLogLevelsAndOriginServicesAndLabelsNoMatches() {
    assertEquals("Number of records removed was not 0", 0,
        controller.deleteLogEntriesByLogLevelsAndOriginServicesAndLabels(new Level[] {Level.INFO},
            BAD_ARRAY, BAD_ARRAY, Long.MAX_VALUE, Long.MAX_VALUE));
  }

  @Test
  public void testDeleteLogEntriesByLogLevelsAndOriginServicesAndLabelsAndKeywords() {
    assertEquals("Number of records removed was not 0", 0,
        controller.deleteLogEntriesByLogLevelsAndOriginServicesAndLabelsAndKeywords(TEST_LEVELS,
            TEST_SERVICES, TEST_LABELS, TEST_KEYWORDS, Long.MAX_VALUE, Long.MAX_VALUE));
  }

  @Test
  public void testDeleteLogEntriesByLogLevelsAndOriginServicesAndLabelsAndKeywordsNoMatches() {
    assertEquals("Number of records removed was not 0", 0,
        controller.deleteLogEntriesByLogLevelsAndOriginServicesAndLabelsAndKeywords(
            new Level[] {Level.INFO}, BAD_ARRAY, BAD_ARRAY, BAD_ARRAY, Long.MAX_VALUE,
            Long.MAX_VALUE));
  }

  // getLogEntriesByOriginServices(@PathVariable String[] originServices,
  // @PathVariable long start, @PathVariable long end, @PathVariable int limit);
  // getLogEntriesByKeywords(@PathVariable String[] keywords, @PathVariable long start,
  // @PathVariable long end, @PathVariable int limit);
  // getLogEntriesByLogLevels(@PathVariable Level[] logLevels, @PathVariable long start,
  // @PathVariable long end, @PathVariable int limit);
  // getLogEntriesByLogLevelsAndOriginServices(@PathVariable Level[] logLevels,
  // @PathVariable String[] originServices, @PathVariable long start, @PathVariable long end,
  // @PathVariable int limit);
  // getLogEntriesByLogLevelsAndOriginServicesAndLabels(@PathVariable Level[] logLevels,
  // @PathVariable String[] originServices, @PathVariable String[] labels,
  // @PathVariable long start, @PathVariable long end, @PathVariable int limit);
  // getLogEntriesByLogLevelsAndOriginServicesAndLabelsAndKeywords(
  // @PathVariable Level[] logLevels, @PathVariable String[] originServices,
  // @PathVariable String[] labels, @PathVariable String[] keywords, @PathVariable long start,
  // @PathVariable long end, @PathVariable int limit);
  // deleteLogEntriesByTimePeriod(@PathVariable long start, @PathVariable long end);
  // deleteLogEntriesByKeywords(@PathVariable String[] keywords, @PathVariable long start,
  // @PathVariable long end);
  // deleteLogEntriesByLabels(@PathVariable String[] labels, @PathVariable long start,
  // @PathVariable long end);
  // deleteLogEntriesByOriginServices(@PathVariable String[] originServices,
  // @PathVariable long start, @PathVariable long end);
  // deleteLogEntriesByLogLevels(@PathVariable Level[] logLevels, @PathVariable long start,
  // @PathVariable long end);
  //
  // deleteLogEntriesByLogLevelsAndOriginServices(@PathVariable Level[] logLevels,
  // @PathVariable String[] originServices, @PathVariable long start, @PathVariable long end);
  // deleteLogEntriesByLogLevelsAndOriginServicesAndLabels(@PathVariable Level[] logLevels,
  // @PathVariable String[] originServices, @PathVariable String[] labels,
  // @PathVariable long start, @PathVariable long end);
  // deleteLogEntriesByLogLevelsAndOriginServicesAndLabelsAndKeywords(
  // @PathVariable Level[] logLevels, @PathVariable String[] originServices,
  // @PathVariable String[] labels, @PathVariable String[] keywords, @PathVariable long start,
  // @PathVariable long end);



  // use Java reflection to change controller's service
  private void changeControllerService(boolean isReset) {
    try {
      Class<?> controllerClass = controller.getClass();
      Field temp = controllerClass.getDeclaredField("service");
      temp.setAccessible(true);
      if (isReset) {
        temp.set(controller, service);
      } else {
        temp.set(controller, null);
      }
    } catch (Exception e) {
      System.out.println("Problem changing the controller's service");
    }
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
