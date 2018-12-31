package data.query;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import data.dataObject.CookerDataObject;
import data.dataObject.EventDataObject;
import data.domain.Event;
import driver.PostgreSQLDriver;
import net.dv8tion.jda.core.entities.User;

public class CookerQuery extends Query<CookerDataObject> {

	public List<CookerDataObject> getCookerByEventId(Integer eventId) throws SQLException {
		ArrayList<CookerDataObject> cookerDOs = new ArrayList<>();
		String query =
				"SELECT "
				+ " * "
				+ "FROM "
				+ "	%s "
				+ "WHERE "
				+ "	%s = %s";
		CookerDataObject dataObject = new CookerDataObject();
		query = String.format(query, dataObject.getTableName(), CookerDataObject.COLUMN_EVENT_ID, eventId);
		Connection connection = PostgreSQLDriver.getConnection();
		Statement statement = connection.createStatement();
		System.out.println(query);
		ResultSet result = statement.executeQuery(query);
		while(result.next()) {
			CookerDataObject cookerDataObject = new CookerDataObject();
			for(String column : dataObject.getColumnNames()) {
				cookerDataObject.set(result.getObject(column), column);
			}
			cookerDOs.add(cookerDataObject);
		}
		return cookerDOs;
	}

	public void deleteEvent(String channelId) throws SQLException {
		EventQuery eventQuery = new EventQuery();
		EventDataObject event = eventQuery.getEventByChannelId(channelId);
		String query = "DELETE FROM %s WHERE %s = %s";
		query = String.format(query, new CookerDataObject().getTableName(), CookerDataObject.COLUMN_EVENT_ID, event.getId());
		Connection connection = PostgreSQLDriver.getConnection();
		Statement statement = connection.createStatement();
		System.out.println(query);
		statement.executeUpdate(query);
		statement.close();
	}
	
	public void save(Integer eventId, Event event) throws SQLException {
		List<CookerDataObject> cookerDataObjects = getCookerByEventId(eventId);
		for(Entry<User, Integer> entry : event.getHaveCooked().entrySet()) {
			CookerDataObject toSave = null;
			for(CookerDataObject cookerDataObject : cookerDataObjects) {
				if(cookerDataObject.getUserId().equals(entry.getKey().getId())) {
					toSave = cookerDataObject;
				}
			}
			if(toSave == null) {
				toSave = new CookerDataObject();
				toSave.setEventId(eventId);
				toSave.setUserId(entry.getKey().getId());
				toSave.setHaveCooked(entry.getValue());
				insert(toSave);
			} else {
				toSave.setHaveCooked(entry.getValue());
				update(toSave);
			}
		}
	}

}
