package lms;

import java.awt.Button;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;


public class Patron {
	

	private String id = "00";
	

	private String firstName;

	private String lastName;
	
	/**
	 * The number of books that a patron has on hold.
	 */
	private int numHolds;
	

	private int numBooksOut;
	

	private double fine;
	

	private Patron globalPatron;
	

	private static int MAX_BOOKS = 10;
	

	private static int MAX_HOLDS = 25;
	

	private static String selectedOption = "Search for Book"; // default value
	

	public Patron(Connection connection, String id){
		
		// construct SELECT query
		String getPatronInfoQuery = "SELECT firstName, lastName, numBooksOut, numHolds, totalFineAmount FROM patrons WHERE patron_ID = " + id + ";";
		
		String[] columns = {"firstName", "lastName", "numBooksOut", "numHolds", "totalFineAmount"};
		// pass to db and get results back
		String[][] patronInfo = DatabaseQueries.readFromDatabase(connection, getPatronInfoQuery, columns);		// returns 2 x 5 array; results are in row index 1
		
		if (patronInfo == null || patronInfo.length == 1) {	// id passed does not exist in the db (length = 1) or some other error occurred
			JOptionPane.showMessageDialog(null,"Invalid entry! Patron ID " + id + " does not exist.\nPlease try again or for a new user, create a new profile.", "Error!", JOptionPane.INFORMATION_MESSAGE);
			return;	// exit constructor
		}
		
		// valid/existing Patron; instantiate variables from 2nd row (index [1]) of array
		this.id = id;
		this.firstName = patronInfo[1][0];
		this.lastName = patronInfo[1][1];
		this.numBooksOut = Integer.parseInt(patronInfo[1][2]);	// convert to integer
		this.numHolds = Integer.parseInt(patronInfo[1][3]);
		this.fine = Double.parseDouble(patronInfo[1][4]);
		
		// make the globalPatron be THIS instance of the Patron Object
		globalPatron = this;
		
	}
	

	public static void createPatron(Connection connection, String firstName, String lastName) {
		DatabaseQueries.addToTable(connection, "patrons", new String[]{firstName, lastName});		
	}
	

	public static void bookSearch(Connection connection, String keyword, String criterion, String printOption) { //added printOption
		
		if (connection == null) {		// necessary for after button click in search box
			connection = DatabaseConnection.openDatabase();
		}
		
		String[] returnColumns = {"Book ID", "Title", "Author", "Available"};
		
		// if no search criteria passed, display all books (call that method)
		if (keyword == null || keyword == "" || criterion == null || criterion == "") {
			getAllBooks(connection, "books", returnColumns);
			return;
		}
		
		// construct query string
		String searchQuery = "SELECT book_ID AS \"Book ID\", title, author, NOT checkedOut AS Available "
				+ "FROM books "
				+ "WHERE " + keyword + " = \"" + criterion + "\" "
						+ "ORDER BY title, author;";
		//System.out.println(searchQuery);			// in case you want to print it to see it
		
		if (printOption.equals("console")) {
			String [][] results = DatabaseQueries.readFromDatabase(connection, searchQuery, returnColumns); // added this and if/else statement
			JOptionPane.showMessageDialog( DatabaseQueries.results, JOptionPane.INFORMATION_MESSAGE);
			System.out.println();	// added blank line
		} else {
			// read from db; calls printResults method (results displayed in window)
			DatabaseQueries.printFromDatabase(connection, searchQuery, returnColumns);
		}
		
	}
	

	private static void getAllBooks(Connection connection, String table, String[] returnColumns) {
		// construct query string
		String allBooksQuery = "SELECT book_ID AS \"Book ID\", title, author, NOT checkedOut AS Available "
				+ "FROM " + table
				+ " ORDER BY title, author;";
		//System.out.println(allBooksQuery);			// in case you want to print it to see it
		
		// read from db; calls printResults method (results displayed in window)
		DatabaseQueries.printFromDatabase(connection, allBooksQuery, returnColumns);
		
	}
	

