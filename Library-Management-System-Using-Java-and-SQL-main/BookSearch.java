package lms;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;


public class BookSearch extends JFrame {


	private JButton search = new JButton("Search!");
	private JButton seeAll = new JButton("See All Books");
	private String keywordOption = "Title"; // default
	JTextField criterionEntry = new JTextField();
	

	public BookSearch(){
		
		JPanel display = new JPanel();
		display.setLayout(new BorderLayout(5,5));
		
		JPanel north = new JPanel();
		north.setLayout(new GridLayout(2,1,10,5));
		JLabel header = new JLabel("SEARCH FOR BOOKS");
		header.setHorizontalAlignment(SwingConstants.CENTER);
		header.setFont(new Font("Arial", Font.BOLD, 15));
		north.add(header);
		north.add(seeAll);
		
		display.add(north, BorderLayout.NORTH);
		
		JPanel west = new JPanel();
		west.setLayout(new GridLayout(2,1,10,5));
		west.add(new JLabel("Search by:"));
		
		
		String [] keywords = {"Title", "Author", "Genre"};
		JComboBox<String> searchDropDown = new JComboBox<String>(keywords);
		searchDropDown.setSelectedIndex(0);
		searchDropDown.addActionListener(new ActionListener(){  
			public void actionPerformed(ActionEvent e){  
				// set value of selectedOption
				// https://stackoverflow.com/questions/4962416/preferred-way-of-getting-the-selected-item-of-a-jcombobox
				keywordOption = String.valueOf(searchDropDown.getSelectedItem());  
		}  
		});
		
		west.add(searchDropDown);
		
		display.add(west, BorderLayout.WEST);
		
		JPanel center = new JPanel();
		center.setLayout(new GridLayout(2,1,10,5));
		center.add(new JPanel()); // spacer
		
		center.add(criterionEntry);
		
		display.add(center, BorderLayout.CENTER);
		
		JPanel south = new JPanel();
		south.setLayout(new GridLayout(1,3,25,5));
		south.add(new JLabel());	//spacer
		south.add(search);	//spacer
		south.add(new JLabel());	//spacer
		
		display.add(south, BorderLayout.SOUTH);
		
		
		add(display);
		

		UpdateListener buttonPress = new UpdateListener();
		search.addActionListener(buttonPress);

		search.addActionListener(e -> this.dispose());	// closes search box
		seeAll.addActionListener(buttonPress);
		seeAll.addActionListener(e -> this.dispose());
	}
	

	public static void main(String[] args){
		BookSearch frame = new BookSearch();
		frame.setDefaultCloseOperation(HIDE_ON_CLOSE);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setTitle("SEARCH");
		frame.setVisible(true);		
	}
	

	class UpdateListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getSource()==search){
				// get info from text entry field
				String criterion = criterionEntry.getText().trim();	// remove excess white space on either side of text
			
				Patron.bookSearch(null, keywordOption.toLowerCase(), criterion, "window");
			}
			
			if(e.getSource()==seeAll){
				Patron.bookSearch(null, null, null, "window");
			}
		}
		
	}
	
	
}
