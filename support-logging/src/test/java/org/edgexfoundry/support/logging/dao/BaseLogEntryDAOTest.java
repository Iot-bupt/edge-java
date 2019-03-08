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

package org.edgexfoundry.support.logging.dao;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;

import org.edgexfoundry.support.domain.logging.LogEntry;
import org.edgexfoundry.support.logging.dao.impl.BaseLogEntryDAO;
import org.edgexfoundry.support.logging.dao.impl.FileLogEntryDAO;
import org.edgexfoundry.test.category.RequiresNone;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.event.Level;

@Category({RequiresNone.class})
public class BaseLogEntryDAOTest {

  private static final String[] TEST_LABELS = {"test", "entry2"};
  private static final Level TEST_LEVEL = Level.DEBUG;
  private static final String TEST_MSG = "now is the time for all good men";
  private static final String TEST_ORIGIN_SERVICE = "core-data";

  private BaseLogEntryDAO dao;

  @Before
  public void setup() throws Exception {
    dao = new FileLogEntryDAO(); // BaseLogEntryDAO is abstract; create instance of concrete
    changeAddColor(true); // see the default add color to true
  }

  @Test
  public void testSave() {
    LogEntry entry = buildLogEntry(TEST_ORIGIN_SERVICE, TEST_LEVEL, TEST_LABELS, TEST_MSG);
    assertTrue("Base log entry save did not save correctly", dao.save(entry));
  }

  @Test
  public void testSaveNoColor() throws Exception {
    changeAddColor(false);
    LogEntry entry = buildLogEntry(TEST_ORIGIN_SERVICE, TEST_LEVEL, TEST_LABELS, TEST_MSG);
    assertTrue("Base log entry save did not save correctly", dao.save(entry));
  }

  @Test
  public void testSaveNoLabels() {
    LogEntry entry = buildLogEntry(TEST_ORIGIN_SERVICE, TEST_LEVEL, null, TEST_MSG);
    assertTrue("Base log entry save did not save correctly", dao.save(entry));
  }

  @Test
  public void testSaveCachedOriginServices() {
    LogEntry entry = buildLogEntry(TEST_ORIGIN_SERVICE, TEST_LEVEL, null, TEST_MSG);
    assertTrue("Base log entry save did not save correctly", dao.save(entry));
    assertTrue("Base log entry save did not save correctly", dao.save(entry));
  }

  @Test
  public void testSaveError() {
    LogEntry entry = buildLogEntry(TEST_ORIGIN_SERVICE, Level.ERROR, TEST_LABELS, TEST_MSG);
    assertTrue("Base log entry save did not save correctly", dao.save(entry));
  }

  @Test
  public void testSaveInfo() {
    LogEntry entry = buildLogEntry(TEST_ORIGIN_SERVICE, Level.INFO, TEST_LABELS, TEST_MSG);
    assertTrue("Base log entry save did not save correctly", dao.save(entry));
  }

  @Test
  public void testSaveTrace() {
    LogEntry entry = buildLogEntry(TEST_ORIGIN_SERVICE, Level.TRACE, TEST_LABELS, TEST_MSG);
    assertFalse("Base log entry for traceable should not return true on save but did",
        dao.save(entry));
  }

  @Test
  public void testSaveWARN() {
    LogEntry entry = buildLogEntry(TEST_ORIGIN_SERVICE, Level.WARN, TEST_LABELS, TEST_MSG);
    assertTrue("Base log entry save did not save correctly", dao.save(entry));
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

  private void changeAddColor(boolean addColor) throws Exception {
    Class<?> daoClass = BaseLogEntryDAO.class;
    Field temp = daoClass.getDeclaredField("addColor");
    temp.setAccessible(true);
    temp.set(dao, addColor);
  }

}
