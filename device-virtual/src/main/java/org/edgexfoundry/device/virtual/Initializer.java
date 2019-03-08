/*******************************************************************************
 * Copyright 2016-2017 Dell Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @microservice:  device-sdk-tools
 * @author: Tyler Cox, Dell
 * @version: 1.0.0
 *******************************************************************************/
package org.edgexfoundry.device.virtual;

import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.edgexfoundry.device.virtual.config.ApplicationProperties;
import org.edgexfoundry.device.virtual.data.DeviceStore;
import org.edgexfoundry.device.virtual.scheduling.Scheduler;
import org.edgexfoundry.device.virtual.service.CleanupService;
import org.edgexfoundry.support.logging.client.EdgeXLogger;
import org.edgexfoundry.support.logging.client.EdgeXLoggerFactory;

@Component
public class Initializer extends BaseService {

	private final static EdgeXLogger logger = EdgeXLoggerFactory.getEdgeXLogger(Initializer.class);
	
	@Autowired
	DeviceStore devices;
	
	@Autowired
	Scheduler schedules;
	
	@Autowired
	private ApplicationProperties applicationProperties;
	
	@Autowired
	private CleanupService cleanupService;

	@Override
	public boolean initialize(String deviceServiceId) {
		// load the devices in cache.
		devices.initialize(deviceServiceId);
		schedules.initialize(getServiceName());
		logger.info("Initialized device service successfully");
		return true;
	}
	
	@PreDestroy
	public void cleanup() {
		if (applicationProperties.isAutoCleanup()) {
			cleanupService.doCleanup();
		}
	}
	
}
