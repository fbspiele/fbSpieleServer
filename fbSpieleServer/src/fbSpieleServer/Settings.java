package fbSpieleServer;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Settings {
	private static String settingsFilename;
	static final String defaultSettingsFileName = "defaultSettings.txt";

	static final String settingsKeyTeam1 = "team 1";
	static String team1 = "";
	static final String settingsKeyTeam2 = "team 2";
	static String team2 = "";
	
	static final String settingsKeyPunkteTeam1 = "punkte team 1";
	static int punkteTeam1 = 0;
	static final String settingsKeyPunkteTeam2 = "punkte team 2";
	static int punkteTeam2 = 0;
	
	static final String settingsKeyBisherMaxPunkteTeam1 = "bisher maximale punkte team 1";
	static int bisherMaximalePunkteTeam1 = 0;
	static final String settingsKeyBisherMaxPunkteTeam2 = "bisher maximale punkte team 2";
	static int bisherMaximalePunkteTeam2 = 0;
	
	
	
	static final String settingsKeyEncryptedPassword = "encrypted password";
	static String encryptedPassword = "";
	static final String settingsKeyEncryptedSalt= "encrypted salt";	
	static String encryptedSalt = "";
	
	
	static String[] settingsList;

    
    public static void loadSettings() {
    	encryptedPassword = getStringSetting(settingsKeyEncryptedPassword);
    	encryptedSalt = getStringSetting(settingsKeyEncryptedSalt);
    	team1 = getStringSetting(settingsKeyTeam1);
    	team2 = getStringSetting(settingsKeyTeam2);
    	punkteTeam1 = getIntSetting(settingsKeyPunkteTeam1);
    	punkteTeam2 = getIntSetting(settingsKeyPunkteTeam2);
    	bisherMaximalePunkteTeam1 = getIntSetting(settingsKeyBisherMaxPunkteTeam1);
    	bisherMaximalePunkteTeam2 = getIntSetting(settingsKeyBisherMaxPunkteTeam2);
    }
    
    public Settings(String settingsFileName) {
    	this.settingsFilename = settingsFileName;
    	refreshSettingsStringList(true);
    }
    
    static String getDefaultSettingsFileText() {
		String defaultSettingsFileText = "this is the default fiel" + "\n" + "make the appropriate changes here" + "\n" + "then rename this file to settings.txt and rerun the server" + "\n\n";

		defaultSettingsFileText = defaultSettingsFileText + settingsKeyEncryptedPassword + "\n" + "my super nice password (encrypted)" + "\n\n";
		defaultSettingsFileText = defaultSettingsFileText + settingsKeyEncryptedSalt + "\n" + "my super nice salt (encrypted)" + "\n\n";
		
		defaultSettingsFileText = defaultSettingsFileText + settingsKeyTeam1 + "\n" + "team1" + "\n\n";
		defaultSettingsFileText = defaultSettingsFileText + settingsKeyTeam2 + "\n" + "team2" + "\n\n";

		defaultSettingsFileText = defaultSettingsFileText + settingsKeyPunkteTeam1 + "\n" + "0" + "\n\n";
		defaultSettingsFileText = defaultSettingsFileText + settingsKeyPunkteTeam2 + "\n" + "0" + "\n\n";

		defaultSettingsFileText = defaultSettingsFileText + settingsKeyBisherMaxPunkteTeam1 + "\n" + "0" + "\n\n";
		defaultSettingsFileText = defaultSettingsFileText + settingsKeyBisherMaxPunkteTeam2 + "\n" + "0" + "\n\n";
		
		return defaultSettingsFileText;
    }
	
    // returns false if no settings file could be found (creates default settingsfile)
    
    public static boolean refreshSettingsStringList(boolean createDefaultFileIfNonExistent) {
    	//final String currentDir = System.getProperty("user.dir");
    	Path path = Path.of(settingsFilename);
    	String settings = "";
    	try {
    		settings = new String(Files.readString(path));	
    		//System.out.println("settings:");
        	//System.out.println(settings);
    	}
    	catch(Exception e) {
    		if(createDefaultFileIfNonExistent) {
        		System.out.println("creating default file");
        		System.out.println("terminating app for rerun (reload)");
        		
        		try {
        		    Files.write(Paths.get(defaultSettingsFileName), getDefaultSettingsFileText().getBytes(StandardCharsets.UTF_8));
        		} catch (Exception ex) {
        			ex.printStackTrace();
        		}    			
    		}
    		System.out.println(e);
    		return false;
    	}
    	
    	settingsList = settings.split("\n");
    	return true;
    }
    
    public static int getIntSetting(String settingKey) {
    	if(!refreshSettingsStringList(false)) {
    		return (Integer) null;
    	}

    	String newValue = null;
    	for(int i = 0; i<settingsList.length; i++) {
    		if(settingsList[i].contentEquals(settingKey)) {
    			newValue = settingsList[i+1];
    			//System.out.println("loading: "+ settingKey+": " + newValue);
    		}
    	}
    	try {
    		return Integer.valueOf(newValue);
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    		return 0;
    	}
    }
    
    public static String getStringSetting(String settingKey) {
    	if(!refreshSettingsStringList(false)) {
    		return null;
    	}

    	String newValue = null;
    	for(int i = 0; i<settingsList.length; i++) {
    		if(settingsList[i].contentEquals(settingKey)) {
    			newValue = settingsList[i+1];
    			//System.out.println("laoding: "+ settingKey+": " + newValue);
    		}
    	}
		return newValue;
    	
    }
    
    static void saveIntSetting(String settingKey, int newValue) {
    	for(int i = 0; i<settingsList.length; i++) {
    		if(settingsList[i].contentEquals(settingKey)) {
    			settingsList[i+1] = String.valueOf(newValue);
    			//System.out.println("saving: "+ settingKey+": " + newValue);
    		}
    	}
    	saveSettingsList();    	
    }
    static void saveStringSetting(String settingKey, String newValue) {
    	for(int i = 0; i<settingsList.length; i++) {
    		if(settingsList[i].contentEquals(settingKey)) {
    			settingsList[i+1] = newValue;
    			//System.out.println("saving: "+ settingKey+": " + newValue);
    		}
    	}
    	saveSettingsList();
    }
    
    static void saveSettingsList() {
    	StringBuilder sb = new StringBuilder();
    	for(int i = 0; i<settingsList.length; i++) {
    		sb.append(settingsList[i]).append("\n");
    	}
		try {
		    Files.write(Paths.get(settingsFilename), sb.toString().getBytes(StandardCharsets.UTF_8));
		} catch (Exception ex) {
			ex.printStackTrace();
		}    	
    }
}
