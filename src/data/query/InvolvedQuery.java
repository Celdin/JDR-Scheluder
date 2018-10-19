package data.query;

import java.util.ArrayList;
import java.util.List;

import data.dataObject.InvolvedDataObject;

public class InvolvedQuery extends Query<InvolvedDataObject> {

	@Override
	public List<InvolvedDataObject> getAll() {
		List<InvolvedDataObject> involvedDOs = new ArrayList<InvolvedDataObject>();
		return involvedDOs;
	}

	public List<InvolvedDataObject> getInvolvedByEventId(Integer eventId) {
		List<InvolvedDataObject> involvedDOs = new ArrayList<InvolvedDataObject>();
		return involvedDOs;
	}

}
