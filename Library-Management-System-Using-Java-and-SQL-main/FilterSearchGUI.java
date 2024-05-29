package lms;

import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public abstract class FilterSearchGUI implements ActionListener {
	
	/**
	 * Default Keyword Option
	 */
	private static String selectedOption = "patron_ID"; // default value
	
	/**
	 * Array of Keyword Options
	 */
	private String[] options;
	
	/**
	 * Text field to input criterion on which to match filter keyword
	 */
	protected JTextField criterion = new JTextField();
	
	protected JComboBox keywordDropDown;

	/**
	 * Constructor. Creates the general filter page GUI. The specific keywords and action events are coded in subclasses
	 * @param options the list of options to appear as keywords
	 */
	public FilterSearchGUI(String[] options){ 
		
		//Set dropdown options equal to passed in subclass options 
		this.options = options;
		
		JFrame filterWindow = new JFrame();
		filterWindow.setSize(500, 300);
		
		JPanel filterContents = new JPanel();
		//filterContents.setBackground(Color.WHITE);
		GridLayout pageLayout = new GridLayout(4,1,10,0);
		filterContents.setLayout(pageLayout);
		
		JLabel header = new JLabel("Filter Search");
		header.setFont(new Font("Arial", Font.BOLD, 15));
		header.setHorizontalAlignment(SwingConstants.CENTER);
		header.setForeground(Color.black);
		filterContents.add(header);
		
		JLabel instructions = new JLabel("Select an Action...");
		instructions.setHorizontalAlignment(SwingConstants.CENTER);
		filterContents.add(instructions);

		 //Create a box that holds a keyword drop down and a criterion text field
        JPanel box2 = new JPanel();
        box2.setLayout(new BoxLayout(box2, BoxLayout.LINE_AXIS));
        
        //Set a border, mainly to squish the components closer to the center
        box2.setBorder(new EmptyBorder(10, 20, 10, 20));
        
        JPanel keywordPanel = new JPanel();
        //keywordPanel.setPreferredSize(new Dimension(200,25));
        JLabel keywordLabel = new JLabel("Filter Category:");
        //add combo box to allow for drop down options of potential keyword filters
        keywordDropDown = new JComboBox(options);
		keywordDropDown.setSelectedIndex(0);
		//Add action listener and command
		keywordDropDown.setActionCommand("keyword");
		keywordDropDown.addActionListener(this);
		
        keywordPanel.add(keywordLabel);
        keywordPanel.add(keywordDropDown);
        box2.add(keywordPanel);
        
        JPanel criterionPanel = new JPanel();
        criterionPanel.setPreferredSize(new Dimension(100,25));
        JLabel criterionLabel = new JLabel("Equals:");
        criterion.setPreferredSize(new Dimension(100,25));
        criterionPanel.add(criterionLabel);
        criterionPanel.add(criterion);
        box2.add(criterionPanel);
		
		filterContents.add(box2);
		
		Button goButton = new Button("GO!");
		goButton.setActionCommand("go");
		goButton.addActionListener(this);
		
		filterContents.setBorder(new EmptyBorder(0, 0, 20, 0));
		
		JPanel bottomContents = new JPanel();
		bottomContents.setLayout(new GridLayout(1,5));
		bottomContents.add(new JLabel()); // place holder
		bottomContents.add(new JLabel()); // place holder
		bottomContents.add(goButton);
		bottomContents.add(new JLabel()); // place holder
		bottomContents.add(new JLabel()); // place holder
		filterContents.add(bottomContents);
		
		filterWindow.add(filterContents);
		
		// call method to display Welcome Window
		//filterWindow.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		
		//filterWindow.setPreferredSize(new Dimension(400, 300));
		//filterWindow.pack();		
		filterWindow.setLocationRelativeTo(null);
		filterWindow.setTitle("filters - Main Page");
		filterWindow.setVisible(true);
	}
	
	/**
	 * Overriding the abstract actionPerformed method. Currently abstract, to be filled by subclasses   
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		
		}
	

	}
