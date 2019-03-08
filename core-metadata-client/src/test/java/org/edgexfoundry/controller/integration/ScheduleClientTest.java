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

import static org.edgexfoundry.test.data.ScheduleData.TEST_SCHEDULE_NAME;
import static org.edgexfoundry.test.data.ScheduleData.checkTestData;
import static org.edgexfoundry.test.data.ScheduleData.newTestInstance;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.util.List;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.NotFoundException;

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
public class ScheduleClientTest {

  private static final String ENDPT = "http://localhost:48081/api/v1/schedule";
  private static final String EVT_ENDPT = "http://localhost:48081/api/v1/scheduleevent";
  private static final String ADDR_ENDPT = "http://localhost:48081/api/v1/addressable";

  private ScheduleClient client;
  private ScheduleEventClient evtClient;
  private AddressableClient addrClient;
  private String id;
  private String start;
  private String end;
  private Addressable addr;

  // setup tests add function
  @Before
  public void setup() throws Exception {
    evtClient = new ScheduleEventClientImpl();
    client = new ScheduleClientImpl();
    addrClient = new AddressableClientImpl();
    addr = AddressableData.newTestInstance();
    setURL();
    addrClient.add(addr);
    Schedule schedule = ScheduleData.newTestInstance();
    start = schedule.getStart();
    end = schedule.getEnd();
    id = client.add(schedule);
  }

  private void setURL() throws Exception {
    Class<?> clientClass = client.getClass();
    Field temp = clientClass.getDeclaredField("url");
    temp.setAccessible(true);
    temp.set(client, ENDPT);
    Class<?> clientClass2 = evtClient.getClass();
    Field temp2 = clientClass2.getDeclaredField("url");
    temp2.setAccessible(true);
    temp2.set(evtClient, EVT_ENDPT);
    Class<?> clientClass3 = addrClient.getClass();
    Field temp3 = clientClass3.getDeclaredField("url");
    temp3.setAccessible(true);
    temp3.set(addrClient, ADDR_ENDPT);
  }

  // cleanup tests delete function
  @After
  public void cleanup() throws Exception {
    List<ScheduleEvent> events = evtClient.scheduleEvents();
    events.forEach((event) -> evtClient.delete(event.getId()));
    List<Schedule> schedules = client.schedules();
    schedules.forEach((sch) -> client.delete(sch.getId()));
    List<Addressable> addressables = addrClient.addressables();
    addressables.forEach((addr) -> addrClient.delete(addr.getId()));
  }

  @Test
  public void testSchedule() {
    Schedule schedule = client.schedule(id);
    checkTestData(schedule, id, start, end, ScheduleData.TEST_RUN_ONCE_FALSE);
  }

  @Test(expected = NotFoundException.class)
  public void testScheduleWithUnknownId() {
    client.schedule("nosuchid");
  }

  @Test
  public void testSchedules() {
    List<Schedule> schedules = client.schedules();
    assertEquals("Find all not returning a list with one schedule", 1, schedules.size());
    checkTestData(schedules.get(0), id, start, end, ScheduleData.TEST_RUN_ONCE_FALSE);
  }

  @Test
  public void testScheduleForName() {
    Schedule schedule = client.scheduleForName(TEST_SCHEDULE_NAME);
    checkTestData(schedule, id, start, end, ScheduleData.TEST_RUN_ONCE_FALSE);
  }

  @Test(expected = NotFoundException.class)
  public void testScheduleForNameWithNoneMatching() {
    client.scheduleForName("badname");
  }

  @Test(expected = ClientErrorException.class)
  public void testAddWithBadCron() {
    Schedule schedule = newTestInstance();
    schedule.setName("NewName");
    schedule.setCron("badcron");
    client.add(schedule);
  }

  @Test(expected = ClientErrorException.class)
  public void testAddWithSameName() {
    Schedule schedule = client.schedule(id);
    schedule.setId(null);
    client.add(schedule);
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
    assertTrue("Delete did not return correctly", client.deleteByName(TEST_SCHEDULE_NAME));
  }

  @Test(expected = NotFoundException.class)
  public void testDeleteByNameWithNone() {
    client.delete("badname");
  }

  @Test(expected = ClientErrorException.class)
  public void testDeleteAssociatedToEvent() {
    ScheduleEvent event = ScheduleEventData.newTestInstance();
    event.setAddressable(addr);
    evtClient.add(event);
    client.delete(id);
  }

  @Test
  public void testUpdate() {
    Schedule schedule = client.schedule(id);
    schedule.setOrigin(1234);
    assertTrue("Update did not complete successfully", client.update(schedule));
    Schedule schedule2 = client.schedule(id);
    assertEquals("Update did not work correclty", 1234, schedule2.getOrigin());
    assertNotNull("Modified date is null", schedule2.getModified());
    assertNotNull("Create date is null", schedule2.getCreated());
    assertTrue("Modified date and create date should be different after update",
        schedule2.getModified() != schedule2.getCreated());
  }

  @Test(expected = ClientErrorException.class)
  public void testUpdateWithBadCron() {
    Schedule schedule = client.schedule(id);
    schedule.setCron("badcron");
    client.update(schedule);
  }

  @Test
  public void testUpdateChangeNameWhileNotAssocToEvent() {
    Schedule schedule = client.schedule(id);
    schedule.setName("newname");
    assertTrue("Update did not complete successfully", client.update(schedule));
  }

  @Test(expected = ClientErrorException.class)
  public void testUpdateChangeNameWhileAssocToEvent() {
    ScheduleEvent event = ScheduleEventData.newTestInstance();
    event.setAddressable(addr);
    evtClient.add(event);
    Schedule schedule = client.schedule(id);
    schedule.setName("newname");
    client.update(schedule);
  }

  @Test(expected = NotFoundException.class)
  public void testUpdateWithNone() {
    Schedule schedule = client.schedule(id);
    schedule.setId("badid");
    schedule.setName("badname");
    schedule.setOrigin(1234);
    client.update(schedule);
  }

}
