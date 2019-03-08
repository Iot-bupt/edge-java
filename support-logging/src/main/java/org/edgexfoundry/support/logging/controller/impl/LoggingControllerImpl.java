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

package org.edgexfoundry.support.logging.controller.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.edgexfoundry.exception.controller.LimitExceededException;
import org.edgexfoundry.exception.controller.ServiceException;
import org.edgexfoundry.support.domain.logging.LogEntry;
import org.edgexfoundry.support.domain.logging.MatchCriteria;
import org.edgexfoundry.support.logging.controller.LoggingController;
import org.edgexfoundry.support.logging.service.LoggingService;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/logs")
public class LoggingControllerImpl implements LoggingController {

  private static final Logger logger = Logger.getLogger(LoggingControllerImpl.class);

  private static final String FETCH_ERROR = "Error fetching logEntry:";
  private static final String REMOVE_ERROR = "Error removing logEntry:";
  private static final String LOG_ENTRY = "LogEntry";

  @Autowired
  private LoggingService service;

  @Value("${read.max.limit:100}")
  private int maxLimit;

  /**
   * Receive request to create a new logEntry into logging service. ServiceException (HTTP 503) for
   * unknown or unanticipated issues.
   * 
   * @param entry - logEntry to be created
   * @return timestamp(in the form of long) being accepted
   * @throws ServiceException (HTTP 503) for unknown or unanticipated issues
   */
  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<?> addLogEntry(@RequestBody LogEntry entry) {
    logger.debug("Receiving logging request...");
    Date currentTime = Calendar.getInstance().getTime();
    entry.setCreated(currentTime.getTime());
    try {
      service.addLogEntry(entry);
      return new ResponseEntity<>(currentTime.getTime(), HttpStatus.ACCEPTED);
    } catch (Exception e) {
      logger.error("Error adding logEntry:", e);
      throw new ServiceException(e);
    }
  }

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
  @RequestMapping(value = "/{limit}", method = RequestMethod.GET)
  public List<LogEntry> getLogEntries(@PathVariable int limit) {
    if (limit > maxLimit) {
      throw new LimitExceededException(LOG_ENTRY);
    }
    return getEnteries(new MatchCriteria(), limit);
  }

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
  @RequestMapping(value = "/{start}/{end}/{limit}", method = RequestMethod.GET)
  public List<LogEntry> getLogEntriesByTime(@PathVariable long start, @PathVariable long end,
      @PathVariable int limit) {
    if (limit > maxLimit) {
      throw new LimitExceededException(LOG_ENTRY);
    }
    MatchCriteria criteria = new MatchCriteria();
    criteria.setStart(start);
    criteria.setEnd(end);
    return getEnteries(criteria, limit);
  }

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
  @RequestMapping(value = "/labels/{labels}/{start}/{end}/{limit}", method = RequestMethod.GET)
  public List<LogEntry> getLogEntriesByLabels(@PathVariable String[] labels,
      @PathVariable long start, @PathVariable long end, @PathVariable int limit) {
    if (limit > maxLimit) {
      throw new LimitExceededException(LOG_ENTRY);
    }
    MatchCriteria criteria = new MatchCriteria();
    criteria.setStart(start);
    criteria.setEnd(end);
    criteria.setLabels(labels);
    return getEnteries(criteria, limit);
  }

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
  @RequestMapping(value = "/originServices/{originServices}/{start}/{end}/{limit}",
      method = RequestMethod.GET)
  public List<LogEntry> getLogEntriesByOriginServices(@PathVariable String[] originServices,
      @PathVariable long start, @PathVariable long end, @PathVariable int limit) {
    if (limit > maxLimit) {
      throw new LimitExceededException(LOG_ENTRY);
    }
    MatchCriteria criteria = new MatchCriteria();
    criteria.setStart(start);
    criteria.setEnd(end);
    criteria.setOriginServices(originServices);
    return getEnteries(criteria, limit);
  }

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
  @RequestMapping(value = "/keywords/{keywords}/{start}/{end}/{limit}", method = RequestMethod.GET)
  public List<LogEntry> getLogEntriesByKeywords(@PathVariable String[] keywords,
      @PathVariable long start, @PathVariable long end, @PathVariable int limit) {
    if (limit > maxLimit) {
      throw new LimitExceededException(LOG_ENTRY);
    }
    MatchCriteria criteria = new MatchCriteria();
    criteria.setStart(start);
    criteria.setEnd(end);
    criteria.setMessageKeywords(keywords);
    return getEnteries(criteria, limit);
  }

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
  @RequestMapping(value = "/logLevels/{logLevels}/{start}/{end}/{limit}",
      method = RequestMethod.GET)
  public List<LogEntry> getLogEntriesByLogLevels(@PathVariable Level[] logLevels,
      @PathVariable long start, @PathVariable long end, @PathVariable int limit) {
    if (limit > maxLimit) {
      throw new LimitExceededException(LOG_ENTRY);
    }
    MatchCriteria criteria = new MatchCriteria();
    criteria.setStart(start);
    criteria.setEnd(end);
    criteria.setLogLevels(logLevels);
    return getEnteries(criteria, limit);
  }

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
  @RequestMapping(
      value = "/logLevels/{logLevels}/originServices/{originServices}/{start}/{end}/{limit}",
      method = RequestMethod.GET)
  public List<LogEntry> getLogEntriesByLogLevelsAndOriginServices(@PathVariable Level[] logLevels,
      @PathVariable String[] originServices, @PathVariable long start, @PathVariable long end,
      @PathVariable int limit) {
    if (limit > maxLimit) {
      throw new LimitExceededException(LOG_ENTRY);
    }
    MatchCriteria criteria = new MatchCriteria();
    criteria.setStart(start);
    criteria.setEnd(end);
    criteria.setLogLevels(logLevels);
    criteria.setOriginServices(originServices);
    return getEnteries(criteria, limit);
  }

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
  @RequestMapping(
      value = "/logLevels/{logLevels}/originServices/{originServices}/labels/{labels}/{start}/{end}/{limit}",
      method = RequestMethod.GET)
  public List<LogEntry> getLogEntriesByLogLevelsAndOriginServicesAndLabels(
      @PathVariable Level[] logLevels, @PathVariable String[] originServices,
      @PathVariable String[] labels, @PathVariable long start, @PathVariable long end,
      @PathVariable int limit) {
    if (limit > maxLimit) {
      throw new LimitExceededException(LOG_ENTRY);
    }
    MatchCriteria criteria = new MatchCriteria();
    criteria.setStart(start);
    criteria.setEnd(end);
    criteria.setLogLevels(logLevels);
    criteria.setOriginServices(originServices);
    criteria.setLabels(labels);
    return getEnteries(criteria, limit);
  }

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
  @RequestMapping(
      value = "/logLevels/{logLevels}/originServices/{originServices}/labels/{labels}/keywords/{keywords}/{start}/{end}/{limit}",
      method = RequestMethod.GET)
  public List<LogEntry> getLogEntriesByLogLevelsAndOriginServicesAndLabelsAndKeywords(
      @PathVariable Level[] logLevels, @PathVariable String[] originServices,
      @PathVariable String[] labels, @PathVariable String[] keywords, @PathVariable long start,
      @PathVariable long end, @PathVariable int limit) {
    if (limit > maxLimit) {
      throw new LimitExceededException(LOG_ENTRY);
    }
    MatchCriteria criteria = new MatchCriteria();
    criteria.setStart(start);
    criteria.setEnd(end);
    criteria.setLogLevels(logLevels);
    criteria.setOriginServices(originServices);
    criteria.setLabels(labels);
    criteria.setMessageKeywords(keywords);
    return getEnteries(criteria, limit);
  }

