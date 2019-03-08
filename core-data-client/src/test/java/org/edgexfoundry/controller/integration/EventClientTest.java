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
 * @microservice: core-data-client
 * @author: Jim White, Dell
 * @version: 1.0.0
 *******************************************************************************/

package org.edgexfoundry.controller.integration;

import static org.edgexfoundry.test.data.EventData.TEST_DEVICE_ID;
import static org.edgexfoundry.test.data.EventData.checkTestData;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;

import javax.ws.rs.NotFoundException;

import org.edgexfoundry.controller.EventClient;
import org.edgexfoundry.controller.ReadingClient;
import org.edgexfoundry.controller.ValueDescriptorClient;
import org.edgexfoundry.controller.impl.EventClientImpl;
import org.edgexfoundry.controller.impl.ReadingClientImpl;
import org.edgexfoundry.controller.impl.ValueDescriptorClientImpl;
import org.edgexfoundry.domain.common.ValueDescriptor;
import org.edgexfoundry.domain.core.Event;
import org.edgexfoundry.domain.core.Reading;
import org.edgexfoundry.test.category.RequiresCoreDataRunning;
import org.edgexfoundry.test.category.RequiresMongoDB;
import org.edgexfoundry.test.data.EventData;
import org.edgexfoundry.test.data.ReadingData;
import org.edgexfoundry.test.data.ValueDescriptorData;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category({RequiresMongoDB.class, RequiresCoreDataRunning.class})
public class EventClientTest {

  private static final String ENDPT = "http://localhost:48080/api/v1/event";
  private static final String READING_ENDPT = "http://localhost:48080/api/v1/reading";
  private static final String VD_ENDPT = "http://localhost:48080/api/v1/valuedescriptor";
  private static final int LIMIT = 10;

  private EventClient client;
  private ValueDescriptorClient vdClient;
  private ReadingClient rdClient;
  private String id;

  // setup tests the add function
  @Before
  public void setup() throws Exception {
    client = new EventClientImpl();
    vdClient = new ValueDescriptorClientImpl();
    rdClient = new ReadingClientImpl();
    setURL();
    ValueDescriptor valueDescriptor = ValueDescriptorData.newTestInstance();
    vdClient.add(valueDescriptor);
    Reading reading = ReadingData.newTestInstance();
    reading.setName(ValueDescriptorData.TEST_NAME);
    Event event = EventData.newTestInstance();
    event.addReading(reading);
    id = client.add(event);
    assertNotNull("Event did not get created correctly", id);
  }

  private void setURL() throws Exception {
    Class<?> clientClass = client.getClass();
    Field temp = clientClass.getDeclaredField("url");
    temp.setAccessible(true);
    temp.set(client, ENDPT);
    Class<?> clientClass2 = vdClient.getClass();
    Field temp2 = clientClass2.getDeclaredField("url");
    temp2.setAccessible(true);
    temp2.set(vdClient, VD_ENDPT);
    Class<?> clientClass3 = rdClient.getClass();
    Field temp3 = clientClass3.getDeclaredField("url");
    temp3.setAccessible(true);
    temp3.set(rdClient, READING_ENDPT);
  }

  // cleanup tests the delete function
  @After
  public void cleanup() throws InterruptedException {
    List<Event> es = client.events();
    es.forEach(e -> client.delete(e.getId()));
    List<Reading> rds = rdClient.readings();
    rds.forEach(r -> rdClient.delete(r.getId()));
    List<ValueDescriptor> vds = vdClient.valueDescriptors();
    vds.forEach(v -> vdClient.delete(v.getId()));
  }

  @Test
  public void testEvent() {
    Event event = client.event(id);
    checkTestData(event, id);
  }

  @Test(expected = NotFoundException.class)
  public void testEventWithUnknownnId() {
    client.event("nosuchid");
  }

  @Test
  public void testEvents() {
    List<Event> es = client.events();
    assertEquals("Find all not returning a list with one event", 1, es.size());
    checkTestData(es.get(0), id);
  }

  @Test
  public void testEventsByDevice() {
    List<Event> es = client.eventsForDevice(TEST_DEVICE_ID, LIMIT);
    assertEquals("Find by device id not returning a list with one event", 1, es.size());
    checkTestData(es.get(0), id);
  }

