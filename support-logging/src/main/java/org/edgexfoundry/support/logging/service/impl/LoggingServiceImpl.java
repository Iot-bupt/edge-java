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

package org.edgexfoundry.support.logging.service.impl;

import java.util.List;

import org.edgexfoundry.support.domain.logging.LogEntry;
import org.edgexfoundry.support.domain.logging.MatchCriteria;
import org.edgexfoundry.support.logging.dao.LogEntryDAO;
import org.edgexfoundry.support.logging.service.LoggingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class LoggingServiceImpl implements LoggingService {

  @Autowired
  @Qualifier("serviceDAO")
  private LogEntryDAO logEntryDAO;

  @Override
  @Async
  public void addLogEntry(LogEntry entry) {
    logEntryDAO.save(entry);
  }

  @Override
  public List<LogEntry> searchByCriteria(MatchCriteria criteria) {
    return searchByCriteria(criteria, -1);
  }

  @Override
  public List<LogEntry> searchByCriteria(MatchCriteria criteria, int limit) {
    return logEntryDAO.findByCriteria(criteria, limit);
  }

  @Override
  public List<LogEntry> removeByCriteria(MatchCriteria criteria) {
    return logEntryDAO.removeByCriteria(criteria);
  }

}
