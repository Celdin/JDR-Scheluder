package data.dataObject;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class InvolvedDataObject extends DataObject {
	private final static String TABLE_NAME = "INVOLVED"; 
	
	private String userId;
	private String eventId;
	
	@Override
	public String getTableName() {
		return TABLE_NAME;
	}
}
