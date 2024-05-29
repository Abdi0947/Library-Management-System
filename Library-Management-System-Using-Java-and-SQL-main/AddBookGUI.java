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

public class AddBookGUI implements ActionListener {
	

	public static JTextField title = new JTextField();
	

	public static JTextField author = new JTextField();

	public static JTextField genre = new JTextField();
	
	

	public static JButton addBook = new JButton("Add Book");

	public AddBookGUI(){    
	
	        // Creating instance of JFrame
	        JFrame frame = new JFrame("Welcome to our humble library!");
	        // Set the width and height of frame
	        frame.setSize(800, 400);
	            
	        //Create the panel that contains the "boxes" of content
	        JPanel boxes = new JPanel();
	        boxes.setLayout(new GridLayout(3,1,10,0));
	        
	        //Create the Header for the Sign in section
	        JPanel box1 = new JPanel();
	        JLabel signInHeader = new JLabel("Add Book to Database");
	        signInHeader.setHorizontalAlignment(SwingConstants.CENTER);
	        signInHeader.setFont(new Font("Arial", Font.BOLD, 18));
	        //signInHeader.setVerticalAlignment(SwingConstants.TOP);
	        box1.add(signInHeader);
	        
	        //Create a box that holds a Patron/Librarian radio button and the sign in text field
	        JPanel box2 = new JPanel();
	        box2.setLayout(new BoxLayout(box2, BoxLayout.LINE_AXIS));
	        
	        //Set a border, mainly to squish the components closer to the center
	        box2.setBorder(new EmptyBorder(10, 30, 10, 30));
	        
	        JPanel titlePanel = new JPanel();
	        titlePanel.setPreferredSize(new Dimension(120,25));
	        JLabel titleLabel = new JLabel("Title:");
	        title.setPreferredSize(new Dimension(120,25));
	        titlePanel.add(titleLabel);
	        titlePanel.add(title);
	        box2.add(titlePanel);
	        
	        JPanel authorPanel = new JPanel();
	        author.setPreferredSize(new Dimension(120,25));
	        JLabel authorLabel = new JLabel("Author:");
	        author.setPreferredSize(new Dimension(120,25));
	        authorPanel.add(authorLabel);
	        authorPanel.add(author);
	        box2.add(authorPanel);
	        
	        JPanel genrePanel = new JPanel();
	        genre.setPreferredSize(new Dimension(150,25));
	        JLabel genreLabel = new JLabel("Genre:");
	        genre.setPreferredSize(new Dimension(150,25));
	        genrePanel.add(genreLabel);
	        genrePanel.add(genre);
	        box2.add(genrePanel);
	        
	    

	        JPanel box3 = new JPanel();
	        
	        addBook.setMnemonic(KeyEvent.VK_C);
	        addBook.setActionCommand("add");
	        addBook.addActionListener(this);
	        box3.add(addBook);
	        

	      
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


	@Override
	public void actionPerformed(ActionEvent e) {
		
		if ("add".equals(e.getActionCommand())) {
			Connection connection = DatabaseConnection.openDatabase();
			
			//Ensure no fields are blank before adding
			if(!title.getText().isBlank() && !author.getText().isBlank() && !genre.getText().isBlank()) {
				Librarian.addBook(connection, "books", title.getText(), author.getText(), genre.getText());
				

				String id = DatabaseQueries.getLastID(connection, "books", "book_ID");

				JOptionPane.showMessageDialog(null, "Book successfully added! Its id is" + id, "Success!", JOptionPane.INFORMATION_MESSAGE);
			}else {
				JOptionPane.showMessageDialog(null, "No book was added because one or more fields were left blank.", "Field Blank", JOptionPane.WARNING_MESSAGE);
			}
			
			
			DatabaseConnection.closeDatabase(connection);
		} 
    }

	
}