	public static void getRandomBook(Connection connection, String genre) {
		
		String[] returnColumns = {"Book ID", "Title", "Author", "Available"};
		
		// construct query string
		String searchQuery = "SELECT book_ID AS \"Book ID\", title, author, NOT checkedOut AS Available "
				+ "FROM books "
				+ "WHERE genre = \"" + genre + "\";";
		//System.out.println(searchQuery);			// in case you want to print it to see it
		
		// read from db; get results array
		String[][] results = DatabaseQueries.readFromDatabase(connection, searchQuery, returnColumns);
		
		if (results == null || results.length == 1) {	// no results returned (length 1 means just the headers returned
			System.out.println("There are no books with that genre in the system. Sorry! Please return to the main patron page and try again.");
		} else {
			java.util.Random random = new java.util.Random();
			/* Since the passed upper bound in NOT included,
			 * the random num generator will return 0 to length-2.
			 * We don't want row 0 of results as that's just the headers.
			 * Adding 1 means that we can get row indices 1 to length-1.
			 */
			int randRow = random.nextInt(results.length-1) + 1;
			
			// print results to console
			System.out.println("Book ID: " + results[randRow][0]);
			System.out.println("Title: " + results[randRow][1]);
			System.out.println("Author: " + results[randRow][2]);
			System.out.println("Availability: " + results[randRow][3]);
			System.out.println("\nTo get another recommendation, return to the main patron page and choose \"Get Book Recommendation\" again.");
		}

	}
	

	public void getPatronBooks(Connection connection, String view, String printOption) {	// added String printOption
		
		// declare/initialize variables with useless values for now
		String selectQuery = "";
		String[] returnColumns = null;
		
		// conditions for which view table since we want to display different columns
		if (view.equals("holdsview")) {
			selectQuery = "SELECT book_ID AS \"Book ID\", title, author, NOT checkedOut AS Available "
					+ "FROM " + view
					+ " WHERE patron_ID = " + this.id 
					+ " ORDER BY `title`;";
			//System.out.println(selectQuery);			// in case you want to print it to see it
			
			// give value/meaning to array
			returnColumns = new String[]{"Book ID", "Title", "Author", "Available"};
			
		} else if (view.equals("checkoutsview"))  {
			selectQuery = "SELECT book_ID AS \"Book ID\", title, author, dueDate AS `Due Date`, daysLate AS `Days Overdue`, fineAmount AS Fine "
					+ "FROM " + view
					+ " WHERE patron_ID = " + this.id 
					+ " ORDER BY `Due Date`, `title`;";
			//System.out.println(selectQuery);			// in case you want to print it to see it
			
			// give value/meaning to array
			returnColumns = new String[]{"Book ID", "Title", "Author", "Due Date", "Days Overdue", "Fine"};
		}
		
		if (!selectQuery.equals("") && returnColumns != null) {		// in case there are issues with the view string
			if (printOption.equals("console")) {
				String [][] results = DatabaseQueries.readFromDatabase(connection, selectQuery, returnColumns); // added this and if/else statement
				DatabaseQueries.consoleDisplay(results);
				System.out.println();	// added blank line
			} else {
				// read from db; calls printResults method (results displayed in window)
				DatabaseQueries.printFromDatabase(connection, selectQuery, returnColumns);
			}

		}

		
	}


	public void getUnheldBooks(Connection connection) {
		
		// declare/initialize variables with useless values for now
		String selectQuery = "";
		
		// First uses a subquery to find all holds of this specific patron in holdsview.
		// Then join that selection with the books table, preserving all book rows
		selectQuery = "SELECT books.book_ID AS \"Book ID\", books.title, books.author, books.genre, NOT books.checkedOut AS Available "
				+ "FROM (SELECT * FROM holdsview WHERE patron_ID = " + this.id + ") AS holds "
				+ "RIGHT JOIN books "
				+ "ON holds.book_ID = books.book_ID "
				+ "WHERE patron_ID IS NULL "
				+ "ORDER BY title, author;";
		
		String[] returnColumns = {"Book ID", "Title", "Author", "Genre", "Available"};
		// read from db; print list
		String [][] results = DatabaseQueries.readFromDatabase(connection, selectQuery, returnColumns); // added
		DatabaseQueries.consoleDisplay(results);		//DatabaseQueries.printFromDatabase(connection, selectQuery, returnColumns);
		System.out.println();	// added blank line
		
	}	
	

