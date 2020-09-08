package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;


public class GUI {

	static JFrame frame;
	static JLabel category;
	static String[] categories;
	static JButton right, left;
	static JScrollPane drinklist, foodlist;
	static JList<ListItem>[] list;
	static JLabel selected, amount, prize; 
	static JPanel numberfield;
	static JLabel[] numbers;
	static JButton enter, delete;
	static JScrollPane orderlist;
	static JList<JLabel> orderlist_content;
	static ArrayList<JLabel> orderlist_labels;
	static JLabel enterID;
	static JButton finish, cancel;
	static ActionHandler listener;
	static int focus;
	static ImageFormatter imageformatter;
	
	
	public static void create() {
		
		//Erzeugen des Pogramm Fensters
		frame = new JFrame("Cafeteria");
		frame.setSize(1920, 1080);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.getContentPane().setBackground(Color.WHITE);
		frame.setLayout(null);
		
		
		//Intialisieren aller Komponente
		init();
		
		
		frame.addWindowListener(new WindowListener() {
			
			public void windowClosing(WindowEvent e) {
				
				try {
					
					Database.result.close();
					Database.statement.close();
					Database.connection.close();
					
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				
			}
			
			public void windowOpened(WindowEvent e) {}
			
			public void windowIconified(WindowEvent e) {}
			
			public void windowDeiconified(WindowEvent e) {}
			
			public void windowDeactivated(WindowEvent e) {}
			
			public void windowClosed(WindowEvent e) {}
			
			public void windowActivated(WindowEvent e) {}
			
		});
		
		frame.setVisible(true);
		
	}
	

	@SuppressWarnings("unchecked")
	private static void init() {
		
		int x = 500;
		int y = 100;
		
		
		//Laden der Datanbankverbindung
		new Database();
		//Initialisieren einer Variable, mit dessen Hilfe die Eingabe des Nummernfeldes navigiert wird
		focus = 0;
		listener = new ActionHandler();
		imageformatter = new ImageFormatter();
		
		categories = new String[3];
		categories[0] = "Sandwiches";
		categories[1] = "Snacks";
		categories[2] = "Eis";
		
		
		//Button zum wechseln der Speisenkategorie
		left = new JButton();
		left.setSize(40, 40);
		left.setLocation(x, y+5);
		left.setFocusPainted(false);
		left.setBorderPainted(false);
		left.setBackground(Color.WHITE);
		left.setIcon(imageformatter.getImage(left.getWidth(), left.getHeight(), "/main/arrow_left.png"));
		left.addActionListener(listener);
		x += left.getWidth()+10;
		
		
		//Anzeige der aktuellen Speisenkategorie
		category = new JLabel();
		category.setSize(180, 50);
		category.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
		category.setLocation(x, y);
		category.setForeground(Color.BLACK);
		category.setHorizontalAlignment(JLabel.CENTER);
		category.setVerticalAlignment(JLabel.CENTER);
		category.setFont(new Font("Calibri", Font.BOLD, 20));
		category.setText(categories[0]);
		x += category.getWidth()+10;
		
		
		//Button zum wechseln der Speisenkategorie
		right = new JButton();
		right.setSize(40, 40);
		right.setLocation(x, y+5);
		right.setFocusPainted(false);
		right.setBorderPainted(false);
		right.setBackground(Color.WHITE);
		right.setIcon(imageformatter.getImage(right.getWidth(), right.getHeight(), "/main/arrow_right.png"));
		right.addActionListener(listener);
		x = x + right.getWidth() - left.getX() + 100;
		y += category.getHeight()+10;
		
		
		//Initialisieren der Pruduktlisten
		list = new JList[Database.lists.length];
		for(int i=0; i < list.length; i++) {			

			list[i] = new JList<ListItem>();
			list[i].setPreferredSize(new Dimension(x-4, Database.lists[i].size()*(ListItem.height+1)+3));
			
			for (int i2 = 0; i2 < Database.lists[i].size(); i2++) {
				((ListItem) Database.lists[i].get(i2)).setLocation(2, 2 + (ListItem.height+1)*i2);
				list[i].add((ListItem) Database.lists[i].get(i2));
			}
				
		}

		//Anzeige der Produktauswahl für Getränke
		drinklist = new JScrollPane();
		drinklist.setSize(x, 600);
		drinklist.setPreferredSize(drinklist.getSize());
		drinklist.setLocation(50, y);
		drinklist.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
		drinklist.setVerticalScrollBar(new JScrollBar());
		drinklist.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		drinklist.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		drinklist.setViewportView(list[0]);
		
		
		//Anzeige der Produktauswahl für Speisen
		foodlist = new JScrollPane();
		foodlist.setSize(x, 600);
		foodlist.setLocation(x + left.getX() - drinklist.getWidth() -50, y);
		foodlist.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
		foodlist.setVerticalScrollBar(new JScrollBar());
		foodlist.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		foodlist.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		foodlist.setViewportView(list[1]);
		x = foodlist.getX() + foodlist.getWidth() + 100;
		
		
		//Anzeige des Namens eines ausgewählten Produktes
		selected = new JLabel();
		selected.setSize(400, 90);
		selected.setLocation(x, y);
		selected.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
		selected.setForeground(Color.BLACK);
		selected.setFont(new Font("Calibri", Font.BOLD, 24));
		selected.setHorizontalAlignment(JLabel.CENTER);
		y += selected.getHeight()+10;
		
		//Anzeige der Bestellmenge eines ausgewählten Produktes
		amount = new JLabel();
		amount.setSize(150, 50);
		amount.setLocation(x, y);
		amount.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
		amount.setForeground(Color.BLACK);
		amount.setFont(new Font("Calibri", Font.BOLD, 18));
		amount.setHorizontalAlignment(JLabel.CENTER);
		
		//Anzeige des Preises eines ausgewählten Produktes bei entsprechender Menge
		prize = new JLabel();
		prize.setSize(240, 50);
		prize.setLocation(x+amount.getWidth()+10, y);
		prize.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
		prize.setForeground(Color.BLACK);
		prize.setFont(new Font("Calibri", Font.BOLD, 18));
		prize.setHorizontalAlignment(JLabel.CENTER);
		y += prize.getHeight()+42;
		
		
		//Erzeugen eines Nummern-Eingabe-Feldes
		numberfield = new JPanel();
		numberfield.setSize(400, 340);
		numberfield.setLocation(x, y);
		numberfield.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
		numberfield.setLayout(new GridBagLayout());
		y += numberfield.getHeight()+8;
		
		
		numbers = new JLabel[10];
		for(int i=0; i < numbers.length; i++) {
			
			numbers[i] = new JLabel();
			numbers[i].setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
			numbers[i].setOpaque(true);
			numbers[i].setBackground(Color.WHITE);
			numbers[i].setForeground(Color.BLACK);
			numbers[i].setHorizontalAlignment(JLabel.CENTER);
			numbers[i].setFont(new Font("Calibri", Font.BOLD, 24));
			if(i < 9) {
				numbers[i].setText(String.valueOf(i+1));
			} else {
				numbers[i].setText(String.valueOf(0));
			}
			numbers[i].addMouseListener(new MouseListener() {
				
				boolean entered = false;
				
				public void mouseEntered(MouseEvent e) {
					
					for(int i=0; i < numbers.length; i++) {
						if(e.getSource() == numbers[i]) {
							numbers[i].setBackground(new Color(200, 200, 200));
							entered = true;
						}
					}
					
				}
				
				public void mouseExited(MouseEvent e) {
					
					for(int i=0; i < numbers.length; i++) {
						if(e.getSource() == numbers[i]) {
							numbers[i].setBackground(Color.WHITE);
							entered = false;
						}
					}
						
				}
				
				public void mousePressed(MouseEvent e) {
					
					for(int i=0; i < numbers.length; i++) {
						if(e.getSource() == numbers[i]) {
							
							numbers[i].setBackground(new Color(100, 100, 100));
							
							//Textbearbeitung der Mengenanzeige [(JLabel) amount]
							if(focus == 0) {
								
								//Aufhebung der Standart-Produktmenge
								if(amount.getForeground() == Color.LIGHT_GRAY) {
									
									amount.setForeground(Color.BLACK);
									amount.setText("");
									
								}
								
								//Ergänzung des Label-Inhalts um die neue Eingabe
								String str = amount.getText();
								if(str.length() <= 3 && Main.selectedID != -1) {
									if(i < 9) {
										amount.setText(str+String.valueOf(i+1));
									} else {
										if(!str.equals(""))
											amount.setText(str+String.valueOf(0));
									}
								}
								
								
								//Überprüfung der Verfügbarkeit des ausgewählten Produktes
								String s = amount.getText();
								if(!s.equals("")) {
									
									for(int i2=0; i2 < Database.lists.length; i2++) {
										for (int i3 = 0; i3 < Database.lists[i2].size(); i3++) {
											
											if(((ListItem) Database.lists[i2].get(i3)).getID() == Main.selectedID) {
												
												if(Integer.parseInt(s) > ((ListItem) Database.lists[i2].get(i3)).getAmount()) {
													amount.setForeground(Color.RED);
												} else if(amount.getForeground() != Color.BLACK) {
													amount.setForeground(Color.BLACK);
												}
												
												double d = ((ListItem) Database.lists[i2].get(i3)).getPrize() * Integer.parseInt(s);
												d = Math.round(d*100)/100.0;
												String str2 = String.valueOf(d);
												if(str2.substring(str2.length()-2, str2.length()-1).equals("."))
													str2 += "0";
												str2 += "€";
												
												GUI.prize.setText(str2);
												
											}
											
										}
									}
									
								}
								
								//Textbearbeitung des Eingabefeldes der Kunden-ID [(JLabel) enterID]
							} else if(focus == 1) {
								
								//Ergänzung des Label-Inhalts um die neue Eingabe
								String str = enterID.getText();
								if(i < 9) {
									enterID.setText(str+String.valueOf(i+1));
								} else {
									enterID.setText(str+String.valueOf(0));
								}
								
								//Überprüfung auf Rabatt
								((EndPrizeView) orderlist_content.getComponent(orderlist_content.getComponentCount()-1)).update();
								
							}
							
						}
					}
					
				}
				
				
				public void mouseReleased(MouseEvent e) {
					
					for(int i=0; i < numbers.length; i++) {
						if(e.getSource() == numbers[i]) {
							if(entered) {
								numbers[i].setBackground(new Color(200, 200, 200));
							} else {
								numbers[i].setBackground(Color.WHITE);
							}
						}
					}
					
				}

				public void mouseClicked(MouseEvent e) {}
				
			});
			
		}
		
		//Button zur Textbearteitung (Entfernen eines Zeichens)
		delete = new JButton();
		delete.setFocusPainted(false);
		delete.setBackground(new Color(240, 240, 240));
		delete.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
		delete.setIcon(imageformatter.getImage(26, 26, "/main/delete.png"));
		delete.addActionListener(listener);
		
		
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 0.5;
		c.weighty = 0.5;
		
		int gridx = 0;
		int gridy = 0;
		for(int i=0; i < numbers.length; i++) {
			
			if(gridy < 4) {
				if(gridx < 3) {
					
					c.fill = GridBagConstraints.BOTH;					
					c.gridx = gridx;
					c.gridy = gridy;
					gridx++;
					
					numberfield.add(numbers[i], c);
					
				} else {
					
					gridy++;
					gridx = 0;
					i--;
					
				}
			}
			
		}
		
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 1;
		c.gridy = 3;
		c.gridwidth = 2;
		numberfield.add(delete, c);
		
		
		//Button zum Bestätigen einer Unterbestellung
		enter = new JButton();
		enter.setSize(numberfield.getWidth(), 60);
		enter.setLocation(x, y);
		enter.setFocusPainted(false);
		enter.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
		enter.setBackground(new Color(200, 235, 200));
		enter.setForeground(Color.BLACK);
		enter.setFont(new Font("Calibri", Font.BOLD, 24));
		enter.setText("Bestätigen");
		enter.addActionListener(listener);
		x += enter.getWidth() + 100;
		y = drinklist.getY();

		
		orderlist_labels =  new ArrayList<JLabel>();
		orderlist_content = new JList<JLabel>();
		orderlist_content.setFixedCellHeight(ListItem.height);
		orderlist_content.setPreferredSize(new Dimension(drinklist.getWidth()-4, ListItem.height));
		
		//Anzeige der Bestellungen
		orderlist = new JScrollPane();
		orderlist.setSize(drinklist.getSize());
		orderlist.setPreferredSize(orderlist.getSize());
		orderlist.setLocation(x, y);
		orderlist.setForeground(Color.WHITE);
		orderlist.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
		orderlist.setVerticalScrollBar(new JScrollBar());
		orderlist.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		orderlist.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		orderlist.setViewportView(orderlist_content);
		y += orderlist.getHeight()+10;

		
		//Eingabefeld für die Kunden-ID
		enterID = new JLabel();
		enterID.setSize(orderlist.getWidth(), amount.getHeight());
		enterID.setLocation(x, y);
		enterID.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
		enterID.setForeground(Color.BLACK);
		enterID.setFont(new Font("Calibri", Font.BOLD, 18));
		enterID.setHorizontalAlignment(JLabel.CENTER);
		enterID.addMouseListener(listener);
		y += enterID.getHeight()+20;
		
		
		//Button zum Abschließen der gesamten Bestellung
		finish = new JButton();
		finish.setSize(enterID.getWidth()/3*2 -6, enter.getHeight());
		finish.setLocation(x, y);
		finish.setFocusPainted(false);
		finish.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
		finish.setBackground(enter.getBackground());
		finish.setForeground(Color.BLACK);
		finish.setFont(new Font("Calibri", Font.BOLD, 20));
		finish.setText("Abschließen");
		finish.addActionListener(listener);
		x += finish.getWidth()+6;
		
		
		//Button zum Abbrechen und Zurücksetzen des gesamten Bestellvorgangs
		cancel = new JButton();
		cancel.setSize(enterID.getWidth()/3, enter.getHeight());
		cancel.setLocation(x, y);
		cancel.setFocusPainted(false);
		cancel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
		cancel.setBackground(new Color(235, 200, 200));
		cancel.setForeground(Color.BLACK);
		cancel.setFont(finish.getFont());
		cancel.setText("Abbrechen");
		cancel.addActionListener(listener);
		
		
		
		frame.add(right);
		frame.add(category);
		frame.add(left);
		frame.add(drinklist);
		frame.add(foodlist);
		
		frame.add(selected);
		frame.add(amount);
		frame.add(prize);
		
		frame.add(numberfield);
		frame.add(enter);

		frame.add(orderlist);
		frame.add(enterID);
		frame.add(finish);
		frame.add(cancel);
		
	}
	
}
