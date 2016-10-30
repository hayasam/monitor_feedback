package ch.uzh.ifi.feedback.orchestrator.services;

import com.google.inject.Singleton;

import ch.uzh.ifi.feedback.library.transaction.DbResultParser;
import ch.uzh.ifi.feedback.orchestrator.model.MonitorType;

@Singleton
public class MonitorTypeResultParser extends DbResultParser<MonitorType	>{

	public MonitorTypeResultParser() {
		super(MonitorType.class);
	}

}
	