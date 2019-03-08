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

import static org.edgexfoundry.test.data.ServiceData.TEST_LABELS;
import static org.edgexfoundry.test.data.ServiceData.TEST_SERVICE_NAME;
import static org.edgexfoundry.test.data.ServiceData.checkTestData;
import static org.edgexfoundry.test.data.ServiceData.newTestInstance;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.NotFoundException;

import org.edgexfoundry.controller.AddressableClient;
import org.edgexfoundry.controller.DeviceClient;
import org.edgexfoundry.controller.DeviceProfileClient;
import org.edgexfoundry.controller.DeviceServiceClient;
import org.edgexfoundry.controller.impl.AddressableClientImpl;
import org.edgexfoundry.controller.impl.DeviceClientImpl;
import org.edgexfoundry.controller.impl.DeviceProfileClientImpl;
import org.edgexfoundry.controller.impl.DeviceServiceClientImpl;
import org.edgexfoundry.domain.meta.Addressable;
import org.edgexfoundry.domain.meta.AdminState;
import org.edgexfoundry.domain.meta.Device;
import org.edgexfoundry.domain.meta.DeviceProfile;
import org.edgexfoundry.domain.meta.DeviceService;
import org.edgexfoundry.domain.meta.OperatingState;
import org.edgexfoundry.test.category.RequiresMetaDataRunning;
import org.edgexfoundry.test.category.RequiresMongoDB;
import org.edgexfoundry.test.data.AddressableData;
import org.edgexfoundry.test.data.DeviceData;
import org.edgexfoundry.test.data.ProfileData;
import org.edgexfoundry.test.data.ServiceData;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category({RequiresMongoDB.class, RequiresMetaDataRunning.class})
public class DeviceServiceClientTest {

  private static final String ENDPT = "http://localhost:48081/api/v1/deviceservice";
  private static final String ADDR_ENDPT = "http://localhost:48081/api/v1/addressable";
  private static final String PROFILE_ENDPT = "http://localhost:48081/api/v1/deviceprofile";
  private static final String DEVICE_ENDPT = "http://localhost:48081/api/v1/device";

  private AddressableClient addrClient;
  private DeviceServiceClient client;
  private DeviceProfileClient profileClient;
  private DeviceClient deviceClient;
  private String id;
  private String addrId;

  // setup tests add funciton
  @Before
  public void setup() throws Exception {
    client = new DeviceServiceClientImpl();
    addrClient = new AddressableClientImpl();
    profileClient = new DeviceProfileClientImpl();
    deviceClient = new DeviceClientImpl();
    setURL();

    Addressable addr = AddressableData.newTestInstance();
    addrId = addrClient.add(addr);
    DeviceService service = ServiceData.newTestInstance();
    service.setAddressable(addr);
    id = client.add(service);
    DeviceProfile profile = ProfileData.newTestInstance();
    profileClient.add(profile);
    Device device = DeviceData.newTestInstance();
    device.setProfile(profile);
    device.setService(service);
    device.setAddressable(addr);
    deviceClient.add(device);
  }

  private void setURL() throws Exception {
    Class<?> clientClass = client.getClass();
    Field temp = clientClass.getDeclaredField("url");
    temp.setAccessible(true);
    temp.set(client, ENDPT);
    Class<?> clientClass2 = addrClient.getClass();
    Field temp2 = clientClass2.getDeclaredField("url");
    temp2.setAccessible(true);
    temp2.set(addrClient, ADDR_ENDPT);
    Class<?> clientClass3 = profileClient.getClass();
    Field temp3 = clientClass3.getDeclaredField("url");
    temp3.setAccessible(true);
    temp3.set(profileClient, PROFILE_ENDPT);
    Class<?> clientClass4 = deviceClient.getClass();
    Field temp4 = clientClass4.getDeclaredField("url");
    temp4.setAccessible(true);
    temp4.set(deviceClient, DEVICE_ENDPT);
  }

  // cleanup tests delete function
  @After
  public void cleanup() throws Exception {
    List<Device> devices = deviceClient.devices();
    devices.forEach(d -> deviceClient.delete(d.getId()));
    List<DeviceProfile> profiles = profileClient.deviceProfiles();
    profiles.forEach(p -> profileClient.delete(p.getId()));
    List<DeviceService> ss = client.deviceServices();
    ss.forEach(s -> client.delete(s.getId()));
    List<Addressable> addrs = addrClient.addressables();
    addrs.forEach(a -> addrClient.delete(a.getId()));
  }

  @Test
  public void testDeviceService() {
    DeviceService device = client.deviceService(id);
    checkTestData(device, id);
  }

  @Test(expected = NotFoundException.class)
  public void testDeviceServiceWithUnknownnId() {
    client.deviceService("nosuchid");
  }

  @Test
  public void testDeviceServices() {
    List<DeviceService> services = client.deviceServices();
    assertEquals("Find all not returning a list with one device service", 1, services.size());
    checkTestData(services.get(0), id);
  }

  @Test
  public void testDeviceServiceForName() {
    DeviceService services = client.deviceServiceForName(TEST_SERVICE_NAME);
    checkTestData(services, id);
  }

