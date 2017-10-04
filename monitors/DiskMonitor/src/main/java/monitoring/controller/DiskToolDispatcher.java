/*******************************************************************************
 * Copyright (c) 2016 Universitat Politécnica de Catalunya (UPC)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 * 	Quim Motger (UPC) - main development
 * 	
 * Initially developed in the context of SUPERSEDE EU project
 * www.supersede.eu
 *******************************************************************************/
package monitoring.controller;

import javax.inject.Singleton;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.apache.log4j.Logger;

import monitoring.controller.ToolDispatcher;
import monitoring.model.DiskParserConfiguration;

@Path("configuration")
@Singleton
public class DiskToolDispatcher {
	
	final static Logger logger = Logger.getLogger(DiskToolDispatcher.class);
	
	private ToolDispatcher toolDispatcher = 
			new ToolDispatcher(new DiskParserConfiguration(), "DiskMonitoringConfProfResult");
	
	@POST
	public String addConfiguration(String jsonConf) {
		return toolDispatcher.addConfiguration(jsonConf);
	}
	
	@PUT
	@Path("{id}")
	public String updateConfiguration(@PathParam("id") Integer id, String jsonConf) throws Exception {
		return toolDispatcher.updateConfiguration(id,  jsonConf);
	}
	
	@DELETE
	@Path("{id}")
	public String deleteConfiguration(@PathParam("id") Integer id) {
		return toolDispatcher.deleteConfiguration(id);
	}

}
