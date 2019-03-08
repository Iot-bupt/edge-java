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

import java.util.List;

import org.edgexfoundry.exception.controller.LimitExceededException;
import org.edgexfoundry.exception.controller.ServiceException;
import org.edgexfoundry.support.domain.logging.LogEntry;
import org.slf4j.event.Level;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

public interface LoggingController {

  /**
   * Receive request to create a new logEntry into logging service. ServiceException (HTTP 503) for
   * unknown or unanticipated issues.
   * 
   * @param entry - logEntry to be created
   * @return timestamp(in the form of long) being accepted
   * @throws ServiceException (HTTP 503) for unknown or unanticipated issues
   */
  ResponseEntity<?> addLogEntry(@RequestBody LogEntry entry);

  /**
   * Return a collection of LogEntry - limited in size by the limit parameter.
   * LimitExceededException (HTTP 413) if the number of events exceeds the current max limit.
   * ServiceException (HTTP 503) for unknown or unanticipated issues.
   * 
   * @param limit - the maximum number of records to fetch
   * @return a collection of LogEntry
   * @throws ServiceException (HTTP 503) for unknown or unanticipated issues
   * @throws LimitExceededException (HTTP 413) if the number of events exceeds the current max limit
   */
  List<LogEntry> getLogEntries(@PathVariable int limit);

  /**
   * Return all LogEntry between a given begin and end date/time (in the form of longs)
   * LimitExceededException (HTTP 413) if the number of events exceeds the current max limit.
   * ServiceException (HTTP 503) for unknown or unanticipated issues.
   * 
   * @param start - start date in long form
   * @param end - end date in long form
   * @param limit - maximum number of events to fetch, must be <= MAX_LIMIT
   * @return list of LogEntry between the specified dates
   * @throws ServiceException (HTTP 503) for unknown or unanticipated issues
   * @throws LimitExceededException (HTTP 413) if the number of events exceeds the current max limit
   */
  List<LogEntry> getLogEntriesByTime(@PathVariable long start, @PathVariable long end,
      @PathVariable int limit);


  /**
   * list a collection of LogEntry matching any of the specified labels and being created between
   * the specified start and end dates. LimitExceededException (HTTP 413) if the number of events
   * exceeds the current max limit. ServiceException (HTTP 503) for unknown or unanticipated issues.
   * 
   * @param labels - an array of label (in String form) used as search criteria
   * @param start - start date in long form
   * @param end - end date in long form
   * @param limit - maximum number of events to fetch, must be <= MAX_LIMIT
   * @return list of LogEntry matching any of the specified labels and being created between the
   *         specified dates
   * @throws ServiceException (HTTP 503) for unknown or unanticipated issues
   * @throws LimitExceededException (HTTP 413) if the number of events exceeds the current max limit
   */
  List<LogEntry> getLogEntriesByLabels(@PathVariable String[] labels, @PathVariable long start,
      @PathVariable long end, @PathVariable int limit);

  /**
   * list a collection of LogEntry matching any of the specified originServices and being created
   * between the specified start and end dates. LimitExceededException (HTTP 413) if the number of
   * events exceeds the current max limit. ServiceException (HTTP 503) for unknown or unanticipated
   * issues.
   * 
   * @param originServices - an array of originService (in String form) used as search criteria
   * @param start - start date in long form
   * @param end - end date in long form
   * @param limit - maximum number of events to fetch, must be <= MAX_LIMIT
   * @return list of LogEntry matching any of the specified originServices and being created between
   *         the specified dates
   * @throws ServiceException (HTTP 503) for unknown or unanticipated issues
   * @throws LimitExceededException (HTTP 413) if the number of events exceeds the current max limit
   */
  List<LogEntry> getLogEntriesByOriginServices(@PathVariable String[] originServices,
      @PathVariable long start, @PathVariable long end, @PathVariable int limit);

  /**
   * list a collection of LogEntry whose message containing any of the specified keywords and being
   * created between the specified start and end dates. LimitExceededException (HTTP 413) if the
   * number of events exceeds the current max limit. ServiceException (HTTP 503) for unknown or
   * unanticipated issues.
   * 
   * @param keywords - an array of keyword (in String form) used as search criteria
   * @param start - start date in long form
   * @param end - end date in long form
   * @param limit - maximum number of events to fetch, must be <= MAX_LIMIT
   * @return list of LogEntry whose message contains any of the specified keywords and being created
   *         between the specified dates
   * @throws ServiceException (HTTP 503) for unknown or unanticipated issues
   * @throws LimitExceededException (HTTP 413) if the number of events exceeds the current max limit
   */
  List<LogEntry> getLogEntriesByKeywords(@PathVariable String[] keywords, @PathVariable long start,
      @PathVariable long end, @PathVariable int limit);


