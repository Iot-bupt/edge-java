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

package org.edgexfoundry.support.logging.service;

import static org.junit.Assert.assertTrue;

import org.edgexfoundry.support.domain.logging.LogEntry;
import org.edgexfoundry.support.domain.logging.MatchCriteria;
import org.edgexfoundry.support.logging.dao.LogEntryDAO;
import org.edgexfoundry.support.logging.service.impl.LoggingServiceImpl;
import org.edgexfoundry.test.category.RequiresNone;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.event.Level;

@Category(RequiresNone.class)
public class LoggingServiceTest {

  private static final String[] TEST_LABELS = {"test", "entry2"};
  private static final Level TEST_LEVEL = Level.DEBUG;
  private static final String TEST_MSG = "now is the time for all good men";
  private static final String TEST_ORIGIN_SERVICE = "core-data";

  @InjectMocks
  private LoggingServiceImpl service;

  @Mock
  private LogEntryDAO dao;

  private LogEntry entry;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void testAddEntry() {
    entry = buildLogEntry(TEST_ORIGIN_SERVICE, TEST_LEVEL, TEST_LABELS, TEST_MSG);
    service.addLogEntry(entry);
  }

  @Test
  public void testSearchByCriteria() {
    assertTrue("Log entries found with fake DAO",
        service
            .searchByCriteria(buildCriteria(TEST_ORIGIN_SERVICE, TEST_LEVEL, TEST_LABELS, TEST_MSG))
            .isEmpty());
  }

  @Test
  public void testSearchByCriteriaWithLimit() {
    assertTrue("Log entries found with fake DAO", service
        .searchByCriteria(buildCriteria(TEST_ORIGIN_SERVICE, TEST_LEVEL, TEST_LABELS, TEST_MSG), 10)
        .isEmpty());
  }

  @Test
  public void testRemoveByCriteria() {
    assertTrue("Log entries removed with fake DAO",
        service
            .removeByCriteria(buildCriteria(TEST_ORIGIN_SERVICE, TEST_LEVEL, TEST_LABELS, TEST_MSG))
            .isEmpty());
  }

  private MatchCriteria buildCriteria(String originService, Level logLevel, String[] labels,
      String message) {
    return buildCriteria(originService, logLevel, labels, message, 0, Long.MAX_VALUE);
  }

  private MatchCriteria buildCriteria(String originService, Level logLevel, String[] labels,
      String message, long start, long end) {
    Level[] levels = {logLevel};
    MatchCriteria c = new MatchCriteria();
    c.setLogLevels(levels);
    c.setLabels(labels);
    String[] origServices = {originService};
    c.setOriginServices(origServices);
    String[] msgs = {message};
    c.setMessageKeywords(msgs);
    c.setStart(start);
    c.setEnd(end);
    return c;
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
