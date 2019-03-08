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

import static org.edgexfoundry.test.data.ReportData.TEST_RPT_NAME;
import static org.edgexfoundry.test.data.ReportData.checkTestData;
import static org.edgexfoundry.test.data.ReportData.newTestInstance;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.NotFoundException;

import org.edgexfoundry.controller.AddressableClient;
import org.edgexfoundry.controller.DeviceClient;
import org.edgexfoundry.controller.DeviceProfileClient;
import org.edgexfoundry.controller.DeviceReportClient;
import org.edgexfoundry.controller.DeviceServiceClient;
import org.edgexfoundry.controller.ScheduleClient;
import org.edgexfoundry.controller.ScheduleEventClient;
import org.edgexfoundry.controller.impl.AddressableClientImpl;
import org.edgexfoundry.controller.impl.DeviceClientImpl;
import org.edgexfoundry.controller.impl.DeviceProfileClientImpl;
import org.edgexfoundry.controller.impl.DeviceReportClientImpl;
import org.edgexfoundry.controller.impl.DeviceServiceClientImpl;
import org.edgexfoundry.controller.impl.ScheduleClientImpl;
import org.edgexfoundry.controller.impl.ScheduleEventClientImpl;
import org.edgexfoundry.domain.meta.Addressable;
import org.edgexfoundry.domain.meta.Device;
import org.edgexfoundry.domain.meta.DeviceProfile;
import org.edgexfoundry.domain.meta.DeviceReport;
import org.edgexfoundry.domain.meta.DeviceService;
import org.edgexfoundry.domain.meta.Schedule;
import org.edgexfoundry.domain.meta.ScheduleEvent;
import org.edgexfoundry.test.category.RequiresMetaDataRunning;
import org.edgexfoundry.test.category.RequiresMongoDB;
import org.edgexfoundry.test.data.AddressableData;
import org.edgexfoundry.test.data.DeviceData;
import org.edgexfoundry.test.data.ProfileData;
import org.edgexfoundry.test.data.ReportData;
import org.edgexfoundry.test.data.ScheduleData;
import org.edgexfoundry.test.data.ScheduleEventData;
import org.edgexfoundry.test.data.ServiceData;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category({RequiresMongoDB.class, RequiresMetaDataRunning.class})
public class DeviceReportClientTest {

  private static final String ENDPT = "http://localhost:48081/api/v1/devicereport";
  private static final String EVT_ENDPT = "http://localhost:48081/api/v1/scheduleevent";
  private static final String SCH_ENDPT = "http://localhost:48081/api/v1/schedule";
  private static final String DEVICE_ENDPT = "http://localhost:48081/api/v1/device";
  private static final String SRV_ENDPT = "http://localhost:48081/api/v1/deviceservice";
  private static final String PRO_ENDPT = "http://localhost:48081/api/v1/deviceprofile";
  private static final String ADDR_ENDPT = "http://localhost:48081/api/v1/addressable";

  private DeviceReportClient client;
  private ScheduleEventClient evtClient;
  private ScheduleClient schClient;
  private DeviceClient dClient;
  private DeviceServiceClient srvClient;
  private DeviceProfileClient proClient;
  private AddressableClient addrClient;

  private String id;

  // setup tests add function
  @Before
  public void setup() throws Exception {
    client = new DeviceReportClientImpl();
    evtClient = new ScheduleEventClientImpl();
    schClient = new ScheduleClientImpl();
    dClient = new DeviceClientImpl();
    srvClient = new DeviceServiceClientImpl();
    proClient = new DeviceProfileClientImpl();
    addrClient = new AddressableClientImpl();
    setURL();
    Schedule schedule = ScheduleData.newTestInstance();
    schClient.add(schedule);
    Addressable addr = AddressableData.newTestInstance();
    addrClient.add(addr);
    ScheduleEvent event = ScheduleEventData.newTestInstance();
    event.setAddressable(addr);
    evtClient.add(event);
    DeviceService srv = ServiceData.newTestInstance();
    srv.setAddressable(addr);
    srvClient.add(srv);
    DeviceProfile profile = ProfileData.newTestInstance();
    proClient.add(profile);
    Device device = DeviceData.newTestInstance();
    device.setAddressable(addr);
    device.setProfile(profile);
    device.setService(srv);
    dClient.add(device);
    DeviceReport rpt = ReportData.newTestInstance();
    id = client.add(rpt);
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
    Class<?> clientClass5 = schClient.getClass();
    Field temp5 = clientClass5.getDeclaredField("url");
    temp5.setAccessible(true);
    temp5.set(schClient, SCH_ENDPT);
    Class<?> clientClass6 = evtClient.getClass();
    Field temp6 = clientClass6.getDeclaredField("url");
    temp6.setAccessible(true);
    temp6.set(evtClient, EVT_ENDPT);
    Class<?> clientClass7 = dClient.getClass();
    Field temp7 = clientClass7.getDeclaredField("url");
    temp7.setAccessible(true);
    temp7.set(dClient, DEVICE_ENDPT);
  }

