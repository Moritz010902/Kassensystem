package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;


public class Database {
	
	static Connection connection;
	static Statement statement;
	static ResultSet result;
	@SuppressWarnings("rawtypes")
	static ArrayList[] lists = new ArrayList[4];
	static ArrayList<ListItem> drinks = new ArrayList<ListItem>();
	static ArrayList<ListItem> sandwiches = new ArrayList<ListItem>();
	static ArrayList<ListItem> icecream = new ArrayList<ListItem>();
	static ArrayList<ListItem> snacks = new ArrayList<ListItem>();
	static ArrayList<SubOrder> suborders = new ArrayList<SubOrder>();
	static int orderID;
	static int suborderID;
	static boolean order_next;
	
	
	public Database() {
		
		try {
			
			
			//Erstellen einer Connection
			connection = DriverManager.getConnection("jdbc:mysql://localhost/cafeteria","root","");
			connection.setReadOnly(false);
			
			statement = connection.createStatement();
			//Auslesen der Produkte in der Datenbank
			read("SELECT * FROM `product`");
			
			//Zuordnen der Produkte in Kategorien (Getränk, Sandwich, Snack, Eis)
			while(result.next()) {
				
				String str = String.valueOf(result.getInt(1));
				str = str.substring(0, 1);
				int index = Integer.parseInt(str);
				
				if(index == 1) {
					
					drinks.add(new ListItem(result.getInt(1), result.getString(2), result.getInt(3), result.getDouble(4)));
					
				} else if(index == 2) {
					
					sandwiches.add(new ListItem(result.getInt(1), result.getString(2), result.getInt(3), result.getDouble(4)));
					
				} else if(index == 3) {
					
					snacks.add(new ListItem(result.getInt(1), result.getString(2), result.getInt(3), result.getDouble(4)));
					
				} else if(index == 4) {
					
					icecream.add(new ListItem(result.getInt(1), result.getString(2), result.getInt(3), result.getDouble(4)));
					
				}
				
			}
			
			lists[0] = drinks;
			lists[1] = sandwiches;
			lists[2] = snacks;
			lists[3] = icecream;
			
			
			//Ermitteln der orderID für die nächste Bestellung
			read("SELECT `orderID` FROM `order` ORDER BY `orderID` DESC;");
			if(result.next()) {
				orderID = result.getInt(1) + 1;
			} else {
				orderID = 1;
			}
			
			//Ermitteln der suborderID für die nächste Unterbestellung
			read("SELECT `suborderID` FROM `suborder` ORDER BY `suborderID` DESC;");
			if(result.next()) {
				suborderID = result.getInt(1) + 1;
			} else {
				suborderID = 1;
			}
			
			order_next = false;
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	//Methode zum Auslesen von Werten aus der Datenbank
	public static void read(String command) {
		
		try {
			result = statement.executeQuery(command);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	
	//Methode zum Bearbeiten der Datenbank
	public static void update(String command) {
		
		try {
			statement.executeUpdate(command);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	
	//Methode zum Abschließen einer Unterbestellung
	public static void addSuborder(int orderID, int productID, String name, int amount, double prize) {
		
		//Speichern der Unterbestellung
		suborders.add(new SubOrder(productID, amount, prize));
		
		String p = String.valueOf(prize);
		if(p.substring(p.length()-2, p.length()-1).equals("."))
			p += "0";
		p += "€";
		
		String[] str = {String.valueOf(amount), name, String.valueOf(p)};
		
		//Graphische Darstellung der Unterbestellung
		JLabel label = new JLabel();
		label.setBackground(Color.WHITE);
		label.setSize(ListItem.width, ListItem.height);
		label.setBorder(BorderFactory.createEmptyBorder(5, 30, 0, 0));
		label.setOpaque(true);
		
		for(int i=0; i < str.length; i++) {
			
			JLabel text = new JLabel();
			
			if(i == 0) {
				text.setSize(label.getWidth()/4, ListItem.height);
				text.setLocation(0, 0);
				text.setHorizontalAlignment(JLabel.CENTER);
			} else if(i == 1){
				text.setSize(label.getWidth()/2, ListItem.height);
				text.setLocation(label.getWidth()/4, 0);
				text.setHorizontalAlignment(JLabel.LEFT);
			} else if(i == 2){
				text.setSize(label.getWidth()/4-20, ListItem.height);
				text.setLocation(label.getWidth()/4*3+20, 0);
				text.setHorizontalAlignment(JLabel.LEFT);
			}
			
			text.setForeground(Color.BLACK);
			text.setFont(new Font("Calibri", Font.BOLD, 14));
			text.setText(str[i]);
			label.add(text);
			
		}
		
		//Hinzufügen der Bestellungs - und Preisübersicht
		if(GUI.orderlist_labels.size() > 0)
			GUI.orderlist_labels.remove(GUI.orderlist_labels.get(GUI.orderlist_labels.size()-1));
		
		GUI.orderlist_labels.add(label);
		GUI.orderlist_labels.add(new EndPrizeView());

		GUI.orderlist_content = new JList<JLabel>();
		GUI.orderlist_content.setPreferredSize(new Dimension(GUI.drinklist.getWidth()-4, (GUI.orderlist_labels.size()-1)*(ListItem.height+1)+EndPrizeView.height+3));
		
		for(int i=0; i < GUI.orderlist_labels.size(); i++) {
			
			GUI.orderlist_labels.get(i).setLocation(2, 2 + (ListItem.height+1)*i);
			GUI.orderlist_content.add(GUI.orderlist_labels.get(i));
			
		}
		
		GUI.orderlist.setViewportView(GUI.orderlist_content);
		
		
		//Zurücksetzen der Auswahlfelder
		GUI.selected.setText("");
		GUI.amount.setText("");
		GUI.prize.setText("");
		
		for(int i=0; i < Database.lists.length; i++) {
			for (int i2 = 0; i2 < Database.lists[i].size(); i2++) {
				if(((ListItem) Database.lists[i].get(i2)).getID() == Main.selectedID)
					((ListItem) Database.lists[i].get(i2)).setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
			}
		}
		
		GUI.amount.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
		
		//Deaktivierung der Nummern-Eingabefläche
		GUI.focus = -1;
		
	}
	
	
	//Methode zum Abschließen der gesamten Bestellung
	public static void finishOrder(int customerID) {

		try {
			
			if(suborders.size() > 0) {			
				
				for(int i=0; i < suborders.size(); i++) {
					
					int productID = suborders.get(i).getProductID();
					int amount = suborders.get(i).getAmount();
					
					
					//Auslesen der vorherigen Produktmenge
					int a = amount;
					read("SELECT `amount` FROM `product` WHERE productID=" + suborders.get(i).getProductID());
					if(result.next()) 
						a = result.getInt(1) - amount;
					
					//Aktualisierung der Produktmenge in der Datenbank
					update("UPDATE `product` SET amount=" + a + " WHERE productID=" + productID + ";");
					
					for (int i2 = 0; i2 < lists.length; i2++) {
						for (int i3 = 0; i3 < lists[i2].size(); i3++) {
							
							//Aktualisierung der Produktanzeige
							if(((ListItem) lists[i2].get(i3)).getID() == productID)
								((ListItem) lists[i2].get(i3)).update();
								
						}
					}
					
					//Hinzufügen der Unterbestellung in der Datenbank
					update("INSERT INTO `suborder` VALUES (" + suborderID + ", " + orderID + ", "  + productID + ", "  + amount + ");");
					suborderID++;
					
				}
				
				//Ermitteln der Uhrzeit
				long now = Calendar.getInstance().getTimeInMillis();
				
				//Berechnung des Gesamtpreises
				double totalprize = ((EndPrizeView) GUI.orderlist_content.getComponent(GUI.orderlist_content.getComponentCount()-1)).getPrize();
				totalprize = Math.round(totalprize*100)/100.0;
				
				//Hinzufügen der Bestellung in die Datenbank
				update("INSERT INTO `order` VALUES (" + orderID + ", "  + now + ", "  + totalprize + ", " + customerID + ");");
				orderID++;
			
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	//Innere Klasse zur Abspeicherung von Unterbestellungen
	static class SubOrder {
		
		int productID;
		int amount;
		double prize;
		
		public SubOrder(int productID, int amount, double prize) {
			
			this.productID = productID;
			this.amount = amount;
			this.prize = prize;
			
		}
		
		public int getProductID() {
			
			return productID;
			
		}
		
		public int getAmount() {
			
			return amount;
			
		}
		
		public double getPrize() {
			
			return prize;
			
		}
		
	}

}
