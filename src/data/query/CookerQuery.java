package data.query;

import java.util.ArrayList;
import java.util.List;

import data.dataObject.CookerDataObject;
import data.domain.Event;

public class CookerQuery extends Query<CookerDataObject> {

	@Override
	public List<CookerDataObject> getAll() {
		ArrayList<CookerDataObject> cookerDOs = new ArrayList<>();
		return cookerDOs;
	}

	public List<CookerDataObject> getCookerByEventId(Integer eventId) {
		ArrayList<CookerDataObject> cookerDOs = new ArrayList<>();
		return cookerDOs;
	}

	public void deleteEvent(String channelId) {
		
	}

	@Override
	public Integer save(Event event) {
		EventQuery eventQuery = new EventQuery();
		
		return save(eventQuery.getId(event), event);
	}
	
	public Integer save(Integer eventId, Event event) {
		return 0;
	}

}
