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
package ch.uzh.ifi.feedback.orchestrator.model;

import java.util.List;

import ch.uzh.ifi.feedback.library.rest.annotations.DbAttribute;
import ch.uzh.ifi.feedback.library.rest.annotations.DbIgnore;
import ch.uzh.ifi.feedback.library.rest.annotations.Id;
import ch.uzh.ifi.feedback.library.rest.annotations.NotNull;
import ch.uzh.ifi.feedback.library.rest.annotations.Serialize;
import ch.uzh.ifi.feedback.library.rest.annotations.Unique;
import ch.uzh.ifi.feedback.orchestrator.serialization.MonitorToolSerializationService;

@Serialize(MonitorToolSerializationService.class)
public class MonitorTool extends OrchestratorItem<MonitorTool>{

	@Id
	@DbAttribute("monitor_tool_id")
	private Integer id;
	
	@Unique
	@NotNull
	private String name;
	
	@DbAttribute("monitor_type_id")
	private Integer monitorTypeId;
	
	@DbAttribute("monitor_name")
	private String monitorName;
	
	public String getMonitorName() {
		return monitorName;
	}
	
	public void setMonitorName(String name) {
		this.monitorName = name;
	}
	
	public Integer getMonitorTypeId() {
		return monitorTypeId;
	}

	public void setMonitorTypeId(Integer monitorTypeId) {
		this.monitorTypeId = monitorTypeId;
	}
	
	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
