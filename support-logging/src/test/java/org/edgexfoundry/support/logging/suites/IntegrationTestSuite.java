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

package org.edgexfoundry.support.logging.suites;

import org.edgexfoundry.support.logging.controller.integration.*;
import org.edgexfoundry.support.logging.dao.integration.*;

import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Used in development only. Remove @Ignore to run just the integration tests (not unit tests).
 * These tests do require other resources to run.
 * 
 * @author Jim White
 *
 */
@Ignore
@RunWith(Suite.class)
@Suite.SuiteClasses({LoggingControllerTest.class, FileLogEntryDAOTest.class,
    MongoDBLogEntryDAOTest.class})
public class IntegrationTestSuite {

}