  /**
   * list a collection of LogEntry matching any of the specified logLevels and being created between
   * the specified start and end dates. LimitExceededException (HTTP 413) if the number of events
   * exceeds the current max limit. ServiceException (HTTP 503) for unknown or unanticipated issues.
   * 
   * @param logLevels - an array of logLevel used as search criteria
   * @param start - start date in long form
   * @param end - end date in long form
   * @param limit - maximum number of events to fetch, must be <= MAX_LIMIT
   * @return list of LogEntry matching any of the specified logLevels and being created between the
   *         specified dates
   * @throws ServiceException (HTTP 503) for unknown or unanticipated issues
   * @throws LimitExceededException (HTTP 413) if the number of events exceeds the current max limit
   */
  List<LogEntry> getLogEntriesByLogLevels(@PathVariable Level[] logLevels, @PathVariable long start,
      @PathVariable long end, @PathVariable int limit);

  /**
   * list a collection of LogEntry matching any of the specified logLevels, originServices, and also
   * being created between the specified start and end dates. LimitExceededException (HTTP 413) if
   * the number of events exceeds the current max limit. ServiceException (HTTP 503) for unknown or
   * unanticipated issues.
   * 
   * @param logLevels - an array of logLevel used as search criteria
   * @param originServices - an array of originService (in String form) used as search criteria
   * @param start - start date in long form
   * @param end - end date in long form
   * @param limit - maximum number of events to fetch, must be <= MAX_LIMIT
   * @return list of LogEntry matching any of the specified logLevels, originServices and also being
   *         created between the specified dates
   * @throws ServiceException (HTTP 503) for unknown or unanticipated issues
   * @throws LimitExceededException (HTTP 413) if the number of events exceeds the current max limit
   */
  List<LogEntry> getLogEntriesByLogLevelsAndOriginServices(@PathVariable Level[] logLevels,
      @PathVariable String[] originServices, @PathVariable long start, @PathVariable long end,
      @PathVariable int limit);

  /**
   * list a collection of LogEntry matching any of the specified logLevels, originServices, labels,
   * and also being created between the specified start and end dates. LimitExceededException (HTTP
   * 413) if the number of events exceeds the current max limit. ServiceException (HTTP 503) for
   * unknown or unanticipated issues.
   * 
   * @param logLevels - an array of logLevel used as search criteria
   * @param originServices - an array of originService (in String form) used as search criteria
   * @param labels - an array of labels (in String form) used as search criteria
   * @param start - start date in long form
   * @param end - end date in long form
   * @param limit - maximum number of events to fetch, must be <= MAX_LIMIT
   * @return list of LogEntry matching any of the specified logLevels, originServices, labels and
   *         also being created between the specified dates
   * @throws ServiceException (HTTP 503) for unknown or unanticipated issues
   * @throws LimitExceededException (HTTP 413) if the number of events exceeds the current max limit
   */
  List<LogEntry> getLogEntriesByLogLevelsAndOriginServicesAndLabels(@PathVariable Level[] logLevels,
      @PathVariable String[] originServices, @PathVariable String[] labels,
      @PathVariable long start, @PathVariable long end, @PathVariable int limit);

  /**
   * list a collection of LogEntry whose message containing any of the specified keywords, matching
   * any of the specified logLevels, originServices, labels, and also being created between the
   * specified start and end dates. LimitExceededException (HTTP 413) if the number of events
   * exceeds the current max limit. ServiceException (HTTP 503) for unknown or unanticipated issues.
   * 
   * @param logLevels - an array of logLevel used as search criteria
   * @param originServices - an array of originService (in String form) used as search criteria
   * @param labels - an array of labels (in String form) used as search criteria
   * @param keywords - an array of keywords (in String form) used as search criteria
   * @param start - start date in long form
   * @param end - end date in long form
   * @param limit - maximum number of events to fetch, must be <= MAX_LIMIT
   * @return list a collection of LogEntry whose message containing any of the specified keywords,
   *         matching any of the specified logLevels, originServices, labels, and also being created
   *         between the specified dates
   * @throws ServiceException (HTTP 503) for unknown or unanticipated issues
   * @throws LimitExceededException (HTTP 413) if the number of events exceeds the current max limit
   */
  List<LogEntry> getLogEntriesByLogLevelsAndOriginServicesAndLabelsAndKeywords(
      @PathVariable Level[] logLevels, @PathVariable String[] originServices,
      @PathVariable String[] labels, @PathVariable String[] keywords, @PathVariable long start,
      @PathVariable long end, @PathVariable int limit);


  /**
   * delete all LogEntry being created between specified start and end dates. ServiceException (HTTP
   * 503) for unknown or unanticipated issues.
   * 
   * @param start - start date in long form
   * @param end - end date in long form
   * @return number of logEntries begin removed
   * @throws ServiceException (HTTP 503) for unknown or unanticipated issues
   */
  int deleteLogEntriesByTimePeriod(@PathVariable long start, @PathVariable long end);