  // cleanup tests delete function
  @After
  public void cleanup() throws Exception {
    // make sure to remove events before schedules for dependency issues
    List<DeviceReport> rpts = client.deviceReports();
    rpts.forEach((report) -> client.delete(report.getId()));
    List<ScheduleEvent> events = evtClient.scheduleEvents();
    events.forEach((event) -> evtClient.delete(event.getId()));
    List<Schedule> schs = schClient.schedules();
    schs.forEach((schedule) -> schClient.delete(schedule.getId()));
    List<Device> devices = dClient.devices();
    devices.forEach((device) -> dClient.delete(device.getId()));
    List<DeviceProfile> profiles = proClient.deviceProfiles();
    profiles.forEach((profile) -> proClient.delete(profile.getId()));
    List<DeviceService> services = srvClient.deviceServices();
    services.forEach((service) -> srvClient.delete(service.getId()));
    List<Addressable> addrs = addrClient.addressables();
    addrs.forEach((addr) -> addrClient.delete(addr.getId()));
  }

  @Test
  public void testDeviceReport() {
    DeviceReport device = client.deviceReport(id);
    checkTestData(device, id);
  }

  @Test(expected = NotFoundException.class)
  public void testDeviceReportWithUnknownId() {
    client.deviceReport("nosuchid");
  }

  @Test
  public void testDeviceReports() {
    List<DeviceReport> reports = client.deviceReports();
    assertEquals("Find all not returning a list with one device report", 1, reports.size());
    checkTestData(reports.get(0), id);
  }

  @Test
  public void testDeviceReportForName() {
    DeviceReport report = client.deviceReportForName(TEST_RPT_NAME);
    checkTestData(report, id);
  }

  @Test(expected = NotFoundException.class)
  public void testDeviceReportForNameWithNoneMatching() {
    client.deviceReportForName("badname");
  }

  @Test(expected = NotFoundException.class)
  public void testAddWithoutDevice() {
    DeviceReport rpt = newTestInstance();
    rpt.setDevice("baddevice");
    client.add(rpt);
  }

  @Test(expected = NotFoundException.class)
  public void testAddWithoutEvent() {
    DeviceReport rpt = newTestInstance();
    rpt.setEvent("badscheduleevent");
    client.add(rpt);
  }

  @Test(expected = ClientErrorException.class)
  public void testAddWithSameName() {
    DeviceReport report = client.deviceReport(id);
    report.setId(null);
    client.add(report);
  }

  @Test
  public void testAssociatedValueDesriptors() {
    List<String> valueDescriptorNames = client.associatedValueDesriptors(DeviceData.TEST_NAME);
    assertEquals("list of VDs from device reports for assocaited devices not what expected",
        Arrays.asList(ReportData.TEST_EXPECTED), valueDescriptorNames);
  }

  @Test
  public void testAssociatedValueDesriptorsWithUnknownDevice() {
    assertTrue(
        "List of VDs from device reports for associated devices returning results on bad device id",
        client.associatedValueDesriptors("unknowndevice").isEmpty());
  }

  @Test
  public void testDeviceReportsForDevice() {
    List<DeviceReport> rpts = client.deviceReportsForDevice(DeviceData.TEST_NAME);
    assertEquals("Find by device not returning a list with one device report", 1, rpts.size());
    checkTestData(rpts.get(0), id);
  }

  @Test
  public void testDeviceReportsForDeviceWithUnknownDevice() {
    assertTrue("List of Device reports for associated device returning results on bad device id",
        client.deviceReportsForDevice("unknowndevice").isEmpty());
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
    assertTrue("Delete did not return correctly", client.deleteByName(TEST_RPT_NAME));
  }

  @Test(expected = NotFoundException.class)
  public void testDeleteByNameWithNone() {
    client.delete("badname");
  }

  @Test
  public void testUpdate() {
    DeviceReport report = client.deviceReport(id);
    report.setOrigin(1234);
    assertTrue("Update did not complete successfully", client.update(report));
    DeviceReport report2 = client.deviceReport(id);
    assertEquals("Update did not work correclty", 1234, report2.getOrigin());
    assertNotNull("Modified date is null", report2.getModified());
    assertNotNull("Create date is null", report2.getCreated());
    assertTrue("Modified date and create date should be different after update",
        report2.getModified() != report2.getCreated());
  }

  @Test(expected = NotFoundException.class)
  public void testUpdateWithoutDevice() {
    DeviceReport rpt = client.deviceReport(id);
    rpt.setDevice("baddevice");
    client.update(rpt);
  }

  @Test(expected = NotFoundException.class)
  public void testUpdateWithoutEvent() {
    DeviceReport rpt = client.deviceReport(id);
    rpt.setEvent("badscheduleevent");
    client.update(rpt);
  }

  @Test(expected = NotFoundException.class)
  public void testUpdateWithNone() {
    DeviceReport report = client.deviceReport(id);
    report.setId("badid");
    report.setName("badname");
    report.setOrigin(1234);
    client.update(report);
  }

}
