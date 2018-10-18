package data.dataObject;

import lombok.Data;

@Data
public abstract class DataObject {
	Integer Id;
	
	public abstract String getTableName(); 
}