  @Test(expected = NotFoundException.class)
  public void testDeviceServiceForNameWithNoneMatching() {
    client.deviceServiceForName("badname");
  }

  @Test
  public void testDeviceServiceByLabel() {
    List<DeviceService> services = client.deviceServicesByLabel(TEST_LABELS[0]);
    assertEquals("Find for labels not returning appropriate list", 1, services.size());
    checkTestData(services.get(0), id);
  }

  @Test
  public void testDeviceServicesByLabelWithNoneMatching() {
    assertTrue("No devices should be found with bad label",
        client.deviceServicesByLabel("badlabel").isEmpty());
  }

  @Test
  public void testDeviceServicesForAddress() {
    List<DeviceService> services = client.deviceServicesForAddressable(addrId);
    assertEquals("Find for address not returning appropriate list", 1, services.size());
    checkTestData(services.get(0), id);
  }

  @Test
  public void testDeviceServicesForAddressableByName() {
    List<DeviceService> services =
        client.deviceServicesForAddressableByName(AddressableData.TEST_ADDR_NAME);
    assertEquals("Find for address not returning appropriate list", 1, services.size());
    checkTestData(services.get(0), id);
  }

  @Test(expected = NotFoundException.class)
  public void testDeviceServicesForAddressWithNoneMatching() throws Exception {
    client.deviceServicesForAddressable("badaddress");
  }

  @Test(expected = NotFoundException.class)
  public void testDeviceServicesForAddressByNameWithNoneMatching() throws Exception {
    client.deviceServicesForAddressableByName("badaddress");
  }

  @Test
  public void testAddressablesForAssociatedDevices() {
    Set<Addressable> addressables = client.addressablesForAssociatedDevices(id);
    assertEquals(
        "Find addressables for associated devices not returning appropriate list of addressable", 1,
        addressables.size());
    AddressableData.checkTestData((Addressable) addressables.toArray()[0], addrId);
  }

  @Test(expected = NotFoundException.class)
  public void testAddressableForAssociatedDevicesWithBadService() {
    client.addressablesForAssociatedDevices("badserviceid");
  }

  @Test
  public void testAddressableForAssociatedDevicesByName() {
    Set<Addressable> addressables =
        client.addressablesForAssociatedDevicesByName(TEST_SERVICE_NAME);
    assertEquals(
        "Find addressables for associated devices by name not returning appropriate list of addressable",
        1, addressables.size());
    AddressableData.checkTestData((Addressable) addressables.toArray()[0], addrId);
  }

  @Test(expected = NotFoundException.class)
  public void testAddressableForAssociatedDevicesByNameWithBadService() {
    client.addressablesForAssociatedDevicesByName("badservicename");
  }

  @Test
  public void testAdd() {
    DeviceService service = client.deviceService(id);
    service.setId(null);
    service.setName("NewName");
    String newId = client.add(service);
    assertNotNull("New device id is null", newId);
    assertNotNull("Modified date is null", service.getModified());
    assertNotNull("Create date is null", service.getCreated());
  }

  @Test(expected = ClientErrorException.class)
  public void testAddWithSameName() {
    DeviceService service = newTestInstance();
    client.add(service);
  }

  @Test(expected = ClientErrorException.class)
  public void testAddWithNoAddressable() {
    DeviceService service = client.deviceService(id);
    service.setId(null);
    service.setName("newname");
    service.setAddressable(null);
    client.add(service);
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
    assertTrue("Delete did not return correctly", client.deleteByName(TEST_SERVICE_NAME));
  }

  @Test(expected = NotFoundException.class)
  public void testDeleteByNameWithNone() {
    client.delete("badname");
  }

  @Test
  public void testUpdate() {
    DeviceService service = client.deviceService(id);
    service.setDescription("newdescription");
    assertTrue("Update did not complete successfully", client.update(service));
    DeviceService service2 = client.deviceService(id);
    assertEquals("Update did not work correclty", "newdescription", service2.getDescription());
    assertNotNull("Modified date is null", service2.getModified());
    assertNotNull("Create date is null", service2.getCreated());
    assertTrue("Modified date and create date should be different after update",
        service2.getModified() != service2.getCreated());
  }

  @Test
  public void testUpdateLastConnected() {
    assertTrue("Update did not complete successfully", client.updateLastConnected(id, 1000));
    DeviceService service = client.deviceService(id);
    assertEquals("Update last connected did not work correclty", 1000, service.getLastConnected());
    assertNotNull("Modified date is null", service.getModified());
    assertNotNull("Create date is null", service.getCreated());
    assertTrue("Modified date and create date should be different after update",
        service.getModified() != service.getCreated());
  }

  @Test(expected = NotFoundException.class)
  public void testUpdateLastConnectedNoneFound() {
    client.updateLastConnected("badid", 1000);
  }

