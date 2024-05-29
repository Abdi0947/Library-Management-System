package lms;

import java.awt.*;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

public class DatabaseQueries {


	public static Component results;

	public static void printFromDatabase(Connection connection, String selectQuery, String[] columns) {	// previously readFromDatabase
		
		try {

	        //Run query
			PreparedStatement preparedStatement = connection.prepareStatement(selectQuery, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			// allows pointer to move forward and backward in the query; does not allow for updates
			// https://docs.oracle.com/javase/6/docs/api/java/sql/ResultSet.html
			
			//execute query and get result set
			ResultSet resultSet = preparedStatement.executeQuery();
			
			DatabaseQueries.printResultSetinWindow(resultSet, columns);
			
			resultSet.close();
			preparedStatement.close();
			
		} catch (SQLException SQLe) {
			SQLe.printStackTrace();
		}
	}
	

	private static void printResultSetinWindow(ResultSet resultSet, String[] columns) {
				
		try {
			//point at last entry/row
			resultSet.last();
			// row number (if there are none, this will be 0)
			int numRows = resultSet.getRow();
			
			// when there are results returned
			if(numRows != 0) {
				
				//point at last entry/rows
				resultSet.last();
				// get row value
				numRows = resultSet.getRow();
				// point back to before the first entry/row
			    resultSet.beforeFirst();
				
		        // Create the appropriately sized array for your results
			    // 1 extra row for column (field) headers
		    	String[][] results = new String[numRows+1][columns.length];
		    	
		    	// add column headers to 0th row of results array
		    	
		    	for (int cl = 0; cl < columns.length; cl++) {
		    		results[0][cl] = columns[cl];
		    	}
		    	
		    	
				//Put results in array
		        for(int row = 1; row <= numRows; row++){
		        	
		        	int col = 0;
		        	
		        	if(resultSet.next()){	// if there are results to read/point at
		        		// Get the column values via column headers (String value)
			        	// only use the specified column headers
		        		for(String column: columns){
		        			
		        			// AVAILABLE COLUMN
		        			if (column.equals("Available")) {
		        				// availability is opposite of checkedOut
		        				Boolean available = resultSet.getBoolean(column);
		        				if (available) {
		        					results[row][col] = "Available";
		        				} else {
		        					results[row][col] = "Not Available";
		        				}
		        			// FINE COLUMN: format to display $ and 2 decimal places
		        			} else if (column.equals("Fine")){
		        				double fineAmount = resultSet.getDouble(column);
		        				results[row][col] = "$" + String.format("%2.2f",fineAmount);		// ASSUMPTION: hopefully no one owes more than $ 99.75 in fines!
		        			
		        			// DUE (date) COLUMN:  format to display day of week and month (MMM) and day (DD)
		        			} else if (column.equals("Due")){
		        				Date dueDate = resultSet.getDate(column);
		        				SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd");
		        				results[row][col] = dateFormat.format(dueDate);
		        			
		        			} else {
		        			
		        				results[row][col] = resultSet.getString(column);
		        			}
		        			
			       			col++;
			       		}
		        	}
		        }
							
		        // show in window using method
		        displayInWindow(results);    
				
			} else {		// not results returned
				JOptionPane.showMessageDialog(null, "There are no entries to display for the given search criteria.", "Query Results", JOptionPane.INFORMATION_MESSAGE);
			}
			
		} catch (SQLException SQLe) {
				SQLe.printStackTrace();
		}
		
	}
	

	public static void displayInWindow(String[][] results) {
		JFrame frame = new JFrame();	// create window
		
		//panel for results
		JPanel output = new JPanel();		// create content
		//output.setBackground(Color.WHITE);
		GridLayout layout = new GridLayout(results.length,results[0].length,5,0);
		output.setLayout(layout);
	
		//add titles --> ******** see if we can make these bold or something? *************
		for (int k = 0; k < results[0].length; k ++) {
			JLabel header = new JLabel(results[0][k]);
			header.setHorizontalAlignment(SwingConstants.CENTER);
			header.setForeground(Color.black);
			header.setFont(new Font("Arial", Font.BOLD, 15));
			output.add(header);
		}
		
		//display table entries (the real results)
		for (int r = 1; r < results.length; r++){
			
			for (int c = 0; c < results[r].length; c++){
				JLabel entry = new JLabel(results[r][c]);
				entry.setForeground(new Color(140,40,40));
				
				if (r%2==0) {
					entry.setForeground(new Color(1,20,110));
					//entry.setBackground(new Color (69,69,69));
				}
				//entry.setBorder(new LineBorder(Color.BLACK));
				output.add(entry);	// add to panel (content)
			}
			
		}
		
		frame.add(output);	// add panel/content to frame
		
		
		
		frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setTitle("Search Results");
		frame.setVisible(true);
	}
		

	public static String[][] readFromDatabase(Connection connection, String selectQuery, String[] columns) {
		
		try {

	        //Run query
			PreparedStatement preparedStatement = connection.prepareStatement(selectQuery, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			// allows pointer to move forward and backward in the query; does not allow for updates
			// https://docs.oracle.com/javase/6/docs/api/java/sql/ResultSet.html
			
			//execute query and get result set
			ResultSet resultSet = preparedStatement.executeQuery();
			
			String[][] results = getResultSetArray(resultSet, columns);
			
			resultSet.close();
			preparedStatement.close();
			return results;
			
		} catch (SQLException SQLe) {
			SQLe.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Converts given ResultSet to String[][] array based on passed column names.
	 * @param resultSet: ResultSet (Object) to print
	 * @param columns: Array of Strings (column headers) corresponding to the columns that will be printed
	 * @return results: 2D array of Strings containing the results from a Select query; if no results returned, returns *null*!
	 */
	private static String[][] getResultSetArray(ResultSet resultSet, String[] columns) {
				
		try {
			//point at last entry/row
			resultSet.last();
			// row number (if there are none, this will be 0)
			int numRows = resultSet.getRow();
			
			// when there are results returned
			if(numRows != 0) {
				
				//point at last entry/rows
				resultSet.last();
				// get row value
				numRows = resultSet.getRow();
				// point back to before the first entry/row
			    resultSet.beforeFirst();
				
		        // Create the appropriately sized array for your results
			    // 1 extra row for column (field) headers
		    	String[][] results = new String[numRows+1][columns.length];
		    	
		    	// add column headers to 0th row of results array
		    	
		    	for (int cl = 0; cl < columns.length; cl++) {
		    		results[0][cl] = columns[cl];
		    	}
		    	
		    	
				//Put results in array
		        for(int row = 1; row <= numRows; row++){
		        	
		        	int col = 0;
		        	
		        	if(resultSet.next()){	// if there are results to read/point at
		        		// Get the column values via column headers (String value)
			        	// only use the specified column headers
		        		for(String column: columns){
		        			
		        			// AVAILABLE COLUMN
		        			if (column.equals("Available")) {
		        				// availability is opposite of checkedOut
		        				Boolean available = resultSet.getBoolean(column);
		        				if (available) {
		        					results[row][col] = "Available";
		        				} else {
		        					results[row][col] = "Not Available";
		        				}
		        			// FINE COLUMN: format to display $ and 2 decimal places
		        			} else if (column.equals("Fine")){
		        				double fineAmount = resultSet.getDouble(column);
		        				results[row][col] = "$" + String.format("%2.2f",fineAmount);		// ASSUMPTION: hopefully no one owes more than $ 99.75 in fines!
		        			
		        			// DUE (date) COLUMN:  format to display day of week and month (MMM) and day (DD)
		        			} else if (column.equals("Due")){
		        				Date dueDate = resultSet.getDate(column);
		        				SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd");
		        				results[row][col] = dateFormat.format(dueDate);
		        			
		        			} else {
		        			
		        				results[row][col] = resultSet.getString(column);
		        			}
		        			
			       			col++;
			       		}
		        	}
		        }
							
		        return results;
		        
				
			} else {		// not results returned
				return null;
			}
			
		} catch (SQLException SQLe) {
				SQLe.printStackTrace();
				return null;
		}
		
	}
	
	/**
	 * prints CSV version of results to the console.
	 * @param results: 2D String array containing results of SELECT query
	 */
	public static void consoleDisplay(String[][] results) {
		
		for (int r = 0; r < results.length; r++) {
			// print first column [0] entry
			System.out.print(results[r][0]);
			for (int c = 1; c < results[r].length; c++) {
				System.out.print(", " + results[r][c]);
			}
			System.out.print("\n");
		}
	}
	
	/**
	 * Adds a row of values to the given table.
	 * We assume that the column values will always be in the correct/same order every time that this method is called.
	 * Each table has it's own case for creating the query (String) with actual values/information/data or default, as necessary/appropriate/applicable.
	 * @param connection: Connection (Object) to use for database
	 * @param table: Name of table to add to
	 * @param values: Values for each column of table
	 */
	public static void addToTable(Connection connection, String table, String[] values) {		
		
		String insertQuery = "INSERT INTO " + table + " VALUES (";
		
		// because all the tables are different and not all Strings, each one needs a separate add section and we can't use easy for-loops :(
		// yay for hard-coding! It also makes more sense to just construct a robust string vs. making a string with "?"'s and then updating the PreparedStatement
		// If the array values does not have the proper length for a given table; prints a message and exits the method
		
		if (table.equals("books")) {
			
			// we assume that for the purposes of this program, all books will get the default loan length and daily fine amount
			if (values.length != 3) {	// values array should have length 3 for title [0], author [1], and genre [2]
				JOptionPane.showMessageDialog(null, "Could not add row to table " + table + " due to improper number of entry values.", "Improper Entry", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			
			insertQuery += "default,\"" + values[0] 	// title 
					+ "\",\"" + values [1] 				// author
							+ "\",\"" + values[2] 		// genre
									+ "\",default,default,default,default);";
			  
		} else if (table.equals("patrons")) {
			
			// we assume that for the purposes of this program, all patrons will get the default maxBooksOut and maxHolds values
			if (values.length != 2 ){ // values array should have length 2 for first name [0] and last name [1]
				JOptionPane.showMessageDialog(null, "Could not add row to table " + table + " due to improper number of entry values.", "Improper Entry", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			
			insertQuery += "default,\"" + values[0] 	// first name
					+ "\",\"" + values[1]				// last name 
							+ "\",default,default,default,default,default);";
			  
		} else {	// table = "holds" or table = "checkouts"
			
			if (values.length != 3) {// values array should have length 3 for book_ID [0], patron_ID [1], date [2]
				JOptionPane.showMessageDialog(null, "Could not add row to table " + table + " due to improper number of entry values.", "Improper Entry", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			
			// This method is called for adding holds or checkouts from a patron, so we know the foreign key patron_ID will exist in the patrons table;
			// the code to ensure that the book_ID exists in the books table will occur prior to calling this method.
			// Therefore, we will assume that by this point/for this method, addition to the table will be possible.
			
			insertQuery += "default," + values[0]	// book_ID (in the table it's a number, so it is not necessary to pass quotations for it 
					+ "," + values[1]				// patron_ID
							+ ",\"" + values[2]		// date (as a string)
									+ "\");";
		}
		
		//System.out.println(insertQuery);
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
			
			int rowsAffected = writeToDatabase(preparedStatement);
			//JOptionPane.showMessageDialog(null, rowsAffected + " entry added to " + table, "Insert Results", JOptionPane.INFORMATION_MESSAGE);
			
		} catch (SQLException SQLe){
			SQLe.printStackTrace();
		}		
		
	}
	
	/**
	 * Remove a row/entry from the given table using the condition passed (`keyword` = criterion)
	 * @param connection: Connection (Object) to use for database
	 * @param table: Name of table to remove from
	 * @param keyword: Name column to apply criterion to / use for condition (will always be `book_ID` or `patron_ID`)
	 * @param criterion: ID number of desired row (input from user) [***If for some reason the criterion is for a varchar column, the \" 's will have to be passed, too as the query constructor does NOT include those.]
	 */
	public static void removeFromTable(Connection connection, String table, String keyword, String criterion) {
		
		String removeQuery = "DELETE FROM " + table + " WHERE " + keyword + " = " + criterion + ";";
		//System.out.println(removeQuery);
		
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(removeQuery);
			int rowsAffected = writeToDatabase(preparedStatement);
			
			//JOptionPane.showMessageDialog(null, rowsAffected + " entry(ies) removed from " + table, "Removal Results", JOptionPane.INFORMATION_MESSAGE);
		} catch (SQLException SQLe) {
			SQLe.printStackTrace();
		}		
		
	}
	
	/**
	 * Updates a column's value for a row/entry in the given table using the condition passed (`keyword` = criterion).
	 * @param connection: Connection (Object) to use for database
	 * @param table: Name of table to update
	 * @param column: Name column to update value for
	 * @param value: Value to change above column to
	 * @param keyword: Name of column to apply criterion to / use for condition (will always be `book_ID` or `patron_ID`)
	 * @param criterion: ID number of desired row (input from user) [***If for some reason the criterion is for a varchar column, the \" 's will have to be passed, too as the query constructor does NOT include those.]
	 */
	public static void updateColumn(Connection connection, String table, String column, String value, String keyword, String criterion) {
		String changeValQuery = "UPDATE " + table + " SET " + column + " = " + value + " WHERE " + keyword + " = " + criterion + ";";
		//System.out.println(changeValQuery);
		
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(changeValQuery);
			int rowsAffected = writeToDatabase(preparedStatement);	// will return 0 if no change made; no harm, no foul
			//JOptionPane.showMessageDialog(null, rowsAffected + " entry updated in " + table, "Update Results", JOptionPane.INFORMATION_MESSAGE);
		} catch (SQLException SQLe) {
			SQLe.printStackTrace();
		}
		
	}
	
	/**
	 * Writes to a database using the passed PreparedStatement--executes update and then closes.
	 * Other methods must construct and pass the specific PreparedStatement necessary to perform the desired action.
	 * @param preparedStatement: PreparedStatement to execute
	 * @return rowsAffected:  The number of rows affected by the update.
	 */
	public static int writeToDatabase(PreparedStatement preparedStatement) {
		int rowsAffected = 0;
		try {
			rowsAffected = preparedStatement.executeUpdate();
			preparedStatement.close();
		} catch (SQLException SQLe) {
			SQLe.printStackTrace();
		}
		
		return rowsAffected;
	}

	/**
	 * Converts current (today's) date to String with specified format.
	 * @return strDate: today's date in the form "YYYY-MM-dd" (e.g. 2020-12-15 for Dec. 15th, 2020)
	 */
	public static String getTodaysDateAsString() {
		// CONVERT DATE TO STRING IN GIVEN FORMAT
		// FROM :  https://www.javatpoint.com/java-date-to-string
		java.util.Date date = java.util.Calendar.getInstance().getTime();  
		java.text.DateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd");  
		String strDate = dateFormat.format(date);
		return strDate;
	}
	
	/**
	 * Determines if a book-patron pair already exists in the holds or checkouts table.
	 * Similar to namePairExists but with different search parameters (book_ID and patron_ID).
	 * @param connection: Connection (Object) to use for database
	 * @param table: String name of table to be checked; `holds` or `checkouts` only
	 * @param book_ID: String representation of book_ID in question
	 * @param patron_ID: String representation of patron_ID in question
	 * @return boolean value: true if the pair exists; false otherwise
	 */
	public static boolean bookPatronPairExists(Connection connection, String table, String book_ID, String patron_ID) {
		String selectQuery = "SELECT COUNT(*) FROM " + table + " WHERE book_ID = " + book_ID + " AND patron_ID = " + patron_ID + ";";
		
		String[][]results = readFromDatabase(connection, selectQuery, new String[] {"COUNT(*)"});		// column "count" is just a placeholder; returns a 2x1 array with desired value in row index [1]
		if (Integer.parseInt(results[1][0]) > 0) {	// in case some how there are duplicates
			return true;	// pair exists
		} else { return false; }
		
	}
	
	/**
	 * Determines if a book-patron pair already exists in the holds or checkouts table.
	 * Similar to bookPatronPair but with different search parameters (firstName, lastName)
	 * @param connection: Connection (Object) to use for database
	 * @param table: String name of table to be checked; `patrons` only
	 * @param firstName: first name in question
	 * @param lastName: last name in question
	 * @return boolean value: true if the pair exists; false otherwise
	 */
	public static boolean namePairExists(Connection connection, String table, String firstName, String lastName) {
		String selectQuery = "SELECT COUNT(*) AS COUNT FROM " + table + " WHERE firstName = '" + firstName + "' AND lastName = '" + lastName + "';";

		String[][]results = readFromDatabase(connection, selectQuery, new String[] {"COUNT"});		// column "count" is just a placeholder; returns a 2x1 array with desired value in row index [1]
		System.out.println(results[1][0]);
		if (Integer.parseInt(results[1][0]) > 0) {
			return true;	
		} else { 
			return false; 
		}

	}
	
	/**
	 * Determines if a book-patron pair already exists in the holds or checkouts table.
	 * Similar to bookPatronPair but with different search parameters (firstName, lastName)
	 * @param connection: Connection (Object) to use for database
	 * @param table: String name of table to be checked; `patrons` only
	 * @param id: patron id in question
	 * @return boolean value: true if the pair exists; false otherwise
	 */
	public static boolean patronExists(Connection connection, String table, String id) {
		String selectQuery = "SELECT COUNT(*) AS COUNT FROM " + table + " WHERE patron_ID = '" + id + "';";

		String[][]results = readFromDatabase(connection, selectQuery, new String[] {"COUNT"});		// column "count" is just a placeholder; returns a 2x1 array with desired value in row index [1]
		System.out.println(results[1][0]);
		if (Integer.parseInt(results[1][0]) > 0) {
			return true;	
		} else { 
			return false; 
		}

	}
	
	/**
	 * Gets the most recent (highest) id available from a given table (representing the id of the last item added)
	 * @param connection: Connection (Object) to use for database
	 * @param table: String name of table to query; either `holds` or `checkouts`
	 * @param idName: name of ID column in table
	 * @return ID value as String
	 */
	public static String getLastID(Connection connection, String table, String idName) {
		String selectQuery = "SELECT MAX(" + idName + ") AS id FROM " + table;

		String[][] results = readFromDatabase(connection, selectQuery, new String[] {"id"});		
		// column "id" is just a placeholder; returns a 2x1 array with desired value in row index [1]

		return results[1][0]; 	// value (as String) of last ID
	}
	
	/**
	 * Gets a book's title (from the `books` table) based on the passed ID.
	 * @param connection: Connection (Object) to use for database
	 * @param book_ID: String representation of book_ID in question
	 * @return title as a String
	 */
	public static String getBookTitle(Connection connection, String book_ID) {
		String selectQuery = "SELECT title FROM books WHERE book_ID = " + book_ID + ";";
		String[][] results = readFromDatabase(connection, selectQuery, new String[] {"title"});		// column header is a place holder; returns 2x1 array with desired value in row index [1]
		
		return results[1][0];	// indeces of title String
	}
	
	/**
	 * Check whether the passed book ID exists in a table.
	 * @param connection: Connection (Object) to use for database
	 * @param book_ID: String representation of book_ID in question
	 * @return boolean value: true if the ID exists; false otherwise
	 */
	public static boolean checkForBook(Connection connection, String table, String book_ID) {
		String selectQuery = "SELECT COUNT(*) FROM " + table + " WHERE book_ID = " + book_ID + ";";
		String[][]results = readFromDatabase(connection, selectQuery, new String[] {"COUNT(*)"});		// column "count" is just a placeholder; returns a 2x1 array with desired value in row index [1]
		if (Integer.parseInt(results[1][0]) > 0) {	// used for all tables with book IDs, so there could be multiple
			return true;
		} else { return false; }
	}

	/**
	 * Gets the table ID corresponding to specific book and patron IDs (assumes that there is ONLY 1 instance of each tuple // no duplicates;
	 * in the case of duplicates, coding will pick the earliest-entered ID value (highest in table)).
	 * @param connection: Connection (Object) to use for database
	 * @param table: String name of table to query; either `holds` or `checkouts`
	 * @param book_ID: String representation of book_ID in question
	 * @param patron_ID: String representation of patron_ID in question
	 * @return ID value as String
	 */
	public static String getID(Connection connection, String table, String book_ID, String patron_ID) {
		String selectQuery = "SELECT ";
		// enter column name for ID based on table
		if (table.equals("holds")) {
			selectQuery += "hold_ID AS id ";
		} else { // "checkouts"
			selectQuery += "chkO_ID AS id ";
		}
		
		selectQuery += "FROM " + table + " WHERE book_ID = " + book_ID + " AND patron_ID = " + patron_ID + ";";
		String[][] results = readFromDatabase(connection, selectQuery, new String[] {"id"});		// column "id" is just a placeholder; returns a 2x1 array with desired value in row index [1]
		
		return results[1][0]; 	// value (as String) of hold_ID or check-out ID (chkO_ID)
	}
	
	/** Determines status of book's availability and returns corresponding boolean value. 
	 * @param connection: Connection (Object) to use for database
	 * @param book_ID: String of book ID for book in question
	 * @return boolean: true if not checked out = Available; false otherwise.
	 */
	public static boolean bookAvailable(Connection connection, String book_ID) {
		String selectQuery = "SELECT NOT checkedOut AS Available FROM books WHERE book_ID = " + book_ID + ";";
		String[][] results = readFromDatabase(connection, selectQuery, new String[] {"Available"});	// column header is placehold; returns 2x1 array with desired result in row index [1]
		
		if (results[1][0].equals("Available")) {
			return true;
		} else {
			return false;
		}
	}
	
}