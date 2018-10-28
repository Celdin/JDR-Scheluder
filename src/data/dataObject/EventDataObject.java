package data.dataObject;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class EventDataObject extends DataObject {
	private final static String TABLE_NAME = "EVENT"; 
	
	private String channelId;
	private Long nextDate;
	private String annonceDateId;
	private String annonceCookerId;
	
	@Override
	public String getTableName() {
		return TABLE_NAME;
	}
}
