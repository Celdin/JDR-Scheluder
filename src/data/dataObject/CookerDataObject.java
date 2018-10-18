package data.dataObject;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class CookerDataObject extends DataObject {
	private final static String TABLE_NAME = "COOKER";
	
	private String userId;
	private String eventId;
	private Integer haveCooked;
	
	@Override
	public String getTableName() {
		return TABLE_NAME;
	}
}
