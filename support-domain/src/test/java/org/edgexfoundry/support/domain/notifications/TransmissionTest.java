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
 * @microservice: support-domain
 * @author: Jim White, Dell
 * @version: 1.0.0
 *******************************************************************************/
package org.edgexfoundry.support.domain.notifications;

import org.junit.Test;


/**
 * Added test due to bug created by toString method with conditional appends and automatic string
 * concat in other microservices. Caused problems when properties were null.
 * 
 * @author Jim White
 *
 */
public class TransmissionTest {

  @Test
  public void testToString() {
    Transmission trans = new Transmission();
    trans.toString();
  }

}
