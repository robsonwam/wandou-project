package cn.edu.thu.log.replaylog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import cn.edu.thu.log.web.service.XESReadService;

public class ReplayLog {
	private XLog log;

	private Map<String,Integer> activitiesMapping;
	
	public ReplayLog(XESReadService reader){
		activitiesMapping=new HashMap<String,Integer>();
		while(reader.hasNext()){
			log=reader.next();
			countTransitions(log);
		}	
	}
	
	private void countTransitions(XLog log){
		for(XTrace trace:log)
			for(XEvent event:trace){
				increaseActivityCount(event);
			}	
		
	}
	
	private void increaseActivityCount(XEvent event){
		String activityname=event.getAttributes().get(XConceptExtension.KEY_NAME).toString()+"+";
		if(!activitiesMapping.containsKey(activityname)){
			activitiesMapping.put(activityname, 1);
		}
		else{
			activitiesMapping.put(activityname, activitiesMapping.get(activityname)+1);
		}
	}

	public Map<String, Integer> getActivitiesMapping() {
		return activitiesMapping;
	}

	public void setActivitiesMapping(Map<String, Integer> activitiesMapping) {
		this.activitiesMapping = activitiesMapping;
	}
}
