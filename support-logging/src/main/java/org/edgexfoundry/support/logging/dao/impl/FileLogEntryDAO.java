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

package org.edgexfoundry.support.logging.dao.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.edgexfoundry.support.domain.logging.LogEntry;
import org.edgexfoundry.support.domain.logging.MatchCriteria;
import org.edgexfoundry.support.logging.dao.MDC_ENUM_CONSTANTS;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.rolling.FixedWindowRollingPolicy;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy;

@Component("serviceDAO")
@ConditionalOnProperty(name = {"logging.persistence"}, havingValue = "file")
public class FileLogEntryDAO extends BaseLogEntryDAO {

  private static final String LOG_PATTERN_STR =
      "(^[0-9]*) \\[(.*)\\] \\[(.*)\\] (TRACE|DEBUG|INFO |WARN |ERROR) - (.*)";
  private static final Pattern LOG_PATTERN = Pattern.compile(LOG_PATTERN_STR);
  private static final String LOG_FORMAT = "%X{" + MDC_ENUM_CONSTANTS.CREATED.getValue() + "} [%X{"
      + MDC_ENUM_CONSTANTS.ORIGINSERVICE.getValue() + "}] %X{"
      + MDC_ENUM_CONSTANTS.LABELS.getValue() + "} %-5level - %msg%n";
  private static final String MANDATORY_POSITION_VARIABLE = "%i";
  private static final String TMP_LOGGING_FILE_EXT = ".tmp";

  private final List<LogEntry> logEntries = new ArrayList<>();

  @Value("${logging.persistence.file}")
  private String loggingFilePath = "edgex-support-logging.log";

  @Value("${logging.persistence.file.maxsize}")
  private String loggingFileMaxSize = "5MB";

  @PostConstruct
  private void init() {
    System.out.println("Logging is using Files to persist log messages.");
    initFileLogging();
    loadLoggingCache();
  }

  /**
   * This method would initialize the logback logger being named BaseLogEntryDAO
   */
  private void initFileLogging() {
    LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
    /**
     * // reset loggerContext to remove all internal properties prior to add // rollingFileAppender
     * // 20161122d:comment out following line to avoid reset loggerContext as // it would reset
     * every logger and cause internal loggers not work // anymore. // 20161216:commenting out
     * following line would result in the collision // detected by
     * checkForFileCollisionInPreviousFileAppenders() of logback // FileAppender and fail to restart
     * the FileAppender. To avoid the // collision, removing the FileName out of collision map when
     * detaching // FileAppender(see stopAndDetachFileAppdnder method) // loggerContext.reset();
     * 
     */

    RollingFileAppender<ILoggingEvent> rfAppender = new RollingFileAppender<>();
    rfAppender.setContext(loggerContext);
    rfAppender.setFile(this.loggingFilePath);
    rfAppender.setName(loggingFilePath);

    FixedWindowRollingPolicy rollingPolicy = new FixedWindowRollingPolicy();
    rollingPolicy.setContext(loggerContext);
    rollingPolicy.setParent(rfAppender);
    rollingPolicy.setFileNamePattern(this.loggingFilePath + MANDATORY_POSITION_VARIABLE);
    rollingPolicy.start();

    SizeBasedTriggeringPolicy<ILoggingEvent> triggeringPolicy =
        new ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy<>();
    triggeringPolicy.setMaxFileSize(this.loggingFileMaxSize);
    triggeringPolicy.start();

    PatternLayoutEncoder encoder = new PatternLayoutEncoder();
    encoder.setContext(loggerContext);
    encoder.setPattern(LOG_FORMAT);
    encoder.start();

    rfAppender.setEncoder(encoder);
    rfAppender.setRollingPolicy(rollingPolicy);
    rfAppender.setTriggeringPolicy(triggeringPolicy);

    rfAppender.start();

    Logger baseLogger = loggerContext.getLogger(BaseLogEntryDAO.class);
    baseLogger.addAppender(rfAppender);
  }

