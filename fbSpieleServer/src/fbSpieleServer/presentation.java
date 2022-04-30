package fbSpieleServer;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.annotation.processing.RoundEnvironment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class presentation implements Runnable {
	
	final static String team1IconPath = "src/team1_500x500.png";
	final static String team2IconPath = "src/team2_500x500.png";
	final static String alleTrinkenIconPath = "src/alle trinken.png";
	final static String newRuleIconPath = "src/new rule.png";
	
	final static String rightArrowPath = "src/schatzfragenpfeilrechts.png";
	final static String leftArrowPath = "src/schatzfragenpfeillinks.png";
	
	//int frameWidth = 1920;
	int frameWidth = 1920;
	int defaultFrameWidth = 1920;
	int defaultFrameHeight = 1055;
	int defaultIconWidth = 500;
	
	
	final static String buzzerPanelNameString = "buzzerPanelNameString";
	final static String werIstDasPanelNameString = "werIstDasPanelNameString";
	final static String overviewPanelNameString = "overviewPanelNameString";
	JPanel buzzerPanel;
	JPanel werIstDasPanel;
	JPanel overviewPanel;
	
	static JTextField punkteTeam1TextField;
	static JTextField punkteTeam2TextField;
	static boolean punkteTeam1WarenNochNieSoHoch = false;
	static boolean punkteTeam2WarenNochNieSoHoch = false;
	static JFrame frame;
	
	boolean geradeSchatzFragen = false;
	static boolean team1SchatzungEingegeben = false;
	static boolean team2SchatzungEingegeben = false;
	boolean spielLeitungSchatzungEingegeben = false;
	static boolean geradeSchatzFragenAuflosung = false;
	
	
	ImageIcon team1Icon, team2Icon, alleTrinkenIcon, newRuleIcon;
	
	Settings settings;
	public presentation(Settings settings) {
		this.settings = settings;
	}

	public void run() {

		
		frame = new JFrame("beerly mpics");
	
		//2. Optional: What happens when the frame closes?
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//maximize
	    //frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);

	    frame.setVisible(true);
        frame.setSize(new Dimension(defaultFrameWidth, defaultFrameHeight));
	    
	    frameLayout = frame.getLayout();
    			
		
	    team1Icon = new ImageIcon(team1IconPath);
	    team2Icon = new ImageIcon(team2IconPath);
	    alleTrinkenIcon = new ImageIcon(alleTrinkenIconPath);
	    newRuleIcon = new ImageIcon(newRuleIconPath);
	
	}
	
	public void overViewPanelAnzeigen() {
		displayPanelInFrame(getOverviewPanel());
	}
	
	public void werIstDasBildAnzeigen(int bildNr) {
		displayPanelInFrame(getWerIstDasPanel(bildNr));
	}
	
	JPanel getWerIstDasPanel(int bildNr) {
		
		JPanel panel = new JPanel();
		panel.setName(werIstDasPanelNameString);
		panel.setLayout(null);
		String werIstDasPath = "src/weristdas/";
		
		String bildNrString = "";
		if(bildNr < 10 && bildNr > -1) {
			bildNrString = "0"+ String.valueOf(bildNr);
		}
		else if (bildNr < 100 && bildNr > -1) {
			bildNrString = String.valueOf(bildNr);
		}
		else {
			System.out.println("ERROR bildnr in wer ist das panel nicht zwischen 1 und 99");
			return panel;
		}
		String bildPath = werIstDasPath + "bildnr" + bildNrString + ".jpg";
		//System.out.println("path " + bildPath);
		ImageIcon bildIcon = new ImageIcon(bildPath);
		if(bildIcon.getIconWidth()<0) {
			System.out.println("ERROR wer ist das bildpath existiert nicht!");
			return panel;
		}
		int bildWidth = 1500;
		int bildHeight = 800;
	    
	    
		
		putPictureInPanel(bildIcon, panel, (int) Math.round((defaultFrameWidth - bildWidth) / 2.0), (int) Math.round((defaultFrameHeight - bildHeight) / 2.0), bildWidth,bildHeight);
		
		werIstDasPanel = panel;
		return panel;
	}
	
	public void buzzer(String name, int team, String role, String ip) {
		buzzer(name, team, role, ip, defaultIconWidth, defaultFrameWidth);
	}
	public void buzzer(String name, int team, String role, String ip, int iconWidth, int frameWidth) {
		displayPanelInFrame(getBuzzerPanel(name, team, role, ip, iconWidth, frameWidth));
	}
	
	public void returnToLastPanel() {
		
		if(lastPanelName == buzzerPanelNameString) {
			System.out.println("ERROR LAST PANEL BUZZER PANEL KANN NICHT WIEDER ANGEZEIG WERDEN");
			displayPanelInFrame(getOverviewPanel());
		}
		else if(lastPanelName == werIstDasPanelNameString) {
			displayPanelInFrame(werIstDasPanel);
		}
		else if(lastPanelName == overviewPanelNameString) {
			displayPanelInFrame(getOverviewPanel());
		}
		else {
			System.out.println("ERROR LAST PANEL KEIN NAME DES LAST PANELS GEFUNDEN");
		}
	}
	
	public void returnToVorletztePanel() {

		if(vorletztePanelName == buzzerPanelNameString) {
			System.out.println("ERROR VORLETZTE PANEL BUZZER PANEL KANN NICHT WIEDER ANGEZEIG WERDEN");
			displayPanelInFrame(getOverviewPanel());
		}
		else if(vorletztePanelName == werIstDasPanelNameString) {
			displayPanelInFrame(werIstDasPanel);
		}
		else if(vorletztePanelName == overviewPanelNameString) {
			displayPanelInFrame(getOverviewPanel());
		}
		else {
			System.out.println("ERROR VORLETZTE PANEL KEIN NAME DES VORLETZTEN PANELS GEFUNDEN");
		}
	}
	
	
	
	
	JPanel getOverviewPanel() {
	   	JPanel panel = new JPanel();
	   	panel.setName(overviewPanelNameString);
		panel.setLayout(null);
	
	    int teamPicturesXpos = 200;
	    int teamPicturesYpos = 200;
	    int teamPicturesWidth = 500;
	    int teamPicturesHeight = teamPicturesWidth;
	    JLabel team1 = putPictureInPanel(team1Icon, panel, teamPicturesXpos,teamPicturesYpos,teamPicturesWidth,teamPicturesHeight);
	    JLabel team2 = putPictureInPanel(team2Icon, panel, defaultFrameWidth-teamPicturesXpos-teamPicturesWidth,teamPicturesYpos,teamPicturesWidth,teamPicturesHeight);
	    
	    int alleTrinkenSize = 150;
	    if(settings.getIntSetting(settings.settingsKeyPunkteTeam1) % 5 ==0 && punkteTeam1WarenNochNieSoHoch) {
	    	putPictureInPanel(alleTrinkenIcon, panel, defaultFrameWidth-teamPicturesXpos-teamPicturesWidth + teamPicturesWidth-alleTrinkenSize ,teamPicturesYpos,alleTrinkenSize,alleTrinkenSize);
	    }
	    if(settings.getIntSetting(settings.settingsKeyPunkteTeam2) % 5 ==0 && punkteTeam2WarenNochNieSoHoch) {
	    	putPictureInPanel(alleTrinkenIcon, panel, teamPicturesXpos ,teamPicturesYpos, alleTrinkenSize,alleTrinkenSize);
	    }
	    
	    
	    
	    int newRuleSize = alleTrinkenSize;
	    if(settings.getIntSetting(settings.settingsKeyPunkteTeam1) % 25 ==0 && punkteTeam1WarenNochNieSoHoch) {
	    	putPictureInPanel(newRuleIcon, panel, teamPicturesXpos + teamPicturesWidth-newRuleSize ,teamPicturesYpos,newRuleSize,newRuleSize);
	    }
	    if(settings.getIntSetting(settings.settingsKeyPunkteTeam2) % 25 ==0 && punkteTeam2WarenNochNieSoHoch) {
	    	putPictureInPanel(newRuleIcon, panel, defaultFrameWidth-teamPicturesXpos-teamPicturesWidth ,teamPicturesYpos,newRuleSize,newRuleSize);
	    }
	    
	    
	    
	    createTextField(panel,String.valueOf(settings.getIntSetting(settings.settingsKeyPunkteTeam1)), teamPicturesXpos, teamPicturesYpos + teamPicturesHeight-125, teamPicturesWidth, 300, 100);
	    

	    createTextField(panel,String.valueOf(settings.getIntSetting(settings.settingsKeyPunkteTeam2)), defaultFrameWidth-teamPicturesXpos-teamPicturesWidth, teamPicturesYpos + teamPicturesHeight-125, teamPicturesWidth, 300, 100);
	    
	    

	    
	    if(geradeSchatzFragen) {
	    	createTextField(panel,"schätztnfrage", (int)Math.round((defaultFrameWidth-5*teamPicturesWidth)/2.0),0 , 5*teamPicturesWidth, 300, 200);
	    	if(geradeSchatzFragenAuflosung) {

	    		createTextField(panel,fbSpieleServer.schatzFrageAntwortTeam1String, teamPicturesXpos - 100, teamPicturesYpos + teamPicturesHeight + 100, teamPicturesWidth, 300, 100);
	    		putPictureInPanel(new ImageIcon(rightArrowPath), panel, teamPicturesXpos + (int) Math.round(3.0/4.0 * teamPicturesWidth) ,teamPicturesYpos + teamPicturesHeight ,2*newRuleSize,2*newRuleSize);
	    		createTextField(panel,fbSpieleServer.schatzFrageAbstandTeam1String, teamPicturesXpos + (int) Math.round(1.0/2.0 * teamPicturesWidth) + 25 ,teamPicturesYpos + teamPicturesHeight-50, teamPicturesWidth, 300, 50);
	    		
	    		createTextField(panel,fbSpieleServer.schatzFrageAntwortTeam2String, defaultFrameWidth-teamPicturesXpos-teamPicturesWidth + 100, teamPicturesYpos + teamPicturesHeight + 100, teamPicturesWidth, 300, 100);
	    		putPictureInPanel(new ImageIcon(leftArrowPath), panel, defaultFrameWidth-teamPicturesXpos-teamPicturesWidth + 200 - (int) Math.round(3.0/4.0 * teamPicturesWidth) ,teamPicturesYpos + teamPicturesHeight,2*newRuleSize,2*newRuleSize);
	    		createTextField(panel,fbSpieleServer.schatzFrageAbstandTeam2String, defaultFrameWidth-teamPicturesXpos-teamPicturesWidth + 200 - (int) Math.round(3.0/4.0 * teamPicturesWidth) -90 ,teamPicturesYpos + teamPicturesHeight-50, teamPicturesWidth, 300, 50);
	    		
	    		createTextField(panel,fbSpieleServer.doubleToString(fbSpieleServer.schatzFrageRichtigeAntwort), (int)Math.round((defaultFrameWidth-teamPicturesWidth)/2.0), teamPicturesYpos + teamPicturesHeight + 100, teamPicturesWidth, 300, 100);
	    	}
	    	else {
	    		if(team1SchatzungEingegeben) {
	    			createTextField(panel,"*", teamPicturesXpos, teamPicturesYpos + teamPicturesHeight + 100, teamPicturesWidth, 300, 100);
	    		}
	    		if(team2SchatzungEingegeben) {
	    			createTextField(panel,"*", defaultFrameWidth-teamPicturesXpos-teamPicturesWidth, teamPicturesYpos + teamPicturesHeight + 100, teamPicturesWidth, 300, 100);
	    		}
	    		if(spielLeitungSchatzungEingegeben) {
	    			createTextField(panel,"*", (int)Math.round((defaultFrameWidth-teamPicturesWidth)/2.0), teamPicturesYpos + teamPicturesHeight + 100, teamPicturesWidth, 300, 100);
	    		}
	    	}
	    	
	    	
	    }
	    
	    
	    overviewPanel = panel;
	    return panel;
	}
	
	
	
	JPanel getBuzzerPanel(String name, int team, String role, String ip, int iconWidth, int frameWidth) {

		JPanel panel = new JPanel();
		panel.setName(buzzerPanelNameString);
		panel.setLayout(null);

				
		int buzzerTeamIconWidth = iconWidth;
		int buzzerTeamIconHeight = buzzerTeamIconWidth;
		int buzzerTeamIconX = (int) Math.round((frameWidth - buzzerTeamIconWidth) / 2.0);
		int buzzerTeamIconY = 50;
		
		ImageIcon teamIcon = team1Icon;
		if(team == 1) {
			teamIcon = team1Icon;
		}
		else if(team == 2) {
			teamIcon = team2Icon;
		}
		else {
			System.out.println("ERROR: team in getBuzzerPanel weder 1 noch 2");
		}
		
		JLabel buzzerTeamIconJLabel = putPictureInPanel(teamIcon, panel, buzzerTeamIconX, buzzerTeamIconY, buzzerTeamIconWidth,buzzerTeamIconHeight);
		


		int buzzerNameHeight = 250;
		int buzzerNameY = buzzerTeamIconY + buzzerNameHeight + 150;
		createTextField(panel, name, 0,buzzerNameY,frameWidth,(int) Math.round(buzzerNameHeight*1.5),buzzerNameHeight);
		
		
		int buzzerRoleHeight = 100;
		int buzzerRoleY = buzzerNameY + buzzerNameHeight + 100;
		createTextField(panel, role, 0,buzzerRoleY,frameWidth,(int) Math.round(buzzerRoleHeight*1.5),buzzerRoleHeight);
		

		int buzzerIpHeight = 20;
		int buzzerIpY = buzzerRoleY + buzzerRoleHeight + 50;
		createTextField(panel, ip, 0,buzzerIpY,frameWidth,(int) Math.round(buzzerIpHeight*1.5),buzzerIpHeight);
		
	    
	    buzzerPanel = panel;
		return panel;
	}
	
	LayoutManager frameLayout;
	String currentPanelName;
	String lastPanelName;
	String vorletztePanelName;
	public void displayPanelInFrame(JPanel panel) {
		
		if(lastPanelName == null) {
			if(currentPanelName == null) {
				vorletztePanelName = panel.getName();
			}
			else {
				vorletztePanelName = currentPanelName;
			}
		}
		else {
			vorletztePanelName = lastPanelName;
		}
		
		if(currentPanelName == null) {
			lastPanelName = panel.getName();
		}
		else {
			lastPanelName = currentPanelName;
		}
		
		currentPanelName = panel.getName();		
			
		frame.add(panel);

		LayoutManager tempLayout = new GridLayout(10, 1, 10, 10);
		
		frame.setLayout(new java.awt.FlowLayout() );
		frame.revalidate();
		frame.repaint();
		
		//LAYOUT MUSS VERÄNDERT WERDEN SONST MACHT ES NIX
		frame.setLayout(frameLayout);

		frame.revalidate();
		frame.repaint();
		
	}
	
	


	public void initialReset() {

		overViewPanelAnzeigen();
		
		settings.saveIntSetting(settings.settingsKeyPunkteTeam1, 0);
		settings.saveIntSetting(settings.settingsKeyPunkteTeam2, 0);
		settings.saveIntSetting(settings.settingsKeyBisherMaxPunkteTeam1, 0);
		settings.saveIntSetting(settings.settingsKeyBisherMaxPunkteTeam2, 0);
		
		
		overViewPanelAnzeigen();
	}
	
	public void addPunkte(int team, int punkte) {
		if (team == 1) {
			int punkteTeam1 = settings.getIntSetting(settings.settingsKeyPunkteTeam1);
			int maximalePunkteTeam1 = settings.getIntSetting(settings.settingsKeyBisherMaxPunkteTeam1);
			punkteTeam1 = punkteTeam1 + punkte;
			if(punkteTeam1 > maximalePunkteTeam1) {
				maximalePunkteTeam1 = punkteTeam1;
				punkteTeam1WarenNochNieSoHoch = true;
			}
			else {

				punkteTeam1WarenNochNieSoHoch = false;
			}
			settings.saveIntSetting(settings.settingsKeyPunkteTeam1, punkteTeam1);
			settings.saveIntSetting(settings.settingsKeyBisherMaxPunkteTeam1, maximalePunkteTeam1);
			
		}
		if (team == 2) {
			int punkteTeam2 = settings.getIntSetting(settings.settingsKeyPunkteTeam2);
			int maximalePunkteTeam2 = settings.getIntSetting(settings.settingsKeyBisherMaxPunkteTeam2);
			punkteTeam2 = punkteTeam2 + punkte;
			if(punkteTeam2 > maximalePunkteTeam2) {
				maximalePunkteTeam2 = punkteTeam2;
				punkteTeam2WarenNochNieSoHoch = true;
			}
			else {
				punkteTeam2WarenNochNieSoHoch = false;
			}
			settings.saveIntSetting(settings.settingsKeyPunkteTeam2, punkteTeam2);
			settings.saveIntSetting(settings.settingsKeyBisherMaxPunkteTeam2, maximalePunkteTeam2);
		}
		if(punkte > 0) {
			listeningThreadClass.playCoinSound();
		}
		if(punkte < 0) {
			listeningThreadClass.playLostSomethingSound();
		}
	}
	
	JTextField createTextField(JPanel panel, String text, int posX, int posY, int width, int height, int fontSize) {
	    double frameScale = (double) frameWidth / (double) defaultFrameWidth;
		Font font = new Font("SansSerif", Font.BOLD, (int) Math.round(fontSize * frameScale));
	    JTextField textField = new JTextField();
	    textField.setBorder(javax.swing.BorderFactory.createEmptyBorder());
	    textField.setOpaque(false);
	    textField.setBounds((int) Math.round(posX * frameScale), (int) Math.round(posY*frameScale), (int) Math.round(width*frameScale), (int) Math.round(height*frameScale));
	    textField.setFont(font);
	    textField.setHorizontalAlignment(JTextField.CENTER);
	    textField.setText(text);
	    panel.add(textField);
	    return textField;
	}
	
	JLabel putPictureInPanel(ImageIcon icon, JPanel panel, int posX, int posY, int width, int height) {
		double frameScale = (double) frameWidth / (double) defaultFrameWidth;
	    
	    int iconWidthInDefaultFrame = (int) Math.round(icon.getIconWidth() * getIconScale(icon, width, height));
	    int iconHeightInDefaultFrame = (int) Math.round(icon.getIconHeight() * getIconScale(icon, width, height));
	    
	    
	    icon = scaleIcon(icon, (int) Math.round(width*frameScale), (int) Math.round(height*frameScale));
		
	    //System.out.println("icon w in frame " + iconWidthInDefaultFrame + "\ticon width" + icon.getIconWidth());
	    
	    JLabel label = new JLabel(icon);
		label.setBounds((int) Math.round((posX + (width - iconWidthInDefaultFrame)/2.0)*frameScale), (int) Math.round((posY + (height-iconHeightInDefaultFrame)/2.0)*frameScale), icon.getIconWidth(), icon.getIconHeight() );
	    
	    panel.add(label);
	    return label;
	}
	
	ImageIcon scaleIcon(ImageIcon icon, int width, int height){
		
		double scale = getIconScale(icon, width, height);
		
		int intWidth = (int) Math.round(scale*icon.getIconWidth());
		int intHeight = (int) Math.round(scale*icon.getIconHeight());

		//System.out.println("nw: " + intWidth + "\tnh: "+intHeight);

		
		ImageIcon newIcon;
		if(scale!=1) {
			Image image = icon.getImage();
			newIcon = new ImageIcon(image.getScaledInstance(intWidth, intHeight, Image.SCALE_AREA_AVERAGING));
		}
		else {
			newIcon = icon;
		}	
		return newIcon;
		
	}
	
	double getIconScale(ImageIcon icon, int width, int height) {
		double hScale = 0;
		double wScale = 0;
		double scale = 1;

		//System.out.println("\nnew scale\nw: " + width + "\th: "+height);
		//System.out.println("iw: " + icon.getIconWidth() + "\tih: "+icon.getIconHeight());
		if(height > 0) {
			hScale = (double) height/((double) icon.getIconHeight());
		}
		if(width > 0) {
			wScale = (double) width/((double) icon.getIconWidth());
		}
		//System.out.println("ws: " + wScale + "\ths: "+hScale);
		if(height != 0 && width != 0) {
			scale = Math.min(hScale, wScale);
 		}
		else {
			scale = Math.max(wScale, hScale);
		}
		return scale;
	}
    
}
