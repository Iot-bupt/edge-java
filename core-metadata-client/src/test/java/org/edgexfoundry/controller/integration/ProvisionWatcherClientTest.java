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

import static org.edgexfoundry.test.data.ProvisionWatcherData.KEY1;
import static org.edgexfoundry.test.data.ProvisionWatcherData.NAME;
import static org.edgexfoundry.test.data.ProvisionWatcherData.VAL1;
import static org.edgexfoundry.test.data.ProvisionWatcherData.checkTestData;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.util.List;

import javax.ws.rs.NotFoundException;

import org.edgexfoundry.controller.AddressableClient;
import org.edgexfoundry.controller.DeviceProfileClient;
import org.edgexfoundry.controller.DeviceServiceClient;
import org.edgexfoundry.controller.ProvisionWatcherClient;
import org.edgexfoundry.controller.impl.AddressableClientImpl;
import org.edgexfoundry.controller.impl.DeviceProfileClientImpl;
import org.edgexfoundry.controller.impl.DeviceServiceClientImpl;
import org.edgexfoundry.controller.impl.ProvisionWatcherClientImpl;
import org.edgexfoundry.domain.meta.Addressable;
import org.edgexfoundry.domain.meta.DeviceProfile;
import org.edgexfoundry.domain.meta.DeviceService;
import org.edgexfoundry.domain.meta.ProvisionWatcher;
import org.edgexfoundry.test.category.RequiresMetaDataRunning;
import org.edgexfoundry.test.category.RequiresMongoDB;
import org.edgexfoundry.test.data.AddressableData;
import org.edgexfoundry.test.data.ProfileData;
import org.edgexfoundry.test.data.ProvisionWatcherData;
import org.edgexfoundry.test.data.ServiceData;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category({RequiresMongoDB.class, RequiresMetaDataRunning.class})
public class ProvisionWatcherClientTest {

  private static final String ENDPT = "http://localhost:48081/api/v1/provisionwatcher";
  private static final String PRO_ENDPT = "http://localhost:48081/api/v1/deviceprofile";
  private static final String SRV_ENDPT = "http://localhost:48081/api/v1/deviceservice";
  private static final String ADDR_ENDPT = "http://localhost:48081/api/v1/addressable";

  private ProvisionWatcherClient client;
  private DeviceProfileClient proClient;
  private DeviceServiceClient srvClient;
  private AddressableClient addrClient;

  private String id;

  // setup tests the add function
  @Before
  public void setup() throws Exception {
    client = new ProvisionWatcherClientImpl();
    proClient = new DeviceProfileClientImpl();
    srvClient = new DeviceServiceClientImpl();
    addrClient = new AddressableClientImpl();
    setURL();
    Addressable addr = AddressableData.newTestInstance();
    addrClient.add(addr);
    DeviceService service = ServiceData.newTestInstance();
    service.setAddressable(addr);
    srvClient.add(service);
    DeviceProfile profile = ProfileData.newTestInstance();
    proClient.add(profile);
    ProvisionWatcher provisionWatcher = ProvisionWatcherData.newTestInstance();
    provisionWatcher.setProfile(profile);
    provisionWatcher.setService(service);
    id = client.add(provisionWatcher);
    assertNotNull("ProvisionWatcher did not get created correctly", id);
  }

  private void setURL() throws Exception {
    Class<?> clientClass = client.getClass();
    Field temp = clientClass.getDeclaredField("url");
    temp.setAccessible(true);
    temp.set(client, ENDPT);
    Class<?> clientClass2 = proClient.getClass();
    Field temp2 = clientClass2.getDeclaredField("url");
    temp2.setAccessible(true);
    temp2.set(proClient, PRO_ENDPT);
    Class<?> clientClass3 = srvClient.getClass();
    Field temp3 = clientClass3.getDeclaredField("url");
    temp3.setAccessible(true);
    temp3.set(srvClient, SRV_ENDPT);
    Class<?> clientClass4 = addrClient.getClass();
    Field temp4 = clientClass4.getDeclaredField("url");
    temp4.setAccessible(true);
    temp4.set(addrClient, ADDR_ENDPT);
  }

