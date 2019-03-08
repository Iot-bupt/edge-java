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
 * @microservice: core-metadata-client
 * @author: Jim White, Dell
 * @version: 1.0.0
 *******************************************************************************/

package org.edgexfoundry.controller.integration;

import static org.edgexfoundry.test.data.ScheduleEventData.TEST_SCHEDULE_EVENT_NAME;
import static org.edgexfoundry.test.data.ScheduleEventData.checkTestData;
import static org.edgexfoundry.test.data.ScheduleEventData.newTestInstance;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.util.List;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.ServiceUnavailableException;

import org.edgexfoundry.controller.AddressableClient;
import org.edgexfoundry.controller.ScheduleClient;
import org.edgexfoundry.controller.ScheduleEventClient;
import org.edgexfoundry.controller.impl.AddressableClientImpl;
import org.edgexfoundry.controller.impl.ScheduleClientImpl;
import org.edgexfoundry.controller.impl.ScheduleEventClientImpl;
import org.edgexfoundry.domain.meta.Addressable;
import org.edgexfoundry.domain.meta.Schedule;
import org.edgexfoundry.domain.meta.ScheduleEvent;
import org.edgexfoundry.test.category.RequiresMetaDataRunning;
import org.edgexfoundry.test.category.RequiresMongoDB;
import org.edgexfoundry.test.data.AddressableData;
import org.edgexfoundry.test.data.ScheduleData;
import org.edgexfoundry.test.data.ScheduleEventData;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category({RequiresMongoDB.class, RequiresMetaDataRunning.class})
public class ScheduleEventClientTest {

  private static final String ENDPT = "http://localhost:48081/api/v1/scheduleevent";
  private static final String SCH_ENDPT = "http://localhost:48081/api/v1/schedule";
  private static final String ADDR_ENDPT = "http://localhost:48081/api/v1/addressable";

  private ScheduleEventClient client;
  private ScheduleClient schClient;
  private AddressableClient addrClient;
  private String id;
  private String aid;

  // setup tests add function
  @Before
  public void setup() throws Exception {
    client = new ScheduleEventClientImpl();
    schClient = new ScheduleClientImpl();
    addrClient = new AddressableClientImpl();
    setURLs();
    Addressable addr = AddressableData.newTestInstance();
    aid = addrClient.add(addr);
    Schedule schedule = ScheduleData.newTestInstance();
    schClient.add(schedule);
    ScheduleEvent event = ScheduleEventData.newTestInstance();
    event.setAddressable(addr);
    event.setSchedule(schedule.getName());
    id = client.add(event);
  }

  private void setURLs() throws Exception {
    Class<?> clientClass = client.getClass();
    Field temp = clientClass.getDeclaredField("url");
    temp.setAccessible(true);
    temp.set(client, ScheduleEventClientTest.ENDPT);
    Class<?> clientClass2 = schClient.getClass();
    Field temp2 = clientClass2.getDeclaredField("url");
    temp2.setAccessible(true);
    temp2.set(schClient, SCH_ENDPT);
    Class<?> clientClass3 = addrClient.getClass();
    Field temp3 = clientClass3.getDeclaredField("url");
    temp3.setAccessible(true);
    temp3.set(addrClient, ADDR_ENDPT);
  }

  // cleanup tests delete function
  @After
  public void cleanup() throws Exception {
    // make sure to remove events before schedules for dependency issues
    List<ScheduleEvent> events = client.scheduleEvents();
    events.forEach((event) -> client.delete(event.getId()));
    List<Schedule> schs = schClient.schedules();
    schs.forEach((schedule) -> schClient.delete(schedule.getId()));
    List<Addressable> addrs = addrClient.addressables();
    addrs.forEach((addr) -> addrClient.delete(addr.getId()));
  }

  @Test
  public void testScheduleEvent() {
    ScheduleEvent event = client.scheduleEvent(id);
    checkTestData(event, id);
  }

  @Test(expected = NotFoundException.class)
  public void testScheduleEventWithUnknownId() {
    client.scheduleEvent("nosuchid");
  }

