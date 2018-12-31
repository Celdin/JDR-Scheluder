package driver;

import static java.lang.String.format;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import org.junit.platform.commons.util.StringUtils;

import data.dataObject.CookerDataObject;
import data.dataObject.DataObject;
import data.dataObject.EventDataObject;

public class PostgreSQLDriver {
	private static Connection connection;
	private static String url;
	private static String user;
	private static String password;
	private static List<DataObject> tables = Arrays.asList(new EventDataObject(), new CookerDataObject());
	
	public static void initialise(String url, String user, String password) {
		PostgreSQLDriver.url = url;
		PostgreSQLDriver.user = user;
		PostgreSQLDriver.password = password;
	}

	public static Connection getConnection() throws SQLException{
		if(connection == null) {
			try {
				Class.forName("org.postgresql.Driver");
				connect();
			} catch (ClassNotFoundException e) {
				System.err.println("Add PgSQL driver");
				e.printStackTrace();
			}
		}
		return connection;
	}

	private static void connect() throws SQLException {
		if(url != null) {
			connection = DriverManager.getConnection("jdbc:postgresql://" + url + "?sslmode=require", user, password);
		} else {
			String dbUrl = System.getenv("JDBC_DATABASE_URL");
			connection = DriverManager.getConnection(dbUrl);
		}
		System.out.println("PostgreSQLDriver.connect");
	}
	
	public static void close() throws SQLException {
		connection.close();
		connection = null;
		System.out.println("PostgreSQLDriver.close");
	}
	
	public static void ckeckDatabase() throws SQLException {
		getConnection();
		for(DataObject table : tables) {
			if(tableExist(table.getTableName())) {
				checkColumns(table);
			}else {
				createTable(table);
			}
		}
		close();
	}

	private static void checkColumns(DataObject table) throws SQLException {
		List<String> actualColumns = getActualColumns(table.getTableName());
		List<String> columsToRemove = new ArrayList<>(actualColumns);
		List<String> columsToAdd = new ArrayList<>(table.getColumnNames());
		columsToRemove.removeAll(table.getColumnNames());
		columsToAdd.removeAll(actualColumns);
		if(!columsToRemove.isEmpty() || !columsToAdd.isEmpty()) {
			alterTable(columsToRemove, columsToAdd, table);
		}
	}

	private static void alterTable(List<String> columsToRemove, List<String> columsToAdd, DataObject table) throws SQLException {
        String query = "ALTER TABLE %s "
        		+ "%s;";
        String columnQueryPart = "";
        List<String> columns = new ArrayList<>();
        for(String column : columsToRemove) {
        	columns.add("DROP COLUMN " + column);
        }
        for(String column : columsToAdd) {
        	columns.add("ADD COLUMN " + column + " " + table.getColumnTypes().get(column));
        }
        columnQueryPart = String.join(", ", columns);
        query = format(query, table.getTableName(), columnQueryPart);
		
		System.out.println("PostgreSQLDriver.createTable Query :\n" + query);
		Statement statement = connection.createStatement();
		statement.executeUpdate(query);
		statement.close();
		
		System.out.println("PostgreSQLDriver.createTable " + table.getTableName() + " modified successfully");
	}

	private static List<String> getActualColumns(String tableName) throws SQLException {
		List<String> columns = new ArrayList<>();
		String query = 
				"SELECT COLUMN_NAME "
				+ "FROM "
				+ "	INFORMATION_SCHEMA.COLUMNS "
				+ "WHERE "
				+ "		TABLE_NAME = '%s' "
				+ ";";
		query = format(query, tableName.toLowerCase());
		
		System.out.println("PostgreSQLDriver.tableExist Query :\n" + query);
		Statement statement = connection.createStatement();
		ResultSet result = statement.executeQuery(query);
		while(result.next()) {
			columns.add(result.getString("COLUMN_NAME").toUpperCase());
		}
		statement.close();
		result.close();
		return columns;
	}

	private static void createTable(DataObject table) throws SQLException {
        String query = "CREATE TABLE %s ( "
        		+ "%s);";
        String columnQueryPart = "";
        List<String> columns = new ArrayList<>();
        for(Entry<String, String> column : table.getColumnTypes().entrySet()) {
        	columns.add(column.getKey() + " " + column.getValue());
        }
        columnQueryPart = String.join(", ", columns);
        query = format(query, table.getTableName(), columnQueryPart);
		
		System.out.println("PostgreSQLDriver.createTable Query :\n" + query);
		Statement statement = connection.createStatement();
		statement.executeUpdate(query);
		statement.close();
		
		System.out.println("PostgreSQLDriver.createTable " + table.getTableName() + " created successfully");
	}
	
	private static boolean tableExist(String tableName) throws SQLException {
		String query = 
				"SELECT EXISTS ("
				+ "	SELECT 1"
				+ "	FROM"
				+ "		INFORMATION_SCHEMA.TABLES"
				+ "	WHERE"
				+ "		TABLE_NAME = '%s'"
				+ ");";
		query = format(query, tableName.toLowerCase());
		
		System.out.println("PostgreSQLDriver.tableExist Query :\n" + query);
		Statement statement = connection.createStatement();
		ResultSet result = statement.executeQuery(query);
		result.next();
		boolean exists = result.getBoolean("EXISTS");
		statement.close();
		result.close();
		
		return exists;
	}
}