  // cleanup tests the delete function
  @After
  public void cleanup() {
    List<ProvisionWatcher> watchers = client.provisionWatchers();
    watchers.forEach((watcher) -> client.delete(watcher.getId()));
    List<DeviceService> services = srvClient.deviceServices();
    services.forEach((service) -> srvClient.delete(service.getId()));
    List<DeviceProfile> profiles = proClient.deviceProfiles();
    profiles.forEach((profile) -> proClient.delete(profile.getId()));
    List<Addressable> addressables = addrClient.addressables();
    addressables.forEach((addr) -> addrClient.delete(addr.getId()));
  }

  @Test
  public void testProvisionWatcher() {
    ProvisionWatcher watcher = client.provisionWatcher(id);
    checkTestData(watcher, id);
  }

  @Test(expected = NotFoundException.class)
  public void testProvisionWatcherWithUnknownnId() {
    client.provisionWatcher("nosuchid");
  }

  @Test
  public void testProvisionWatchers() {
    List<ProvisionWatcher> watchers = client.provisionWatchers();
    assertEquals("Find all not returning a list with one object", 1, watchers.size());
    checkTestData(watchers.get(0), id);
  }

  @Test
  public void testProvisionWatcherForName() {
    ProvisionWatcher watcher = client.provisionWatcherForName(NAME);
    checkTestData(watcher, id);
  }

  @Test(expected = NotFoundException.class)
  public void testProvisionWatcherForNameWithNoneMatching() {
    client.provisionWatcherForName("badname");
  }

  @Test
  public void testProvisionWatchersForProfileByName() {
    List<ProvisionWatcher> watchers =
        client.provisionWatcherForProfileByName(ProfileData.TEST_PROFILE_NAME);
    assertEquals("Find for profiles not returning appropriate list", 1, watchers.size());
    checkTestData(watchers.get(0), id);
  }

  @Test(expected = NotFoundException.class)
  public void testProvisionWatchersForProfileWithNone() {
    assertTrue("No provision watchers should be found with bad profile",
        client.provisionWatcherForProfile("badprofile").isEmpty());
  }

  @Test(expected = NotFoundException.class)
  public void testProvisionWatcherForProfileByNameWithNone() {
    client.provisionWatcherForProfileByName("badprofile");
  }

  @Test
  public void testProvisionWatchersForServiceByName() {
    List<ProvisionWatcher> watchers =
        client.provisionWatcherForServiceByName(ServiceData.TEST_SERVICE_NAME);
    assertEquals("Find for service not returning appropriate list", 1, watchers.size());
    checkTestData(watchers.get(0), id);
  }

  @Test(expected = NotFoundException.class)
  public void testProvisionWatchersForServiceWithNone() {
    assertTrue("No provision watchers should be found with bad service",
        client.provisionWatcherForService("badservice").isEmpty());
  }

  @Test(expected = NotFoundException.class)
  public void testProvisionWatcherForServiceByNameWithNone() {
    client.provisionWatcherForServiceByName("badservice");
  }

  @Test
  public void testWatchersForIdentifier() {
    List<ProvisionWatcher> watchers = client.watchersForIdentifier(KEY1, VAL1);
    assertEquals("Find for key / value not returning appropriate list", 1, watchers.size());
    checkTestData(watchers.get(0), id);
  }

  @Test
  public void testWatchersForIdentifierWithNoMatching() {
    assertTrue("No watchers should be found with bad key/value identifier pair",
        client.watchersForIdentifier("badkey", "badvalue").isEmpty());
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
    assertTrue("Delete did not return correctly", client.deleteByName(NAME));
  }

  @Test(expected = NotFoundException.class)
  public void testDeleteByNameWithNone() {
    client.delete("badname");
  }

  @Test
  public void testUpdate() {
    ProvisionWatcher watcher = client.provisionWatcher(id);
    watcher.setOrigin(12345);
    assertTrue("Did not update correctly", client.update(watcher));
  }

  @Test(expected = NotFoundException.class)
  public void testUpdateWithNone() {
    ProvisionWatcher watcher = client.provisionWatcher(id);
    watcher.setId("badid");
    watcher.setName("badname");
    watcher.setOrigin(12345);
    client.update(watcher);
  }

}