  /**
   * delete all LogEntries whose message contains any of specified keywords and also being created
   * between specified start and end dates. ServiceException (HTTP 503) for unknown or unanticipated
   * issues.
   * 
   * @param keywords - an array of keywords (in String form) used as search criteria
   * @param start - start date in long form
   * @param end - end date in long form
   * @return number of logEntries begin removed
   * @throws ServiceException (HTTP 503) for unknown or unanticipated issues
   */
  int deleteLogEntriesByKeywords(@PathVariable String[] keywords, @PathVariable long start,
      @PathVariable long end);

  /**
   * delete all LogEntries matching any of specified labels and also being created between specified
   * start and end dates. ServiceException (HTTP 503) for unknown or unanticipated issues.
   * 
   * @param labels - an array of labels (in String form) used as search criteria
   * @param start - start date in long form
   * @param end - end date in long form
   * @return number of logEntries begin removed
   * @throws ServiceException (HTTP 503) for unknown or unanticipated issues
   */
  int deleteLogEntriesByLabels(@PathVariable String[] labels, @PathVariable long start,
      @PathVariable long end);

  /**
   * delete all LogEntries matching any of specified originServices and also being created between
   * specified start and end dates. ServiceException (HTTP 503) for unknown or unanticipated issues.
   * 
   * @param originServices - an array of originServices (in String form) used as search criteria
   * @param start - start date in long form
   * @param end - end date in long form
   * @return number of logEntries begin removed
   * @throws ServiceException (HTTP 503) for unknown or unanticipated issues
   */
  int deleteLogEntriesByOriginServices(@PathVariable String[] originServices,
      @PathVariable long start, @PathVariable long end);

  /**
   * delete all LogEntries matching any of specified logLevels and also being created between
   * specified start and end dates. ServiceException (HTTP 503) for unknown or unanticipated issues.
   * 
   * @param logLevels - an array of logLevel (in Level form) used as search criteria
   * @param start - start date in long form
   * @param end - end date in long form
   * @return number of logEntries begin removed
   * @throws ServiceException (HTTP 503) for unknown or unanticipated issues
   */
  int deleteLogEntriesByLogLevels(@PathVariable Level[] logLevels, @PathVariable long start,
      @PathVariable long end);

  /**
   * delete all LogEntries matching any of the specified logLevels, originServices, and also being
   * created between the specified start and end dates. ServiceException (HTTP 503) for unknown or
   * unanticipated issues.
   * 
   * @param logLevels - an array of logLevel used as search criteria
   * @param originServices - an array of originService (in String form) used as search criteria
   * @param keywords - an array of keywords (in String form) used as search criteria
   * @param start - start date in long form
   * @param end - end date in long form
   * @return number of logEntries begin removed
   * @throws ServiceException (HTTP 503) for unknown or unanticipated issues
   */
  int deleteLogEntriesByLogLevelsAndOriginServices(@PathVariable Level[] logLevels,
      @PathVariable String[] originServices, @PathVariable long start, @PathVariable long end);

  /**
   * delete all LogEntries matching any of the specified logLevels, originServices, labels, and also
   * being created between the specified start and end dates. ServiceException (HTTP 503) for
   * unknown or unanticipated issues.
   * 
   * @param logLevels - an array of logLevel used as search criteria
   * @param originServices - an array of originService (in String form) used as search criteria
   * @param labels - an array of labels (in String form) used as search criteria
   * @param keywords - an array of keywords (in String form) used as search criteria
   * @param start - start date in long form
   * @param end - end date in long form
   * @return number of logEntries begin removed
   * @throws ServiceException (HTTP 503) for unknown or unanticipated issues
   */
  int deleteLogEntriesByLogLevelsAndOriginServicesAndLabels(@PathVariable Level[] logLevels,
      @PathVariable String[] originServices, @PathVariable String[] labels,
      @PathVariable long start, @PathVariable long end);

  /**
   * delete all LogEntries whose message containing any of the specified keywords, matching any of
   * the specified logLevels, originServices, labels, and also being created between the specified
   * start and end dates. ServiceException (HTTP 503) for unknown or unanticipated issues.
   * 
   * @param logLevels - an array of logLevel used as search criteria
   * @param originServices - an array of originService (in String form) used as search criteria
   * @param labels - an array of labels (in String form) used as search criteria
   * @param keywords - an array of keywords (in String form) used as search criteria
   * @param start - start date in long form
   * @param end - end date in long form
   * @return number of logEntries begin removed
   * @throws ServiceException (HTTP 503) for unknown or unanticipated issues
   */
  int deleteLogEntriesByLogLevelsAndOriginServicesAndLabelsAndKeywords(
      @PathVariable Level[] logLevels, @PathVariable String[] originServices,
      @PathVariable String[] labels, @PathVariable String[] keywords, @PathVariable long start,
      @PathVariable long end);
}
