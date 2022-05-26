package FbSpieleServer;

public class delayedAddPunkte implements Runnable {
	Long time;
	int team;
	int punkte;
	Presentation presentation;
	boolean warSchonAufOverview = false;
	
	public delayedAddPunkte(Presentation presentation, long waitTime, int team, int punkte) {
		time = waitTime;
		this.team = team;
		this.punkte = punkte;
		this.presentation = presentation;
	}
	
	public void run() {
		if(presentation.lastPanelName!=null) {
			if(presentation.lastPanelName.equals(FbSpieleServer.presentation.overviewPanelNameString)) {
				warSchonAufOverview = true;
			}
		}

		if(!warSchonAufOverview) {
			try {
				Thread.sleep(time);
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		presentation.addPunkte(this.team, this.punkte);
		presentation.overViewPanelAnzeigen();
		if(!warSchonAufOverview) {
			try {
				Thread.sleep(time);
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			presentation.returnToVorletztePanel();
		}
	}
}
