package fbSpieleServer;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import fbSpieleServer.clientacceptingthread;

public class fbSpieleServer {
	static int port = 2079;

	
	static String team1Name = "team1";
	static String team2Name = "team2";
	
	static String serverIp = "";
	static ServerSocket server = null;
    public static volatile List<BeerlyClient> clientlist;
    public static volatile List<WoLiegtWasObject> woliegtWasList = new ArrayList<>();
    static JPanel panel;
    static DefaultTableModel tableModel;
    static JTable jTable;
    static JList listNamesInFrame;
    static JList listTeamsInFrame;
    static JList listRolesInFrame;
    static JList listIpsInFrame;
    static JFrame frame;
    static int variableAddSubInt = 10;
    static int werIstDasBildNummer = 1;
    static double schatzFrageRichtigeAntwort = 0;
    static double schatzFrageAntwortTeam1 = 0;
    static double schatzFrageAntwortTeam2 = 0;
    static double schatzFrageAbstandTeam1 = 1;
    static double schatzFrageAbstandTeam2 = 1;
    static String schatzFrageAbstandTeam1String = "";
    static String schatzFrageAbstandTeam2String = "";
    static String schatzFrageAntwortTeam1String = "";
    static String schatzFrageAntwortTeam2String = "";
    

    static Double woLiegtWasRichtigesPhi;
    static Double woLiegtWasRichtigesTheta;
    static int aktuelleWoLiegtWasFrage = -1;
    
    
    static int schatzFrageNahesteAntwortTeam = 0;
    static presentation presentation;
    
    static int guessComparator = 1;
    
    static final int codeNumberDelayedBuzzer = 0;
    static final int codeNumberDelayedReturnToPanel = 1;
    
    
    final static String settingsEncryptionPassword = "kaUY77d7HdbVsDYAsLZAbfaNaKH9XqKDFFVvbYAcvDTZHasVXpPUbxScxQ47cjMcenKgnc4LCKjaiSQfns4F5QXHjhFkw3ZMnoxq";
    final static String settingsEncryptionSalt = "Ks2tSNecEmTxzQiL2iAuVjWMSXtxKa9zwsZqLJjTVQAW7f4dQVYCpA5wVApfNoCHyzgdMrbvRJYRpNYHDtKCZKDqqSHCwjsWvujP";
    
    static Settings settings;
    static Crypto settingsEncryptionCrypto;
    
