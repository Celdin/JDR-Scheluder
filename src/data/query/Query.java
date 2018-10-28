package data.query;

import java.util.List;

import data.dataObject.DataObject;
import data.domain.Event;

public abstract class Query<T extends DataObject> {
	public abstract List<T> getAll();
	public abstract Integer save(Event event);
}
