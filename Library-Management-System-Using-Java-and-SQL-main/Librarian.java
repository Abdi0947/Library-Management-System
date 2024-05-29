package lms;
import java.sql.Connection;



public class Librarian {

	public static void addBook(Connection connection,  String table, String title, String author, String genre) {
		
		// create String array to pass into the INSERT INTO query as values
		String[] values = {title, author, genre};
		
		// Call addToTable() with value array
		DatabaseQueries.addToTable(connection, table, values);
		
	}
	

	public static void removeBook(Connection connection,  String table, int bookID) {
		
		// Create the keyword, or the column we are matching against our ID
		String keyword = "book_ID";
		
		// Create the criterion, which is the specific ID we are passing in. Cast it to a string to work with the generalized
		// removeFromTable method
		String criterion = Integer.toString(bookID);
		
		// Call removeFromTable to construct the DELETE query
		DatabaseQueries.removeFromTable(connection, table, keyword, criterion);
		
	}
	

	public static void removeBookFromAllTables(Connection connection, int bookID) {
		
		// Remove book from holds and checkouts tables first, then remove from books table
		removeBook(connection, "holds" , bookID);
		removeBook(connection, "checkouts" , bookID);
		removeBook(connection, "books" , bookID);
		
	}
	

	public static void removePatron(Connection connection,  String table, int patronID) {
		
		// Create the keyword, or the column we are matching against our ID
		String keyword = "patron_ID";
		
		// Create the criterion, which is the specific ID we are passing in. Cast it to a string to work with the generalized
		// removeFromTable method
		String criterion = Integer.toString(patronID);
		
		// Call removeFromTable to construct the DELETE query
		DatabaseQueries.removeFromTable(connection, table, keyword, criterion);
	}
	

	public static void removePatronFromAllTables(Connection connection, int patronID) {
		
		// Remove from holds and checkouts tables first, then remove from patrons table
		removeBook(connection, "holds" , patronID);
		removeBook(connection, "checkouts" , patronID);
		removeBook(connection, "patrons" , patronID);
		
	}
	

	public static void getHoldsList(Connection connection, String holdsView, String keyword, String criterion) {

		String getHoldsQuery = "SELECT *"
				+ " FROM " + holdsView
				+ " WHERE " + keyword + "= \"" + criterion + "\" "
				+ " ORDER BY firstName, lastName, author, title;";
		
		// Create array of columns that will be returned (to eventually pass into printResult())
		String[] returnColumns = {"book_ID", "title", "author", "checkedOut", "requestDate", "patron_ID", "firstName", "lastName"};
				
		// read from db; calls printResults method (results displayed in window)
		DatabaseQueries.printFromDatabase(connection, getHoldsQuery, returnColumns);
	}
	

	public static void getHoldsList(Connection connection, String holdsView) {

		String getHoldsQuery = "SELECT *"
				+ " FROM " + holdsView
				+ " ORDER BY firstName, lastName, author, title;";
		
		// Create array of columns that will be returned (to eventually pass into printResult())
		String[] returnColumns = {"book_ID", "title", "author", "checkedOut", "requestDate", "patron_ID", "firstName", "lastName"};
				
		// read from db; calls printResults method (results displayed in window)
		DatabaseQueries.printFromDatabase(connection, getHoldsQuery, returnColumns);
	}
	

	public static void getCheckOutsList(Connection connection, String checkOutsView, String keyword, String criterion) {

		String getCheckoutsQuery = "SELECT *"
				+ " FROM " + checkOutsView
				+ " WHERE " + keyword + " = \"" + criterion + "\" "
				+ " ORDER BY firstName, lastName, author, title;";
		
		// Create array of columns that will be returned (to eventually pass into printResult())
		String[] returnColumns = {"patron_ID", "firstName", "lastName", "book_ID", "title", "author", 
				"onHold", "checkOutDate", "dailyFineAmount", "dueDate", "daysLate", "fineAmount"};
				
		// read from db; calls printResults method (results displayed in window)
		DatabaseQueries.printFromDatabase(connection, getCheckoutsQuery, returnColumns);
	}
	

	public static void getCheckOutsList(Connection connection, String checkOutsView) {

		String getCheckoutsQuery = "SELECT *"
				+ " FROM " + checkOutsView
				+ " ORDER BY firstName, lastName, author, title;";
		
		// Create array of columns that will be returned (to eventually pass into printResult())
		String[] returnColumns = {"patron_ID", "firstName", "lastName", "book_ID", "title", "author", 
				"onHold", "checkOutDate", "dailyFineAmount", "dueDate", "daysLate", "fineAmount"};
				
		// read from db; calls printResults method (results displayed in window)
		DatabaseQueries.printFromDatabase(connection, getCheckoutsQuery, returnColumns);
	}
	

	public static void getOverdueList(Connection connection, String checkOutsView, String keyword, String criterion) {	// special case filtering of above based on date???

		String getOverdueQuery = "SELECT patron_ID, firstName, lastName, book_ID, title, author,"
				+ " checkOutDate, dailyFineAmount, dueDate, daysLate, fineAmount"
				+ " FROM " + checkOutsView
				+ " WHERE daysLate > 0 && " + keyword + " = \"" + criterion + "\""
				+ " ORDER BY firstName, lastName, author, title;";
		
		// Create array of columns that will be returned (to eventually pass into printResult())
		String[] returnColumns = {"patron_ID", "firstName", "lastName", "book_ID", "title", "author", 
				"checkOutDate", "dailyFineAmount", "dueDate", "daysLate", "fineAmount"};
				
		// read from db; calls printResults method (results displayed in window)
		DatabaseQueries.printFromDatabase(connection, getOverdueQuery, returnColumns);
		
	}
	

	public static void getOverdueList(Connection connection, String checkOutsView) {	// special case filtering of above based on date???
		// SELECT from VIEW
		// goal: list of over due books
		/* display the following columns from the CheckOutsList view
		 * where daysLate > 0
		 * desiredColums = all but 6 desiredColumns = {1,2,3,4,5,7,9,10,11}
		 * *** unless we group by patron_ID and just get the total that each patron owes?
		*/
		
		// Construct query string
		String getOverdueQuery = "SELECT patron_ID, firstName, lastName, book_ID, title, author,"
				+ " checkOutDate, dailyFineAmount, dueDate, daysLate, fineAmount"
				+ " FROM " + checkOutsView
				+ " WHERE daysLate > 0" 
				+ " ORDER BY firstName, lastName, author, title;";
		
		// Create array of columns that will be returned (to eventually pass into printResult())
		String[] returnColumns = {"patron_ID", "firstName", "lastName", "book_ID", "title", "author", 
				"checkOutDate", "dailyFineAmount", "dueDate", "daysLate", "fineAmount"};
				
		// read from db; calls printResults method (results displayed in window)
		DatabaseQueries.printFromDatabase(connection, getOverdueQuery, returnColumns);
		
	}
	
}