	public void placeHold(Connection connection, String book_ID) {	
		// get book title
		String title = DatabaseQueries.getBookTitle(connection, book_ID);
		
		// check if book_ID/patron_ID tuple already exists in table
		if (DatabaseQueries.bookPatronPairExists(connection, "holds", book_ID, this.id)) {
			// display error message/window
			JOptionPane.showMessageDialog(null, "You have already placed " + title + " (ID: " + book_ID + ") on hold.\nIf you want to place a book on hold, select \"Hold: Place\" on main patron page and enter a different ID.", "BOOK ALREADY ON-HOLD", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		

		this.numHolds += 1;
		DatabaseQueries.updateColumn(connection, "patrons", "numHolds", Integer.toString(this.numHolds), "patron_ID", this.id);
		

		// make date string to pass
		String dateAsString = DatabaseQueries.getTodaysDateAsString();
		String[] values = {book_ID, this.id, dateAsString};
		
		// add row to holds table
		DatabaseQueries.addToTable(connection, "holds", values);
		// update book's row in books table
		DatabaseQueries.updateColumn(connection, "books", "onHold", "true", "book_ID", book_ID);
		
		// display message
		JOptionPane.showMessageDialog(null, title + " (ID: " + book_ID + ") placed on hold!\nIf this was a mistake, select \"Hold: Remove\" on main patron page and enter this book's ID again.\nIf you want to place another book on hold, select \"Hold: Place\" on main patron page again and enter another book ID.", "BOOK PLACED ON-HOLD", JOptionPane.INFORMATION_MESSAGE);
			
	}

	public void checkOutBook(Connection connection, String book_ID) {
		// get book title
		String title = DatabaseQueries.getBookTitle(connection, book_ID);
		
		// check if book_ID/patron_ID tuple already exists in table
		if (DatabaseQueries.bookPatronPairExists(connection, "checkouts", book_ID, this.id)) {
			// display error message/window
			JOptionPane.showMessageDialog(null, "You've already checked out " + title + " (ID: " + book_ID + ").\nIf you want to check out a book, select \"Check Out Book\" on main patron page again and enter a different ID.", "BOOK ALREADY CHECKED-OUT", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		
		// else...
		// check the book is available for check out
		if (DatabaseQueries.bookAvailable(connection, book_ID)) {
			// if book was on hold by patron (b-p pair in `holds` table), remove hold.
			if (DatabaseQueries.bookPatronPairExists(connection, "holds", book_ID, this.id)) {
				System.out.println("Removing " + title + " (ID: " + book_ID + ") from your holds list.\n");
				this.removeHold(connection, book_ID);
			}
			
			// update patron's info in Java and db
			this.numBooksOut += 1;
			DatabaseQueries.updateColumn(connection, "patrons", "numBooksOut", Integer.toString(this.numBooksOut), "patron_ID", this.id);

			// make date string to pass
			String dateAsString = DatabaseQueries.getTodaysDateAsString();
			String[] values = {book_ID, this.id, dateAsString};
			
			// add row to holds table
			DatabaseQueries.addToTable(connection, "checkouts", values);
			// update book's row in books table
			DatabaseQueries.updateColumn(connection, "books", "checkedOut", "true", "book_ID", book_ID);
			
			// display message
			JOptionPane.showMessageDialog(null, title + " (ID: " + book_ID + ") checked out!\nIf this was a mistake, select \"Check In (Return) Book\" on main patron page and enter this book's ID again.\nIf you want to check out another book, select \"Check Out Book\" on main patron page again and enter another book ID.", "BOOK CHECKED-OUT", JOptionPane.INFORMATION_MESSAGE);
		} else {	// book is already checked out (to a different user)
			JOptionPane.showMessageDialog(null, "Unable to check out " + title + " (ID: " + book_ID + ") because it is checked out to another patron.\nIf you want to check out another book, select \"Check Out Book\" on main patron page again and enter another book ID.", "BOOK CHECKED-OUT", JOptionPane.INFORMATION_MESSAGE);
		}
		
	}
		

	public void removeHold(Connection connection, String book_ID) {
		// get book title
		String title = DatabaseQueries.getBookTitle(connection, book_ID);
		
		// verify that the book-patron pair exists in the holds table
		if (DatabaseQueries.bookPatronPairExists(connection, "holds", book_ID, this.id)) { // if so...
			// update patron's info in Java and db
			this.numHolds -= 1;
			DatabaseQueries.updateColumn(connection, "patrons", "numHolds", Integer.toString(this.numHolds), "patron_ID", this.id);
			
			// get hold ID based on book-patron pair
			String hold_ID = DatabaseQueries.getID(connection, "holds", book_ID, this.id);
			
			// remove row from holds table
			DatabaseQueries.removeFromTable(connection, "holds", "hold_ID", hold_ID);
			
			// update book's row in books table IF the book ID does not show up in the holds table any more (since multiple patrons can place holds on the same book)
			if (!DatabaseQueries.checkForBook(connection, "holds", book_ID)) {	// only take action if this returns false (book NOT in table), so use !
				DatabaseQueries.updateColumn(connection, "books", "onHold", "false", "book_ID", book_ID);
				// print msg to console (just for the sake of this program for full transparency)
				System.out.println("ADDITION TO LIBRARIAN LOG: " + DatabaseQueries.getTodaysDateAsString() + " No more holds on " + title + " (ID: " + book_ID + ").\n");
			}
			
			// display message
			JOptionPane.showMessageDialog(null, "Removed hold on " + title + " (ID: " + book_ID + ").\nIf this was in error, select \"Hold: Place\" on main patron page and enter this book's ID again.\nIf you want to remove a hold on another book, select \"Hold: Remove\" on main patron page again and enter another book ID.", "BOOK HOLD REMOVED", JOptionPane.INFORMATION_MESSAGE);		
		} else {
			// give message
			JOptionPane.showMessageDialog(null, "You have not placed a hold on " + title + " (ID: " + book_ID + ").\nIf you want to place it on hold, select \"Hold: Place\" on the main patron page and enter the ID again.", "BOOK NOT ON-HOLD", JOptionPane.INFORMATION_MESSAGE);			
		}
		
	}
	

	public void returnBook(Connection connection, String book_ID) {
		// get book title
		String title = DatabaseQueries.getBookTitle(connection, book_ID);
		
		// verify that the book-patron pair exists in the holds table
		if (DatabaseQueries.bookPatronPairExists(connection, "checkouts", book_ID, this.id)) { // if so...
			// update patron's info in Java and db
			this.numBooksOut -= 1;
			DatabaseQueries.updateColumn(connection, "patrons", "numBooksOut", Integer.toString(this.numBooksOut), "patron_ID", this.id);
			
			// get chkO_ID based on book-patron pair
			String checkOut_ID = DatabaseQueries.getID(connection, "checkouts", book_ID, this.id);
			
			// remove row from checkouts table
			DatabaseQueries.removeFromTable(connection, "checkouts", "chkO_ID", checkOut_ID);
			
			// update book's row in books table
			DatabaseQueries.updateColumn(connection, "books", "checkedOut", "false", "book_ID", book_ID);
			
			// display message
			JOptionPane.showMessageDialog(null, title + " (ID: " + book_ID + ") returned.\nIf this was in error, select \"Check Out Book\" on the main patron page and enter this book's ID again.\nIf you want to return another book, select \"Check In (Return) Book\" on main patron page again and enter another book ID.", "BOOK RETURNED", JOptionPane.INFORMATION_MESSAGE);
			
		} else {
			// give message
			JOptionPane.showMessageDialog(null, "You have not checked out " + title + " (ID: " + book_ID + ").\nIf you want check it out, select \"Check Out Book\" on the main patron page and enter this book's ID again.", "BOOK NOT CHECKED-OUT", JOptionPane.INFORMATION_MESSAGE);			
		}
	}
	
	/**
	 * Method that creates, displays, and takes action on user choices at the Main Patron Page
	 */
	public void runPatron() {
		JFrame patronWindow = new JFrame();
		
		JPanel patronContents = new JPanel();
		//patronContents.setBackground(Color.WHITE);
		GridLayout pageLayout = new GridLayout(4,1,10,0);
		patronContents.setLayout(pageLayout);
		
		JLabel header = new JLabel("Patron Main Page");
		header.setFont(new Font("Arial", Font.BOLD, 15));
		header.setHorizontalAlignment(SwingConstants.CENTER);
		header.setForeground(Color.black);
		patronContents.add(header);
		
		JLabel instructions = new JLabel("Select an Action...");
		instructions.setHorizontalAlignment(SwingConstants.CENTER);
		patronContents.add(instructions);
		
		// create options
		String[] options = {"Search for Book",
				"My Books on Hold",
				"My Books Checked Out",
				"Get Book Recommendation",
				"Show My Fine",
				"Hold: Place",
				"Hold: Remove",
				"Check Out Book",
				"Check In (Return) Book"};
		
		JComboBox<String> patronDropDown = new JComboBox<String>(options);
		patronDropDown.setSelectedIndex(0);
		
		// from https://www.javatpoint.com/java-actionlistener -> anonymous class
		patronDropDown.addActionListener(new ActionListener(){  
			public void actionPerformed(ActionEvent e){  
				// set value of selectedOption
				// https://stackoverflow.com/questions/4962416/preferred-way-of-getting-the-selected-item-of-a-jcombobox
				selectedOption = String.valueOf(patronDropDown.getSelectedItem());  
		}  
		});
		patronContents.add(patronDropDown);
		
		Button goButton = new Button("GO!");
		goButton.addActionListener(new ActionListener(){  
			public void actionPerformed(ActionEvent e){  
				//System.out.println(selectedOption); //prints option selected
				Connection connection = DatabaseConnection.openDatabase();
				
				// actions based on selection
				switch (selectedOption) {				
					case "Search for Book":
						BookSearch.main(null);
					    break;
					case "My Books on Hold":
						globalPatron.getPatronBooks(connection, "holdsview", "window");
						break;
					case "My Books Checked Out":
						globalPatron.getPatronBooks(connection, "checkoutsview", "window");
						break;
					case "Get Book Recommendation":
						JOptionPane.showMessageDialog(null, "In the console below enter a desired genre, then a random book of that genre will be displayed.", "BOOK RECOMMENDATION", JOptionPane.INFORMATION_MESSAGE);

						BufferedReader reader1 = new BufferedReader(new InputStreamReader(System.in));
						System.out.println("Enter desired genre:");
						// get input as String
						String userGenreChoice;
						try {
							userGenreChoice = reader1.readLine();

							getRandomBook(connection, userGenreChoice);

						} catch (IOException e1) { e1.printStackTrace(); }
						
						break;
					case "Show My Fine":
						JOptionPane.showMessageDialog(null, "Current fine amount: $" + String.format("%2.2f", globalPatron.getFine()) + "\nThis value does not include the amount you may have to pay for current overdue (unreturned) books.", globalPatron.getFullName() + " Info", JOptionPane.INFORMATION_MESSAGE);
					    break;
					case "Hold: Place":
						// if user's numHolds = 25, they can't place another hold: display warning message
						if (globalPatron.getNumHolds() == globalPatron.getMaxHolds()) {
							JOptionPane.showMessageDialog(null, "You have already placed 25 books on hold.\nYou may not place any additional holds without removing one or more holds first.", "MAX HOLD REACHED", JOptionPane.INFORMATION_MESSAGE);
						} else {
							// display list of books NOT on hold
							globalPatron.getUnheldBooks(connection);
							
							
							System.out.print("Enter ID of book to hold: ");
							BufferedReader reader2 = new BufferedReader(new InputStreamReader(System.in));
							try {
								// get input as String
								String enteredBookID = reader2.readLine();
								
								try {
									Integer.parseInt(enteredBookID);	// see if the value can be made into an integer
									// if so...
									if (DatabaseQueries.checkForBook(connection, "books", enteredBookID)) {	// verify that book id is in system
										globalPatron.placeHold(connection, enteredBookID);
									} else {		// ID does not exist in system warning message (to console)
										System.out.println("ID " + enteredBookID + " does not exist in the system. Return to main patron page to enter a different ID.");
									}
								} catch (NumberFormatException NFe) {
									System.out.println("You did not enter a valid value. Return to main patron page to try again.");
								}
								
								
							} catch (IOException e2) { e2.printStackTrace(); }
						}
					    break;
					case "Hold: Remove":
						// display list of books on hold by patron
						globalPatron.getPatronBooks(connection, "holdsview", "console");
						BufferedReader reader3 = new BufferedReader(new InputStreamReader(System.in));
						System.out.print("Enter ID of book to remove hold: ");

						try {
							// get input as String
							String enteredBookID = reader3.readLine();
							
							try {
								Integer.parseInt(enteredBookID);	// see if the value can be made into an integer
								// if so...
								if (DatabaseQueries.checkForBook(connection, "books", enteredBookID)) {	// verify that book id is in system
									globalPatron.removeHold(connection, enteredBookID);
								} else {		// ID does not exist in system warning message (to console)
									System.out.println("ID " + enteredBookID + " does not exist in the system. Return to main patron page to enter a different ID.");
								}
							} catch (NumberFormatException NFe) {
								System.out.println("You did not enter a valid value. Return to main patron page to try again.");
							}
							
							
						} catch (IOException e3) { e3.printStackTrace(); }
						
					    break;
					case "Check Out Book":
						// if user's numBooksOut = 10; they can't check out: display warning message
						if (globalPatron.getNumBooksOut() == globalPatron.getMaxBooks()) {
							JOptionPane.showMessageDialog(null, "You have already checked out 10 books.\nYou may not check out any more until you return at least 1 book. Happy reading!", "MAX CHECK-OUTS REACHED", JOptionPane.INFORMATION_MESSAGE);
							return;
						} else {
							// display list of available books (not checked out)
							System.out.println("Books available for checkout--");
							bookSearch(connection, "checkedOut", "0", "console");
							
							BufferedReader reader4 = new BufferedReader(new InputStreamReader(System.in));
							System.out.print("Enter ID of book to check out: ");

							try {
								// get input as String
								String enteredBookID = reader4.readLine();
								
								try {
									Integer.parseInt(enteredBookID);	// see if the value can be made into an integer
									// if so...
									if (DatabaseQueries.checkForBook(connection, "books", enteredBookID)) {	// verify that book id is in system
										globalPatron.checkOutBook(connection, enteredBookID);
									} else {		// ID does not exist in system warning message (to console)
										System.out.println("ID " + enteredBookID + " does not exist in the system. Return to main patron page to enter a different ID.");
									}
								} catch (NumberFormatException NFe) {
									System.out.println("You did not enter a valid value. Return to main patron page to try again.");
								}
								
								
							} catch (IOException e4) { e4.printStackTrace(); }
						}
					    break;
					case "Check In (Return) Book":
						// display list of books checked out to patron
						globalPatron.getPatronBooks(connection, "checkoutsview", "console");
						BufferedReader reader5 = new BufferedReader(new InputStreamReader(System.in));
						System.out.print("Enter ID of book to check in (return): ");

						try {
							// get input as String
							String enteredBookID = reader5.readLine();
							
							try {
								Integer.parseInt(enteredBookID);	// see if the value can be made into an integer
								// if so...
								if (DatabaseQueries.checkForBook(connection, "books", enteredBookID)) {	// verify that book id is in system
									globalPatron.returnBook(connection, enteredBookID);
								} else {		// ID does not exist in system warning message (to console)
									System.out.println("ID " + enteredBookID + " does not exist in the system. Return to main patron page to enter a different ID.");
								}
							} catch (NumberFormatException NFe) {
								System.out.println("You did not enter a valid value. Return to main patron page to try again.");
							}
						} catch (IOException e5) { e5.printStackTrace(); }
					    break;
				}
				DatabaseConnection.closeDatabase(connection);
			  }  
			});
		
		JPanel bottomContents = new JPanel();
		bottomContents.setLayout(new GridLayout(1,5));
		bottomContents.add(new JLabel()); // place holder
		bottomContents.add(new JLabel()); // place holder
		bottomContents.add(goButton);
		bottomContents.add(new JLabel()); // place holder
		bottomContents.add(new JLabel()); // place holder
		patronContents.add(bottomContents);
		
		patronWindow.add(patronContents);
		
		
		patronWindow.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		//patronWindow.setPreferredSize(new Dimension(400, 300));
		patronWindow.pack();		
		patronWindow.setLocationRelativeTo(null);
		patronWindow.setTitle("Patrons - Main Page");
		patronWindow.setVisible(true);
		
	}

	public String getID() {
		return this.id;
	}
	

	public String getFirstName() {
		return this.firstName;
	}
	

	public String getLastName() {
		return this.lastName;
	}
	

	public String getFullName() {
		return this.firstName + " " + this.lastName;
	}
	

	public double getFine() {
		return this.fine;
	}
	

	public int getNumHolds() {
		return this.numHolds;
	}
	

	public int getNumBooksOut() {
		return this.numBooksOut;
	}
	

	public int getMaxBooks() {
		return Patron.MAX_BOOKS;
	}
	

	public int getMaxHolds() {
		return Patron.MAX_HOLDS;
	}
	
}
