package fbSpieleServer;

public class delayedExecuteCodeNumber implements Runnable {
	Long waitTime;
	static int codeNumber;
	
	public delayedExecuteCodeNumber(Long waitTimeMillis, int code) {
		waitTime = waitTimeMillis;
		codeNumber = code;
	}
	
	public void run() {
		try {
			Thread.sleep(waitTime);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fbSpieleServer.runCodeNumber(codeNumber);
	}
}
