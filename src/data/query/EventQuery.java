package data.query;

import java.util.ArrayList;
import java.util.List;

import data.dataObject.EventDataObject;
import data.domain.Event;
import net.dv8tion.jda.core.entities.TextChannel;

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

}
