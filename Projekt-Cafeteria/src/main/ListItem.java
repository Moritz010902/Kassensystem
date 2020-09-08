package main;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.JLabel;


@SuppressWarnings("serial")
public class ListItem extends JLabel {
	
	int id;
	String name;
	int amount;
	double prize;
	JLabel[] view;
	static int width = 357;
	static int height = 30;
	
	
	//Erzeugen einer Produktanzeige
	public ListItem(int id, String name, int amount, double prize) {

		this.id = id;
		this.name = name;
		this.amount = amount;
		this.prize = prize;

		
		setBackground(Color.WHITE);
		setSize(width, height);
		setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
		setOpaque(true);
		
		view = new JLabel[3];
		for (int i = 0; i < view.length; i++) {
			
			view[i] = new JLabel();
			view[i].setBackground(Color.WHITE);
			view[i].setForeground(Color.BLACK);
			view[i].setFont(new Font("Calibri", Font.BOLD, 14));
			view[i].setHorizontalAlignment(JLabel.CENTER);
			view[i].setVerticalAlignment(JLabel.CENTER);
			
		}
		
		//Anzeige der Produktmenge
		view[0].setText(String.valueOf(amount));
		view[0].setSize(getWidth()/4, height);
		view[0].setLocation(0, 0);
		
		//Anzeige des Produknamens
		view[1].setText(name);
		view[1].setSize(getWidth()/2, height);
		view[1].setLocation(getWidth()/4, 0);
		
		String str = String.valueOf(prize);
		str.replace(".", ",");
		if(str.length() == 3)
			str += "0";
		str += "€";
		
		//Anzeige des Preises
		view[2].setText(str);
		view[2].setSize(getWidth()/4, height);
		view[2].setLocation(getWidth()/4*3, 0);	
		
		for (int i = 0; i < view.length; i++)
			add(view[i]);
		
		
		addMouseListener(new MouseListener() {
			
			boolean entered = false;
			
			public void mouseEntered(MouseEvent e) {
				
				setBackground(new Color(200, 200, 200));
				entered = true;
				
			}
			
			public void mouseExited(MouseEvent e) {
				
				setBackground(Color.WHITE);
				entered = false;
					
			}
			
			public void mousePressed(MouseEvent e) {
				
				setBackground(new Color(100, 100, 100));
				
				if(Database.order_next) {
					
					//Zurücksetzen aller Werte
					Database.suborders.clear();
					GUI.orderlist_labels.clear();
					GUI.orderlist_content.removeAll();
					GUI.orderlist.setViewportView(GUI.orderlist_content);
					Database.order_next = false;
					
				}
				
				//Zurücksetzen der graphischen Erscheinung
				for(int i=0; i < Database.lists.length; i++) {
					for (int i2 = 0; i2 < Database.lists[i].size(); i2++) {
						if(((ListItem) Database.lists[i].get(i2)).getID() == Main.selectedID)
							((ListItem) Database.lists[i].get(i2)).setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
					}
				}
				
				GUI.amount.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));
				GUI.orderlist.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
				GUI.enterID.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
				GUI.enterID.setText("");
				
				setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));
				Main.selectedID = id;
				
				//Aktivierung des Nummern-Eingabefeldes für die Produktmenge
				GUI.focus = 0;
				
				//Anzeigen der Werte des ausgewählten Produkts
				GUI.selected.setText(name);
				GUI.amount.setForeground(Color.LIGHT_GRAY);
				GUI.amount.setText(String.valueOf(1));
				
				String str = String.valueOf(prize);
				if(str.substring(str.length()-2, str.length()-1).equals("."))
					str += "0";
				str += "€";
				
				GUI.prize.setText(str);
				
			}
			
			public void mouseReleased(MouseEvent e) {
				
				if(entered) {
					setBackground(new Color(200, 200, 200));
				} else {
					setBackground(Color.WHITE);
				}
				
			}

			public void mouseClicked(MouseEvent e) {}
			
		});
		
	}
	
	public int getID() {
		
		return id;
		
	}
	
	public String getName() {
		
		return name;
		
	}
	
	public int getAmount() {
		
		return amount;
		
	}
	
	public double getPrize() {
		
		return prize;
		
	}
	
	//Methode zur Aktualisierung der Mengenanzeige
	public void update() {
		
		try {
			
			Database.read("SELECT `amount` FROM `product` WHERE productID=" + id);
			if(Database.result.next())
				amount = Database.result.getInt(1);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		//Aktualisierung der Mengenanzeige
		view[0].setText(String.valueOf(amount));
		
	}
	
	public void paintComponent(Graphics g) {
		
		super.paintComponent(g);
		Graphics2D g2D = (Graphics2D) g;
		
		
		g2D.setColor(Color.BLACK);
		g2D.setStroke(new BasicStroke(1));
		
		g2D.drawLine(view[1].getX(), 4, view[1].getX(), getHeight()-6);
		g2D.drawLine(view[2].getX(), 4, view[2].getX(), getHeight()-6);
		
	}

}
