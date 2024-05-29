package lms;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Manages database connection
 */
public class DatabaseConnection {
	
	/**
	 * JDBC database connection String.
	 */
	private static String url = "jdbc:mysql://127.0.0.1:3307/librarymanagementsystem";
	
	private static String username = "root";
	private static String password = "";
	//private static String otherPassword = "d@nNsH3rM2020";
	
	/**
	 * Opens database connection
	 * @return DB connection
	 */
	public static Connection openDatabase() {
		Connection connection = null;
		
		try {
			// load the appropriate MySQL driver
			Class.forName("com.mysql.cj.jdbc.Driver");
			
			// create connection using JDBC driver
			connection = DriverManager.getConnection(
					DatabaseConnection.url,
					DatabaseConnection.username,
					DatabaseConnection.password);
		} catch (ClassNotFoundException CNFe) {
			CNFe.printStackTrace();
		} catch (SQLException SQLe) {
			SQLe.printStackTrace();
		}
		
		return connection;
	}
	
	/**
	 * Closes given database connection.
	 * @param connection - connection to be closed
	 */
	public static void closeDatabase(Connection connection) {
		try {
			connection.close();
		} catch (SQLException SQLe){
			SQLe.printStackTrace();
		}
	}

}
