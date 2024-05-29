package lms;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;

public class holdsFilter extends FilterSearchGUI implements ActionListener {
	
	/**
	 * Default Keyword Option
	 */
	private static String selectedOption = "patron_ID"; // default value
	
	/**
	 * Array of Keyword Options
	 */
	private static final String[] options = {"patron_ID", "book_ID"};
	
	/**
	 * Constructor. Calls Superclass constructor and passes in new array of keywords
	 */
	public holdsFilter() {
		super(options);
		
		
		
	}
	
	/**
	 * Overriding the abstract actionPerformed method. Handles the connection to database and keyword recognition 
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		     
		Connection connection = DatabaseConnection.openDatabase();
		// if the action is a dropDown switch, switch our selectedOption. Otherwise, show a table.
		if ("keyword".equals(e.getActionCommand())) {
			selectedOption = String.valueOf(keywordDropDown.getSelectedItem());  
		}else if ("go".equals(e.getActionCommand())) {
			switch (selectedOption) {				
				case "patron_ID":
					System.out.println(super.criterion.getText());
				    Librarian.getHoldsList(connection, "holdsview", "patron_ID", super.criterion.getText());
				    break;
				case "book_ID":
					Librarian.getHoldsList(connection, "holdsview", "book_ID", super.criterion.getText());
					break;
				
			}
		}
		DatabaseConnection.closeDatabase(connection);
		
			  	

	}

}
