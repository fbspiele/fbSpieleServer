package fbSpieleServer;

import java.net.Socket;

public class BeerlyClient {
	Socket socket;
	String name;
	int team;
	String role;
	
	Crypto crypto;
	
	BeerlyClient(Socket socket, Crypto crypto){
		this.socket = socket;
		this.crypto = crypto;
		name = "";
		team = 0;
		role = "";
	}
	
	void updateName(String newName){
		name = newName;
		fbSpieleServer.updateClientList();
	}
	void updateTeam(int newTeam) {
		team = newTeam;
		fbSpieleServer.updateClientList();
	}
	void updateRole(String newRole) {
		role = newRole;
		fbSpieleServer.updateClientList();
	}
}
