package data.query;

import static java.lang.String.format;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import data.dataObject.DataObject;
import driver.PostgreSQLDriver;
import lombok.Setter;

public abstract class Query<T extends DataObject> {
	private static final int MAX_IN_CLAUSE = 1000;
	
	@Setter
	private Supplier<T> supplier;
	
	public Query() {
		super();
	};
	
	public List<T> getAll() throws SQLException {
		List<T> dataObjects = new ArrayList<>();
		String query = 
				"SELECT * "
				+ "FROM "
				+ "	%s "
				+ ";";
		query = format(query, supplier.get().getTableName());
		
		Connection connection = PostgreSQLDriver.getConnection();
		Statement statement = connection .createStatement();
		ResultSet result = statement.executeQuery(query);
		while(result.next()) {
			T dataObject = supplier.get();
			for(String column : dataObject.getColumnNames()) {
				dataObject.set(result.getObject(column), column);
			}
			dataObjects.add(dataObject);
		}
		statement.close();
		result.close();
		return dataObjects;
	}

	protected static void insert(DataObject dataObject) throws SQLException {
		List<String> columnNames = new ArrayList<>(dataObject.getColumnNames());
		columnNames.remove(DataObject.COLUMN_ID);
		String query = 
				"INSERT INTO %s "
				+ "(" + String.join(", ", columnNames) + ")  VALUES (%s);";
		String values;
		List<String> valueList = new ArrayList<>();
		for(String column : columnNames) {
			Object value = dataObject.get(column);
			if(value != null) {
				if(value instanceof String) {
					valueList.add("'" + value.toString() + "'");
				} else {
					valueList.add(value.toString());
				}
			} else {
				valueList.add(null);
			}
		}
		values = String.join(", ", valueList);
		query = String.format(query, dataObject.getTableName(), values);
		Connection connection = PostgreSQLDriver.getConnection();
		Statement statement = connection.createStatement();
		System.out.println(query);
		statement.executeUpdate(query);
		statement.close();
	}
	protected static void update(DataObject dataObject) throws SQLException {
		String query = "UPDATE %s SET %s WHERE %s";
		String values;
		List<String> valueList = new ArrayList<>();
		for(String column : dataObject.getColumnNames()) {
			Object value = dataObject.get(column);
			if(value != null) {
				if(value instanceof String) {
					valueList.add(column + " = '" + value.toString() + "'");
				} else {
					valueList.add(column + " = " + value.toString());
				}
			}
		}
		values = String.join(", ", valueList);

		query = format(query, dataObject.getTableName(), values, DataObject.COLUMN_ID + " = " + dataObject.getId());
		Connection connection = PostgreSQLDriver.getConnection();
		Statement statement = connection.createStatement();
		System.out.println(query);
		statement.executeUpdate(query);
		statement.close();
	}
	
	protected static void delete(DataObject dataObject) throws SQLException {
		String query = "DELETE FROM %s WHERE %s = %s";
		query = format(query, dataObject.getTableName(), DataObject.COLUMN_ID, dataObject.getId());
		Connection connection = PostgreSQLDriver.getConnection();
		Statement statement = connection.createStatement();
		System.out.println(query);
		statement.executeUpdate(query);
		statement.close();
	}
	
	protected String buildInClause(List<?> objects, String parameter) {
		String clause = " " + parameter + " IN(%s) ";
		switch (objects.size()) {
		case 0:
			clause = " " + parameter + " IS NULL ";
			break;
		case 1:
			clause = " " + parameter + " =  ";
			Object x = objects.get(0);
			if(x instanceof String) {
				clause += "'" + objects.get(0).toString() + "'";
			} else {
				clause += objects.get(0).toString();
			}
			break;

		default:
			List<String> clauses = new ArrayList<>();
			for(int i = 0; i < objects.size(); i += MAX_IN_CLAUSE) {
				List<String> values = new ArrayList<>();
				for(Object object : objects.subList(i, Integer.min(objects.size(), i + MAX_IN_CLAUSE ))) {
					if(object instanceof String) {
						values.add("'" + object.toString() + "'");
					} else {
						values.add(object.toString());
					}
				}
				clauses.add(String.join(", ", values));
			}
			clause = String.format(clause, String.join(") OR " + parameter + " IN(", clauses));
			break;
		}
		return clause;
	}
}