  @Test
  public void testUpdateLastConnectedByName() {
    assertTrue("Update did not complete successfully",
        client.updateLastConnectedByName(TEST_SERVICE_NAME, 1000));
    DeviceService service = client.deviceServiceForName(TEST_SERVICE_NAME);
    assertEquals("Update last connected did not work correclty", 1000, service.getLastConnected());
    assertNotNull("Modified date is null", service.getModified());
    assertNotNull("Create date is null", service.getCreated());
    assertTrue("Modified date and create date should be different after update",
        service.getModified() != service.getCreated());
  }

  @Test(expected = NotFoundException.class)
  public void testUpdateLastConnectedByNameNoneFound() {
    client.updateLastConnectedByName("badname", 1000);
  }

  @Test
  public void testUpdateLastReported() {
    assertTrue("Update did not complete successfully", client.updateLastReported(id, 1000));
    DeviceService service = client.deviceService(id);
    assertEquals("Update last reported did not work correclty", 1000, service.getLastReported());
    assertNotNull("Modified date is null", service.getModified());
    assertNotNull("Create date is null", service.getCreated());
    assertTrue("Modified date and create date should be different after update",
        service.getModified() != service.getCreated());
  }

  @Test(expected = NotFoundException.class)
  public void testUpdateLastReportedNoneFound() {
    client.updateLastReported("badid", 1000);
  }

  @Test
  public void testUpdateLastReportedByName() {
    assertTrue("Update did not complete successfully",
        client.updateLastReportedByName(TEST_SERVICE_NAME, 1000));
    DeviceService service = client.deviceServiceForName(TEST_SERVICE_NAME);
    assertEquals("Update last reported did not work correclty", 1000, service.getLastReported());
    assertNotNull("Modified date is null", service.getModified());
    assertNotNull("Create date is null", service.getCreated());
    assertTrue("Modified date and create date should be different after update",
        service.getModified() != service.getCreated());
  }

  @Test(expected = NotFoundException.class)
  public void testUpdateLastReportedByNameNoneFound() {
    client.updateLastReportedByName("badname", 1000);
  }

  @Test
  public void testUpdateOpState() {
    assertTrue("Update did not complete successfully",
        client.updateOpState(id, OperatingState.DISABLED.toString()));
    DeviceService service = client.deviceService(id);
    assertEquals("Update op state did not work correclty", OperatingState.DISABLED,
        service.getOperatingState());
    assertNotNull("Modified date is null", service.getModified());
    assertNotNull("Create date is null", service.getCreated());
    assertTrue("Modified date and create date should be different after update",
        service.getModified() != service.getCreated());
  }

  @Test(expected = NotFoundException.class)
  public void testUpdateOpStateNoneFound() {
    client.updateOpState("badid", OperatingState.DISABLED.toString());
  }

  @Test
  public void testUpdateOpStateByName() {
    assertTrue("Update did not complete successfully",
        client.updateOpStateByName(TEST_SERVICE_NAME, OperatingState.DISABLED.toString()));
    DeviceService service = client.deviceServiceForName(TEST_SERVICE_NAME);
    assertEquals("Update op state did not work correclty", OperatingState.DISABLED,
        service.getOperatingState());
    assertNotNull("Modified date is null", service.getModified());
    assertNotNull("Create date is null", service.getCreated());
    assertTrue("Modified date and create date should be different after update",
        service.getModified() != service.getCreated());
  }

  @Test(expected = NotFoundException.class)
  public void testUpdateOpStateByNameNoneFound() {
    client.updateOpStateByName("badname", OperatingState.DISABLED.toString());
  }

  @Test
  public void testUpdateAdminState() {
    assertTrue("Update did not complete successfully",
        client.updateAdminState(id, AdminState.LOCKED.toString()));
    DeviceService service = client.deviceService(id);
    assertEquals("Update admin state did not work correclty", AdminState.LOCKED,
        service.getAdminState());
    assertNotNull("Modified date is null", service.getModified());
    assertNotNull("Create date is null", service.getCreated());
    assertTrue("Modified date and create date should be different after update",
        service.getModified() != service.getCreated());
  }

  @Test(expected = NotFoundException.class)
  public void testUpdateAdminStateNoneFound() {
    client.updateAdminState("badid", AdminState.LOCKED.toString());
  }

  @Test
  public void testUpdateAdminStateByName() {
    assertTrue("Update did not complete successfully",
        client.updateAdminStateByName(TEST_SERVICE_NAME, AdminState.LOCKED.toString()));
    DeviceService service = client.deviceServiceForName(TEST_SERVICE_NAME);
    assertEquals("Update admin state did not work correclty", AdminState.LOCKED,
        service.getAdminState());
    assertNotNull("Modified date is null", service.getModified());
    assertNotNull("Create date is null", service.getCreated());
    assertTrue("Modified date and create date should be different after update",
        service.getModified() != service.getCreated());
  }

  @Test(expected = NotFoundException.class)
  public void testUpdateAdminStateByNameNoneFound() {
    client.updateOpStateByName("badname", AdminState.LOCKED.toString());
  }

  @Test(expected = NotFoundException.class)
  public void testUpdateWithNone() {
    DeviceService service = client.deviceService(id);
    service.setId("badid");
    service.setName("badname");
    service.setDescription("newdescription");
    client.update(service);
  }

}