  @Test
  public void testEventsByDeviceWithUnknownDevice() {
    assertTrue("Find with bad device id return something other than an empty collection",
        client.eventsForDevice("baddevice", LIMIT).isEmpty());
  }

  @Test
  public void testEventsByTime() {
    long now = new Date().getTime();
    List<Event> es = client.events(now - 86400000, now + 86400000, LIMIT);
    assertEquals("Find by start/end time not returning a list with one event", 1, es.size());
    checkTestData(es.get(0), id);
  }

  @Test
  public void testEventsByTimeWithNoneMatching() {
    long now = new Date().getTime();
    assertTrue(
        "Find by start/end time returning something other than an empty collection for bad start/end times",
        client.events(0, now - 86400000, LIMIT).isEmpty());
  }

  @Test
  public void testReadingsForDeviceAndValueDescriptor() {
    List<Reading> readings =
        client.readingsForDeviceAndValueDescriptor(TEST_DEVICE_ID, ReadingData.TEST_NAME, 10);
    assertEquals(
        "Find readings by device id/name and value descriptor - through event - is not returning the correct results",
        1, readings.size());
  }

  @Test
  public void testReadingsForDeviceAndValueDescriptorWithUnknownDevice() {
    assertTrue(
        "Find readings by device id/name and value descriptor with bad device is returning something other than an empty collection",
        client.readingsForDeviceAndValueDescriptor("Baddeviceid", ReadingData.TEST_NAME, 10)
            .isEmpty());
  }

  @Test
  public void testReadingsForDeviceAndValueDescriptorWithUnknownValueDescriptor() {
    assertTrue(
        "Find readings by device id/name and value descriptor with value descriptor is returning something other than an empty collection",
        client.readingsForDeviceAndValueDescriptor(TEST_DEVICE_ID, "badvd", 10).isEmpty());
  }

  @Test
  public void testUpdate() {
    Event event = client.event(id);
    event.setOrigin(12345);
    assertTrue("Update did not complete successfully", client.update(event));
    Event event2 = client.event(id);
    assertEquals("Update did not work correclty", 12345, event2.getOrigin());
    assertNotNull("Modified date is null", event2.getModified());
    assertNotNull("Create date is null", event2.getCreated());
    assertTrue("Modified date and create date should be different after update",
        event2.getModified() != event2.getCreated());
  }

  @Test(expected = NotFoundException.class)
  public void testDeleteWithNone() {
    client.delete("badid");
  }

  @Test
  public void testDeleteByDevice() {
    assertEquals("Delete by device did not complete successfully", 1,
        client.deleteByDevice(TEST_DEVICE_ID));
  }

  @Test
  public void testDeleteByDeviceWithNoneMatching() {
    assertEquals("Delete by device did not complete successfully", 0,
        client.deleteByDevice("BaddeviceId"));
  }

  @Test
  public void testMarkPushed() {
    assertTrue("Event not successfully marked pushed", client.markedPushed(id));
  }

  @Test(expected = NotFoundException.class)
  public void testMarkPushedWithUnknownId() {
    client.markedPushed("unknowneventid");
  }

  @Test
  public void testScrubPushedEvents() {
    Event event = client.event(id);
    event.setPushed(123);
    assertTrue("Event pushed not set for test", client.update(event));
    List<Reading> readings = rdClient.readings();
    for (Reading reading : readings) {
      reading.setPushed(123);
      assertTrue("Reading pushed not set for test", rdClient.update(reading));
    }
    assertEquals("Scrub of events did not scrub properly", 1, client.scrubPushedEvents());
  }

  @Test
  public void testScrubPushedEventsWithNoEventsToScrub() {
    assertEquals("Scrubbed events when none were supposed to be found", 0,
        client.scrubPushedEvents());
  }

  @Test
  public void testScrubOldEvents() throws InterruptedException {
    assertEquals("Old events not scrubbed properly", 1, client.scrubOldEvents(1));
  }

  @Test
  public void testScrubOldEventsWithNoEventsToScrub() {
    long now = new Date().getTime();
    assertEquals("Found old events to scrub when none were supposed to be found", 0,
        client.scrubOldEvents(now + 2000));
  }

}
