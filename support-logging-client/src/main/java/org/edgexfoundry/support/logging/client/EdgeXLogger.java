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
 * @microservice: support-logging-client library
 * @author: Jude Hung, Dell
 * @version: 1.0.0
 *******************************************************************************/

package org.edgexfoundry.support.logging.client;

import org.edgexfoundry.support.domain.logging.LogEntry;
import org.slf4j.Marker;
import org.slf4j.event.Level;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.client.AsyncRestTemplate;

public class EdgeXLogger {

  // LOGGER is used internally to log all informative messages relating to EdgeXLogger
  // implementation
  private static final org.slf4j.Logger LOGGER =
      org.slf4j.LoggerFactory.getLogger(EdgeXLogger.class);

  // slf4jLogger is used as reference to logger
  private org.slf4j.Logger slf4jLogger;

  private AsyncRestTemplate asyncRestClient;

  EdgeXLogger(String name) {
    slf4jLogger = org.slf4j.LoggerFactory.getLogger(name);
    initAsync();
  }

  public boolean isRemoteLoggingEnabled() {
    return EdgeXLoggerPropertyHolder.isRemoteLoggingEnabled();
  }

  public void debug(String msg, String... labels) {
    slf4jLogger.debug(msg);
    log(Level.DEBUG, msg, labels);
  }

  public void debug(String msg) {
    debug(msg, (String[]) null);
  }

  public void debug(String format, Object arg) {
    slf4jLogger.debug(format, arg);
    log(Level.DEBUG, getFormattedMessage(format, arg), (String[]) null);
  }

  public void debug(String format, Object arg1, Object arg2) {
    slf4jLogger.debug(format, arg1, arg2);
    log(Level.DEBUG, getFormattedMessage(format, arg1, arg2), (String[]) null);
  }

  public void debug(String format, Object... arguments) {
    slf4jLogger.debug(format, arguments);
    log(Level.DEBUG, getFormattedMessage(format, arguments), (String[]) null);
  }

  public void debug(String msg, Throwable t) {
    slf4jLogger.debug(msg, t);
    log(Level.DEBUG, msg, (String[]) null);
  }

  public void debug(Marker marker, String msg) {
    slf4jLogger.debug(marker, msg);
    log(Level.DEBUG, msg, (String[]) null);
  }

  public void debug(Marker marker, String format, Object arg) {
    slf4jLogger.debug(marker, format, arg);
    log(Level.DEBUG, getFormattedMessage(format, arg), (String[]) null);
  }

  public void debug(Marker marker, String format, Object arg1, Object arg2) {
    slf4jLogger.debug(marker, format, arg1, arg2);
    log(Level.DEBUG, getFormattedMessage(format, arg1, arg2), (String[]) null);
  }

  public void debug(Marker marker, String format, Object... arguments) {
    slf4jLogger.debug(marker, format, arguments);
    log(Level.DEBUG, getFormattedMessage(format, arguments), (String[]) null);
  }

  public void debug(Marker marker, String msg, Throwable t) {
    slf4jLogger.debug(marker, msg, t);
    log(Level.DEBUG, msg, (String[]) null);
  }

  public void error(String msg, String... labels) {
    slf4jLogger.error(msg);
    log(Level.ERROR, msg, labels);
  }

  public void error(String msg) {
    error(msg, (String[]) null);
  }

  public void error(String format, Object arg) {
    slf4jLogger.error(format, arg);
    log(Level.ERROR, getFormattedMessage(format, arg), (String[]) null);
  }

  public void error(String format, Object arg1, Object arg2) {
    slf4jLogger.error(format, arg1, arg2);
    log(Level.ERROR, getFormattedMessage(format, arg1, arg2), (String[]) null);
  }

  public void error(String format, Object... arguments) {
    slf4jLogger.error(format, arguments);
    log(Level.ERROR, getFormattedMessage(format, arguments), (String[]) null);
  }

  public void error(String msg, Throwable t) {
    slf4jLogger.error(msg, t);
    log(Level.ERROR, msg, (String[]) null);
  }

  public void error(Marker marker, String msg) {
    slf4jLogger.error(marker, msg);
    log(Level.ERROR, msg, (String[]) null);
  }

  public void error(Marker marker, String format, Object arg) {
    slf4jLogger.error(marker, format, arg);
    log(Level.ERROR, getFormattedMessage(format, arg), (String[]) null);
  }

  public void error(Marker marker, String format, Object arg1, Object arg2) {
    slf4jLogger.error(marker, format, arg1, arg2);
    log(Level.ERROR, getFormattedMessage(format, arg1, arg2), (String[]) null);
  }

  public void error(Marker marker, String format, Object... arguments) {
    slf4jLogger.error(marker, format, arguments);
    log(Level.ERROR, getFormattedMessage(format, arguments), (String[]) null);
  }

