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

import static org.edgexfoundry.test.data.ReadingData.checkTestData;
import static org.edgexfoundry.test.data.ValueDescriptorData.TEST_NAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.util.List;

import javax.ws.rs.NotFoundException;

import org.edgexfoundry.controller.ReadingClient;
import org.edgexfoundry.controller.ValueDescriptorClient;
import org.edgexfoundry.controller.impl.ReadingClientImpl;
import org.edgexfoundry.controller.impl.ValueDescriptorClientImpl;
import org.edgexfoundry.domain.common.IoTType;
import org.edgexfoundry.domain.common.ValueDescriptor;
import org.edgexfoundry.domain.core.Reading;
import org.edgexfoundry.test.category.RequiresCoreDataRunning;
import org.edgexfoundry.test.category.RequiresMongoDB;
import org.edgexfoundry.test.data.DeviceData;
import org.edgexfoundry.test.data.EventData;
import org.edgexfoundry.test.data.ReadingData;
import org.edgexfoundry.test.data.ValueDescriptorData;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category({RequiresMongoDB.class, RequiresCoreDataRunning.class})
public class ReadingClientTest {

  private static final String ENDPT = "http://localhost:48080/api/v1/reading";
  private static final String VD_ENDPT = "http://localhost:48080/api/v1/valuedescriptor";
  private static final int LIMIT = 10;

  private ReadingClient client;
  private ValueDescriptorClient vdClient;
  private String id;

  // setup tests the add function
  @Before
  public void setup() throws Exception {
    vdClient = new ValueDescriptorClientImpl();
    client = new ReadingClientImpl();
    setURL();
    ValueDescriptor valueDescriptor = ValueDescriptorData.newTestInstance();
    vdClient.add(valueDescriptor);
    Reading reading = ReadingData.newTestInstance();
    reading.setName(ValueDescriptorData.TEST_NAME);
    id = client.add(reading);
    assertNotNull("Reading did not get created correctly", id);
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
  }

  // cleanup tests the delete function
  @After
  public void cleanup() {
    List<Reading> readings = client.readings();
    readings.forEach((reading) -> client.delete(reading.getId()));
    List<ValueDescriptor> valueDescriptors = vdClient.valueDescriptors();
    valueDescriptors.forEach((valueDescriptor) -> vdClient.delete(valueDescriptor.getId()));
  }

  @Test
  public void testReading() {
    Reading reading = client.reading(id);
    checkTestData(reading, id);
  }

  @Test(expected = NotFoundException.class)
  public void testReadingWithUnknownnId() {
    client.reading("nosuchid");
  }

  @Test
  public void testReadings() {
    List<Reading> readings = client.readings();
    assertEquals("Find all not returning a list with one reading", 1, readings.size());
    checkTestData(readings.get(0), id);
  }

  @Test
  public void testReadingsForDevice() { // metadata not up and no devices in database
    List<Reading> readings = client.readings(DeviceData.TEST_NAME, LIMIT);
    assertEquals("Find all for device not returning a list with no reading", 0, readings.size());
  }

  @Test
  public void testReadingByName() {
    List<Reading> readings = client.readingsByName(TEST_NAME, LIMIT);
    checkTestData(readings.get(0), id);
  }

  @Test
  public void testReadingForNameWithNoneMatching() {
    assertTrue("Reading found for bad name", client.readingsByName("badname", LIMIT).isEmpty());
  }

  @Test
  public void testReadingByNameAndDevice() {
    List<Reading> readings = client.readingsByNameAndDevice(TEST_NAME, EventData.TEST_DEVICE_ID, LIMIT);
    checkTestData(readings.get(0), id);
  }

  @Test
  public void testReadingForNameAndDeviceWithNoneMatching() {
    assertTrue("Reading found for bad name and device",
        client.readingsByNameAndDevice("badname", "baddevice", LIMIT).isEmpty());
  }

  @Test
  public void testReadingsByLabel() {
    List<Reading> readings = client.readingsByLabel(ValueDescriptorData.TEST_LABELS[0], LIMIT);
    assertEquals("Find by label not returning a list with one reading", 1, readings.size());
    checkTestData(readings.get(0), id);
  }

  @Test
  public void testReadingsByLabelWithNoneMatching() {
    List<Reading> readings = client.readingsByLabel("badlabel", LIMIT);
    assertTrue("Reading found with bad label", readings.isEmpty());
  }

  @Test
  public void testReadingsByUoMLabel() {
    List<Reading> readings = client.readingsByUoMLabel(ValueDescriptorData.TEST_UOMLABEL, LIMIT);
    assertEquals("Find by UOM label not returning a list with one reading", 1, readings.size());
    checkTestData(readings.get(0), id);
  }

  @Test
  public void testReadingsByUOMLabelWithNoneMatching() {
    List<Reading> readings = client.readingsByUoMLabel("badlable", LIMIT);
    assertTrue("Reading found with bad UOM label", readings.isEmpty());
  }

  @Test
  public void testReadingsByType() {
    List<Reading> readings = client.readingsByType(ValueDescriptorData.TEST_TYPE.toString(), LIMIT);
    assertEquals("Find by UOM label not returning a list with one reading", 1, readings.size());
    checkTestData(readings.get(0), id);
  }

  @Test
  public void testReadingsByTypeWithNoneMatching() {
    List<Reading> readings = client.readingsByType(IoTType.F.toString(), LIMIT);
    assertTrue("Reading found with bad type", readings.isEmpty());
  }

  @Test(expected = NotFoundException.class)
  public void testDeleteWithNone() {
    client.delete("badid");
  }

  @Test
  public void testUpdate() {
    Reading reading = client.reading(id);
    reading.setOrigin(12345);
    assertTrue("Update did not complete successfully", client.update(reading));
    Reading reading2 = client.reading(id);
    assertEquals("Update did not work correclty", 12345, reading2.getOrigin());
    assertNotNull("Modified date is null", reading2.getModified());
    assertNotNull("Create date is null", reading2.getCreated());
    assertTrue("Modified date and create date should be different after update",
        reading2.getModified() != reading2.getCreated());
  }

}
