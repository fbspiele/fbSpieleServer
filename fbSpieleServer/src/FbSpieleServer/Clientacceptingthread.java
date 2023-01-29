package FbSpieleServer;

import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;



public class Clientacceptingthread implements Runnable{
	Settings settings;
	Crypto settingsEncryptionCrypto;
	FbSpieleServer fbSpieleServer;
	public Clientacceptingthread(FbSpieleServer fbSpieleServer, ServerSocket recServer, Settings settings, Crypto settingsEncryptionCrypto) {
		//System.out.println("client accepting thread started");
		this.fbSpieleServer = fbSpieleServer;
		server = recServer;
		this.settings = settings;
		this.settingsEncryptionCrypto = settingsEncryptionCrypto;
	}
	ServerSocket server;
	Thread listeningThread;
	public void run(){
		while(true) {
			try {

				
			Socket client = server.accept();
	        System.out.println("\r\nnew connection: "+client.getInetAddress().getHostAddress());
	        
	        //if connected before kill previous thread of this ip
	        BeerlyClient oldSocket = FbSpieleServer.getFbSocketByClientIp(client.getInetAddress());
	        if (oldSocket!=null) {
	        	FbSpieleServer.removeClient(oldSocket);
	        }
	        
	        Crypto clientCrypto = new Crypto(settingsEncryptionCrypto.decryptHex(settings.getStringSetting(settings.settingsKeyEncryptedPassword)),settingsEncryptionCrypto.decryptHex(settings.getStringSetting(settings.settingsKeyEncryptedSalt)));
	        
	        BeerlyClient beerlyClient = new BeerlyClient(client, clientCrypto);
	        
	        FbSpieleServer.addClient(beerlyClient);

	        String connectedMessage = "connected";

			String encryptedMsg;
        	if(FbSpieleServer.settings.getBooleanSetting(FbSpieleServer.settings.settingsKeyUseEncryption, FbSpieleServer.settings.defaultUseEncryption)) {
        		encryptedMsg = clientCrypto.encryptHex(connectedMessage);
        	}
        	else {
        		encryptedMsg = connectedMessage;
        	}

			new PrintWriter(client.getOutputStream(),true).println(encryptedMsg+"\n\n\n");
			
	        listeningThread = new Thread(new ListeningThread(fbSpieleServer, beerlyClient));
	        listeningThread.setName("listening_to_"+client.getInetAddress().getHostAddress());
	        listeningThread.start();
			
			}
			catch(Exception e) {
				System.out.println(e);
			}
		}
	}

}
