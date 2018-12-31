package data.dataObject;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class CookerDataObject extends DataObject {
	private final static String TABLE_NAME = "COOKER";

	public final static String COLUMN_USER_ID  = "USER_ID";
	public final static String COLUMN_EVENT_ID  = "EVENT_ID";
	public final static String COLUMN_HAVE_COOKED  = "HAVE_COOKED";

	private final static String COLUMN_USER_ID_TYPE  = "TEXT";
	private final static String COLUMN_EVENT_ID_TYPE  = "INT";
	private final static String COLUMN_HAVE_COOKED_TYPE  = "INT";

	
	private String userId;
	private Integer eventId;
	private Integer haveCooked;
	
	@Override
	public String getTableName() {
		return TABLE_NAME;
	}

	@Override
	public List<String> getColumnNames() {
		List<String> columnName = super.getColumnNames();
		columnName.addAll(Arrays.asList(COLUMN_USER_ID, COLUMN_EVENT_ID, COLUMN_HAVE_COOKED));
		return columnName;
	}

	@Override
	public Map<String, String> getColumnTypes() {
		Map<String, String> columnType = super.getColumnTypes();
		columnType.put(COLUMN_USER_ID, COLUMN_USER_ID_TYPE);
		columnType.put(COLUMN_EVENT_ID, COLUMN_EVENT_ID_TYPE);
		columnType.put(COLUMN_HAVE_COOKED, COLUMN_HAVE_COOKED_TYPE);
		return columnType;
	}

	@Override
	public Object get(String columnName) {
		Object value = super.get(columnName);
		if(value != null) return value;
		switch (columnName) {
		case COLUMN_USER_ID:
			return userId;
		case COLUMN_EVENT_ID:
			return eventId;
		case COLUMN_HAVE_COOKED:
			return haveCooked;
		default:
			return null;
		}
	}
	
	@Override
	public void set(Object object, String columnName) {
		super.set(object, columnName);
		switch (columnName) {
		case COLUMN_USER_ID:
			userId = (String) object;
			break;
		case COLUMN_EVENT_ID:
			eventId = (Integer) object;
			break;
		case COLUMN_HAVE_COOKED:
			haveCooked = (Integer) object;
			break;
		default:
			break;
		}
	}
}
