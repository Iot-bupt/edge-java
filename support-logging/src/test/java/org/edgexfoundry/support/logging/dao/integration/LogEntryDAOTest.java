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

package org.edgexfoundry.support.logging.dao.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.test.context.junit4.SpringRunner;

import org.edgexfoundry.EdgeXSupportLoggingApplication;
import org.edgexfoundry.support.domain.logging.LogEntry;
import org.edgexfoundry.support.domain.logging.MatchCriteria;
import org.edgexfoundry.support.logging.dao.LogEntryDAO;
import org.edgexfoundry.support.logging.dao.MDC_ENUM_CONSTANTS;

/**
 * @author Jim
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = EdgeXSupportLoggingApplication.class)
public abstract class LogEntryDAOTest {

  private static final String[] TEST_LABELS = {"test", "entry2"};
  private static final Level TEST_LEVEL = Level.DEBUG;
  private static final String TEST_MSG = "now is the time for all good men";
  private static final String TEST_ORIGIN_SERVICE = "core-data";

  protected Logger logger = LoggerFactory.getLogger(this.getClass());

  @Autowired
  protected LogEntryDAO logEntryDAO;

  @Before
  public void setUp() {
    cleanPersistence();
  }

  @After
  public void tearDown() {
    cleanPersistence();
  }

  protected LogEntry buildLogEntry(String originService, Level LogLevel, String[] labels,
      String message) {
    LogEntry entry = new LogEntry();
    entry.setOriginService(originService);
    entry.setLabels(labels);
    entry.setLogLevel(LogLevel);
    entry.setMessage(message);
    return entry;
  }

  protected Criteria buildCriteria(LogEntry entry) {
    Criteria result = Criteria.where(MDC_ENUM_CONSTANTS.ORIGINSERVICE.getValue())
        .is(entry.getOriginService()).and(MDC_ENUM_CONSTANTS.LOGLEVEL.getValue())
        .is(entry.getLogLevel()).and(MDC_ENUM_CONSTANTS.MESSAGE.getValue()).is(entry.getMessage())
        .and(MDC_ENUM_CONSTANTS.LABELS.getValue()).in((Object[]) entry.getLabels());
    return result;
  }

  @Test
  public void testSaveLoggable() {
    LogEntry debug = buildLogEntry(TEST_ORIGIN_SERVICE, TEST_LEVEL, TEST_LABELS, TEST_MSG);
    logEntryDAO.save(debug);
    verifyPersistence(debug, true);
  }

  @Test
  public void testSaveUnloggable() {
    LogEntry trace = buildLogEntry(TEST_ORIGIN_SERVICE, Level.TRACE, TEST_LABELS, TEST_MSG);
    logEntryDAO.save(trace);
    verifyPersistence(trace, false);
  }

  /**
   * From Jude Hung - test to verify a bug fix that FileLogEntryDAO may miss the log entries due to
   * file locking interference from logback
   */
  @Test
  public void testSaveFindThenRemoveByCriteria() {
    LogEntry debug = buildLogEntry(TEST_ORIGIN_SERVICE, TEST_LEVEL, TEST_LABELS, TEST_MSG);
    LogEntry info = buildLogEntry(TEST_ORIGIN_SERVICE, Level.INFO, TEST_LABELS, TEST_MSG);
    LogEntry warn = buildLogEntry(TEST_ORIGIN_SERVICE, Level.WARN, TEST_LABELS, TEST_MSG);
    LogEntry error = buildLogEntry(TEST_ORIGIN_SERVICE, Level.ERROR, TEST_LABELS, TEST_MSG);
    LogEntry trace = buildLogEntry(TEST_ORIGIN_SERVICE, Level.TRACE, TEST_LABELS, TEST_MSG);
    logEntryDAO.save(debug);
    verifyPersistence(debug, true);
    logEntryDAO.save(info);
    verifyPersistence(info, true);
    logEntryDAO.save(warn);
    verifyPersistence(warn, true);
    logEntryDAO.save(error);
    verifyPersistence(error, true);
    // Verify if TRACE is not loggable due to logging.level.edgexfoundry.support.logging=DEBUG as
    // defined in test-mongodb.properties
    logEntryDAO.save(trace);
    verifyPersistence(trace, false);
    MatchCriteria criteria = new MatchCriteria();
    List<LogEntry> logEntriesFound = logEntryDAO.findByCriteria(criteria, -1);
    assertTrue("Expect 4 but got " + logEntriesFound.size() + " logEntries to be found.",
        logEntriesFound.size() == 4);
    List<LogEntry> logEntriesRemoved = logEntryDAO.removeByCriteria(criteria);
    assertTrue("Expect 4 but got " + logEntriesRemoved.size() + " logEntries to be removed.",
        logEntriesRemoved.size() == 4);
    logEntriesFound = logEntryDAO.findByCriteria(criteria, -1);
    assertTrue("Expect 0 but got " + logEntriesFound.size() + " logEntries to be found.",
        logEntriesFound.size() == 0);
  }

  @Test
  public void testFindByTime() {
    LogEntry entry = buildLogEntry(TEST_ORIGIN_SERVICE, TEST_LEVEL, TEST_LABELS, TEST_MSG);
    logEntryDAO.save(entry);
    verifyPersistence(entry, true);
    MatchCriteria criteria = buildCriteria(null, null, null, null, -1, Long.MAX_VALUE);
    assertEquals("Correct number of entries not found with query", 1,
        logEntryDAO.findByCriteria(criteria, 10).size());
  }

  @Test
  public void testFindByTimeNoMatch() {
    LogEntry entry = buildLogEntry(TEST_ORIGIN_SERVICE, TEST_LEVEL, TEST_LABELS, TEST_MSG);
    logEntryDAO.save(entry);
    verifyPersistence(entry, true);
    MatchCriteria criteria =
        buildCriteria(null, null, null, null, Long.MAX_VALUE - 100, Long.MAX_VALUE);
    assertTrue("No entries should have been found but results were returned",
        logEntryDAO.findByCriteria(criteria, 10).isEmpty());
  }

  @Test
  public void testFindByLabel() {
    LogEntry entry = buildLogEntry(TEST_ORIGIN_SERVICE, TEST_LEVEL, TEST_LABELS, TEST_MSG);
    logEntryDAO.save(entry);
    verifyPersistence(entry, true);
    MatchCriteria criteria = buildCriteria(null, null, TEST_LABELS, null, -1, Long.MAX_VALUE);
    assertEquals("Correct number of entries not found with query", 1,
        logEntryDAO.findByCriteria(criteria, 10).size());
  }

  @Test
  public void testFindByLabelNoMatch() {
    LogEntry entry = buildLogEntry(TEST_ORIGIN_SERVICE, TEST_LEVEL, TEST_LABELS, TEST_MSG);
    logEntryDAO.save(entry);
    verifyPersistence(entry, true);
    MatchCriteria criteria =
        buildCriteria(null, null, new String[] {"foo"}, null, -1, Long.MAX_VALUE);
    assertTrue("No entries should have been found but results were returned",
        logEntryDAO.findByCriteria(criteria, 10).isEmpty());
  }

  @Test
  public void testFindByLogLevel() {
    LogEntry entry = buildLogEntry(TEST_ORIGIN_SERVICE, TEST_LEVEL, TEST_LABELS, TEST_MSG);
    logEntryDAO.save(entry);
    verifyPersistence(entry, true);
    MatchCriteria criteria = buildCriteria(null, TEST_LEVEL, null, null, -1, Long.MAX_VALUE);
    assertEquals("Correct number of entries not found with query", 1,
        logEntryDAO.findByCriteria(criteria, 10).size());
  }

  @Test
  public void testFindByLogLevelNoMatch() {
    LogEntry entry = buildLogEntry(TEST_ORIGIN_SERVICE, TEST_LEVEL, TEST_LABELS, TEST_MSG);
    logEntryDAO.save(entry);
    verifyPersistence(entry, true);
    MatchCriteria criteria = buildCriteria(null, Level.ERROR, null, null, -1, Long.MAX_VALUE);
    assertTrue("No entries should have been found but results were returned",
        logEntryDAO.findByCriteria(criteria, 10).isEmpty());
  }

  @Test
  public void testFindByOriginService() {
    LogEntry entry = buildLogEntry(TEST_ORIGIN_SERVICE, TEST_LEVEL, TEST_LABELS, TEST_MSG);
    logEntryDAO.save(entry);
    verifyPersistence(entry, true);
    MatchCriteria criteria =
        buildCriteria(TEST_ORIGIN_SERVICE, null, null, null, -1, Long.MAX_VALUE);
    assertEquals("Correct number of entries not found with query", 1,
        logEntryDAO.findByCriteria(criteria, 10).size());
  }

  @Test
  public void testFindByOriginServiceNoMatch() {
    LogEntry entry = buildLogEntry(TEST_ORIGIN_SERVICE, TEST_LEVEL, TEST_LABELS, TEST_MSG);
    logEntryDAO.save(entry);
    verifyPersistence(entry, true);
    MatchCriteria criteria = buildCriteria("foobar", null, null, null, -1, Long.MAX_VALUE);
    assertTrue("No entries should have been found but results were returned",
        logEntryDAO.findByCriteria(criteria, 10).isEmpty());
  }

  @Test
  public void testFindByLogLevelAndOriginService() {
    LogEntry entry = buildLogEntry(TEST_ORIGIN_SERVICE, TEST_LEVEL, TEST_LABELS, TEST_MSG);
    logEntryDAO.save(entry);
    verifyPersistence(entry, true);
    MatchCriteria criteria =
        buildCriteria(TEST_ORIGIN_SERVICE, TEST_LEVEL, null, null, -1, Long.MAX_VALUE);
    assertEquals("Correct number of entries not found with query", 1,
        logEntryDAO.findByCriteria(criteria, 10).size());
  }

  @Test
  public void testFindByLogLevelOriginServiceNoMatch() {
    LogEntry entry = buildLogEntry(TEST_ORIGIN_SERVICE, TEST_LEVEL, TEST_LABELS, TEST_MSG);
    logEntryDAO.save(entry);
    verifyPersistence(entry, true);
    MatchCriteria criteria = buildCriteria("foobar", Level.ERROR, null, null, -1, Long.MAX_VALUE);
    assertTrue("No entries should have been found but results were returned",
        logEntryDAO.findByCriteria(criteria, 10).isEmpty());
  }

  @Test
  public void testFindByLogLevelAndOriginServiceAndLabels() {
    LogEntry entry = buildLogEntry(TEST_ORIGIN_SERVICE, TEST_LEVEL, TEST_LABELS, TEST_MSG);
    logEntryDAO.save(entry);
    verifyPersistence(entry, true);
    MatchCriteria criteria =
        buildCriteria(TEST_ORIGIN_SERVICE, TEST_LEVEL, TEST_LABELS, null, -1, Long.MAX_VALUE);
    assertEquals("Correct number of entries not found with query", 1,
        logEntryDAO.findByCriteria(criteria, 10).size());
  }

  @Test
  public void testFindByLogLevelOriginServiceAndLabelsNoMatch() {
    LogEntry entry = buildLogEntry(TEST_ORIGIN_SERVICE, TEST_LEVEL, TEST_LABELS, TEST_MSG);
    logEntryDAO.save(entry);
    verifyPersistence(entry, true);
    MatchCriteria criteria =
        buildCriteria("foobar", Level.ERROR, new String[] {"foo"}, null, -1, Long.MAX_VALUE);
    assertTrue("No entries should have been found but results were returned",
        logEntryDAO.findByCriteria(criteria, 10).isEmpty());
  }

  @Test
  public void testRemoveByTime() {
    LogEntry entry = buildLogEntry(TEST_ORIGIN_SERVICE, TEST_LEVEL, TEST_LABELS, TEST_MSG);
    logEntryDAO.save(entry);
    verifyPersistence(entry, true);
    MatchCriteria criteria = buildCriteria(null, null, null, null, -1, Long.MAX_VALUE);
    assertEquals("Correct number of entries not found with query", 1,
        logEntryDAO.removeByCriteria(criteria).size());
  }

  @Test
  public void testRemovedByTimeNoMatch() {
    LogEntry entry = buildLogEntry(TEST_ORIGIN_SERVICE, TEST_LEVEL, TEST_LABELS, TEST_MSG);
    logEntryDAO.save(entry);
    verifyPersistence(entry, true);
    MatchCriteria criteria =
        buildCriteria(null, null, null, null, Long.MAX_VALUE - 100, Long.MAX_VALUE);
    assertTrue("No entries should have been found but results were returned",
        logEntryDAO.removeByCriteria(criteria).isEmpty());
  }


  @Test
  public void testRemoveByLabel() {
    LogEntry entry = buildLogEntry(TEST_ORIGIN_SERVICE, TEST_LEVEL, TEST_LABELS, TEST_MSG);
    logEntryDAO.save(entry);
    verifyPersistence(entry, true);
    MatchCriteria criteria = buildCriteria(null, null, TEST_LABELS, null, -1, Long.MAX_VALUE);
    assertEquals("Correct number of entries not found with query", 1,
        logEntryDAO.removeByCriteria(criteria).size());
  }

  @Test
  public void testRemoveByLabelNoMatch() {
    LogEntry entry = buildLogEntry(TEST_ORIGIN_SERVICE, TEST_LEVEL, TEST_LABELS, TEST_MSG);
    logEntryDAO.save(entry);
    verifyPersistence(entry, true);
    MatchCriteria criteria =
        buildCriteria(null, null, new String[] {"foo"}, null, -1, Long.MAX_VALUE);
    assertTrue("No entries should have been found but results were returned",
        logEntryDAO.removeByCriteria(criteria).isEmpty());
  }

  @Test
  public void testRemoveByLogLevel() {
    LogEntry entry = buildLogEntry(TEST_ORIGIN_SERVICE, TEST_LEVEL, TEST_LABELS, TEST_MSG);
    logEntryDAO.save(entry);
    verifyPersistence(entry, true);
    MatchCriteria criteria = buildCriteria(null, TEST_LEVEL, null, null, -1, Long.MAX_VALUE);
    assertEquals("Correct number of entries not found with query", 1,
        logEntryDAO.removeByCriteria(criteria).size());
  }

  @Test
  public void testRemoveByLogLevelNoMatch() {
    LogEntry entry = buildLogEntry(TEST_ORIGIN_SERVICE, TEST_LEVEL, TEST_LABELS, TEST_MSG);
    logEntryDAO.save(entry);
    verifyPersistence(entry, true);
    MatchCriteria criteria = buildCriteria(null, Level.ERROR, null, null, -1, Long.MAX_VALUE);
    assertTrue("No entries should have been found but results were returned",
        logEntryDAO.removeByCriteria(criteria).isEmpty());
  }

  @Test
  public void testRemoveByOriginService() {
    LogEntry entry = buildLogEntry(TEST_ORIGIN_SERVICE, TEST_LEVEL, TEST_LABELS, TEST_MSG);
    logEntryDAO.save(entry);
    verifyPersistence(entry, true);
    MatchCriteria criteria =
        buildCriteria(TEST_ORIGIN_SERVICE, null, null, null, -1, Long.MAX_VALUE);
    assertEquals("Correct number of entries not found with query", 1,
        logEntryDAO.removeByCriteria(criteria).size());
  }

  @Test
  public void testRemoveByOriginServiceNoMatch() {
    LogEntry entry = buildLogEntry(TEST_ORIGIN_SERVICE, TEST_LEVEL, TEST_LABELS, TEST_MSG);
    logEntryDAO.save(entry);
    verifyPersistence(entry, true);
    MatchCriteria criteria = buildCriteria("foobar", null, null, null, -1, Long.MAX_VALUE);
    assertTrue("No entries should have been found but results were returned",
        logEntryDAO.removeByCriteria(criteria).isEmpty());
  }

  @Test
  public void testRemoveByLogLevelAndOriginService() {
    LogEntry entry = buildLogEntry(TEST_ORIGIN_SERVICE, TEST_LEVEL, TEST_LABELS, TEST_MSG);
    logEntryDAO.save(entry);
    verifyPersistence(entry, true);
    MatchCriteria criteria =
        buildCriteria(TEST_ORIGIN_SERVICE, TEST_LEVEL, null, null, -1, Long.MAX_VALUE);
    assertEquals("Correct number of entries not found with query", 1,
        logEntryDAO.removeByCriteria(criteria).size());
  }

  @Test
  public void testRemoveByLogLevelOriginServiceNoMatch() {
    LogEntry entry = buildLogEntry(TEST_ORIGIN_SERVICE, TEST_LEVEL, TEST_LABELS, TEST_MSG);
    logEntryDAO.save(entry);
    verifyPersistence(entry, true);
    MatchCriteria criteria = buildCriteria("foobar", Level.ERROR, null, null, -1, Long.MAX_VALUE);
    assertTrue("No entries should have been found but results were returned",
        logEntryDAO.removeByCriteria(criteria).isEmpty());
  }

  @Test
  public void testRemoveByLogLevelAndOriginServiceAndLabels() {
    LogEntry entry = buildLogEntry(TEST_ORIGIN_SERVICE, TEST_LEVEL, TEST_LABELS, TEST_MSG);
    logEntryDAO.save(entry);
    verifyPersistence(entry, true);
    MatchCriteria criteria =
        buildCriteria(TEST_ORIGIN_SERVICE, TEST_LEVEL, TEST_LABELS, null, -1, Long.MAX_VALUE);
    assertEquals("Correct number of entries not found with query", 1,
        logEntryDAO.removeByCriteria(criteria).size());
  }

  @Test
  public void testRemoveByLogLevelOriginServiceAndLabelsNoMatch() {
    LogEntry entry = buildLogEntry(TEST_ORIGIN_SERVICE, TEST_LEVEL, TEST_LABELS, TEST_MSG);
    logEntryDAO.save(entry);
    verifyPersistence(entry, true);
    MatchCriteria criteria =
        buildCriteria("foobar", Level.ERROR, new String[] {"foo"}, null, -1, Long.MAX_VALUE);
    assertTrue("No entries should have been found but results were returned",
        logEntryDAO.removeByCriteria(criteria).isEmpty());
  }

  private MatchCriteria buildCriteria(String originService, Level logLevel, String[] labels,
      String message, long start, long end) {
    MatchCriteria c = new MatchCriteria();
    if (logLevel != null) {
      Level[] levels = {logLevel};
      c.setLogLevels(levels);
    }
    if (labels != null)
      c.setLabels(labels);
    if (originService != null) {
      String[] origServices = {originService};
      c.setOriginServices(origServices);
    }
    if (message != null) {
      String[] msgs = {message};
      c.setMessageKeywords(msgs);
    }
    c.setStart(start);
    c.setEnd(end);
    return c;
  }

  abstract public void cleanPersistence();

  abstract public void verifyPersistence(LogEntry entry, boolean expectToBeSaved);

}
