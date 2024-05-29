package lms;


import java.awt.Button;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;


public class WelcomeGUI implements ActionListener {
	
	//Add various components. Helps to add them here in case we want to use the information in our ActionListener later
	
	/**
	 * radio button that signifies a patron is signing in
	 */
	public static JRadioButton patButton = new JRadioButton("Patron");
	
	/**
	 * radio button that signifies a librarian is signing in
	 */
	public static JRadioButton libButton = new JRadioButton("Librarian");
	
	/**
	 * Button group, which will ensure only one radio button is selected at a time
	 */
	public static ButtonGroup radioButtons = new ButtonGroup();
	
	/**
	 * Text field to input login info
	 */
	public static JTextField loginInfo = new JTextField();
	
	/**
	 * Button used to navigate to "create patron" window
	 */
	public static JButton createPatron = new JButton("Create Patron Account");
	    
	/**
	 * Constructor for the welcome window.
	 * Sets sizes, layouts, and action events/listeners for all elements of the page
	 */
	

	
	public WelcomeGUI(){    
		
	        // Creating instance of JFrame
	        JFrame frame = new JFrame("Welcome to our HU library!");
	        // Set the width and height of frame
	        frame.setSize(800, 400);
	            
	        //Create the panel that contains the "boxes" of content
	        JPanel boxes = new JPanel();
	        boxes.setLayout(new GridLayout(6,1,10,0));
	        
	        //Create the Header for the Sign in section
	        JPanel box1 = new JPanel();
	        JLabel signInHeader = new JLabel("Sign In");
	        signInHeader.setHorizontalAlignment(SwingConstants.CENTER);
	        signInHeader.setFont(new Font("Arial", Font.BOLD, 18));
	        //signInHeader.setVerticalAlignment(SwingConstants.TOP);
	        box1.add(signInHeader);
	        
	        //Create a box that holds a Patron/Librarian radio button and the sign in text field
	        JPanel box2 = new JPanel();
	        box2.setLayout(new BoxLayout(box2, BoxLayout.LINE_AXIS));
	        
	        //Set a border, mainly to squish the components closer to the center
	        box2.setBorder(new EmptyBorder(10, 200, 10, 200));
	        
	        // Add button to choose between librarian and patron sign in
	        //JRadioButton patButton = new JRadioButton("Patron");
	        patButton.setMnemonic(KeyEvent.VK_P);
	        patButton.setActionCommand("Patron");
	        //Set the patron button to be selected by default
	        patButton.setSelected(true);
	        //patButton.doClick();
	        
	        //JRadioButton libButton = new JRadioButton("Librarian");
	        libButton.setMnemonic(KeyEvent.VK_L);
	        libButton.setActionCommand("Librarian");
	        
	      
	        
	        //Add radio buttons to group
	        radioButtons.add(libButton);
		    radioButtons.add(patButton);
		    
		    //Use a different panel for each group to better control proportions
	        JPanel rButtons = new JPanel();
	        rButtons.add(patButton);
	        rButtons.add(libButton);
	        box2.add(rButtons);
	        
	        JPanel loginText = new JPanel();
	        loginText.setPreferredSize(new Dimension(200,25));
	        loginInfo.setPreferredSize(new Dimension(200,25));
	        loginInfo.setActionCommand("enter");
	        loginInfo.addActionListener(this);
	        loginText.add(loginInfo);
	        box2.add(loginText);
	    
	        //Add instructions for signing in. Using a TextArea here and formatting it to look like a label so I could have more
	        //control over how the large amount of text wraps.
	        JPanel box3 = new JPanel();
	        JTextArea signInText = new JTextArea(2, 60);
	        signInText.setText("If you are a patron, sign in using your Patron ID. If you a librarian, sign in"
	        		+ " using the super-secret password that may or may not be 'librarian'. Press enter to submit your response.");
	        signInText.setWrapStyleWord(true);
	        signInText.setLineWrap(true);
	        signInText.setOpaque(false);
	        signInText.setEditable(false);
	        signInText.setFocusable(false);
	        signInText.setBackground(UIManager.getColor("Label.background"));
	        signInText.setFont(UIManager.getFont("Label.font"));
	        signInText.setBorder(UIManager.getBorder("Label.border"));
	        box3.add(signInText);
	        
	        //Create Header for Create Patron Account Section
	        JPanel box4 = new JPanel();
	        JLabel createHeader = new JLabel("Don't Have an Account?");
	        createHeader.setHorizontalAlignment(SwingConstants.CENTER);
	        createHeader.setFont(new Font("Arial", Font.BOLD, 18));
	        box4.add(createHeader);
	        
	        //Add createPatron Button
	        JPanel box5 = new JPanel();
	        
	        createPatron.setMnemonic(KeyEvent.VK_C);
	        createPatron.setActionCommand("createPatron");
	        createPatron.addActionListener(this);
	        box5.add(createPatron);
	        
	        // Use an empty panel to add space between two main sections
	        JPanel spacer = new JPanel();
	        spacer.add(Box.createRigidArea(new Dimension(10, 50)));
	      
	        boxes.add(box1);
	        boxes.add(box2);
	        boxes.add(box3);
	        boxes.add(spacer);
	        boxes.add(box4);
	        boxes.add(box5);
	        
	        //Give entire window some padding
	        boxes.setBorder(new EmptyBorder(10, 0, 30, 0));
	        
	        //Add boxes to frame
	        frame.add(boxes);
	        //Setting the frame visibility to true
	        frame.setVisible(true);
	        //Close application upon pressing x
	        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    }

	/**
	 * Overriding the abstract actionPerformed method
	 * Writes logic to handle the actions for various buttons/fields in the window
	 * Detects login info/createPatron button clicks to navigate to correct page.   
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		Connection connection = DatabaseConnection.openDatabase();
		//Check when enter is pressed on text field
		if ("enter".equals(e.getActionCommand())) {
			//If patron radio button selected
			if (patButton.isSelected()) {
				//See if the submitted id is in the database
				Boolean patronExists = DatabaseQueries.patronExists(connection, "patrons", loginInfo.getText());
				//if the id is in database, create a new Patron with their id passed in, then run patron gui 
				if(!loginInfo.getText().isBlank() && patronExists){
					Patron pat = new Patron(connection, loginInfo.getText());
					pat.runPatron();
				} else {
					//print error message
					JOptionPane.showMessageDialog(null, "Whoops! We can't find any Patrons with that id. Please try again or create a new Patron Account.", "Patron Doesn't Exist", JOptionPane.ERROR_MESSAGE);
				}
			
			//if librarian radio button selected
			}else if (libButton.isSelected()) {
				if (loginInfo.getText().equals("librarian")) {
					new LibrarianGUI();
				} else {
					System.out.println(loginInfo.getText());
					JOptionPane.showMessageDialog(null, "Sorry, that's the wrong login info for the Librarian Account (hint: try 'librarian')", "Incorrect Information", JOptionPane.INFORMATION_MESSAGE);
				}
				
			}
		} else if ("createPatron".equals(e.getActionCommand())) {
			new CreatePatronGUI();
		}
		DatabaseConnection.closeDatabase(connection);
	
    }

	

}
