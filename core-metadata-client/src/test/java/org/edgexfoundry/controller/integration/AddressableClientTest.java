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

import static org.edgexfoundry.test.data.AddressableData.TEST_ADDRESS;
import static org.edgexfoundry.test.data.AddressableData.TEST_ADDR_NAME;
import static org.edgexfoundry.test.data.AddressableData.TEST_PORT;
import static org.edgexfoundry.test.data.AddressableData.TEST_PUBLISHER;
import static org.edgexfoundry.test.data.AddressableData.TEST_TOPIC;
import static org.edgexfoundry.test.data.AddressableData.checkTestData;
import static org.edgexfoundry.test.data.AddressableData.newTestInstance;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.util.List;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.NotFoundException;

import org.edgexfoundry.controller.AddressableClient;
import org.edgexfoundry.controller.impl.AddressableClientImpl;
import org.edgexfoundry.domain.meta.Addressable;
import org.edgexfoundry.test.category.RequiresMetaDataRunning;
import org.edgexfoundry.test.category.RequiresMongoDB;
import org.edgexfoundry.test.data.AddressableData;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category({RequiresMongoDB.class, RequiresMetaDataRunning.class})
public class AddressableClientTest {

  private static final String ENDPT = "http://localhost:48081/api/v1/addressable";

  private AddressableClient client;
  private String id;

  // setup tests the add function
  @Before
  public void setup() throws Exception {
    client = new AddressableClientImpl();
    setURL();
    Addressable addressable = AddressableData.newTestInstance();
    id = client.add(addressable);
    assertNotNull("Addressable did not get created correctly", id);
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
    List<Addressable> addrs = client.addressables();
    addrs.forEach((a) -> client.delete(a.getId()));
  }

  @Test
  public void testAddressable() {
    Addressable addr = client.addressable(id);
    checkTestData(addr, id);
  }

  @Test(expected = NotFoundException.class)
  public void testAddressableWithUnknownnId() {
    client.addressable("nosuchid");
  }

  @Test
  public void testAddressables() {
    List<Addressable> addrs = client.addressables();
    assertEquals("Find all not returning a list with one object", 1, addrs.size());
    checkTestData(addrs.get(0), id);
  }

  @Test
  public void testAddressableForName() {
    Addressable addr = client.addressableForName(TEST_ADDR_NAME);
    checkTestData(addr, id);
  }

  @Test(expected = NotFoundException.class)
  public void testAddressableForNameWithNoneMatching() {
    client.addressableForName("badname");
  }

  @Test
  public void testAddressablesForAddress() {
    List<Addressable> addrs = client.addressablesByAddress(TEST_ADDRESS);
    assertEquals("Find for address not returning appropriate list", 1, addrs.size());
    checkTestData(addrs.get(0), id);
  }

  @Test
  public void testAddressablesForPort() {
    List<Addressable> addrs = client.addressablesByPort("" + TEST_PORT);
    assertEquals("Find for port not returning appropriate list", 1, addrs.size());
    checkTestData(addrs.get(0), id);
  }

  @Test
  public void testAddressablesForTopic() {
    List<Addressable> addrs = client.addressablesByTopic(TEST_TOPIC);
    assertEquals("Find for " + "topic not returning appropriate list", 1, addrs.size());
    checkTestData(addrs.get(0), id);
  }

  @Test
  public void testAddressablesForPublisher() {
    List<Addressable> as = client.addressablesByPublisher(TEST_PUBLISHER);
    assertEquals("Find for publisher  not returning appropriate list", 1, as.size());
    checkTestData(as.get(0), id);
  }

  @Test(expected = ClientErrorException.class)
  public void testAddWithSameName() {
    Addressable addr = newTestInstance();
    client.add(addr);
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
    assertTrue("Delete did not return correctly", client.deleteByName(TEST_ADDR_NAME));
  }

  @Test(expected = NotFoundException.class)
  public void testDeleteByNameWithNone() {
    client.delete("badname");
  }

  @Test
  public void testUpdate() {
    Addressable a = client.addressable(id);
    a.setAddress("newaddress");
    assertTrue("Did not update correctly", client.update(a));
  }

  @Test(expected = NotFoundException.class)
  public void testUpdateWithNone() {
    Addressable a = client.addressable(id);
    a.setId("badid");
    a.setName("badname");
    a.setAddress("newaddress");
    client.update(a);
  }

}
