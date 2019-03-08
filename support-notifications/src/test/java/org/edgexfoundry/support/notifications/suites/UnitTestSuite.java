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
 * @microservice: support-notifications
 * @author: Jim White, Dell
 * @version: 1.0.0
 *******************************************************************************/

package org.edgexfoundry.support.notifications.suites;

import org.edgexfoundry.support.notifications.controller.CleanupControllerTest;
import org.edgexfoundry.support.notifications.controller.LocalErrorControllerTest;
import org.edgexfoundry.support.notifications.controller.NotificationsControllerTest;
import org.edgexfoundry.support.notifications.controller.PingControllerTest;
import org.edgexfoundry.support.notifications.controller.integration.SubscriptionControllerTest;
import org.edgexfoundry.support.notifications.controller.integration.TransmissionControllerTest;
import org.edgexfoundry.support.notifications.service.CleanupServiceTest;
import org.edgexfoundry.support.notifications.service.CriticalSeverityResendTaskTest;
import org.edgexfoundry.support.notifications.service.DistributionCoordinatorTest;
import org.edgexfoundry.support.notifications.service.EMAILSendingServiceTest;
import org.edgexfoundry.support.notifications.service.EscalationServiceTest;
import org.edgexfoundry.support.notifications.service.NormalSeverityDistributionExecutorTest;
import org.edgexfoundry.support.notifications.service.NormalSeverityResendExecutorTest;
import org.edgexfoundry.support.notifications.service.NotificationHandlerTest;
import org.edgexfoundry.support.notifications.service.RESTfulSendingServiceTest;
import org.edgexfoundry.support.notifications.service.SubscriptionHandlerTest;
import org.edgexfoundry.support.notifications.service.TransmissionHandlerTest;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Used in development only. Remove @Ignore to run just the unit tests (not integration tests).
 * These tests do not require any other resources to run.
 * 
 * @author Jim White
 *
 */
@Ignore
@RunWith(Suite.class)
@Suite.SuiteClasses({CleanupControllerTest.class, LocalErrorControllerTest.class,
    NotificationsControllerTest.class, PingControllerTest.class, SubscriptionControllerTest.class,
    TransmissionControllerTest.class, CleanupServiceTest.class,
    CriticalSeverityResendTaskTest.class, DistributionCoordinatorTest.class,
    EMAILSendingServiceTest.class, EscalationServiceTest.class,
    NormalSeverityDistributionExecutorTest.class, NormalSeverityResendExecutorTest.class,
    NotificationHandlerTest.class, RESTfulSendingServiceTest.class, SubscriptionHandlerTest.class,
    TransmissionHandlerTest.class

})
public class UnitTestSuite {

}
