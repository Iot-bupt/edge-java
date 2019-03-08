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

import static org.edgexfoundry.test.data.CommandData.TEST_CMD_NAME;
import static org.edgexfoundry.test.data.CommandData.checkTestData;
import static org.junit.Assert.assertEquals;
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
public class CommandClientTest {

  private static final String ENDPT = "http://localhost:48081/api/v1/command";
  private static final String PRF_ENDPT = "http://localhost:48081/api/v1/deviceprofile";

  private CommandClient client;
  private DeviceProfileClient profileClient;
  private String id;

  // setup tests the add function
  @Before
  public void setup() throws Exception {
    profileClient = new DeviceProfileClientImpl();
    client = new CommandClientImpl();
    setURL();
    Command command = CommandData.newTestInstance();
    id = client.add(command);
    assertNotNull("Command did not get created correctly", id);
  }

  private void setURL() throws Exception {
    Class<?> clientClass = client.getClass();
    Field temp = clientClass.getDeclaredField("url");
    temp.setAccessible(true);
    temp.set(client, ENDPT);
    Class<?> clientClass2 = profileClient.getClass();
    Field temp2 = clientClass2.getDeclaredField("url");
    temp2.setAccessible(true);
    temp2.set(profileClient, PRF_ENDPT);
  }

  // cleanup tests the delete function
  @After
  public void cleanup() {
    List<DeviceProfile> profiles = profileClient.deviceProfiles();
    profiles.forEach((profile) -> profileClient.delete(profile.getId()));
    List<Command> commands = client.commands();
    commands.forEach((command) -> client.delete(command.getId()));
  }

  @Test
  public void testCommand() {
    Command cmd = client.command(id);
    checkTestData(cmd, id);
  }

  @Test(expected = NotFoundException.class)
  public void testCommandWithUnknownnId() {
    client.command("nosuchid");
  }

  @Test
  public void testCommands() {
    List<Command> commands = client.commands();
    assertEquals("Find all not returning a list with one command", 1, commands.size());
    checkTestData(commands.get(0), id);
  }

  @Test
  public void testCommandForName() {
    List<Command> commands = client.commandsForName(TEST_CMD_NAME);
    assertEquals("Find all for name not returning a list with one command", 1, commands.size());
    checkTestData(commands.get(0), id);
  }

  @Test
  public void testCommandForNameWithNoneMatching() {
    List<Command> commands = client.commandsForName("badname");
    assertTrue("Commands found for bad name", commands.isEmpty());
  }

  @Test(expected = NotFoundException.class)
  public void testDeleteWithNone() {
    client.delete("badid");
  }

  @Test
  public void testUpdate() {
    Command cmd = client.command(id);
    cmd.setOrigin(12345);
    assertTrue("Update did not complete successfully", client.update(cmd));
    Command cmd2 = client.command(id);
    assertEquals("Update did not work correclty", 12345, cmd2.getOrigin());
    assertNotNull("Modified date is null", cmd2.getModified());
    assertNotNull("Create date is null", cmd2.getCreated());
    assertTrue("Modified date and create date should be different after update",
        cmd2.getModified() != cmd2.getCreated());
  }

  @Test(expected = ClientErrorException.class)
  public void testDeleteCommandAssociatedtoDeviceProfile() {
    DeviceProfile profile = ProfileData.newTestInstance();
    Command cmd = CommandData.newTestInstance();
    profile.addCommand(cmd);
    String profileId = profileClient.add(profile);
    assertNotNull("New profile appears not to have been saved", profileId);
    DeviceProfile profile2 = profileClient.deviceProfile(profileId);
    client.delete(profile2.getCommands().get(0).getId());
  }

}
