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

import static org.edgexfoundry.test.data.ProfileData.TEST_LABELS;
import static org.edgexfoundry.test.data.ProfileData.TEST_MAUFACTURER;
import static org.edgexfoundry.test.data.ProfileData.TEST_MODEL;
import static org.edgexfoundry.test.data.ProfileData.TEST_PROFILE_NAME;
import static org.edgexfoundry.test.data.ProfileData.checkTestData;
import static org.edgexfoundry.test.data.ProfileData.newTestInstance;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.util.List;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.NotFoundException;

import org.edgexfoundry.controller.CommandClient;
import org.edgexfoundry.controller.DeviceProfileClient;
import org.edgexfoundry.controller.impl.CommandClientImpl;
import org.edgexfoundry.controller.impl.DeviceProfileClientImpl;
import org.edgexfoundry.domain.meta.Command;
import org.edgexfoundry.domain.meta.DeviceProfile;
import org.edgexfoundry.test.category.RequiresMetaDataRunning;
import org.edgexfoundry.test.category.RequiresMongoDB;
import org.edgexfoundry.test.data.CommandData;
import org.edgexfoundry.test.data.ProfileData;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category({RequiresMongoDB.class, RequiresMetaDataRunning.class})
public class DeviceProfileClientTest {

  private static final String ENDPT = "http://localhost:48081/api/v1/deviceprofile";
  private static final String CMD_ENDPT = "http://localhost:48081/api/v1/command";

  private DeviceProfileClient client;
  private CommandClient cmdClient;
  private String id;

  // setup tests add funciton
  @Before
  public void setup() throws Exception {
    cmdClient = new CommandClientImpl();
    client = new DeviceProfileClientImpl();
    setURL();
    Command command = CommandData.newTestInstance();
    DeviceProfile profile = ProfileData.newTestInstance();
    profile.addCommand(command);
    id = client.add(profile);
  }

  private void setURL() throws Exception {
    Class<?> clientClass = client.getClass();
    Field temp = clientClass.getDeclaredField("url");
    temp.setAccessible(true);
    temp.set(client, ENDPT);
    Class<?> clientClass2 = cmdClient.getClass();
    Field temp2 = clientClass2.getDeclaredField("url");
    temp2.setAccessible(true);
    temp2.set(cmdClient, CMD_ENDPT);
  }

  // cleanup tests delete function
  @After
  public void cleanup() throws Exception {
    List<DeviceProfile> profiles = client.deviceProfiles();
    profiles.forEach((profile) -> client.delete(profile.getId()));
    List<Command> cmds = cmdClient.commands();
    cmds.forEach((command) -> cmdClient.delete(command.getId()));
  }

  @Test
  public void testDeviceProfile() {
    DeviceProfile profile = client.deviceProfile(id);
    checkTestData(profile, id);
  }

  @Test(expected = NotFoundException.class)
  public void testDeviceProfileWithUnknownId() {
    client.deviceProfile("nosuchid");
  }

  @Test
  public void testDeviceProfiles() {
    List<DeviceProfile> profiles = client.deviceProfiles();
    assertEquals("Find all not returning a list with one device profile", 1, profiles.size());
    checkTestData(profiles.get(0), id);
  }

  @Test
  public void testDeviceProfileForName() {
    DeviceProfile profile = client.deviceProfileForName(TEST_PROFILE_NAME);
    checkTestData(profile, id);
  }

  @Test(expected = NotFoundException.class)
  public void testDeviceProfileForNameWithNoneMatching() {
    client.deviceProfileForName("badname");
  }

  @Test
  public void testDeviceProfilesByModel() {
    List<DeviceProfile> profiles = client.deviceProfilesByModel(TEST_MODEL);
    assertEquals("Find for model not returning appropriate list", 1, profiles.size());
    checkTestData(profiles.get(0), id);
  }

  @Test
  public void testDeviceProfilesByModelWithNoneMatching() {
    assertTrue("No devices should be found with bad model",
        client.deviceProfilesByModel("badmodel").isEmpty());
  }

