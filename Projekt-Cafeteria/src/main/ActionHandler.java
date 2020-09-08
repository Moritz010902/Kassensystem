package main;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.BorderFactory;


public class ActionHandler implements ActionListener, MouseListener {

	public void actionPerformed(ActionEvent e) {

		
		if(e.getSource() == GUI.right) {
			
			//Wechseln der Produktkategorie
			if(!GUI.category.getText().equals(GUI.categories[GUI.categories.length-1])) {	
				for(int i=0; i < GUI.categories.length; i++) {	
					if(GUI.category.getText().equals(GUI.categories[i])) {
						
						//Aktualisierung der Text- und Listenanzeige
						GUI.category.setText(GUI.categories[i+1]);
						GUI.foodlist.setViewportView(GUI.list[i+2]);
						break;
						
					}		
				}			
			} else {	
				
				//Aktualisierung der Text- und Listenanzeige
				GUI.category.setText(GUI.categories[0]);
				GUI.foodlist.setViewportView(GUI.list[1]);
				
			}
			
		}
		
		
		if(e.getSource() == GUI.left) {
			
			//Wechseln der Produktkategorie
			if(!GUI.category.getText().equals(GUI.categories[0])) {
				for(int i=0; i < GUI.categories.length; i++) {	
					if(GUI.category.getText().equals(GUI.categories[i])) {
						
						//Aktualisierung der Text- und Listenanzeige
						GUI.category.setText(GUI.categories[i-1]);
						GUI.foodlist.setViewportView(GUI.list[i]);
						break;
						
					}
				}
			} else {
				
				//Aktualisierung der Text- und Listenanzeige
				GUI.category.setText(GUI.categories[GUI.categories.length-1]);
				GUI.foodlist.setViewportView(GUI.list[GUI.list.length-1]);
				
			}
			
		}
		
		
		if(e.getSource() == GUI.delete) {
			
			//Textbearbeitung bei der Mengenanzeige eines ausgewählzen Produktes
			if(GUI.focus == 0) {
				
				//Aktualisierung der Mengenanzeige
				String str = GUI.amount.getText();
				if(!str.equals(""))
					GUI.amount.setText(str.substring(0, str.length()-1));
				
				String s = GUI.amount.getText();
				for(int i2=0; i2 < Database.lists.length; i2++) {
					for (int i3 = 0; i3 < Database.lists[i2].size(); i3++) {
						
						if(((ListItem) Database.lists[i2].get(i3)).getID() == Main.selectedID) {
							
							if(!s.equals("")) {
								
								//Überprüfung auf Vorhandensein der gewünschen Menge
								if(Integer.parseInt(s) > ((ListItem) Database.lists[i2].get(i3)).getAmount()) {
									GUI.amount.setForeground(Color.RED);
								} else if(GUI.amount.getForeground() != Color.BLACK) {
									GUI.amount.setForeground(Color.BLACK);
								}
								
								//Atualisierung der Preisanzeige
								double d = ((ListItem) Database.lists[i2].get(i3)).getPrize() * Integer.parseInt(s);
								d = Math.round(d*100)/100.0;
								String str2 = String.valueOf(d);
								if(str2.substring(str2.length()-2, str2.length()-1).equals("."))
									str2 += "0";
								str2 += "€";
								
								GUI.prize.setText(str2);
							
							} else {
								
								GUI.prize.setText("0.00€");
								
							}
							
						}
						
					}
				}
			
				//Textbearbeitung der Kunden-ID Eingabefläche
			} else if(GUI.focus == 1) {
				
				String str = GUI.enterID.getText();
				if(!str.equals(""))
					GUI.enterID.setText(str.substring(0, str.length()-1));
				
			}
			
		}
		
		if(e.getSource() == GUI.enter) {
			
			if(!GUI.amount.getText().equals("")) {
				
				int a = Integer.parseInt(GUI.amount.getText());
				for(int i=0; i < Database.lists.length; i++) {
					for (int i2 = 0; i2 < Database.lists[i].size(); i2++) {
						
						if(((ListItem) Database.lists[i].get(i2)).getID() == Main.selectedID) {

							//Abschließen einer Unterbestellung
							String str = ((ListItem) Database.lists[i].get(i2)).getName();
							double d = ((ListItem) Database.lists[i].get(i2)).getPrize()*a;
							d = Math.round(d*100)/100.0;
							Database.addSuborder(Database.orderID, Main.selectedID, str, a, d);
							
						}
						
					}
				}
				
			}
			
		}
		
		
		if(e.getSource() == GUI.finish) {
			
			//Überprüfung auf Korrektheit der Kunden-ID
			boolean b = false;
			Database.read("SELECT customerID FROM customer;");
			ArrayList<Integer> list = new ArrayList<Integer>();
			String str = GUI.enterID.getText();
			
			try {
				
				while(Database.result.next())
					list.add(Database.result.getInt(1));
				
				if(!str.equals("")) {
					for (int i = 0; i < list.size(); i++) {
						
						if(list.get(i) == Integer.parseInt(str)) {
							b = true;
							break;
						}
						
					}
				}
				
			} catch (SQLException e1) {
					e1.printStackTrace();
			}
			
			if(b && !Database.order_next) {
				
				//Abließen der gesamten Bestellung
				GUI.orderlist.setBorder(BorderFactory.createLineBorder(new Color(140, 230, 140), 2));
				GUI.enterID.setBorder(BorderFactory.createLineBorder(new Color(140, 230, 140), 2));
				Database.finishOrder(Integer.parseInt(str));
				Database.order_next = true;
				
			} else {
			
				//Graphische Fehleranzeige
				if(GUI.orderlist_labels.size() != 0) {
					GUI.orderlist.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
					GUI.enterID.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
				} 
		
			}
			
		}
		
		
		if(e.getSource() == GUI.cancel) {
			
			//Zurücksetzen der Werte der bestehenden Bestellung
			Database.suborders.clear();
			GUI.orderlist_labels.clear();
			GUI.orderlist_content.removeAll();
			GUI.orderlist.setViewportView(GUI.orderlist_content);
			
			
			for(int i=0; i < Database.lists.length; i++) {
				for (int i2 = 0; i2 < Database.lists[i].size(); i2++) {
					if(((ListItem) Database.lists[i].get(i2)).getID() == Main.selectedID)
						((ListItem) Database.lists[i].get(i2)).setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
				}
			}
			
			GUI.amount.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
			GUI.orderlist.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
			GUI.enterID.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
			
			GUI.selected.setText("");
			GUI.amount.setText("");
			GUI.prize.setText("");
			GUI.enterID.setText("");
			
			//Deaktivierung des Nummern-Eingabefeldes
			GUI.focus = -1;
			
		}
		
	}
	

	public void mousePressed(MouseEvent e) {

		if(e.getSource() == GUI.enterID) {
			
			//Aktivierung des Nummern-Eingabefeldes für Eingabe der Kunden-ID
			GUI.focus = 1;
			GUI.amount.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
			GUI.orderlist.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
			GUI.enterID.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));
			
			//Zurücksetzen der Anzeigen eines ausgewählten Produkts
			for(int i2=0; i2 < Database.lists.length; i2++) {
				for (int i3 = 0; i3 < Database.lists[i2].size(); i3++) {
					
					if(((ListItem) Database.lists[i2].get(i3)).getID() == Main.selectedID)
						((ListItem) Database.lists[i2].get(i3)).setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
					
				}
			}
			
			GUI.selected.setText("");
			GUI.amount.setText("");
			GUI.prize.setText("");
			
		}
		
	}

	public void mouseClicked(MouseEvent e) {}
	
	public void mouseReleased(MouseEvent e) {}

	public void mouseEntered(MouseEvent e) {}

	public void mouseExited(MouseEvent e) {}

}
