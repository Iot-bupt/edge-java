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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.edgexfoundry.support.domain.logging.LogEntry;
import org.edgexfoundry.support.domain.logging.MatchCriteria;
import org.edgexfoundry.test.category.RequiresSpring;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(locations = "classpath:test-file.properties")
@Category({RequiresSpring.class})
public class FileLogEntryDAOTest extends LogEntryDAOTest {

  @Value("${logging.persistence.file}")
  private String loggingFilePath;

  @Override
  public void cleanPersistence() {
    // remove log entries out of loggingFilePath
    MatchCriteria criteria = new MatchCriteria();
    logEntryDAO.removeByCriteria(criteria);
  }

  @Override
  public void verifyPersistence(LogEntry entry, boolean expectToBeSaved) {
    File logFile = new File(loggingFilePath);

    BufferedReader reader = null;
    boolean foundEntry = false;
    String currentLine;
    try {
      reader = new BufferedReader(new FileReader(logFile));
      while ((currentLine = reader.readLine()) != null) {
        String trimmedLine = currentLine.trim();
        if (trimmedLine.equals(entry.toString())) {
          foundEntry = true;
          break;
        }
      }
      if (expectToBeSaved) {
        assertTrue("Expect to found LogEntry:" + entry.toString() + ".", foundEntry);
      } else {
        assertFalse("Expect not to found LogEntry:" + entry.toString() + ".", foundEntry);
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (null != reader) {
        try {
          reader.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

}