  @Test
  public void testScheduleEvents() {
    List<ScheduleEvent> events = client.scheduleEvents();
    assertEquals("Find all not returning a list with one schedule event", 1, events.size());
    checkTestData(events.get(0), id);
  }

  @Test
  public void testScheduleEventsForAddressable() {
    List<ScheduleEvent> events = client.scheduleEventsForAddressable(aid);
    assertEquals("Find all not returning a list with one schedule event", 1, events.size());
    checkTestData(events.get(0), id);
  }

  @Test
  public void testScheduleEventsForAddressableByName() {
    List<ScheduleEvent> events =
        client.scheduleEventsForAddressableByName(AddressableData.TEST_ADDR_NAME);
    assertEquals("Find all not returning a list with one schedule event", 1, events.size());
    checkTestData(events.get(0), id);
  }

  @Test
  public void testScheduleEventsForServiceByName() {
    List<ScheduleEvent> events =
        client.scheduleEventsForServiceByName(ScheduleEventData.TEST_SERVICE_NAME);
    assertEquals("Find all not returning a list with one schedule event", 1, events.size());
    checkTestData(events.get(0), id);
  }

  @Test
  public void testScheduleEventForName() {
    ScheduleEvent event = client.scheduleEventForName(TEST_SCHEDULE_EVENT_NAME);
    checkTestData(event, id);
  }

  @Test(expected = NotFoundException.class)
  public void testScheduleEventForNameWithNoneMatching() {
    client.scheduleEventForName("badname");
  }

  @Test(expected = NotFoundException.class)
  public void testAddWithBadScheduleName() {
    ScheduleEvent event = newTestInstance();
    event.setName("NewName");
    event.setSchedule("badschedule");
    client.add(event);
  }

  @Test(expected = ClientErrorException.class)
  public void testAddWithNoScheduleName() {
    ScheduleEvent event = newTestInstance();
    event.setName("NewName");
    event.setSchedule(null);
    client.add(event);
  }

  @Test(expected = ClientErrorException.class)
  public void testAddWithSameName() {
    ScheduleEvent event = client.scheduleEvent(id);
    event.setId(null);
    client.add(event);
  }

  @Test
  public void testDelete() {
    assertTrue("Delete did not return correctly", client.delete(id));
  }

  @Test(expected = NotFoundException.class)
  public void testDeleteWithNone() {
    client.delete("badid");
  }

  @Test
  public void testDeleteByName() {
    assertTrue("Delete did not return correctly", client.deleteByName(TEST_SCHEDULE_EVENT_NAME));
  }

  @Test(expected = NotFoundException.class)
  public void testDeleteByNameWithNone() {
    client.delete("badname");
  }

  @Test
  public void testUpdate() {
    ScheduleEvent event = client.scheduleEvent(id);
    event.setOrigin(1234);
    assertTrue("Update did not complete successfully", client.update(event));
    ScheduleEvent events = client.scheduleEvent(id);
    assertEquals("Update did not work correclty", 1234, events.getOrigin());
    assertNotNull("Modified date is null", events.getModified());
    assertNotNull("Create date is null", events.getCreated());
    assertTrue("Modified date and create date should be different after update",
        events.getModified() != events.getCreated());
  }

  @Test(expected = NotFoundException.class)
  public void testUpdateWithBadScheduleName() {
    ScheduleEvent event = client.scheduleEvent(id);
    event.setSchedule("badschedule");
    client.update(event);
  }

  @Test
  public void testUpdateChangeNameWhileNotAssocToDeviceReport() {
    ScheduleEvent event = client.scheduleEvent(id);
    event.setName("newname");
    assertTrue("Update did not complete successfully", client.update(event));
  }

  @Test(expected = NotFoundException.class)
  public void testUpdateWithNone() {
    ScheduleEvent event = client.scheduleEvent(id);
    event.setId("badid");
    event.setName("badname");
    event.setOrigin(1234);
    client.update(event);
  }

}