  /**
   * delete all LogEntry being created between specified start and end dates. ServiceException (HTTP
   * 503) for unknown or unanticipated issues.
   * 
   * @param start - start date in long form
   * @param end - end date in long form
   * @return number of logEntries begin removed
   * @throws ServiceException (HTTP 503) for unknown or unanticipated issues
   */
  @RequestMapping(value = "/{start}/{end}", method = RequestMethod.DELETE)
  public int deleteLogEntriesByTimePeriod(@PathVariable long start, @PathVariable long end) {
    MatchCriteria criteria = new MatchCriteria();
    criteria.setStart(start);
    criteria.setEnd(end);
    return deleteLogEntries(criteria);
  }

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
  @RequestMapping(value = "/keywords/{keywords}/{start}/{end}", method = RequestMethod.DELETE)
  public int deleteLogEntriesByKeywords(@PathVariable String[] keywords, @PathVariable long start,
      @PathVariable long end) {
    MatchCriteria criteria = new MatchCriteria();
    criteria.setStart(start);
    criteria.setEnd(end);
    criteria.setMessageKeywords(keywords);
    return deleteLogEntries(criteria);
  }

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
  @RequestMapping(value = "/labels/{labels}/{start}/{end}", method = RequestMethod.DELETE)
  public int deleteLogEntriesByLabels(@PathVariable String[] labels, @PathVariable long start,
      @PathVariable long end) {
    MatchCriteria criteria = new MatchCriteria();
    criteria.setStart(start);
    criteria.setEnd(end);
    criteria.setLabels(labels);
    return deleteLogEntries(criteria);
  }

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
  @RequestMapping(value = "/originServices/{originServices}/{start}/{end}",
      method = RequestMethod.DELETE)
  public int deleteLogEntriesByOriginServices(@PathVariable String[] originServices,
      @PathVariable long start, @PathVariable long end) {
    MatchCriteria criteria = new MatchCriteria();
    criteria.setStart(start);
    criteria.setEnd(end);
    criteria.setOriginServices(originServices);
    return deleteLogEntries(criteria);
  }

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
  @RequestMapping(value = "/logLevels/{logLevels}/{start}/{end}", method = RequestMethod.DELETE)
  public int deleteLogEntriesByLogLevels(@PathVariable Level[] logLevels, @PathVariable long start,
      @PathVariable long end) {
    MatchCriteria criteria = new MatchCriteria();
    criteria.setStart(start);
    criteria.setEnd(end);
    criteria.setLogLevels(logLevels);
    return deleteLogEntries(criteria);
  }

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
  @RequestMapping(value = "/logLevels/{logLevels}/originServices/{originServices}/{start}/{end}",
      method = RequestMethod.DELETE)
  public int deleteLogEntriesByLogLevelsAndOriginServices(@PathVariable Level[] logLevels,
      @PathVariable String[] originServices, @PathVariable long start, @PathVariable long end) {
    MatchCriteria criteria = new MatchCriteria();
    criteria.setStart(start);
    criteria.setEnd(end);
    criteria.setLogLevels(logLevels);
    criteria.setOriginServices(originServices);
    return deleteLogEntries(criteria);
  }

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
  @RequestMapping(
      value = "/logLevels/{logLevels}/originServices/{originServices}/labels/{labels}/{start}/{end}",
      method = RequestMethod.DELETE)
  public int deleteLogEntriesByLogLevelsAndOriginServicesAndLabels(@PathVariable Level[] logLevels,
      @PathVariable String[] originServices, @PathVariable String[] labels,
      @PathVariable long start, @PathVariable long end) {
    MatchCriteria criteria = new MatchCriteria();
    criteria.setStart(start);
    criteria.setEnd(end);
    criteria.setLogLevels(logLevels);
    criteria.setOriginServices(originServices);
    criteria.setLabels(labels);
    return deleteLogEntries(criteria);
  }

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
  @RequestMapping(
      value = "/logLevels/{logLevels}/originServices/{originServices}/labels/{labels}/keywords/{keywords}/{start}/{end}",
      method = RequestMethod.DELETE)
  public int deleteLogEntriesByLogLevelsAndOriginServicesAndLabelsAndKeywords(
      @PathVariable Level[] logLevels, @PathVariable String[] originServices,
      @PathVariable String[] labels, @PathVariable String[] keywords, @PathVariable long start,
      @PathVariable long end) {
    MatchCriteria criteria = new MatchCriteria();
    criteria.setStart(start);
    criteria.setEnd(end);
    criteria.setLogLevels(logLevels);
    criteria.setOriginServices(originServices);
    criteria.setLabels(labels);
    criteria.setMessageKeywords(keywords);
    return deleteLogEntries(criteria);
  }

  private List<LogEntry> getEnteries(MatchCriteria criteria, int limit) {
    try {
      return service.searchByCriteria(criteria, limit);
    } catch (Exception e) {
      logger.error(FETCH_ERROR, e);
      throw new ServiceException(e);
    }
  }

  private int deleteLogEntries(MatchCriteria criteria) {
    try {
      return service.removeByCriteria(criteria).size();
    } catch (Exception e) {
      logger.error(REMOVE_ERROR, e);
      throw new ServiceException(e);
    }
  }
}