    fbSpieleServer(){    	
    	

    	serverIp = getServerIp();
    	createServer();

        clientlist = new ArrayList<BeerlyClient>();
        
        if(!server.isBound()) {return;}
    	
        try {
			acceptNewClients();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    public static void main(String[] args) {
    	
    	
    	settingsEncryptionCrypto = new Crypto(settingsEncryptionPassword, settingsEncryptionSalt);
    	
    	settings = new Settings("settings.txt");
    	
    	
    	Settings.loadSettings();
        team1Name = settings.team1;
        team2Name = settings.team2;

    	fbSpieleServer beerlyMpicsServer = new fbSpieleServer();
        
        presentation = new presentation(settings);
        
        Thread presentationThread = new Thread(presentation);
        presentationThread.setName("presentation thread");
        presentationThread.start();
        
        try {
			beerlyMpicsServer.acceptNewClients();
		} catch (Exception e) {
			e.printStackTrace();
		}
        
    	frameStuff();
    }

    private static String getServerIp() {
    	String serverIp = "";
    	try{
    		final DatagramSocket socket = new DatagramSocket();
    		socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
    		serverIp = socket.getLocalAddress().getHostAddress();
    		socket.close();
    		}
    	catch(NullPointerException e) {
    		e.printStackTrace();
    	}
    	catch(IOException e){
    		e.printStackTrace();
    	}
		return serverIp;
	}

    public static void createServer() {
    	try {
	        server = new ServerSocket(port, 1, InetAddress.getByName(serverIp));
		    System.out.println("server: " + server.getInetAddress().getHostAddress() 
	        		+":" + server.getLocalPort());
    	} catch (Exception e) {
        	System.out.println("wahrscheinlich ip adresse falsch oder schon gebindet\r"+e);
    	}
    }

    public void acceptNewClients() throws Exception{
    	new Thread(new clientacceptingthread(server, settings, settingsEncryptionCrypto)).start();
    }

    public static BeerlyClient getFbSocketByClientIp(InetAddress ip) {
    	for(BeerlyClient client : clientlist) {
    		if(client.socket.getInetAddress().equals(ip)) {
    			return client;
    		}
    	}
    	return null;
    }
    
    public static void updateClientName(BeerlyClient client, String newName) {
    	
    }
    
    public static void addClient(BeerlyClient client) {
    	clientlist.add(client);
    	updateClientList();
    }
    
    public static void removeClient(BeerlyClient client) {
    	clientlist.remove(client);
    	updateClientList();
    }
    
    
    public static void updateClientList(){


		BeerlyClient nullClient = new BeerlyClient(null, null);
		
		tableModel.getDataVector().clear();

    	tableModel.addRow(nullClient.getTableRightAnswerArray(guessComparator));
    	for(BeerlyClient client:clientlist) {
        	tableModel.addRow(client.getTableArray(guessComparator));
    	}
    	
    	resizeColumnWidth(jTable);
		jTable.setModel(tableModel);
		
		StringBuilder sb = new StringBuilder();

		DefaultListModel listModelNames = new DefaultListModel();
		DefaultListModel listModelTeams = new DefaultListModel();
		DefaultListModel listModelRoles = new DefaultListModel();
		DefaultListModel listModelIps = new DefaultListModel();
		listModelNames.addElement("name");
		listModelNames.addElement(" ");
		listModelTeams.addElement("team");
		listModelTeams.addElement(" ");
		listModelRoles.addElement("role");
		listModelRoles.addElement(" ");
		listModelIps.addElement("ip");
		listModelIps.addElement(" ");
		for (BeerlyClient client : clientlist) {
			sb.append("[");
			sb.append(client.socket.toString());
			sb.append(", name=");
			sb.append(client.name);
			sb.append(", team=");
			sb.append(client.team);
			sb.append(", role=");
			sb.append(client.role);
			sb.append("]");
			listModelNames.addElement(client.name);
			listModelTeams.addElement(client.team);
			String roleString = client.role;
			if(roleString.equals("")) {
				roleString = " ";
			}
			listModelRoles.addElement(roleString);
			listModelIps.addElement(client.socket.getInetAddress().getHostAddress());
		}
		listNamesInFrame.setModel(listModelNames);
		listTeamsInFrame.setModel(listModelTeams);
		listRolesInFrame.setModel(listModelRoles);
		listIpsInFrame.setModel(listModelIps);
		frame.pack();
    	System.out.println("clientlist: "+sb.toString());
    }
    
    public static void resizeColumnWidth(JTable table) {
    	//https://stackoverflow.com/a/17627497
        final TableColumnModel columnModel = table.getColumnModel();
        for (int column = 0; column < table.getColumnCount(); column++) {
            int width = 100; // Min width
            int maxWidth = 150; 
            for (int row = 0; row < table.getRowCount(); row++) {
                TableCellRenderer renderer = table.getCellRenderer(row, column);
                Component comp = table.prepareRenderer(renderer, row, column);
                width = Math.max(comp.getPreferredSize().width +1 , width);
            }
            if(width > maxWidth)
                width=maxWidth;
            columnModel.getColumn(column).setPreferredWidth(width+10);
        }
    }
    
	public static void frameStuff() {
    	//1. Create the frame.
    	frame = new JFrame("beerly mpics server");

    	//2. Optional: What happens when the frame closes?
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    	
        final JButton b1 = new JButton();
        b1.setText("b1");
        b1.doClick();
        b1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.out.println(b1.getText() + " clicked");
				
				//listeningThreadClass.executeMacro("buzzerVon(mein name,mein team,meine role,2399203)");
			}
        });
        final JButton b2 = new JButton();
        b2.setText("b2");
        b2.doClick();
        b2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//System.out.println(b2.getText() + " clicked");
			}
        });
        
        
        
        listNamesInFrame = new JList();
        listTeamsInFrame = new JList();
        listRolesInFrame = new JList();
        listIpsInFrame = new JList();
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JPanel panelOben = new JPanel();
        JPanel panelUnten = new JPanel();
        
        panelOben.add(getPasswordSaltPanel());
        panelOben.add(getGeneralPanel());
        panelOben.add(getAddSubPointsPanel());
        panelOben.add(getWerIstDasPanel());
        //panelOben.add(getSchatzFragePanel());
        panelOben.add(getWoliegtWasPanel());
        
        
        panelOben.add(b1);
        panelOben.add(b2);
    	//frame.add(b2);

		BeerlyClient nullClient = new BeerlyClient(null, null);
		
		tableModel = new DefaultTableModel(nullClient.getTableHeadersArray(),0);
        jTable = new JTable(tableModel);
        

        jTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        JScrollPane jTableScrollPanel = new JScrollPane(jTable);
        jTableScrollPanel.setPreferredSize(new Dimension(1200,800));
        panelUnten.add(jTableScrollPanel);
        resizeColumnWidth(jTable);
        //panel.add(jTable);
        
        
        panelOben.add(listNamesInFrame);
        panelOben.add(listTeamsInFrame);
        panelOben.add(listRolesInFrame);
        panelOben.add(listIpsInFrame);
        
        panel.add(panelOben);
        panel.add(panelUnten);

        frame.getContentPane().add(panel);

        //frame.getContentPane().add(panel);
    	//frame.setSize(800,400);
    	//frame.setLayout(null);
    	//4. Size the frame.
    	frame.pack();

		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    	frame.setLocation(100, 100);
    	
    	//5. Show it.
    	frame.setVisible(true);
    	
    }
	
	static JPanel getGeneralPanel(){
		//general
		//initial start
		final JButton initialStartButton = new JButton();
		initialStartButton.setText("initial reset");
		initialStartButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {

				//System.out.println(initialStartButton.getText() + " clicked");
				
				presentation.initialReset();
				
			}
		});
		
		//nächstes spiel sound
		final JButton nachstesSpielSoundButton = new JButton();
		nachstesSpielSoundButton.setText("nächstes spiel");
		nachstesSpielSoundButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {

				//System.out.println(nachstesSpielSoundButton.getText() + " clicked");

				presentation.overViewPanelAnzeigen();
				listeningThreadClass.playNextGameRevealSound();
				
			}
		});
		//overview anzeigen
		final JButton overviewAnzeigen = new JButton();
		overviewAnzeigen.setText("overview");
		overviewAnzeigen.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {

				//System.out.println(nachstesSpielSoundButton.getText() + " clicked");

				presentation.overViewPanelAnzeigen();
				
			}
		});
		final JPanel generalPanel = new JPanel();
		generalPanel.setLayout(new BoxLayout(generalPanel, BoxLayout.Y_AXIS));
		generalPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 50));
		generalPanel.add(initialStartButton);
		generalPanel.add(overviewAnzeigen);
		generalPanel.add(nachstesSpielSoundButton);
		return generalPanel;		
	}
	
	static JPanel getPasswordSaltPanel() {
		final JButton newPasswordButton = new JButton();
		newPasswordButton.setText("set new password");
		newPasswordButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {

    	        String inputString = JOptionPane.showInputDialog(null, "new password");
    	        String encryptedPassword = settingsEncryptionCrypto.encryptHex(inputString);
    	        settings.saveStringSetting(settings.settingsKeyEncryptedPassword, encryptedPassword);
			}
		});


		final JButton newSaltButton = new JButton();
		newSaltButton.setText("set new salt");
		newSaltButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {

    	        String inputString = JOptionPane.showInputDialog(null, "new salt");
    	        String encryptedSalt = settingsEncryptionCrypto.encryptHex(inputString);
    	        settings.saveStringSetting(settings.settingsKeyEncryptedSalt, encryptedSalt);
			}
		});
		
		final JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(newPasswordButton);
		panel.add(newSaltButton);
		return panel;		
		
	}
 
	static JPanel getAddSubPointsPanel() {
		
    	//team1+-1
    	final JButton buttonTeam1Plus1 = new JButton();
    	buttonTeam1Plus1.setText("+1");
    	buttonTeam1Plus1.addActionListener(new ActionListener(){
    		public void actionPerformed(ActionEvent arg0) {
    			presentation.overViewPanelAnzeigen();
    			
				//System.out.println(buttonTeam1Plus1.getText() + " clicked");
    			new Thread(new delayedAddPunkte(presentation, (long) 1000,1,1)).start();
    			//presentation.addPunkte(1,1);
				//listeningThreadClass.executeMacro("addPunkte(1,1)");		
    			//new Thread(new delayedCoinSound()).start();
    			//listeningThreadClass.playCoinSound();
    		}
    	});
    	final JButton buttonTeam1Minus1 = new JButton();
    	buttonTeam1Minus1.setText("-1");
    	buttonTeam1Minus1.addActionListener(new ActionListener(){
    		public void actionPerformed(ActionEvent arg0) {
    			presentation.overViewPanelAnzeigen();
				//System.out.println(buttonTeam1Minus1.getText() + " clicked");

    			//presentation.addPunkte(1,-1);
    			new Thread(new delayedAddPunkte(presentation, (long) 1000,1,-1)).start();
				//listeningThreadClass.executeMacro("addPunkte(1,-1)");
				//new Thread(new delayedLostSomethingSound()).start();
    			//listeningThreadClass.playLostSomethingSound();
    		}
    	});
        

    	//team1+-1
    	final JButton buttonTeam1PlusX = new JButton();
    	buttonTeam1PlusX.setText("+x");
    	buttonTeam1PlusX.addActionListener(new ActionListener(){
    		public void actionPerformed(ActionEvent arg0) {
    			presentation.overViewPanelAnzeigen();
				//System.out.println(buttonTeam1PlusX.getText() + " clicked");
    			//presentation.addPunkte(1,variableAddSubInt);
    			new Thread(new delayedAddPunkte(presentation, (long) 1000,1,variableAddSubInt)).start();
				//listeningThreadClass.executeMacro("addPunkte(1,"+variableAddSubInt+")");
		    	//new Thread(new delayedCoinSound()).start();
    			//listeningThreadClass.playCoinSound();
    		}
    	});
    	final JButton buttonTeam1MinusX = new JButton();
    	buttonTeam1MinusX.setText("-x");
    	buttonTeam1MinusX.addActionListener(new ActionListener(){
    		public void actionPerformed(ActionEvent arg0) {
    			presentation.overViewPanelAnzeigen();
				//System.out.println(buttonTeam1MinusX.getText() + " clicked");
    			//presentation.addPunkte(1,-variableAddSubInt);
    			new Thread(new delayedAddPunkte(presentation, (long) 1000,1,-variableAddSubInt)).start();
				//listeningThreadClass.executeMacro("addPunkte(1,-"+variableAddSubInt+")");
				//new Thread(new delayedLostSomethingSound()).start();
    			//listeningThreadClass.playLostSomethingSound();
    		}
    	});

    	
    	JPanel addTeam1 = new JPanel();
    	addTeam1.add(buttonTeam1PlusX);
    	addTeam1.add(buttonTeam1Plus1);
    	
        JTextField AddSubTeam1Text;
    	AddSubTeam1Text = new JTextField();
    	
    	JPanel addSubTeam1;
    	addSubTeam1 = new JPanel();
    	
    	AddSubTeam1Text.setText(team1Name + " (team 1)");
    	AddSubTeam1Text.setHorizontalAlignment(JTextField.CENTER);
    	addSubTeam1.setLayout(new BoxLayout(addSubTeam1, BoxLayout.Y_AXIS));
    	

    	JPanel subTeam1 = new JPanel();
    	subTeam1.add(buttonTeam1MinusX);
    	subTeam1.add(buttonTeam1Minus1);
    	
    	addSubTeam1.add(addTeam1);
    	addSubTeam1.add(AddSubTeam1Text);
    	addSubTeam1.add(subTeam1);
    	
    	
    	//team2+-1
    	final JButton buttonTeam2Plus1 = new JButton();
    	buttonTeam2Plus1.setText("+1");
    	buttonTeam2Plus1.addActionListener(new ActionListener(){
    		public void actionPerformed(ActionEvent arg0) {
    			presentation.overViewPanelAnzeigen();

				//System.out.println(buttonTeam2Plus1.getText() + " clicked");
    			//presentation.addPunkte(2,1);

    			new Thread(new delayedAddPunkte(presentation, (long) 1000,2,1)).start();
				//listeningThreadClass.executeMacro("addPunkte(2,1)");
		    	
			
    		}
    	});
    	final JButton buttonTeam2Minus1 = new JButton();
    	buttonTeam2Minus1.setText("-1");
    	buttonTeam2Minus1.addActionListener(new ActionListener(){
    		public void actionPerformed(ActionEvent arg0) {
    			presentation.overViewPanelAnzeigen();

				//System.out.println(buttonTeam2Minus1.getText() + " clicked");
    			//presentation.addPunkte(2,-1);

    			new Thread(new delayedAddPunkte(presentation, (long) 1000,2,-1)).start();
				//listeningThreadClass.executeMacro("addPunkte(2,-1)");
				
    		}
    	});

    	final JButton buttonTeam2PlusX = new JButton();
    	buttonTeam2PlusX.setText("+x");
    	buttonTeam2PlusX.addActionListener(new ActionListener(){
    		public void actionPerformed(ActionEvent arg0) {
    			presentation.overViewPanelAnzeigen();

				//System.out.println(buttonTeam2PlusX.getText() + " clicked");
    			//presentation.addPunkte(2,variableAddSubInt);

    			new Thread(new delayedAddPunkte(presentation, (long) 1000,2,variableAddSubInt)).start();
				//listeningThreadClass.executeMacro("addPunkte(2,"+variableAddSubInt+")");
				
    		}
    	});
    	final JButton buttonTeam2MinusX = new JButton();
    	buttonTeam2MinusX.setText("-x");
    	buttonTeam2MinusX.addActionListener(new ActionListener(){
    		public void actionPerformed(ActionEvent arg0) {
    			presentation.overViewPanelAnzeigen();

				//System.out.println(buttonTeam2MinusX.getText() + " clicked");
    			//presentation.addPunkte(2,-variableAddSubInt);

    			new Thread(new delayedAddPunkte(presentation, (long) 1000,2,-variableAddSubInt)).start();
				//listeningThreadClass.executeMacro("addPunkte(2,-"+variableAddSubInt+")");
				
    		}
    	});

    	
    	JPanel addTeam2 = new JPanel();
    	addTeam2.add(buttonTeam2Plus1);
    	addTeam2.add(buttonTeam2PlusX);
    	
    	JPanel subTeam2 = new JPanel();
    	subTeam2.add(buttonTeam2Minus1);
    	subTeam2.add(buttonTeam2MinusX);

        

    	JPanel addSubTeam2;
    	addSubTeam2 = new JPanel();

        JTextField AddSubTeam2Text;
        AddSubTeam2Text = new JTextField();
        AddSubTeam2Text.setText(team2Name + " (team 2)");
        AddSubTeam2Text.setHorizontalAlignment(JTextField.CENTER);
    	
    	addSubTeam2.setLayout(new BoxLayout(addSubTeam2, BoxLayout.Y_AXIS));
    	addSubTeam2.add(addTeam2);
    	addSubTeam2.add(AddSubTeam2Text);
    	addSubTeam2.add(subTeam2);
    	


    
    
    	
    	

    	JPanel addSubPoints = new JPanel();
    	
    	
    	JPanel middlePanelForX = new JPanel();
    	final JButton xButton = new JButton();

    	xButton.setText("x=10");
    	xButton.addActionListener(new ActionListener(){
    		public void actionPerformed(ActionEvent arg0) {

				//System.out.println(xButton.getText() + " clicked");


    	        String inputString = JOptionPane.showInputDialog(null, "new x value");
    	        variableAddSubInt = Integer.parseInt(inputString);
    	        xButton.setText("x="+variableAddSubInt);
			
    		}
    	});
    	middlePanelForX.setLayout(new BoxLayout(middlePanelForX, BoxLayout.Y_AXIS));
    	
    	middlePanelForX.add(xButton);
    	
    	addSubPoints.add(addSubTeam1);
    	addSubPoints.add(middlePanelForX);
    	addSubPoints.add(addSubTeam2);
    	
    	
    	addSubPoints.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 50));
    	
    	
    
    	
    	return addSubPoints;
	}
	
	static JPanel getWerIstDasPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    	panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 50));		

    	
    	JTextField titleText = new JTextField();
    	titleText.setText("wer ist das");
    	titleText.setHorizontalAlignment(JTextField.CENTER);
    	
    	panel.add(titleText);


    	JButton buttonWerIstDasStarten = new JButton();
    	buttonWerIstDasStarten.setText("starten");
    	buttonWerIstDasStarten.addActionListener(new ActionListener(){
    		public void actionPerformed(ActionEvent arg0) {
				//System.out.println(buttonWerIstDasStarten.getText() + " clicked");

				presentation.werIstDasBildAnzeigen(0);
    		}
    	});
    	
    	panel.add(buttonWerIstDasStarten);

    	JTextField bildNummerAnzeige = new JTextField();
    	bildNummerAnzeige.setText("next bild nr "+werIstDasBildNummer);
    	bildNummerAnzeige.setHorizontalAlignment(JTextField.CENTER);
    	
    	
    	JButton buttonPlus1 = new JButton();
    	buttonPlus1.setText("nr +1");
    	buttonPlus1.addActionListener(new ActionListener(){
    		public void actionPerformed(ActionEvent arg0) {
				//System.out.println(buttonPlus1.getText() + " clicked");
    			werIstDasBildNummer++;	
    	    	bildNummerAnzeige.setText("next bild nr "+werIstDasBildNummer);		
    		}
    	});
    	

    	JButton buttonMinus1 = new JButton();
    	buttonMinus1.setText("nr -1");
    	buttonMinus1.addActionListener(new ActionListener(){
    		public void actionPerformed(ActionEvent arg0) {
				//System.out.println(buttonPlus1.getText() + " clicked");
    			werIstDasBildNummer--;		
    	    	bildNummerAnzeige.setText("next bild nr "+werIstDasBildNummer);
    		}
    	});
    	

    	JButton buttonWerIstDasGo = new JButton();
    	buttonWerIstDasGo.setText("go");
    	buttonWerIstDasGo.addActionListener(new ActionListener(){
    		public void actionPerformed(ActionEvent arg0) {
				//System.out.println(buttonWerIstDasGo.getText() + " clicked");
    			
				presentation.werIstDasBildAnzeigen(werIstDasBildNummer);
    			
    			
    			werIstDasBildNummer++;	
    	    	bildNummerAnzeige.setText("next bild nr "+werIstDasBildNummer);			
    		}
    	});
    	
    	
    	panel.add(buttonPlus1);
    	panel.add(bildNummerAnzeige);
    	panel.add(buttonMinus1);
    	panel.add(buttonWerIstDasGo);
    	
		return panel;
	}
	
	
	static int upateWoLiegtWasFragen(String filename) {

		
		
    	Path path = Path.of("woLiegtWas",filename);
    	String fileContent;
		try {
			 fileContent = new String(Files.readString(path));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
		
		System.out.println(fileContent);
		woliegtWasList = new ArrayList<>();
		int counter = 0;
		
    	String[] lines = fileContent.split("\n");
    	for (int i = 0; i< lines.length; i++) {
    		System.out.println(lines[i]);
    		
    		if(lines[i].contains(",")&&i!=0) {
    			counter++;
    			WoLiegtWasObject object = new WoLiegtWasObject();
    			object.name = lines[i-1];
    			String[] substrings = lines[i].split(",");
    			object.theta = Double.valueOf(substrings[0]);	// google macht zuerst theta dann phi
    			object.phi = Double.valueOf(substrings[1]);
    			woliegtWasList.add(object);
    		}
    	}

		updateAktuelleFrageNummer(1);
    	return counter;
		
	}
	static JTextField frageNummerText, keyText;
	
	
	static void updateAktuelleFrageNummer(int newNumber) {
		aktuelleWoLiegtWasFrage = newNumber;
    	frageNummerText.setText("frage nr "+aktuelleWoLiegtWasFrage);
    	System.out.println(woliegtWasList.size());
    	if(woliegtWasList.size()>0&& woliegtWasList.size()>=aktuelleWoLiegtWasFrage-1) {
        	if(woliegtWasList.get(aktuelleWoLiegtWasFrage-1)!=null) {
        		if(keyText!=null) {
        			keyText.setText(woliegtWasList.get(aktuelleWoLiegtWasFrage-1).name);
        		}
        	}
        	else {
        		if(keyText!=null) {
                	keyText.setText("nächste frage text a");
        		}
        	}
    	}
    	else {
    		if(keyText!=null) {
            	keyText.setText("nächste frage text b");
    		}
    	}
	}
	
	static JPanel getWoliegtWasPanel() {
		
		
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));	

    	
    	JTextField titleText = new JTextField();
    	titleText.setText("wo liegt was");
    	titleText.setHorizontalAlignment(JTextField.CENTER);
    	
    	panel.add(titleText);
    	
    	JButton fileButton = new JButton();
    	fileButton.setText("file?");
    	fileButton.addActionListener(new ActionListener(){
    		public void actionPerformed(ActionEvent arg0) {
		        String filename = JOptionPane.showInputDialog(null, "file name?");
		        int fragenAnzahl = upateWoLiegtWasFragen(filename);
		        fileButton.setText("file = "+filename+" ("+fragenAnzahl+" Fragen)");
			}});
    	
    	panel.add(fileButton);
    	
    	JPanel fragenNavigationPanel = new JPanel();
    	fragenNavigationPanel.setLayout(new BoxLayout(fragenNavigationPanel, BoxLayout.X_AXIS));

    	

    	frageNummerText = new JTextField();
    	frageNummerText.setHorizontalAlignment(JTextField.CENTER);
		updateAktuelleFrageNummer(aktuelleWoLiegtWasFrage);
    	
    	
    	
    	JButton frageNummerMinus = new JButton();
    	frageNummerMinus.setText("-1");
    	frageNummerMinus.addActionListener(new ActionListener(){
    		public void actionPerformed(ActionEvent arg0) {
    			updateAktuelleFrageNummer(aktuelleWoLiegtWasFrage - 1);
			}});


    	
    	JButton frageNummerPlus = new JButton();
    	frageNummerPlus.setText("+1");
    	frageNummerPlus.addActionListener(new ActionListener(){
    		public void actionPerformed(ActionEvent arg0) {
    			updateAktuelleFrageNummer(aktuelleWoLiegtWasFrage + 1);
			}});
    	fragenNavigationPanel.add(frageNummerMinus);
    	fragenNavigationPanel.add(frageNummerText);
    	fragenNavigationPanel.add(frageNummerPlus);

    	panel.add(fragenNavigationPanel);
    	

    	keyText = new JTextField();
    	keyText.setText("nächste frage key");
    	keyText.setHorizontalAlignment(JTextField.CENTER);

    	JButton startFrage = new JButton();
    	startFrage.setText("start diese frage (reset)");
    	startFrage.addActionListener(new ActionListener(){
    		public void actionPerformed(ActionEvent arg0) {
    			for(BeerlyClient client : clientlist) {
    				client.resetWhereIsWhatAnswer();
    			}
    			WoLiegtWasObject object = woliegtWasList.get(aktuelleWoLiegtWasFrage-1);
    			woLiegtWasRichtigesPhi = object.phi;
    			woLiegtWasRichtigesTheta = object.theta;
    			updateClientList();
    		}
    	});
    	

    	panel.add(keyText);
    	panel.add(startFrage);
    	
    	
    	
    	
    	

    	/*JButton schatzFrageStarten = new JButton();
    	schatzFrageStarten.setText("starten/beenden");
    	schatzFrageStarten.addActionListener(new ActionListener(){
    		public void actionPerformed(ActionEvent arg0) {
				//System.out.println(buttonWerIstDasStarten.getText() + " clicked");
    			
    			presentation.geradeSchatzFragen = !presentation.geradeSchatzFragen;
    			presentation.overViewPanelAnzeigen();
    		}
    	});
    	
    	panel.add(schatzFrageStarten);
    	*/
    	JButton richtigeWoAntwort = new JButton();
    	richtigeWoAntwort.setText("antwort?");
    	richtigeWoAntwort.addActionListener(new ActionListener(){
    		public void actionPerformed(ActionEvent arg0) {
				//System.out.println(buttonWerIstDasStarten.getText() + " clicked");

    			
    			
				presentation.geradeSchatzFragenAuflosung = false;
    	        String inputString = JOptionPane.showInputDialog(null, "richtige antwort?");
    	        schatzFrageRichtigeAntwort = Double.parseDouble(inputString);
    	        
    	        updateClientList();
    	        
    	        richtigeWoAntwort.setText("antwort = "+schatzFrageRichtigeAntwort);

    	        presentation.spielLeitungSchatzungEingegeben = true;
    	        presentation.overViewPanelAnzeigen();
			
				//listeningThreadClass.executeMacro("schatzFrageRichtigeAntwort("+schatzFrageRichtigeAntwort+")");
    		}
    	});
    	
    	
    	/*
    	panel.add(richtigeAntwort);
    	

    	JButton auflosung = new JButton();
    	auflosung.setText("auflosung");
    	auflosung.addActionListener(new ActionListener(){
    		public void actionPerformed(ActionEvent arg0) {
				//System.out.println(auflosung.getText() + " clicked");

    			presentation.geradeSchatzFragenAuflosung = true;
    			
    			
    			
    			if (schatzFrageAntwortTeam1 < schatzFrageRichtigeAntwort) {
    				schatzFrageAbstandTeam1 = schatzFrageRichtigeAntwort / schatzFrageAntwortTeam1;
    				schatzFrageAbstandTeam1String = "*"; 
    			}
    			else {
    				schatzFrageAbstandTeam1 = schatzFrageAntwortTeam1 / schatzFrageRichtigeAntwort;
    				schatzFrageAbstandTeam1String = "/"; 
    			}
    			if (schatzFrageAntwortTeam2 < schatzFrageRichtigeAntwort) {
    				schatzFrageAbstandTeam2 = schatzFrageRichtigeAntwort / schatzFrageAntwortTeam2; 
    				schatzFrageAbstandTeam2String = "*"; 
    			}
    			else {
    				schatzFrageAbstandTeam2 = schatzFrageAntwortTeam2 / schatzFrageRichtigeAntwort;
    				schatzFrageAbstandTeam2String = "/";
    			}
    			
    			
    			//richtiges Team rausfinden
    			if(schatzFrageAbstandTeam1 < schatzFrageAbstandTeam2) {
    				schatzFrageNahesteAntwortTeam = 1;
    			}
    			else if(schatzFrageAbstandTeam1 > schatzFrageAbstandTeam2) {
    				schatzFrageNahesteAntwortTeam = 2;
    			}
    			else {
    				schatzFrageNahesteAntwortTeam = 0;
    			}
    			
    			System.out.println("schatz näheres team: " + schatzFrageNahesteAntwortTeam);
    			
    			System.out.println("\nschätzfrage\n richtigeantwort: " +schatzFrageRichtigeAntwort+ "\n team 1 mit " +schatzFrageAntwortTeam1 + " um " + schatzFrageAbstandTeam1String + schatzFrageAbstandTeam1  + " daneben\n team 2 mit " +schatzFrageAntwortTeam2 + " um " + schatzFrageAbstandTeam2String + schatzFrageAbstandTeam2 +" daneben");
    			
    			schatzFrageAbstandTeam1String = schatzFrageAbstandTeam1String+doubleToString(schatzFrageAbstandTeam1);
    			schatzFrageAbstandTeam2String = schatzFrageAbstandTeam2String+doubleToString(schatzFrageAbstandTeam2);
    			
    			schatzFrageAntwortTeam1String = doubleToString(schatzFrageAntwortTeam1);
    			schatzFrageAntwortTeam2String = doubleToString(schatzFrageAntwortTeam2);
    			
    			


    			
    			presentation.overViewPanelAnzeigen();
    			presentation.team1SchatzungEingegeben = false;
    			presentation.team2SchatzungEingegeben = false;
    			presentation.spielLeitungSchatzungEingegeben = false;
			
				//listeningThreadClass.executeMacro("schatzFrageAuflosen()");
    		}
    	});

    	
    	panel.add(auflosung);		
    	*/
    	return panel;
		
	}
	public static String doubleToString(double number) {
		int cutoffPower = 5;
		if(Math.abs(number)>=Math.pow(10, cutoffPower)||Math.abs(number)<=Math.pow(10, -cutoffPower)) {
			return String.format("%7.1e", number);
		}
		else {
			
			//wenn ganzzahl dann nur ganze zahl
			if(number == Math.round(number)) {
				return "" + Math.round(number);
			}
			else {
				String returnString = "" + String.format ("%f", number);;

				if(returnString.length()>cutoffPower + 2) {
					returnString = returnString.substring(0,cutoffPower + 2);
				}
				return returnString;
			}
		}
	}
	
	public static void runCodeNumber(int codeNumber) {
		if(codeNumber == codeNumberDelayedBuzzer) {
			resetBuzzer();
		}
		if(codeNumber == codeNumberDelayedReturnToPanel) {
			presentation.returnToLastPanel();
		}
	}
	
	//buzzering
	static volatile boolean buzzerScharf = true;
	public static boolean checkFirstBuzzer() {
		if(buzzerScharf) {
			System.out.println("erster buzzer gepressed");
			buzzerScharf = false;
	    	new Thread(new delayedExecuteCodeNumber((long) 5000, codeNumberDelayedBuzzer)).start();
			return true;
		}
		else {
			System.out.println("zu langsam");
			return false;
		}
	}
	public static void overViewPanelAnzeigen() {
		presentation.overViewPanelAnzeigen();
	}
	public static void resetBuzzer() {
		buzzerScharf = true;
		presentation.returnToLastPanel();
	}
	public static void firstBuzzerPressed(BeerlyClient beerlyClient) {
		/*String macroText = "buzzerVon("
								+beerlyClient.name.toString()+","
								+beerlyClient.team.toString()+","
								+beerlyClient.role.toString()+","
								+beerlyClient.socket.getInetAddress().getHostAddress()+")";
		listeningThreadClass.executeMacro(macroText);*/
		presentation.buzzer(beerlyClient.name.toString(),
								beerlyClient.team,
								beerlyClient.role.toString(),
								beerlyClient.socket.getInetAddress().getHostAddress().toString());
	}
}
