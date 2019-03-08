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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.edgexfoundry.support.domain.logging.LogEntry;
import org.edgexfoundry.support.logging.dao.LogEntryDAO;
import org.edgexfoundry.support.logging.dao.MDC_ENUM_CONSTANTS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Value;

public abstract class BaseLogEntryDAO implements LogEntryDAO {

  private static final Logger LOGGER = LoggerFactory.getLogger(BaseLogEntryDAO.class);
  private static final String NO_COLOR = "\033[0m"; // ANSI default color format
  private static final String GREEN = "\033[1;32m"; // ANSI foreground green
  private static final String RED = "\033[1;31m"; // ANSI foreground red
  private static final String YELLOW = "\033[1;33m"; // ANSI foreground yellow

  @Value("${logging.color.enabled}")
  private Boolean addColor = false;
  private Map<String, String> colors = new HashMap<>();
  private int baseColorCount = 33;

  /*
   * (non-Javadoc)
   * 
   * @see org.edgexfoundry.support.logging.dao.LogEntryDAO#save(org.edgexfoundry.support.
   * logging.domain.LogEntry)
   */
  @Override
  public boolean save(LogEntry entry) {
    MDC.put(MDC_ENUM_CONSTANTS.CREATED.getValue(), Long.toString(entry.getCreated()));
    MDC.put(MDC_ENUM_CONSTANTS.ORIGINSERVICE.getValue(), entry.getOriginService());
    MDC.put(MDC_ENUM_CONSTANTS.LABELS.getValue(),
        null == entry.getLabels() ? "[]" : Arrays.toString(entry.getLabels()));
    Level level = entry.getLogLevel();
    if (!colors.containsKey(entry.getOriginService()))
      colors.put(entry.getOriginService(), generateColor());
    boolean loggable = false;
    synchronized (LOGGER) {
      String msg = wrapMessage(entry);
      switch (level) {
        case DEBUG:
          LOGGER.debug(msg);
          loggable = LOGGER.isDebugEnabled();
          break;
        case INFO:
          LOGGER.info(msg);
          loggable = LOGGER.isInfoEnabled();
          break;
        case WARN:
          LOGGER.warn(msg);
          loggable = LOGGER.isWarnEnabled();
          break;
        case ERROR:
          LOGGER.error(msg);
          loggable = LOGGER.isErrorEnabled();
          break;
        default:
          LOGGER.trace(msg);
          loggable = LOGGER.isTraceEnabled();
          break;
      }
    }
    return loggable;
  }

  private String generateColor() {
    baseColorCount++;
    if (baseColorCount == 38) // end of base 8 foreground colors
      baseColorCount = 90; // ANSI base 16 foreground colors
    if (baseColorCount == 91) // skip high visibility RGY for differentiation
      baseColorCount = 94; // ANSI base 16 foreground colors
    if (baseColorCount == 98) // end of base 16 foreground colors
      baseColorCount = 44; // ANSI base 8 background colors
    if (baseColorCount == 48) // end of base 8 background colors
      baseColorCount = 104; // start of base 16 background colors
    if (baseColorCount == 108)// end of base 16 background colors
      baseColorCount = 34; // start of base 16 background colors
    return String.format("\033[0m\033[1;%dm", baseColorCount); // generate ANSI color code
  }

  private String wrapMessage(LogEntry entry) {
    if (addColor) {
      switch (entry.getLogLevel()) {
        case DEBUG:
        case INFO:
        case TRACE:
          System.out.print(GREEN);
          break;
        case WARN:
          System.out.print(YELLOW);
          break;
        case ERROR:
        default:
          System.out.print(RED);
          break;
      }
      return colors.get(entry.getOriginService()) + entry.getMessage() + NO_COLOR;
    } else
      return entry.getMessage();
  }

}
