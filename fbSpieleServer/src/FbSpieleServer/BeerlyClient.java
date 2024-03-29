package FbSpieleServer;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class BeerlyClient {
	Socket socket;
	String name;
	int team;
	String role;
	String color;
	Double guessAnswer;
	Double guessDistance;
	Double whereIsWhatAnswerPhi;
	Double whereIsWhatAnswerTheta;
	Double whereIsWhatAnswerDistance;

	Double rightWhereIsWhatAnswerPhi;
	Double rightWhereIsWhatAnswerTheta;
	
	int abstandPosition = -1;
	
	Crypto crypto;

	BeerlyClient(){
	}
	
	BeerlyClient(Socket socket, Crypto crypto){
		this.socket = socket;
		this.crypto = crypto;
		name = "";
		team = 0;
		role = "";
		color = "";
	}
	
	void updateName(String newName){
		name = newName;
		FbSpieleServer.updateClientList();
	}
	void updateTeam(int newTeam) {
		team = newTeam;
		FbSpieleServer.updateClientList();
	}
	void updateRole(String newRole) {
		role = newRole;
		FbSpieleServer.updateClientList();
	}
	void updateColor(String newColor){
		color = newColor;
		FbSpieleServer.updateClientList();
	}
	void updateGuessAnswer(Double newAnswer) {
		guessAnswer = newAnswer;
		FbSpieleServer.updateClientList();
	}
	void updateWhereIsWhatAnswer(Double newPhi, Double newTheta) {
		whereIsWhatAnswerPhi = newPhi;
		whereIsWhatAnswerTheta = newTheta;
		FbSpieleServer.updateClientList();
	}
	void resetWhereIsWhatAnswer() {
		whereIsWhatAnswerPhi = null;
		whereIsWhatAnswerTheta = null;
		whereIsWhatAnswerDistance = null;
		rightWhereIsWhatAnswerPhi = null;
		rightWhereIsWhatAnswerTheta = null;
		abstandPosition = -1;
		sendToSocket(FbSpieleServer.woLiegtWasReset);
		FbSpieleServer.updateClientList();
	}
	
	void  resetSchatztnAnswer() {
		guessAnswer = null;
		guessDistance = null;
		abstandPosition = -1;
		sendToSocket(FbSpieleServer.schatztn_sendReset);
		FbSpieleServer.updateClientList();
	}
	
	
	
	void updateGuessDistance(int comparison) {
		if(guessAnswer==null) {
			guessDistance = null;
			return;
		}
		double tempGuessDistance =  calcGuessDistance(guessAnswer, FbSpieleServer.schatztnRichtigeValue, comparison);
		if (tempGuessDistance < 0) {
			guessDistance = null;
		}
		else {
			guessDistance = tempGuessDistance;
		}
	}
	
	double calcGuessDistance(Double myAnswer, Double rightAnswer, int comparison) {
		// 0 = addition/subtraction
		// 1 = multiplication/division
		if(myAnswer==null) {
			System.out.println("error no guessAnswer or rightGuessAnswer for "+name+"("+socket.getInetAddress()+")");
			return -1;
		}
		if(comparison == 0) {
			return Math.abs(myAnswer-rightAnswer);
		}
		else if(comparison == 1) {
			if(myAnswer>rightAnswer) {
				return Math.abs(myAnswer/rightAnswer);				
			}
			else {
				return Math.abs(rightAnswer/myAnswer);	
			}
		}
		System.out.println("wrong comparison for "+name+"("+socket.getInetAddress()+") neither 0 (=addition) nor 1 (=multiplication)");
		return -1;
	}
	
	void updateWhereIsWhatDistance() {
		if(whereIsWhatAnswerPhi != null && whereIsWhatAnswerTheta != null && FbSpieleServer.woLiegtWasRichtigesPhi != null && FbSpieleServer.woLiegtWasRichtigesTheta != null) {
			whereIsWhatAnswerDistance = calcDistanceBetweenCoords(new double[] {whereIsWhatAnswerPhi,whereIsWhatAnswerTheta}, new double[] {FbSpieleServer.woLiegtWasRichtigesPhi, FbSpieleServer.woLiegtWasRichtigesTheta});
		}
	}

    double calcDistanceBetweenCoords(double[] kugelCoordinates1, double[] kugelCoordinates2){
        double earthRadius = 6371.000785; //in km; mittlerer radius (volumengleiche kugel aus https://de.wikipedia.org/wiki/Erdradius)
        return earthRadius * calcRadAngleBetweenCoords(kugelCoordinates1, kugelCoordinates2);
    }
    
    double calcRadAngleBetweenCoords(double[] kugelCoordinates1, double[] kugelCoordinates2){
        //https://en.wikipedia.org/wiki/Great-circle_distance#:~:text=The%20great-circle%20distance,%20orthodromic,line%20through%20the%20sphere's%20interior).


        double[] kugelCoords1 = new double[2];
        double[] kugelCoords2 = new double[2];
        //Log.v(tag,"coords" + "\t" + kugelCoordinates1[0] + "\t" + kugelCoordinates1[1] + "\t" + kugelCoordinates2[0] + "\t" + kugelCoordinates2[1]);
        kugelCoords1[0] = Math.toRadians(kugelCoordinates1[0]);
        kugelCoords1[1] = Math.toRadians(kugelCoordinates1[1]);
        kugelCoords2[0] = Math.toRadians(kugelCoordinates2[0]);
        kugelCoords2[1] = Math.toRadians(kugelCoordinates2[1]);

        double term1 = Math.pow( Math.cos(kugelCoords2[1])*Math.sin(kugelCoords1[0]-kugelCoords2[0]), 2);
        double term2 = Math.pow( Math.cos(kugelCoords1[1])*Math.sin(kugelCoords2[1]) - Math.sin(kugelCoords1[1])*Math.cos(kugelCoords2[1])*Math.cos(kugelCoords1[0]-kugelCoords2[0]) , 2);
        double term3 = Math.sin(kugelCoords1[1])*Math.sin(kugelCoords2[1]) + Math.cos(kugelCoords1[1])*Math.cos(kugelCoords2[1])*Math.cos(kugelCoords1[0]-kugelCoords2[0]);

        double angle = Math.atan2(Math.sqrt(term1+term2), term3);

        //Log.v(tag, "angle " + Math.toDegrees(angle));

        return Math.abs(angle);
    }
	
	Object[] getTableHeadersArray() {
		Object[] returnObject = {"ip", "name", "team", "role", "color", "schätztn", "schätztn dist", "wo phi", "wo theta", "wo dist"};
		return  returnObject;
	}
	
	Object[] getTableRightAnswerArray(int guessComparison) {
		
		String rightGuessAnswerString = "";
		String rightGuessDistanceString = ""; 
		if(FbSpieleServer.schatztnRichtigeValue!=null) {
			rightGuessAnswerString = String.valueOf(FbSpieleServer.schatztnRichtigeValue);
			rightGuessDistanceString = String.valueOf(calcGuessDistance(FbSpieleServer.schatztnRichtigeValue, FbSpieleServer.schatztnRichtigeValue, guessComparison));
		}
		

		String rightWhereIsWhatAnswerPhiString = "";
		String rightWhereIsWhatAnswerThetaString = "";
		String rightWhereIsWhatAnswerDistString = "";
		
		if(FbSpieleServer.woLiegtWasRichtigesPhi!=null) {
			rightWhereIsWhatAnswerPhiString = String.valueOf(FbSpieleServer.woLiegtWasRichtigesPhi);
		}

		if(FbSpieleServer.woLiegtWasRichtigesTheta!=null) {
			rightWhereIsWhatAnswerThetaString = String.valueOf(FbSpieleServer.woLiegtWasRichtigesTheta);
		}
		
		if(FbSpieleServer.woLiegtWasRichtigesPhi!=null && FbSpieleServer.woLiegtWasRichtigesTheta!=null) {
			rightWhereIsWhatAnswerDistString = String.valueOf(calcDistanceBetweenCoords(new double[] {FbSpieleServer.woLiegtWasRichtigesPhi,FbSpieleServer.woLiegtWasRichtigesTheta}, new double[] {FbSpieleServer.woLiegtWasRichtigesPhi,FbSpieleServer.woLiegtWasRichtigesTheta}));
		}
		 
		Object[] returnObject = {"", "right answer", "", "", "", rightGuessAnswerString, rightGuessDistanceString, rightWhereIsWhatAnswerPhiString, rightWhereIsWhatAnswerThetaString, rightWhereIsWhatAnswerDistString};
		return  returnObject;
	}

	Object[] getTableArray(int guessComparison) {
		updateGuessDistance(guessComparison);
		updateWhereIsWhatDistance();
		Object[] returnObject = {socket.getInetAddress(), name, team, role, color, guessAnswer, guessDistance, whereIsWhatAnswerPhi, whereIsWhatAnswerTheta, whereIsWhatAnswerDistance};
		
		return returnObject;
	}

	public void sendToSocket(String msg) {
		try {
			String encryptedMsg;
        	if(FbSpieleServer.settings.getBooleanSetting(FbSpieleServer.settings.settingsKeyUseEncryption, FbSpieleServer.settings.defaultUseEncryption)) {
        		encryptedMsg = this.crypto.encryptHex(msg);
        	}
        	else {
        		encryptedMsg = msg;
        	}
			System.out.println("sending to "+name+"("+socket.getInetAddress()+")"+"\n\tmessage\n\t\t"+msg+"\n\tencrypted\n\t\t"+encryptedMsg);
			PrintWriter printWriter = new PrintWriter(this.socket.getOutputStream(),true);
			printWriter.println(encryptedMsg+"\n\n\n");
			printWriter.flush();	//avoids that 2 message will be received at once (-> wouldn't be able to be decrypted)
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
