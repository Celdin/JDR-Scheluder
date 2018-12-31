package data.query;

import static java.lang.String.format;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import data.dataObject.DataObject;
import data.dataObject.EventDataObject;
import data.domain.Event;
import driver.PostgreSQLDriver;

public class EventQuery extends Query<EventDataObject> {
	
	public EventQuery() {
		super();
		setSupplier(() -> new EventDataObject());
	}

	public EventDataObject getEventByChannelId(String channelId) throws SQLException {
		List<EventDataObject> events = getEventByChannelIds(Collections.singletonList(channelId));
		if(!events.isEmpty()) {
			return events.get(0);
		}
		return null;
	}

	public List<EventDataObject> getEventByChannelIds(List<String> channelsId) throws SQLException {
		List<EventDataObject> eventDOs = new ArrayList<>();
		String query = 
				"SELECT * "
				+ "FROM "
				+ "	%s "
				+ "WHERE"
				+ "	%s ;";
		query = format(query, EventDataObject.TABLE_NAME, buildInClause(channelsId, EventDataObject.COLUMN_CHANNEL_ID));

		Connection connection = PostgreSQLDriver.getConnection();
		Statement statement = connection .createStatement();
		System.out.println(query);
		ResultSet result = statement.executeQuery(query);
		while(result.next()) {
			EventDataObject dataObject = new EventDataObject();
			for(String column : dataObject.getColumnNames()) {
				dataObject.set(result.getObject(column), column);
			}
			dataObject.set(result.getObject(DataObject.COLUMN_ID), DataObject.COLUMN_ID);
			eventDOs.add(dataObject);
		}
		statement.close();
		result.close();
		return eventDOs;
	}

	public void deleteEvent(String channelId) throws SQLException {
		EventDataObject event = getEventByChannelId(channelId);
		if(event != null) {
			delete(event);
		}
	}

	public Integer save(Event event) throws SQLException {
		Integer id = getId(event);
		if(id != null) {
			EventDataObject eventDataObject = buildEventDataObject(event);
			eventDataObject.setId(id);
			update(eventDataObject);
		} else {
			create(event);
		}
		return id;
	}

	public Integer getId(Event event) throws SQLException {
		EventDataObject eventDO = getEventByChannelId(event.getAnnonceDate().getChannel().getId());
		if(eventDO == null)
			return null;
		else
			return eventDO.getId();
	}

	public static void create(Event botEvent) throws SQLException {
		EventDataObject eventDataObject = buildEventDataObject(botEvent);
		insert(eventDataObject);
	}

	private static EventDataObject buildEventDataObject(Event botEvent) {
		EventDataObject eventDataObject = new EventDataObject();
		eventDataObject.setChannelId(botEvent.getAnnonceDate().getChannel().getId());
		eventDataObject.setAnnonceDateId(botEvent.getAnnonceDate().getId());
		eventDataObject.setAnnonceCookerId(botEvent.getAnnonceCooker().getId());
		eventDataObject.setNextDate(botEvent.getNextDate());
		return eventDataObject;
	}
}
