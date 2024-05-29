package lms;

import java.awt.Button;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class LibrarianGUI implements ActionListener {
	
	private static String selectedOption = "Add Book to Database"; // default value

	
	public LibrarianGUI(){ 

		
		JFrame librarianWindow = new JFrame();
		
		JPanel librarianContents = new JPanel();
		//librarianContents.setBackground(Color.WHITE);
		GridLayout pageLayout = new GridLayout(4,1,10,0);
		librarianContents.setLayout(pageLayout);
		
		JLabel header = new JLabel("Librarian Main Page");
		header.setFont(new Font("Arial", Font.BOLD, 15));
		header.setHorizontalAlignment(SwingConstants.CENTER);
		header.setForeground(Color.black);
		librarianContents.add(header);
		
		JLabel instructions = new JLabel("Select an Action...");
		instructions.setHorizontalAlignment(SwingConstants.CENTER);
		librarianContents.add(instructions);
		
		// create options
		String[] options = {"Add Book to Database",
				"Add Patron to Database",
				"Get list of holds",
				"Get list of Checkouts",
				"Get list of overdue Books"};

		
		JComboBox librarianDropDown = new JComboBox(options);
		librarianDropDown.setSelectedIndex(0);
		
		// from https://www.javatpoint.com/java-actionlistener -> anonymous class
		librarianDropDown.addActionListener(new ActionListener(){  
			public void actionPerformed(ActionEvent e){  
				// set value of selectedOption
				// https://stackoverflow.com/questions/4962416/preferred-way-of-getting-the-selected-item-of-a-jcombobox
				selectedOption = String.valueOf(librarianDropDown.getSelectedItem());  
		}  
		});
		librarianContents.add(librarianDropDown);
		
		Button goButton = new Button("GO!");
		goButton.setActionCommand("go");
		goButton.addActionListener(this);
		
		
		JPanel bottomContents = new JPanel();
		bottomContents.setLayout(new GridLayout(1,5));
		bottomContents.add(new JLabel()); // place holder
		bottomContents.add(new JLabel()); // place holder
		bottomContents.add(goButton);
		bottomContents.add(new JLabel()); // place holder
		bottomContents.add(new JLabel()); // place holder
		librarianContents.add(bottomContents);
		
		librarianWindow.add(librarianContents);
		
		// call method to display Welcome Window
		//librarianWindow.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		
		//librarianWindow.setPreferredSize(new Dimension(400, 300));
		librarianWindow.pack();		
		librarianWindow.setLocationRelativeTo(null);
		librarianWindow.setTitle("Librarians - Main Page");
		librarianWindow.setVisible(true);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		//prints option selected       
		Connection connection = DatabaseConnection.openDatabase();
		// actions based on selection
		switch (selectedOption) {				
			case "Add Book to Database":
			    new AddBookGUI();
				//WINDOW, drop-down list (keyword = type of search), 1 entry field, search button -> call the printFromDatabase/printResultSetinWindow methods /  see all books button -> getAllBooks
			    break;
			case "Add Patron to Database":
				new CreatePatronGUI();
				break;
			case "Get list of holds":
				//Open a window of all holds
				new holdsFilter();
				Librarian.getHoldsList(connection, "holdsview");
				
				
				// Window of potential filter fields
				
				break;
			case "Get list of Checkouts":
				//Open a window of all holds
				new checkOutFilter();
				Librarian.getCheckOutsList(connection, "checkoutsview");
				
				// Window of potential filter fields
				
				break;
			case "Get list of overdue Books":
				//Open a window of all holds
				new overdueFilter();
				Librarian.getOverdueList(connection, "checkoutsview");
				
				// Window of potential filter fields
			    break;
			
			}
		DatabaseConnection.closeDatabase(connection);
		
	  	}  
	

	}


