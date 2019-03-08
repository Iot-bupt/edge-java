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

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.TestPropertySource;

import org.edgexfoundry.support.domain.logging.LogEntry;
import org.edgexfoundry.test.category.RequiresMongoDB;
import org.edgexfoundry.test.category.RequiresSpring;

@TestPropertySource(locations = "classpath:test-mongodb.properties")
@Category({RequiresMongoDB.class, RequiresSpring.class})
public class MongoDBLogEntryDAOTest extends LogEntryDAOTest {

  @Autowired
  private MongoTemplate mongoTemplate;

  @Override
  public void cleanPersistence() {
    mongoTemplate.remove(new Query(), "logEntry");
  }

  @Override
  public void verifyPersistence(LogEntry entry, boolean expectToBeSaved) {
    List<LogEntry> result =
        mongoTemplate.find(new Query().addCriteria(buildCriteria(entry)), LogEntry.class);
    if (expectToBeSaved) {
      assertTrue("Expect to find one logEntry in MongoDB but none.", result.size() == 1);
    } else {
      assertTrue("Expect to find zero logEntry in MongoDB but " + result.size(),
          result.size() == 0);
    }
  }
}