  /**
   * This method would load the log entries existed in the active log at the time when the logging
   * service is launched. For performance consideration, this method wouldn't load those log entries
   * existed in the archived log file.
   */
  private void loadLoggingCache() {
    String currentLine;
    String trimmedLine;
    LogEntry logEntry;
    try (FileReader inputFile = new FileReader(loggingFilePath);
        BufferedReader reader = new BufferedReader(inputFile)) {
      while ((currentLine = reader.readLine()) != null) {
        // trim newline when comparing with lineToRemove
        trimmedLine = currentLine.trim();
        logEntry = convertString2LogEntry(trimmedLine);
        if (null != logEntry) {
          logEntries.add(logEntry);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void stopAndDetachFileAppdnder(String fileAppenderName) {
    LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
    Logger baseLogger = loggerContext.getLogger(BaseLogEntryDAO.class);
    RollingFileAppender<ILoggingEvent> rfAppender =
        (RollingFileAppender<ILoggingEvent>) baseLogger.getAppender(fileAppenderName);
    rfAppender.stop();
    baseLogger.detachAppender(rfAppender);
    // 20161216:commenting out loggerContext.reset() at initFileLogging()
    // would result in the collision detected by
    // checkForFileCollisionInPreviousFileAppenders() of logback
    // FileAppender and fail to restart the FileAppender.
    // To avoid the collision, removing the FileName out of collision map
    // when detaching FileAppender
    @SuppressWarnings("unchecked")
    Map<String, String> map = (Map<String, String>) loggerContext
        .getObject(CoreConstants.RFA_FILENAME_PATTERN_COLLISION_MAP);
    if (null != map) {
      map.remove(fileAppenderName);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.edgexfoundry.support.logging.dao.LogEntryDAO#save(org.edgexfoundry.support.
   * logging.domain.LogEntry)
   */
  @Override
  public boolean save(LogEntry entry) {
    boolean result = super.save(entry);
    if (result) {// only add logEntry into cache when it's loggable
      logEntries.add(entry);
    }
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.edgexfoundry.support.logging.dao.LogEntryDAO#findByCriteria(org.edgexfoundry.
   * support.logging.domain.MatchCriteria, int)
   */
  @Override
  public List<LogEntry> findByCriteria(MatchCriteria criteria, int limit) {
    List<LogEntry> result = new ArrayList<>();
    int i = 0;
    if (!logEntries.isEmpty() && null != criteria) {
      for (LogEntry entry : logEntries) {
        if (compareArrays(entry.getLabels(), criteria.getLabels())
            && containSource(entry.getLogLevel(), criteria.getLogLevels())
            && containSource(entry.getOriginService(), criteria.getOriginServices())
            && containStringKeyword(entry.getMessage(), criteria.getMessageKeywords())
            && matchTimestamp(entry.getCreated(), criteria.getStart(), criteria.getEnd())) {

          if (limit >= 0 && ++i > limit) {// break the iteration as
                                          // the number of fetched
                                          // logs has reached the
                                          // limit
            break;
          }
          result.add(entry);
        }
      }
    }
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.edgexfoundry.support.logging.dao.LogEntryDAO#removeByCriteria(org.edgexfoundry.support.
   * logging.domain.MatchCriteria)
   */
  @Override
  public List<LogEntry> removeByCriteria(MatchCriteria criteria) {
    List<LogEntry> targets = this.findByCriteria(criteria, -1);
    if (!targets.isEmpty()) {
      try {
        if (removeFileLogEntries(targets)) {
          logEntries.removeAll(targets);
        } else {
          throw new IOException("failed to remove file log entries");
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return targets;
  }

  private synchronized boolean removeFileLogEntries(List<LogEntry> targets) throws IOException {

    // to remove log entries out of log files, need to stop fileAppender to
    // release file lock
    stopAndDetachFileAppdnder(this.loggingFilePath);

    File inputFile = new File(loggingFilePath);
    File tempFile = new File(loggingFilePath + TMP_LOGGING_FILE_EXT);

    try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
      String currentLine;
      String trimmedLine;
      boolean toRemove;
      while ((currentLine = reader.readLine()) != null) {
        trimmedLine = currentLine.trim();
        toRemove = false;
        String lineToRemove;
        for (LogEntry entry : targets) {
          lineToRemove = entry.toString();
          if (trimmedLine.equals(lineToRemove)) {
            toRemove = true;
            break;
          }
        }
        if (toRemove) {
          continue;
        }
        writer.write(currentLine + System.getProperty("line.separator"));
      }
    }

    try {
      Files.move(tempFile.toPath(), inputFile.toPath(),
          java.nio.file.StandardCopyOption.REPLACE_EXISTING);
      return true;
    } catch (IOException ioe) {
      return false;
    } finally {
      // remember to initialize logging system to add back
      // RollingFileAppender, so the future logging request still could
      // work
      initFileLogging();
    }

  }

  private LogEntry convertString2LogEntry(String target) {
    LogEntry result = null;
    if (null != target) {
      Matcher matcher = LOG_PATTERN.matcher(target);
      if (matcher.find() && matcher.groupCount() == 5) {
        result = new LogEntry();
        result.setCreated(Long.parseLong(matcher.group(1)));
        result.setOriginService("".equals(matcher.group(2)) ? null : matcher.group(2));
        result.setLabels("".equals(matcher.group(3)) ? null : matcher.group(3).split(", "));
        result.setLogLevel(Level.valueOf(matcher.group(4).trim()));
        result.setMessage(matcher.group(5));
      }
    }
    return result;
  }

  /**
   * Compare two arrays to see if targets array contains one object of sources array
   * 
   * @param sources
   * @param targets
   * @return true if one object of sources is in the targets or targets is null/empty; return false
   *         otherwise.
   */
  private static <A> boolean compareArrays(A[] sources, A[] targets) {
    // null or empty targets means nothing to match for, so return true
    if (null == targets || targets.length == 0) {
      return true;
    }
    if (null != sources && sources.length > 0 && targets.length > 0) {
      List<A> list = new ArrayList<>(Arrays.asList(targets));
      for (A source : sources) {
        if (list.contains(source)) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Check if targets array contain source object
   * 
   * @param source
   * @param targets
   * @return true if targets array contain source object or targets array is null/empty; return
   *         false otherwise
   */
  private static <A> boolean containSource(A source, A[] targets) {
    if (null == targets || targets.length == 0) {
      return true;
    }
    if (null != source && targets.length > 0) {
      List<A> list = new ArrayList<>(Arrays.asList(targets));
      return list.contains(source);
    }
    return false;
  }

  /**
   * Check if specific String contains any keyword listed in an array
   * 
   * @param message
   * @param keywords
   * @return true if any string of keywords is contained by message; false otherwise
   */
  private static boolean containStringKeyword(String message, String[] keywords) {
    if (null == keywords || keywords.length == 0) {
      return true;
    }
    for (String keyword : keywords) {
      if (message.contains(keyword)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Check if date time representing in target long value is within start and end date time interval
   * 
   * @param target
   * @param start
   * @param end
   * @return true if date time representing in target long value is within start and end date time
   *         interval; return false otherwise
   */
  private static boolean matchTimestamp(long target, long start, long end) {
    Date entryTime = new Date(target);
    if (0L != start) {
      if (0L != end) {
        return (entryTime.after(new Date(start)) && entryTime.before(new Date(end)));
      } else {
        return entryTime.after(new Date(start));
      }
    } else {
      if (0L != end) {
        return entryTime.before(new Date(end));
      } else {
        return true;
      }
    }
  }

}