  public void error(Marker marker, String msg, Throwable t) {
    slf4jLogger.error(marker, msg, t);
    log(Level.ERROR, msg, (String[]) null);
  }

  public void trace(String msg, String... labels) {
    slf4jLogger.trace(msg);
    log(Level.TRACE, msg, labels);
  }

  public void trace(String msg) {
    trace(msg, (String[]) null);
  }

  public void trace(String format, Object arg) {
    slf4jLogger.trace(format, arg);
    log(Level.TRACE, getFormattedMessage(format, arg), (String[]) null);
  }

  public void trace(String format, Object arg1, Object arg2) {
    slf4jLogger.trace(format, arg1, arg2);
    log(Level.TRACE, getFormattedMessage(format, arg1, arg2), (String[]) null);
  }

  public void trace(String format, Object... arguments) {
    slf4jLogger.trace(format, arguments);
    log(Level.TRACE, getFormattedMessage(format, arguments), (String[]) null);
  }

  public void trace(String msg, Throwable t) {
    slf4jLogger.trace(msg, t);
    log(Level.TRACE, msg, (String[]) null);
  }

  public void trace(Marker marker, String msg) {
    slf4jLogger.trace(marker, msg);
    log(Level.TRACE, msg, (String[]) null);
  }

  public void trace(Marker marker, String format, Object arg) {
    slf4jLogger.trace(marker, format, arg);
    log(Level.TRACE, getFormattedMessage(format, arg), (String[]) null);
  }

  public void trace(Marker marker, String format, Object arg1, Object arg2) {
    slf4jLogger.trace(marker, format, arg1, arg2);
    log(Level.TRACE, getFormattedMessage(format, arg1, arg2), (String[]) null);
  }

  public void trace(Marker marker, String format, Object... arguments) {
    slf4jLogger.trace(marker, format, arguments);
    log(Level.TRACE, getFormattedMessage(format, arguments), (String[]) null);
  }

  public void trace(Marker marker, String msg, Throwable t) {
    slf4jLogger.trace(marker, msg, t);
    log(Level.TRACE, msg, (String[]) null);
  }

  public void info(String msg, String... labels) {
    slf4jLogger.info(msg);
    log(Level.INFO, msg, labels);
  }

  public void info(String msg) {
    info(msg, (String[]) null);
  }

  public void info(String format, Object arg) {
    slf4jLogger.info(format, arg);
    log(Level.INFO, getFormattedMessage(format, arg), (String[]) null);
  }

  public void info(String format, Object arg1, Object arg2) {
    slf4jLogger.info(format, arg1, arg2);
    log(Level.INFO, getFormattedMessage(format, arg1, arg2), (String[]) null);
  }

  public void info(String format, Object... arguments) {
    slf4jLogger.info(format, arguments);
    log(Level.INFO, getFormattedMessage(format, arguments), (String[]) null);
  }

  public void info(String msg, Throwable t) {
    slf4jLogger.info(msg, t);
    log(Level.INFO, msg, (String[]) null);
  }

  public void info(Marker marker, String msg) {
    slf4jLogger.info(marker, msg);
    log(Level.INFO, msg, (String[]) null);
  }

  public void info(Marker marker, String format, Object arg) {
    slf4jLogger.info(marker, format, arg);
    log(Level.INFO, getFormattedMessage(format, arg), (String[]) null);
  }

  public void info(Marker marker, String format, Object arg1, Object arg2) {
    slf4jLogger.info(marker, format, arg1, arg2);
    log(Level.INFO, getFormattedMessage(format, arg1, arg2), (String[]) null);
  }

  public void info(Marker marker, String format, Object... arguments) {
    slf4jLogger.info(marker, format, arguments);
    log(Level.INFO, getFormattedMessage(format, arguments), (String[]) null);
  }

  public void info(Marker marker, String msg, Throwable t) {
    slf4jLogger.info(marker, msg, t);
    log(Level.INFO, msg, (String[]) null);
  }

  public void warn(String msg, String... labels) {
    slf4jLogger.warn(msg);
    log(Level.WARN, msg, labels);
  }

  public void warn(String msg) {
    warn(msg, (String[]) null);
  }

  public void warn(String format, Object arg) {
    slf4jLogger.warn(format, arg);
    log(Level.WARN, getFormattedMessage(format, arg), (String[]) null);
  }

  public void warn(String format, Object... arguments) {
    slf4jLogger.warn(format, arguments);
    log(Level.WARN, getFormattedMessage(format, arguments), (String[]) null);
  }

  public void warn(String format, Object arg1, Object arg2) {
    slf4jLogger.warn(format, arg1, arg2);
    log(Level.WARN, getFormattedMessage(format, arg1, arg2), (String[]) null);
  }

  public void warn(String msg, Throwable t) {
    slf4jLogger.warn(msg, t);
    log(Level.WARN, msg, (String[]) null);
  }

