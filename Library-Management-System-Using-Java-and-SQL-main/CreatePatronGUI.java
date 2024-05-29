package lms;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.sql.Connection;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

public class CreatePatronGUI implements ActionListener {
	
	//Add various components. Helps to add them here in case we want to use the information in our ActionListener later
	
	

	public static JTextField firstName = new JTextField();
	

	public static JTextField lastName = new JTextField();
	
	
	/**
	 * Button used to navigate to "create patron" window
	 */
	public static JButton createPatron = new JButton("Create Patron Account");
	    
	/**
	 * Constructor for the welcome window.
	 * Sets sizes, layouts, and action events/listeners for all elements of the page
	 */
	
	
	
	
	public CreatePatronGUI(){    
	
	        // Creating instance of JFrame
	        JFrame frame = new JFrame("Welcome to our humble library!");
	        // Set the width and height of frame
	        frame.setSize(800, 400);
	            
	        //Create the panel that contains the "boxes" of content
	        JPanel boxes = new JPanel();
	        boxes.setLayout(new GridLayout(3,1,10,0));
	        
	        //Create the Header for the Sign in section
	        JPanel box1 = new JPanel();
	        JLabel signInHeader = new JLabel("New Patron Account");
	        signInHeader.setHorizontalAlignment(SwingConstants.CENTER);
	        signInHeader.setFont(new Font("Arial", Font.BOLD, 18));
	        //signInHeader.setVerticalAlignment(SwingConstants.TOP);
	        box1.add(signInHeader);
	        
	        //Create a box that holds a Patron/Librarian radio button and the sign in text field
	        JPanel box2 = new JPanel();
	        box2.setLayout(new BoxLayout(box2, BoxLayout.LINE_AXIS));
	        
	        //Set a border, mainly to squish the components closer to the center
	        box2.setBorder(new EmptyBorder(10, 50, 10, 50));
	        
	        JPanel firstNamePanel = new JPanel();
	        firstNamePanel.setPreferredSize(new Dimension(200,25));
	        JLabel firstNameLabel = new JLabel("First Name:");
	        firstName.setPreferredSize(new Dimension(200,25));
	        firstNamePanel.add(firstNameLabel);
	        firstNamePanel.add(firstName);
	        box2.add(firstNamePanel);
	        
	        JPanel lastNamePanel = new JPanel();
	        lastName.setPreferredSize(new Dimension(200,25));
	        JLabel lastNameLabel = new JLabel("Last Name:");
	        lastName.setPreferredSize(new Dimension(200,25));
	        lastNamePanel.add(lastNameLabel);
	        lastNamePanel.add(lastName);
	        box2.add(lastNamePanel);
	        
	    
	        //Add createPatron Button
	        JPanel box3 = new JPanel();
	        
	        createPatron.setMnemonic(KeyEvent.VK_C);
	        createPatron.setActionCommand("create");
	        createPatron.addActionListener(this);
	        box3.add(createPatron);
	        
	        // Use an empty panel to add space between two main sections
	        //JPanel spacer = new JPanel();
	        //spacer.add(Box.createRigidArea(new Dimension(10, 50)));
	      
	        boxes.add(box1);
	        boxes.add(box2);
	        boxes.add(box3);
	        
	        //Give entire window some padding
	        boxes.setBorder(new EmptyBorder(10, 0, 30, 0));
	        
	        //Add boxes to frame
	        frame.add(boxes);
	        //Setting the frame visibility to true
	        frame.setVisible(true);
	        //Close the Window without exiting application on close
	        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	    }

	/**
	 * Overriding the abstract actionPerformed method
	 * Writes logic to handle the actions for various buttons/fields in the window
	 * Detects login info/createPatron button clicks to navigate to correct page.   
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if ("create".equals(e.getActionCommand())) {
			Connection connection = DatabaseConnection.openDatabase();
			
			//Ensure no fields are blank before adding
			if(!firstName.getText().isBlank() && !lastName.getText().isBlank()) { 
				Boolean matchingNames = DatabaseQueries.namePairExists(connection, "patrons", firstName.getText(), lastName.getText());
				
				//If the name combo in table, show error
				if (matchingNames) {
					JOptionPane.showMessageDialog(null, "Whoops! An account already exists for this Patron. Please choose a different name", "Patron Already Exists", JOptionPane.ERROR_MESSAGE);
					
				}
				//Otherwise,  add new Patron
				else {
					Patron.createPatron(connection, firstName.getText(), lastName.getText());
					
					//get the patron id
					String id = DatabaseQueries.getLastID(connection, "patrons", "patron_ID");
					
					JOptionPane.showMessageDialog(null, "Patron Successfully Created! This Patron's ID is " + id + " (needed to log in!)", "Success!", JOptionPane.INFORMATION_MESSAGE);
				}
			}else {
				JOptionPane.showMessageDialog(null, "No patron was added because one or more fields were left blank.", "Field Blank", JOptionPane.WARNING_MESSAGE);
			}
			DatabaseConnection.closeDatabase(connection);
		} 
    }

	
}
