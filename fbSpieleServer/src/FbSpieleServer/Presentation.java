package FbSpieleServer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.processing.RoundEnvironment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Presentation implements Runnable {
	
	final static String team1IconPath = "resources/pictures/team1_500x500.png";
	final static String team2IconPath = "resources/pictures/team2_500x500.png";
	final static String alleTrinkenIconPath = "resources/pictures/alle trinken.png";
	final static String newRuleIconPath = "resources/pictures/new rule.png";
	
	final static String rightArrowPath = "resources/pictures/schatzfragenpfeilrechts.png";
	final static String leftArrowPath = "resources/pictures/schatzfragenpfeillinks.png";
	
	
	
	//int frameWidth = 1920;
	int frameWidth = 1920;
	//int defaultFrameWidth = 1920;
	int defaultFrameWidth = frameWidth;
	int defaultFrameHeight = (int) Math.round(1055.0/1920.0*defaultFrameWidth);
	//int defaultFrameHeight = 100;
	int defaultIconWidth = 500;
	
	
	final static String buzzerPanelNameString = "buzzerPanelNameString";
	final static String werIstDasPanelNameString = "werIstDasPanelNameString";
	final static String overviewPanelNameString = "overviewPanelNameString";
	final static String woLiegtWasPanelNameString = "woLiegtWasPanelNameString";
	final static String schatztnPanelNameString = "schatztnPanelNameString";
	
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
	
	ImageIcon team1Icon, team2Icon, alleTrinkenIcon, newRuleIcon;
	
	Settings settings;
	public Presentation(Settings settings) {
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
	
	public void werIstDasBildAnzeigen(String path) {
		displayPanelInFrame(getWerIstDasPanel(path));
	}
	
	JPanel getWerIstDasPanel(String path) {
		
		JPanel panel = new JPanel();
		panel.setName(werIstDasPanelNameString);
		panel.setLayout(null);
		System.out.println("path: "+path);
		ImageIcon bildIcon = new ImageIcon(path);
		if(bildIcon.getIconWidth()<0) {
			System.out.println("ERROR wer ist das bildpath existiert nicht oder ist kein bild!");
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
		else if(vorletztePanelName == woLiegtWasPanelNameString) {
			woLiegtWasPresentationStarten();
		}
		else if(vorletztePanelName == schatztnPanelNameString) {
			schatztnPresentationStarten();
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
		else if(vorletztePanelName == woLiegtWasPanelNameString) {
			woLiegtWasPresentationStarten();
		}
		else if(vorletztePanelName == schatztnPanelNameString) {
			schatztnPresentationStarten();
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
	    
	    int punktYpos = teamPicturesYpos + teamPicturesHeight;

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
	    
	    
	    
	    JLabel team1 = putPictureInPanel(team1Icon, panel, teamPicturesXpos,teamPicturesYpos,teamPicturesWidth,teamPicturesHeight);
	    JLabel team2 = putPictureInPanel(team2Icon, panel, defaultFrameWidth-teamPicturesXpos-teamPicturesWidth,teamPicturesYpos,teamPicturesWidth,teamPicturesHeight);
	    
	    
	    
	    createTextField(panel,String.valueOf(settings.getIntSetting(settings.settingsKeyPunkteTeam1)), teamPicturesXpos, punktYpos, teamPicturesWidth, 300, 100);
	    

	    createTextField(panel,String.valueOf(settings.getIntSetting(settings.settingsKeyPunkteTeam2)), defaultFrameWidth-teamPicturesXpos-teamPicturesWidth, punktYpos, teamPicturesWidth, 300, 100);
	    
	    
	    
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
			ListeningThread.playCoinSound();
		}
		if(punkte < 0) {
			ListeningThread.playLostSomethingSound();
		}
	}
	
	JTextField createTextField(JPanel panel, String text, int posX, int posY, int width, int height, int fontSize, Color textColor) {
	    double frameScale = (double) frameWidth / (double) defaultFrameWidth;
		Font font = new Font("SansSerif", Font.BOLD, (int) Math.round(fontSize * frameScale));
	    JTextField textField = new JTextField();
	    textField.setBorder(javax.swing.BorderFactory.createEmptyBorder());
	    textField.setOpaque(false);
	    textField.setBounds((int) Math.round(posX * frameScale), (int) Math.round(posY*frameScale), (int) Math.round(width*frameScale), (int) Math.round(height*frameScale));
	    textField.setFont(font);
	    textField.setHorizontalAlignment(JTextField.CENTER);
	    textField.setText(text);
	    textField.setForeground(textColor);
	    panel.add(textField);
	    return textField;		
	}
	
	JTextField createTextField(JPanel panel, String text, int posX, int posY, int width, int height, int fontSize) {
		return createTextField(panel, text, posX, posY, width, height, fontSize, Color.decode("#000000"));
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
	
	
	public static TeamListPresentation woLiegtWasPresentation;
	void woLiegtWasPresentationStarten() {
		woLiegtWasPresentation = new TeamListPresentation("wo liegt was?", woLiegtWasPanelNameString);
		displayPanelInFrame(woLiegtWasPresentation.getGesamtPanel());
	}
	

	
	
	public static TeamListPresentation schatztnPresentation;
	void schatztnPresentationStarten() {
		schatztnPresentation = new TeamListPresentation("schätztn?", schatztnPanelNameString);
		displayPanelInFrame(schatztnPresentation.getGesamtPanel());
	}
	
	
	public TeamListPresentation getWoLiegtWasPresentation() {
		if(woLiegtWasPresentation == null) {
			return null;
		}
		else {
			return woLiegtWasPresentation;
		}
	}
	

	
	public TeamListPresentation getSchatztnPresentation() {
		if(schatztnPresentation == null) {
			return null;
		}
		else {
			return schatztnPresentation;
		}
	}
	
	class TeamListPresentation {
		String panelTagString;
		String title;
		
		JPanel panelTeam1;
		JPanel panelTeam2;
		JPanel panelGes;
		
		JTextField losungsTextField;
		
		int titleWidth = defaultFrameWidth;

	    int teamPicturesYpos = 100;
	    int teamPicturesWidth = 200;
	    int teamPicturesHeight = teamPicturesWidth;
	    
	    int teamListHeight = 800;
		
		List<Spieler> spielerListTeam1 = new ArrayList<>();
		List<Spieler> spielerListTeam2 = new ArrayList<>();
		
		TeamListPresentation(String title, String panelTagString){
			this.title = title;
			panelGes = new JPanel();
			panelGes.setLayout(null);
			this.panelTagString = panelTagString;
			panelGes.setName(panelTagString);
			JTextField titleText = new JTextField();
			titleText.setText(title);
			titleText.setBackground(new Color(0,0,0,0));
			//panelGes.add(titleText);
			createTextField(panelGes, title,(int) Math.round(defaultFrameWidth/2.0-titleWidth/2.0), 20, titleWidth, 200, 100);
	    	putPictureInPanel(team1Icon, panelGes, (int) Math.round(1.0/4.0*defaultFrameWidth - teamPicturesWidth/2.0),teamPicturesYpos,teamPicturesWidth,teamPicturesHeight);
	    	putPictureInPanel(team2Icon, panelGes, (int) Math.round(3.0/4.0*defaultFrameWidth - teamPicturesWidth/2.0),teamPicturesYpos,teamPicturesWidth,teamPicturesHeight);
	    	losungsTextField = createTextField(panelGes, "", 0, defaultFrameHeight - 200, defaultFrameWidth, 200, 100);
			panelTeam1 = new JPanel();
			panelTeam2 = new JPanel();
			panelGes.add(panelTeam1);
			panelGes.add(panelTeam2);
		}
		
		class Spieler{
			InetAddress ip;
			String name;
			String schatzung = "";
			String abstand = "";
			Color color;
			int team;
			JTextField spielerSchatzungTextField;
			JTextField spielerAbstandTextField;
			Spieler(InetAddress ip, String name, int team, String schatzung, String color){
				this.ip = ip;
				this.name = name;
				this.team = team;
				this.schatzung = schatzung;
				this.color = Color.decode(color);
			}
			public void updateSchatzung(String newSchatzung, String abstand){
				schatzung = newSchatzung;
				abstand = abstand;
			}
		}
		
		String getSpielerWoLiegtWasAntwort(BeerlyClient client, boolean zensiert) {
			// to be overwritten
			if(zensiert) {
				if(client.whereIsWhatAnswerPhi == null || client.whereIsWhatAnswerTheta == null) {
					return "";
				}
				else {
					return "*";
				}
			}
			else {
				if(client.whereIsWhatAnswerPhi == null || client.whereIsWhatAnswerTheta == null) {
					return "";
				}
				else {
					DecimalFormat outputFormat = new DecimalFormat("#.####");
					return outputFormat.format(client.whereIsWhatAnswerPhi)+","+outputFormat.format(client.whereIsWhatAnswerTheta);	
				}			
			}
		}

		
		String getSpielerSchatztnAntwort(BeerlyClient client, boolean zensiert) {
			// to be overwritten
			if(zensiert) {
				if(client.guessAnswer == null) {
					return "";
				}
				else {
					return "*";
				}
			}
			else {
				if(client.guessAnswer == null) {
					return "";
				}
				else {
					return FbSpieleServer.doubleToString(client.guessAnswer);
				}			
			}
		}
		 
		 

		String getSpielerWoLiegtWasAbstand(BeerlyClient client, boolean zensiert) {
			// to be overwritten
			if(zensiert) {
				if(client.whereIsWhatAnswerDistance == null) {
					return "";
				}
				else {
					return "*";
				}
			}
			else {
				if(client.whereIsWhatAnswerDistance == null) {
					return "";
				}
				else {
					DecimalFormat outputFormat = new DecimalFormat("#.####");
					return outputFormat.format(client.whereIsWhatAnswerDistance);	
				}			
			}
		}


		String getSpielerSchatztnAbstand(BeerlyClient client, boolean zensiert) {
			// to be overwritten
			if(zensiert) {
				if(client.guessDistance == null) {
					return "";
				}
				else {
					return "*";
				}
			}
			else {
				if(client.guessDistance == null) {
					return "";
				}
				else {
					return FbSpieleServer.doubleToString(client.guessDistance);
				}			
			}
		}


		String getSpielerAbstandNumber(BeerlyClient client, boolean zensiert) {
			// to be overwritten
			if(zensiert) {
				if(client.abstandPosition == -1) {
					return "";
				}
				else {
					return "*";
				}
			}
			else {
				if(client.abstandPosition == -1) {
					return "";
				}
				else {
					return String.valueOf(client.abstandPosition);
				}			
			}
		}
		
		
		void updateTeamsPanel(boolean zensiert) {
			
			System.out.println("updateTeamsPanel");
			
			if(panelGes==null) {
				return;
			}
			panelGes.remove(panelTeam1);
			panelGes.remove(panelTeam2);
			panelTeam1 = new JPanel();
			panelTeam2 = new JPanel();
			panelTeam1.setLayout(panelGes.getLayout());
			panelTeam2.setLayout(panelGes.getLayout());
			panelTeam1.setBounds(panelGes.getBounds());
			panelTeam2.setBounds(panelGes.getBounds());
			
			if(zensiert) {
				losungsTextField.setText("");
			}
			
			List<BeerlyClient> spielerListTeam1 = new ArrayList<>();
			List<BeerlyClient> spielerListTeam2 = new ArrayList<>();

			int textFieldWidthsName = (int) Math.round(0.5/9.0*defaultFrameWidth);
			int textFieldWidthsAnswer = (int) Math.round(1.25/9.0*defaultFrameWidth);
			
			int xPosSpielerNamesTeam1 = (int) Math.round(0.0/9.0*defaultFrameWidth);
			int xPosSchatzTeam1 = xPosSpielerNamesTeam1+textFieldWidthsName;
			int xPosAbstandTeam1 = xPosSchatzTeam1+textFieldWidthsAnswer;

			int xPosSpielerNamesTeam2 = (int) Math.round(8.5/9.0*defaultFrameWidth);
			int xPosSchatzTeam2 = xPosSpielerNamesTeam2-textFieldWidthsAnswer;
			int xPosAbstandTeam2 = xPosSchatzTeam2-textFieldWidthsAnswer;
			
			int positionWidth = (int) Math.round(0.1/9.0*defaultFrameWidth);
			
			int xPosNummerTeam1 = xPosAbstandTeam1+textFieldWidthsAnswer;
			int xPosNummerTeam2 = xPosAbstandTeam2-positionWidth;
			
			int yPosTeams = 400;
			

		    List<BeerlyClient> orderedList = new ArrayList<BeerlyClient>();
		    orderedList.addAll(FbSpieleServer.clientlist);
			
			if(!zensiert) {
				if(panelTagString == woLiegtWasPanelNameString) {
				    for(BeerlyClient clientListClient : orderedList) {
				    	if(clientListClient.whereIsWhatAnswerDistance!=null) {
					    	int clientListClientNumber = 1;
					    	for(BeerlyClient client : orderedList) {
						    	if(client.whereIsWhatAnswerDistance!=null) {
							    	if(client.whereIsWhatAnswerDistance<clientListClient.whereIsWhatAnswerDistance) {
							    		clientListClientNumber++;
							    	}
						    	}
						    }
					    	clientListClient.abstandPosition = clientListClientNumber;			    		
				    	}
				    }
				    orderedList.sort(new Comparator<BeerlyClient>() {
				    	public int compare(BeerlyClient one, BeerlyClient two) {
				    		return Integer.compare(one.abstandPosition, two.abstandPosition);
				    	}
				    });
				    List<BeerlyClient> noAnswerList = new ArrayList<BeerlyClient>();
				    for(BeerlyClient client : orderedList) {
				    	if(client.abstandPosition== -1) {
				    		noAnswerList.add(client);
				    	}
				    }
				    for(BeerlyClient client : noAnswerList) {
				    	orderedList.remove(client);
				    }

				    for(BeerlyClient client : noAnswerList) {
				    	orderedList.add(client);
				    }					
				}
				else {
				    for(BeerlyClient clientListClient : orderedList) {
				    	if(clientListClient.guessDistance!=null) {
					    	int clientListClientNumber = 1;
					    	for(BeerlyClient client : orderedList) {
						    	if(client.guessDistance!=null) {
							    	if(client.guessDistance<clientListClient.guessDistance) {
							    		clientListClientNumber++;
							    	}
						    	}
						    }
					    	clientListClient.abstandPosition = clientListClientNumber;			    		
				    	}
				    }
				    orderedList.sort(new Comparator<BeerlyClient>() {
				    	public int compare(BeerlyClient one, BeerlyClient two) {
				    		return Integer.compare(one.abstandPosition, two.abstandPosition);
				    	}
				    });
				    List<BeerlyClient> noAnswerList = new ArrayList<BeerlyClient>();
				    for(BeerlyClient client : orderedList) {
				    	if(client.abstandPosition== -1) {
				    		noAnswerList.add(client);
				    	}
				    }
				    for(BeerlyClient client : noAnswerList) {
				    	orderedList.remove(client);
				    }

				    for(BeerlyClient client : noAnswerList) {
				    	orderedList.add(client);
				    }
				}
			}

			for(BeerlyClient client : orderedList) {
				if(client.team == 1) {
					spielerListTeam1.add(client);
				}
				else if (client.team == 2) {
					spielerListTeam2.add(client);
				}
			}
			
			int listSize = Math.max(spielerListTeam1.size(), spielerListTeam2.size());
			
			int entryHeight = 100;
			if(listSize != 0) {
				entryHeight = Math.round((defaultFrameHeight - yPosTeams)/(listSize+1));
			}
			
			if(entryHeight > 50) {
				entryHeight = 50;
			}
			
			int textSize = (int) Math.round(entryHeight/2.0);

			BeerlyClient spieler;
			
			for (int i = 0; i<spielerListTeam1.size(); i++) {
				spieler = spielerListTeam1.get(i);
				Color color = Color.decode(spieler.color);
				String antwort = "";
				String distance = "";
				if(panelTagString == woLiegtWasPanelNameString) {
					antwort = getSpielerWoLiegtWasAntwort(spieler, zensiert);
					distance = getSpielerWoLiegtWasAbstand(spieler, zensiert);
				}
				else {
					antwort = getSpielerSchatztnAntwort(spieler, zensiert);
					distance = getSpielerSchatztnAbstand(spieler, zensiert);
				}
				createTextField(panelTeam1, spieler.name, xPosSpielerNamesTeam1, yPosTeams+i*entryHeight, textFieldWidthsName, entryHeight, textSize, color);
				createTextField(panelTeam1, antwort, xPosSchatzTeam1, yPosTeams+i*entryHeight, textFieldWidthsAnswer, entryHeight, textSize, color);
				createTextField(panelTeam1, distance, xPosAbstandTeam1, yPosTeams+i*entryHeight, textFieldWidthsAnswer, entryHeight, textSize, color);
				createTextField(panelTeam1, getSpielerAbstandNumber(spieler, zensiert), xPosNummerTeam1, yPosTeams+i*entryHeight, positionWidth, entryHeight, textSize, color);
			}

			
			for (int i = 0; i<spielerListTeam2.size(); i++) {
				spieler = spielerListTeam2.get(i);
				Color color = Color.decode(spieler.color);
				String antwort = "";
				String distance = "";
				if(panelTagString == woLiegtWasPanelNameString) {
					antwort = getSpielerWoLiegtWasAntwort(spieler, zensiert);
					distance = getSpielerWoLiegtWasAbstand(spieler, zensiert);
				}
				else {
					antwort = getSpielerSchatztnAntwort(spieler, zensiert);
					distance = getSpielerSchatztnAbstand(spieler, zensiert);
				}
				createTextField(panelTeam2, spieler.name, xPosSpielerNamesTeam2, yPosTeams+i*entryHeight, textFieldWidthsName, entryHeight, textSize, color);
				createTextField(panelTeam2, antwort, xPosSchatzTeam2, yPosTeams+i*entryHeight, textFieldWidthsAnswer, entryHeight, textSize, color);
				createTextField(panelTeam2, distance, xPosAbstandTeam2, yPosTeams+i*entryHeight, textFieldWidthsAnswer, entryHeight, textSize, color);
				createTextField(panelTeam2, getSpielerAbstandNumber(spieler, zensiert), xPosNummerTeam2, yPosTeams+i*entryHeight, positionWidth, entryHeight, textSize, color);
			}
			panelGes.add(panelTeam1);
			panelGes.add(panelTeam2);
			
			panelGes.revalidate();
			panelGes.repaint();
		}
		
		void auflosen(String losung) {
			losungsTextField.setText(losung);
			updateTeamsPanel(false);
		}
		
		JPanel getGesamtPanel() {
			return panelGes;
		}
		
	}
    
}