  public void warn(Marker marker, String msg) {
    slf4jLogger.warn(marker, msg);
    log(Level.WARN, msg, (String[]) null);
  }

  public void warn(Marker marker, String format, Object arg) {
    slf4jLogger.warn(marker, format, arg);
    log(Level.WARN, getFormattedMessage(format, arg), (String[]) null);
  }

  public void warn(Marker marker, String format, Object arg1, Object arg2) {
    slf4jLogger.warn(marker, format, arg1, arg2);
    log(Level.WARN, getFormattedMessage(format, arg1, arg2), (String[]) null);
  }

  public void warn(Marker marker, String format, Object... arguments) {
    slf4jLogger.warn(marker, format, arguments);
    log(Level.WARN, getFormattedMessage(format, arguments), (String[]) null);
  }

  public void warn(Marker marker, String msg, Throwable t) {
    slf4jLogger.warn(marker, msg, t);
    log(Level.WARN, msg, (String[]) null);
  }

  // following methods are simply offered by org.slf4j.Logger and would delegate to slf4jLoffer
  // internally.
  public String getName() {
    return slf4jLogger.getName();
  }

  public boolean isTraceEnabled() {
    return slf4jLogger.isTraceEnabled();
  }

  public boolean isTraceEnabled(Marker marker) {
    return slf4jLogger.isTraceEnabled(marker);
  }

  public boolean isDebugEnabled() {
    return slf4jLogger.isDebugEnabled();
  }

  public boolean isDebugEnabled(Marker marker) {
    return slf4jLogger.isDebugEnabled(marker);
  }

  public boolean isInfoEnabled() {
    return slf4jLogger.isInfoEnabled();
  }

  public boolean isInfoEnabled(Marker marker) {
    return slf4jLogger.isInfoEnabled(marker);
  }

  public boolean isWarnEnabled() {
    return slf4jLogger.isWarnEnabled();
  }

  public boolean isWarnEnabled(Marker marker) {
    return slf4jLogger.isWarnEnabled(marker);
  }

  public boolean isErrorEnabled() {
    return slf4jLogger.isErrorEnabled();
  }

  public boolean isErrorEnabled(Marker marker) {
    return slf4jLogger.isErrorEnabled(marker);
  }

  private void log(Level logLevel, String msg, String... labels) {
    boolean loggable = false;
    switch (logLevel) {
      case TRACE:
        loggable = slf4jLogger.isTraceEnabled();
        break;
      case DEBUG:
        loggable = slf4jLogger.isDebugEnabled();
        break;
      case WARN:
        loggable = slf4jLogger.isWarnEnabled();
        break;
      case INFO:
        loggable = slf4jLogger.isInfoEnabled();
        break;
      case ERROR:
        loggable = slf4jLogger.isErrorEnabled();
        break;
      default:
        loggable = slf4jLogger.isInfoEnabled();
        break;
    }
    if (loggable && isRemoteLoggingEnabled()) {
      LogEntry logEntry = buildLogEntry(logLevel, msg, labels);
      invokeAsyncRestLogging(logEntry);
    }
  }

  private void initAsync() {
    asyncRestClient = new AsyncRestTemplate();
    asyncRestClient.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
  }

  private LogEntry buildLogEntry(Level logLevel, String msg, String... labels) {
    LogEntry logEntry = new LogEntry();
    logEntry.setLogLevel(logLevel);
    logEntry.setMessage(msg);
    logEntry.setLabels(labels);
    logEntry.setOriginService(getOriginServiceName());
    return logEntry;
  }

  private void invokeAsyncRestLogging(LogEntry logEntry) {

    HttpEntity<LogEntry> entity = new HttpEntity<>(logEntry);

    ListenableFuture<ResponseEntity<Long>> futureEntity = asyncRestClient
        .postForEntity(EdgeXLoggerPropertyHolder.getLoggingServiceURL(), entity, Long.class);

    // register a callback for asynchronous invocation
    futureEntity.addCallback(new ListenableFutureCallback<ResponseEntity<Long>>() {

      public void onSuccess(ResponseEntity<Long> result) {// callback for success
        LOGGER.debug("Result from remote logging service: " + result);
      }

      public void onFailure(Throwable ex) {// callback for failure
        LOGGER.error("Error invoking remote logging service: " + ex);
      }
    });
  }

  private String getOriginServiceName() {
    return EdgeXLoggerPropertyHolder.getApplicationName();
  }

  private String getFormattedMessage(String format, Object arg) {
    FormattingTuple ft = MessageFormatter.format(format, arg);
    return ft.getMessage();
  }

  private String getFormattedMessage(String format, Object arg1, Object arg2) {
    FormattingTuple ft = MessageFormatter.format(format, arg1, arg2);
    return ft.getMessage();
  }

  private String getFormattedMessage(String format, Object... arguments) {
    FormattingTuple ft = MessageFormatter.arrayFormat(format, arguments);
    return ft.getMessage();
  }

}
