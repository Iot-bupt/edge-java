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
 * @microservice: core-test library
 * @author: Jim White, Dell
 * @version: 1.0.0
 *******************************************************************************/
package org.edgexfoundry.test.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.edgexfoundry.domain.meta.Schedule;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public interface ScheduleData {

  static final String TEST_SCHEDULE_ID1 = "one";
  static final String TEST_SCHEDULE_ID2 = "two";
  static final String TEST_SCHEDULE_NAME = "test schedule";
  static final String TEST_SCHEDULE_NAME_NONE = "test schedule none";
  static final String TEST_START = ""; // defaults to now
  static final String TEST_END = ""; // defaults to ZDT MAX
  static final String TEST_TIME_2015 = "20150101T000000";
  static final String TEST_FREQUENCY_2S = "PT2S";
  static final String TEST_FREQUENCY_5S = "PT5S";
  static final String TEST_FREQUENCY_10S = "PT10S";
  static final String TEST_FREQUENCY_20S = "PT20S";
  static final String TEST_FREQUENCY_1M = "PT1M";
  static final String TEST_FREQUENCY_1H = "PT1H";
  static final String TEST_FREQUENCY_1D = "P1D";
  static final String TEST_FREQUENCY_1MN = "P1M";
  static final String TEST_FREQUENCY_1D1H = "P1DT1H";
  static final String TEST_CRON = "0 0 12 * * ?";
  static final boolean TEST_RUN_ONCE_TRUE = true;
  static final boolean TEST_RUN_ONCE_FALSE = false;

  static Schedule newTestInstance() {
    DateTimeFormatter formatter =
        DateTimeFormatter.ofPattern(Schedule.DATETIME_FORMATS[0]).withZone(ZoneId.systemDefault());
    Instant now = Instant.now();
    String start = formatter.format(now.plusSeconds(1));
    String end = formatter.format(now.plusSeconds(3));
    Schedule s = new Schedule(TEST_SCHEDULE_NAME, start, end, TEST_FREQUENCY_2S, TEST_CRON,
        TEST_RUN_ONCE_FALSE);
    s.setOrigin(CommonData.TEST_ORIGIN);
    return s;
  }

  static void checkTestData(Schedule schedule, String id, String start, String end,
      boolean runonce) {
    assertEquals("Schedule id does not match expected", id, schedule.getId());
    assertEquals("Schedule name does not match expected", TEST_SCHEDULE_NAME, schedule.getName());
    assertEquals("Schedule origin does not match expected", CommonData.TEST_ORIGIN,
        schedule.getOrigin());
    assertEquals("Schedule cron does not match expected", TEST_CRON, schedule.getCron());
    assertEquals("Schedule end does not match expected", end, schedule.getEnd());
    assertEquals("Schedule start does not match expected", start, schedule.getStart());
    assertEquals("Schedule frequency does not match expected", TEST_FREQUENCY_2S,
        schedule.getFrequency());
    assertEquals("Schedule run once does not match expected", runonce, schedule.getRunOnce());
    assertNotNull("Schedule schedule modified date is null", schedule.getModified());
    assertNotNull("Schedule schedule created date is null", schedule.getCreated());
  }
}
