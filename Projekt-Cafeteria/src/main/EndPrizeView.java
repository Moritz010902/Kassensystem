package main;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.JLabel;


@SuppressWarnings("serial")
public class EndPrizeView extends JLabel {

	static final int height = 80;
	boolean rebateactive;
	double p = 0;
	double reduced = 0;
	JLabel text, prize;
	JLabel rebate, prize2;
	
	
	//Erzeugnug einer Preisübersicht
	public EndPrizeView() {
		
		setBackground(Color.WHITE);
		setSize(ListItem.width, height);
		setOpaque(true);
		
		
		for (int i = 0; i < Database.suborders.size(); i++) {
			p += Database.suborders.get(i).getPrize();
		}
		
		p = Math.round(p*100)/100.0;
		String str1 = String.valueOf(p);
		if(str1.substring(str1.length()-2, str1.length()-1).equals("."))
			str1 += "0";
		str1 += "€";
		
		reduced = p * 0.9;
		reduced = Math.round(reduced*100)/100.0;
		String str2 = String.valueOf(reduced);
		if(str2.substring(str2.length()-2, str2.length()-1).equals("."))
			str2 += "0";
		str2 += "€";
		
		
		//Textanzeige: "Gesamtsumme"
		text = new JLabel();
		text.setSize(getWidth()/4, (height-25)/2);
		text.setLocation(getWidth()/4, 25);
		text.setHorizontalAlignment(JLabel.LEFT);
		text.setForeground(Color.BLACK);
		text.setFont(new Font("Calibri", Font.BOLD, 14));
		text.setText("Gesamtsumme");
		
		//Anzeige des Gesamtpeises
		prize = new JLabel();
		prize.setSize(getWidth()/4-20, (height-25)/2);
		prize.setLocation(getWidth()/4*3+20, 25);
		prize.setHorizontalAlignment(JLabel.LEFT);
		prize.setForeground(Color.BLACK);
		prize.setFont(new Font("Calibri", Font.BOLD, 14));
		prize.setText(str1);
		
		
		//Textanzeige: "-10% Rabatt"
		rebate = new JLabel();
		rebate.setSize(getWidth()/4, (height-25)/2);
		rebate.setLocation(getWidth()/4, text.getHeight()+25);
		rebate.setHorizontalAlignment(JLabel.LEFT);
		rebate.setForeground(Color.BLACK);
		rebate.setFont(new Font("Calibri", Font.ITALIC, 12));
		rebate.setText("-10% Rabatt");
		
		//Anzeige des Gesamtpeises abzüglich Rabatt
		prize2 = new JLabel();
		prize2.setSize(getWidth()/4-20, (height-25)/2);
		prize2.setLocation(getWidth()/4*3+20, text.getHeight()+25);
		prize2.setHorizontalAlignment(JLabel.LEFT);
		prize2.setForeground(Color.BLACK);
		prize2.setFont(new Font("Calibri", Font.BOLD, 14));
		prize2.setText(str2);

		
		update();
		
		
		add(text);
		add(prize);
		
		if(rebateactive) {
			add(rebate);
			add(prize2);
		}
		
	}
	
	public double getPrize() {
		
		update();
		
		if(rebateactive) {
			return reduced;
		} else {
			return p;
		}

	}
	
	//Methode um zu Ermitteln, ob für diese Bestellung ein Rabatt gegeben wird
	public void update() {
		
		boolean b = false;
		Database.read("SELECT customerID FROM customer;");
		ArrayList<Integer> list = new ArrayList<Integer>();
		String id = GUI.enterID.getText();
		
		try {
			
			while(Database.result.next())
				list.add(Database.result.getInt(1));
			
			if(!id.equals("")) {
				for (int i = 0; i < list.size(); i++) {
					
					if(list.get(i) == Integer.parseInt(id)) {
						b = true;
						break;
					}
					
				}
			}
			
		
		
			if(b) {
			
				Database.read("SELECT discount FROM customer WHERE customerID=" + id + ";");

				if(Database.result.next()) {
					if(Database.result.getBoolean(1))
						rebateactive = true;
				}
			
			} else {
				rebateactive = false;
			}
		
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		
		if(rebateactive) {
			
			if(getComponentCount() == 2) {
				add(rebate);
				add(prize2);
			}
			
		} else {
			
			if(getComponentCount() == 4) {
				remove(rebate);
				remove(prize2);
			}
			
		}
		
	}
	
	public void paintComponent(Graphics g) {
		
		super.paintComponent(g);
		Graphics2D g2D = (Graphics2D) g;
		
		g2D.setColor(Color.BLACK);
		g2D.setStroke(new BasicStroke(1));
		g2D.drawLine(10, 1, getWidth()-20, 1);
		
	}

}
