package data.query;

import java.util.ArrayList;
import java.util.List;

import data.dataObject.EventDataObject;
import data.domain.Event;

public class EventQuery extends Query<EventDataObject> {

	@Override
	public List<EventDataObject> getAll() {
		ArrayList<EventDataObject> eventDOs = new ArrayList<>();
		return eventDOs;
	}

	public List<EventDataObject> getEventByChannelIds(List<String> channelsId) {
		ArrayList<EventDataObject> eventDOs = new ArrayList<>();
		return eventDOs;
	}

	public void deleteEvent(String channelId) {
		
	}

	@Override
	public Integer save(Event event) {
		return 0;
	}

	public Integer getId(Event event) {
		return 0;
	}
	
	

}
