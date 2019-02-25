package data.dataObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class DataObject  implements Comparable<DataObject>{
	public final static String COLUMN_ID  = "ID";

	protected final static String COLUMN_ID_TYPE  = "SERIAL PRIMARY KEY";
	Integer id;
	
	public abstract String getTableName(); 
	
	public Object get(String columnName) {
		switch (columnName) {
		case COLUMN_ID:
			return id;
		default :
			return null;
		}
	}
	
	public void set(Object object, String columnName) {
		if(COLUMN_ID.equals(columnName)) {
			id = (Integer) object;
		}
	}
	
	public List<String> getColumnNames() {
		List<String> tableName = new ArrayList<>();
		tableName.add(COLUMN_ID);
		return tableName;
	}
	public Map<String, String> getColumnTypes(){
		Map<String, String> tableType = new HashMap<>();
		tableType.put(COLUMN_ID, COLUMN_ID_TYPE);
		return tableType;
	}
}
