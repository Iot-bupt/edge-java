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
 * @microservice: core-test library
 * @author: Jim White, Dell
 * @version: 1.0.0
 *******************************************************************************/
package org.edgexfoundry.test.data;

import static org.edgexfoundry.test.data.CommonData.TEST_DESCRIPTION;
import static org.edgexfoundry.test.data.CommonData.TEST_ORIGIN;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.edgexfoundry.domain.common.IoTType;
import org.edgexfoundry.domain.common.ValueDescriptor;

public interface ValueDescriptorData {

  static final String TEST_NAME = "Temperature";
  static final int TEST_MIN = -70;
  static final int TEST_MAX = 140;
  static final IoTType TEST_TYPE = IoTType.I;
  static final String TEST_UOMLABEL = "C";
  static final int TEST_DEF_VALUE = 32;
  static final String TEST_FORMATTING = "%d";
  static final String[] TEST_LABELS = {"temp", "room temp"};

  static final String TEST_NAME1 = "rpm";
  static final String TEST_VALUE1 = "750";
  static final String TEST_NAME2 = "temperature";
  static final String TEST_VALUE2 = "45";

  static ValueDescriptor newTestInstance() {
    ValueDescriptor valDesc = new ValueDescriptor(TEST_NAME, TEST_MIN, TEST_MAX, TEST_TYPE,
        TEST_UOMLABEL, TEST_DEF_VALUE, TEST_FORMATTING, TEST_LABELS, TEST_DESCRIPTION);
    valDesc.setOrigin(TEST_ORIGIN);
    return valDesc;
  }

  static void checkTestData(ValueDescriptor valueDescriptor, String valDescId) {
    assertEquals("ValueDescriptor ID does not match saved id", valDescId, valueDescriptor.getId());
    assertEquals("ValueDescriptor name does not match saved name", TEST_NAME,
        valueDescriptor.getName());
    assertEquals("ValueDescriptor min does not match saved min", TEST_MIN,
        valueDescriptor.getMin());
    assertEquals("ValueDescriptor max does not match saved max", TEST_MAX,
        valueDescriptor.getMax());
    assertEquals("ValueDescriptor type does not match saved type", TEST_TYPE,
        valueDescriptor.getType());
    assertEquals("ValueDescriptor label does not match saved label", TEST_UOMLABEL,
        valueDescriptor.getUomLabel());
    assertEquals("ValueDescriptor default value does not match saved default value", TEST_DEF_VALUE,
        valueDescriptor.getDefaultValue());
    assertEquals("ValueDescriptor formatting does not match saved formatting", TEST_FORMATTING,
        valueDescriptor.getFormatting());
    assertArrayEquals("ValueDescriptor labels does not match saved labels", TEST_LABELS,
        valueDescriptor.getLabels());
    assertEquals("ValueDescriptor description does not match saved description", TEST_DESCRIPTION,
        valueDescriptor.getDescription());
    assertEquals("ValueDescriptor origin does not match saved origin", TEST_ORIGIN,
        valueDescriptor.getOrigin());
    assertNotNull("ValueDescriptor modified date is null", valueDescriptor.getModified());
    assertNotNull("ValueDescriptor create date is null", valueDescriptor.getCreated());
  }

}
