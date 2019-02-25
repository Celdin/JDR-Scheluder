package data.dataObject;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class EventDataObject extends DataObject {
	public final static String TABLE_NAME = "EVENT"; 
	public final static String COLUMN_CHANNEL_ID  = "CHANNEL_ID";
	public final static String COLUMN_NEXT_DATE  = "NEXT_DATE";
	public final static String COLUMN_ANNONCE_DATE_ID  = "ANNONCE_DATE_ID";
	public final static String COLUMN_ANNONCE_COOKER_ID  = "ANNONCE_COOKER_ID";

	private final static String COLUMN_CHANNEL_ID_TYPE  = "TEXT";
	private final static String COLUMN_NEXT_DATE_TYPE  = "BIGINT";
	private final static String COLUMN_ANNONCE_DATE_ID_TYPE  = "TEXT";
	private final static String COLUMN_ANNONCE_COOKER_ID_TYPE  = "TEXT";
	
	private String channelId;
	private Long nextDate;
	private String annonceDateId;
	private String annonceCookerId;
	
	@Override
	public String getTableName() {
		return TABLE_NAME;
	}

	@Override
	public List<String> getColumnNames() {
		List<String> columnName = super.getColumnNames();
		columnName.addAll(Arrays.asList(COLUMN_CHANNEL_ID, COLUMN_NEXT_DATE, COLUMN_ANNONCE_DATE_ID, COLUMN_ANNONCE_COOKER_ID));
		return columnName;
	}

	@Override
	public Map<String, String> getColumnTypes() {
		Map<String, String> columnType = super.getColumnTypes();
		columnType.put(COLUMN_CHANNEL_ID, COLUMN_CHANNEL_ID_TYPE);
		columnType.put(COLUMN_NEXT_DATE, COLUMN_NEXT_DATE_TYPE);
		columnType.put(COLUMN_ANNONCE_DATE_ID, COLUMN_ANNONCE_DATE_ID_TYPE);
		columnType.put(COLUMN_ANNONCE_COOKER_ID, COLUMN_ANNONCE_COOKER_ID_TYPE);
		return columnType;
	}

	@Override
	public Object get(String columnName) {
		Object value = super.get(columnName);
		if(value != null) return value;
		switch (columnName) {
		case COLUMN_CHANNEL_ID:
			return channelId;
		case COLUMN_NEXT_DATE:
			return nextDate;
		case COLUMN_ANNONCE_DATE_ID:
			return annonceDateId;
		case COLUMN_ANNONCE_COOKER_ID:
			return annonceCookerId;
		default:
			return null;
		}
	}
	
	@Override
	public void set(Object object, String columnName) {
		super.set(object, columnName);
		switch (columnName) {
		case COLUMN_CHANNEL_ID:
			channelId = (String) object;
			break;
		case COLUMN_NEXT_DATE:
			nextDate = (Long) object;
			break;
		case COLUMN_ANNONCE_DATE_ID:
			annonceDateId = (String) object;
			break;
		case COLUMN_ANNONCE_COOKER_ID:
			annonceCookerId = (String) object;
			break;
		default:
			break;
		}
	}

	@Override
	public int compareTo(DataObject o) {
		return 0;
	}
}
