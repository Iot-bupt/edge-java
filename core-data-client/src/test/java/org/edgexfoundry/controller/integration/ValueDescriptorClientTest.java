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

import static org.edgexfoundry.test.data.ValueDescriptorData.TEST_LABELS;
import static org.edgexfoundry.test.data.ValueDescriptorData.TEST_NAME;
import static org.edgexfoundry.test.data.ValueDescriptorData.TEST_UOMLABEL;
import static org.edgexfoundry.test.data.ValueDescriptorData.checkTestData;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.util.List;

import javax.ws.rs.NotFoundException;

import org.edgexfoundry.controller.ValueDescriptorClient;
import org.edgexfoundry.controller.impl.ValueDescriptorClientImpl;
import org.edgexfoundry.domain.common.ValueDescriptor;
import org.edgexfoundry.test.category.RequiresCoreDataRunning;
import org.edgexfoundry.test.category.RequiresMongoDB;
import org.edgexfoundry.test.data.DeviceData;
import org.edgexfoundry.test.data.ValueDescriptorData;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category({RequiresMongoDB.class, RequiresCoreDataRunning.class})
public class ValueDescriptorClientTest {

  private static final String ENDPT = "http://localhost:48080/api/v1/valuedescriptor";

  private ValueDescriptorClient client;
  private String id;

  // setup tests the add function
  @Before
  public void setup() throws Exception {
    client = new ValueDescriptorClientImpl();
    setURL();
    ValueDescriptor valueDescriptor = ValueDescriptorData.newTestInstance();
    id = client.add(valueDescriptor);
    assertNotNull("Value Descriptor did not get created correctly", id);
  }

  private void setURL() throws Exception {
    Class<?> clientClass = client.getClass();
    Field temp = clientClass.getDeclaredField("url");
    temp.setAccessible(true);
    temp.set(client, ENDPT);
  }

  // cleanup tests the delete function
  @After
  public void cleanup() {
    List<ValueDescriptor> valueDescriptors = client.valueDescriptors();
    valueDescriptors.forEach((valueDescriptor) -> client.delete(valueDescriptor.getId()));
  }

  @Test
  public void testValueDescriptor() {
    ValueDescriptor vd = client.valueDescriptor(id);
    checkTestData(vd, id);
  }

  @Test(expected = NotFoundException.class)
  public void testValueDescriptorWithUnknownnId() {
    client.valueDescriptor("nosuchid");
  }

  @Test
  public void testValueDescriptors() {
    List<ValueDescriptor> vds = client.valueDescriptors();
    assertEquals("Find all not returning a list with one value descriptor", 1, vds.size());
    checkTestData(vds.get(0), id);
  }

  @Test
  public void testValueDescriptorForName() {
    ValueDescriptor vd = client.valueDescriptorByName(TEST_NAME);
    checkTestData(vd, id);
  }

  @Test(expected = NotFoundException.class)
  public void testValueDescriptorForNameWithNoneMatching() {
    client.valueDescriptorByName("badname");
  }

  @Test
  public void testValueDescriptorsByLabel() {
    List<ValueDescriptor> vds = client.valueDescriptorByLabel(TEST_LABELS[0]);
    assertEquals("Find by label not returning a list with one value descriptor", 1, vds.size());
    checkTestData(vds.get(0), id);
  }

  @Test(expected = NotFoundException.class) // metadata not running and device not there
  public void testValueDescriptorsForDeviceByName() {
    client.valueDescriptorsForDeviceByName(DeviceData.TEST_NAME);
  }

  @Test(expected = NotFoundException.class) // metadata not running and device not there
  public void testValueDescriptorsForDeviceById() {
    client.valueDescriptorsForDeviceById("123");
  }

  @Test
  public void testValueDescriptorsByLabelWithNoneMatching() {
    List<ValueDescriptor> vds = client.valueDescriptorByLabel("badlabel");
    assertTrue("ValueDescriptor found with bad label", vds.isEmpty());
  }

  @Test
  public void testValueDescriptorsByUoMLabel() {
    List<ValueDescriptor> vds = client.valueDescriptorByUOMLabel(TEST_UOMLABEL);
    assertEquals("Find by UOM label not returning a list with one value descriptor", 1, vds.size());
    checkTestData(vds.get(0), id);
  }

  @Test
  public void testValueDescriptorsByUOMLabelWithNoneMatching() {
    List<ValueDescriptor> vds = client.valueDescriptorByUOMLabel("badlabel");
    assertTrue("ValueDescriptor found with bad UOM label", vds.isEmpty());
  }

  // TODO - in the future have Metadata up and also test with devices
  // associated
  @Test(expected = NotFoundException.class)
  public void valueDescriptorsForDeviceByName() {
    client.valueDescriptorsForDeviceByName("unknowndevice");
  }

  // TODO - in the future have Metadata up and also test with devices
  // associated
  @Test(expected = NotFoundException.class)
  public void valueDescriptorsForDeviceById() {
    client.valueDescriptorsForDeviceById("unknowndeviceid");
  }

  @Test(expected = NotFoundException.class)
  public void testDeleteWithNone() {
    client.delete("badid");
  }

  @Test
  public void testUpdate() {
    ValueDescriptor vd = client.valueDescriptor(id);
    vd.setOrigin(12345);
    assertTrue("Update did not complete successfully", client.update(vd));
    ValueDescriptor vd2 = client.valueDescriptor(id);
    assertEquals("Update did not work correclty", 12345, vd2.getOrigin());
    assertNotNull("Modified date is null", vd2.getModified());
    assertNotNull("Create date is null", vd2.getCreated());
    assertTrue("Modified date and create date should be different after update",
        vd2.getModified() != vd2.getCreated());
  }

  @Test
  public void testDeleteByName() {
    assertTrue("ValueDescriptor not deleted by name", client.deleteByName(TEST_NAME));
  }

  @Test(expected = NotFoundException.class)
  public void testDeleteByNameWithNone() {
    client.deleteByName("badname");
  }

}
