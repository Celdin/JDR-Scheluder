package data.query;

import java.util.List;

import data.dataObject.DataObject;

public abstract class Query<T extends DataObject> {
	public abstract List<T> getAll();
}
