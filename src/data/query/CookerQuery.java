package data.query;

import java.util.ArrayList;
import java.util.List;

import data.dataObject.CookerDataObject;
import net.dv8tion.jda.core.entities.TextChannel;

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

}
