package FbSpieleServer;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;





public class ListeningThread implements Runnable {
	FbSpieleServer fbSpieleServer;
	public ListeningThread(FbSpieleServer fbSpieleServer, BeerlyClient recClient) {
		this.fbSpieleServer = fbSpieleServer;
		client = recClient;
	}
	BeerlyClient client;
    
	public void run() {        
        
        String data = null;
        boolean stop = false;
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(client.socket.getInputStream()));
            while ( (data = in.readLine()) != null && client.socket.isConnected() && client.socket.isBound() && !stop) {
        		System.out.println("receiving from "+client.name+"("+client.socket.getInetAddress()+")\n"+"\tencrypted\n\t\t"+data);
                for (String message : data.split("\n\n\n")){	// send always "\n\n\n" to signal the end of message (otherwise sometimes adds linebreak if message to long -> tries to decrypt both parts)
                	message = message.replace("\n","");
                	if (message.length()>0) {
                    	String decryptedData;
                    	boolean decryptTheData = fbSpieleServer.settings.getBooleanSetting(fbSpieleServer.settings.settingsKeyUseEncryption, fbSpieleServer.settings.defaultUseEncryption);
                    	//System.out.println("decrypt? "+decryptTheData);
                    	if(decryptTheData) {
                    		decryptedData = client.crypto.decryptHex(message);
                    	}
                    	else {
                    		decryptedData = message;
                    	}
                    	if(decryptedData!=null) {            		
                            datenVerarbeiten(decryptedData);
                    	}
                    	else {
                    		throw new Exception("decryptedData has error");
                    	}
                	}
                }
            }
            System.out.println("\nconnection closed:" + client.socket.getInetAddress().getHostAddress());
        }
        catch(Exception e) {
        	System.out.println(e);
        }
		try {
			client.socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        fbSpieleServer.removeClient(client);
        //System.out.println("listening thread ended");
        
	}
	
	public void datenVerarbeiten(String msg) {
		System.out.println("\tdecrypted\n\t\t"+msg);
		
		
		if(msg.contains("xxxwoLiegtWasMyCoordsPhiABC")) {
			int phiStart = msg.indexOf("xxxwoLiegtWasMyCoordsPhiABC") + "xxxwoLiegtWasMyCoordsPhiABC".length();
			int phiEnd = msg.indexOf("DEFmyCoordsThetaGHI");

			int thetaStart = msg.indexOf("DEFmyCoordsThetaGHI") + "DEFmyCoordsThetaGHI".length();
			int thetaEnd = msg.indexOf("JKL");
			
			String strPhi = msg.substring(phiStart, phiEnd);
			String strTheta = msg.substring(thetaStart, thetaEnd);
			
			double phi = Double.valueOf(strPhi);
			double theta = Double.valueOf(strTheta);
			
			client.updateWhereIsWhatAnswer(phi, theta);
			Presentation.woLiegtWasPresentation.updateTeamsPanel(true);
		}

		if(msg.contains("schatztn_sendTextStart")) {
			int valueStart = msg.indexOf("schatztn_sendTextStart") + "schatztn_sendTextStart".length();
			int valueEnd = msg.indexOf("schatztn_sendTextEnd");

			
			String strValue = msg.substring(valueStart, valueEnd);
			System.out.println("schatztn new value "+strValue);
			Double value = null;
			try {
				value = Double.valueOf(strValue);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			if(value!=null) {
				client.updateGuessAnswer(value);
				if(Presentation.schatztnPresentation!=null) {
					Presentation.schatztnPresentation.updateTeamsPanel(true);
				}
			}
			else {
				System.out.println("schätzt antwort "+ strValue +" von "+client.name+"("+client.socket.getInetAddress()+")"+" konnte nicht zu double gemacht werden");
			}
		}

		if(msg.contains("buzzer")) {
			//todo
			System.out.println("buzzer von "+client.name + " (team "+client.team +"; " + client.socket.getInetAddress().getHostAddress()+")");
			if(fbSpieleServer.checkFirstBuzzer()) {
				fbSpieleServer.firstBuzzerPressed(client);
				sendToSocket("firstBuzzered");
				playBuzzerSound();
			}
			
			else {
				sendToSocket("toSlowBuzzered");
			}
		}
		
		if(msg.contains("pingRequest")) {
			//System.out.println("pingRequest");
			sendToSocket("pingResponse");
		}
		
		String newNameAnfangString = "myNewName";
		String newNameEndString = "endMyNewName";
		
		if(msg.contains(newNameAnfangString)) {
			String newName = msg.substring(msg.indexOf(newNameAnfangString)+newNameAnfangString.length(), msg.indexOf(newNameEndString));
			client.updateName(newName);
			//System.out.println("name of "+client.socket.getInetAddress().getHostAddress()+": "+newName);
		}
		
		String newColorAnfangString = "myNewColor";
		String newColorEndString = "endMyNewColor";
		
		if(msg.contains(newColorAnfangString)) {
			String newColor = msg.substring(msg.indexOf(newColorAnfangString)+newColorAnfangString.length(), msg.indexOf(newColorEndString));
			client.updateColor(newColor);
			//System.out.println("name of "+client.socket.getInetAddress().getHostAddress()+": "+newName);
		}
		

		String newTeamAnfangString = "myNewTeam";
		String newTeamEndString = "endMyNewTeam";
		
		if(msg.contains(newTeamAnfangString)) {
			String newTeamString = msg.substring(msg.indexOf(newTeamAnfangString)+newTeamAnfangString.length(), msg.indexOf(newTeamEndString));
			int newTeam = Integer.parseInt(newTeamString);
			client.updateTeam(newTeam);
			//System.out.println("name of "+client.socket.getInetAddress().getHostAddress()+": "+newName);
		}
		

		String newRoleAnfangString = "myNewRole";
		String newRoleEndString = "endMyNewRole";
		
		if(msg.contains(newRoleAnfangString)) {
			String newRole = msg.substring(msg.indexOf(newRoleAnfangString)+newRoleAnfangString.length(), msg.indexOf(newRoleEndString));
			client.updateRole(newRole);
			//System.out.println("name of "+client.socket.getInetAddress().getHostAddress()+": "+newName);
		}

		if(msg.indexOf("team")>-1) {
			String newTeamString = msg.substring(msg.indexOf("team")+4);
			System.out.println("team" + newTeamString);
			int team = 0;
			team = Integer.valueOf(newTeamString);
			if(team == 1 || team == 2) {
				client.updateTeam(team);	
			}
			//System.out.println("name of "+client.socket.getInetAddress().getHostAddress()+": "+newName);
		}
		
		if(msg.indexOf("role")>-1) {
			String newTeamString = msg.substring(msg.indexOf("role")+4);
			client.updateRole(newTeamString);
			//System.out.println("name of "+client.socket.getInetAddress().getHostAddress()+": "+newName);
		}
		
		//commands
		if(msg.indexOf("c")+1<msg.lastIndexOf("d")) {
			executeCommand(msg.substring(msg.indexOf("c")+1,msg.lastIndexOf("d")));
		}
		
	}
	
	public void executeCommand(String command) {
		if(command.equals(".temp")) {
			executeMacro("Main");
		}
	}
	
	public static void executeMacro(String macroName) {
		String macroNameTemp = macroName.replace(" ", "_");
		executeTerminalCommand("soffice macro:///Standard.testModule."+macroNameTemp,true);
	}

	public static String executeTerminalCommand(String command) {
		return executeTerminalCommand(command,true);
	}
	public static String executeTerminalCommand(String command, boolean printResult) {
		Process process;
	    StringBuilder stringbuilder = new StringBuilder();
		try {
			process = Runtime.getRuntime().exec(command);
		    process.waitFor();
		    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		    String line = "";
		    while ((line = reader.readLine()) != null) {
		    	stringbuilder.append(line);
		    }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String result = stringbuilder.toString();
		if(printResult) {
			//System.out.println("terminalcommand:\t"+command+"\n\treturned:\t"+result);	
		}
	    return result;
	}
	
	
	public void sendToSocket(String msg) {
		client.sendToSocket(msg);
	}
	
	public static void playBuzzerSound() {
		playSound("buzzerSound.wav");
	}
	
	public static void playNextGameRevealSound() {
		playSound("schlagdenRaabSpieleankündigung.wav");
	}
	
	public static void playCoinSound() {
		playSound("coinSound.wav");
	}
	
	public static void playLostSomethingSound() {
		playSound("lostSomethingSound.wav");
	}

	public static synchronized void playSound(final String url) {
		  new Thread(new Runnable() {
		  // The wrapper thread is unnecessary, unless it blocks on the
		  // Clip finishing; see comments.
		    public void run() {
		      try {
		    	//System.out.println("playSound: "+url);
		        Clip clip = AudioSystem.getClip();
		        File audioFile = new File("resources/sounds/" + url);
		        //System.out.println(clip.toString());
		        AudioInputStream inputStream = AudioSystem.getAudioInputStream(audioFile);
		        clip.open(inputStream);
		        clip.start(); 
		      } catch (Exception e) {
		        System.err.println(e.getMessage());
		      }
		    }
		  }).start();
		}
	    
     
}