  @Test
  public void testDeviceProfilesByManufacturer() {
    List<DeviceProfile> profiles = client.deviceProfilesByManufacturer(TEST_MAUFACTURER);
    assertEquals("Find for manufacturer not returning appropriate list", 1, profiles.size());
    checkTestData(profiles.get(0), id);
  }

  @Test
  public void testDeviceProfilesByManufacturerWithNoneMatching() {
    assertTrue("No devices should be found with bad manufacturer",
        client.deviceProfilesByManufacturer("badmanufacturer").isEmpty());
  }

  @Test
  public void testDeviceProfilesByModelOrManufacturer() {
    List<DeviceProfile> profiles =
        client.deviceProfilesByManufacturerOrModel(TEST_MAUFACTURER, "unknown");
    assertEquals("Find for manufacturer not returning appropriate list", 1, profiles.size());
    checkTestData(profiles.get(0), id);
    client.deviceProfilesByManufacturerOrModel("unknown", TEST_MODEL);
    assertEquals("Find for model not returning appropriate list", 1, profiles.size());
    checkTestData(profiles.get(0), id);

  }

  @Test
  public void testDeviceProfilesByManufacturerOrModelWithNoneMatching() {
    assertTrue("No devices should be found with bad manufacturer and model",
        client.deviceProfilesByManufacturerOrModel("badmanufacturer", "badmodel").isEmpty());
  }

  @Test
  public void testDeviceProfileByLabel() {
    List<DeviceProfile> profiles = client.deviceProfilesByLabel(TEST_LABELS[0]);
    assertEquals("Find for labels not returning appropriate list", 1, profiles.size());
    checkTestData(profiles.get(0), id);
  }

  @Test
  public void testDeviceProfilesByLabelWithNoneMatching() {
    assertTrue("No devices should be found with bad label",
        client.deviceProfilesByLabel("badlabel").isEmpty());
  }

  @Test(expected = ClientErrorException.class)
  public void testAddWithSameName() {
    DeviceProfile profile = newTestInstance();
    client.add(profile);
  }

  @Test(expected = ClientErrorException.class)
  public void testAddWithDuplicateCommandNames() {
    Command cmd1 = CommandData.newTestInstance();
    Command md2 = CommandData.newTestInstance();
    DeviceProfile profile = newTestInstance();
    profile.setName("NewName");
    profile.addCommand(cmd1);
    profile.addCommand(md2);
    client.add(profile);
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
    assertTrue("Delete did not return correctly", client.deleteByName(TEST_PROFILE_NAME));
  }

  @Test(expected = NotFoundException.class)
  public void testDeleteByNameWithNone() {
    client.delete("badname");
  }

  @Test
  public void testUpdate() {
    DeviceProfile device = client.deviceProfile(id);
    device.setDescription("newdescription");
    assertTrue("Update did not complete successfully", client.update(device));
    DeviceProfile device2 = client.deviceProfile(id);
    assertEquals("Update did not work correclty", "newdescription", device2.getDescription());
    assertNotNull("Modified date is null", device2.getModified());
    assertNotNull("Create date is null", device2.getCreated());
    assertTrue("Modified date and create date should be different after update",
        device2.getModified() != device2.getCreated());
  }

  @Test(expected = NotFoundException.class)
  public void testUpdateWithNone() {
    DeviceProfile device = client.deviceProfile(id);
    device.setId("badid");
    device.setName("badname");
    device.setDescription("newdescription");
    client.update(device);
  }

  @Test(expected = ClientErrorException.class)
  public void testUpdateWithTwoCommandSameName() {
    DeviceProfile profile = client.deviceProfile(id);
    assertFalse("Commands cannot be empty for this test", profile.getCommands().isEmpty());
    Command cmd = CommandData.newTestInstance();
    profile.addCommand(cmd);
    client.update(profile);
  }

}
