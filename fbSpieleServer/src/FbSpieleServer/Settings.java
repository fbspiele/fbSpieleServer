package FbSpieleServer;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Settings {
	private static String settingsFilePath;

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
	

	static final String settingsKeyUseEncryption = "use encryption?";
	static boolean defaultUseEncryption = false;
	static boolean useEncryption = defaultUseEncryption;
	
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
    	useEncryption = getBooleanSetting(settingsKeyUseEncryption, defaultUseEncryption);
    }
    
    public Settings(String settingsFilePath) {
    	this.settingsFilePath = settingsFilePath;
    	refreshSettingsStringList(true);
    }
    
    static String getDefaultSettingsFileText() {
		String defaultSettingsFileText = "this is the default fiel" + "\n" + "make the appropriate changes here" + "\n" + "then rename this file to settings.txt and rerun the server" + "\n\n";

		defaultSettingsFileText = defaultSettingsFileText + settingsKeyUseEncryption + "\n" + "false" + "\n\n";		
		defaultSettingsFileText = defaultSettingsFileText + settingsKeyEncryptedPassword + "\n" + "0939d55880794d2e5c3353cb46c02ee42dfbf6794ef8e3bd19df0ddc1ca4c671afd3d2a6656c519a6a02e4e0" + "\n\n";		// from password "default_password"
		defaultSettingsFileText = defaultSettingsFileText + settingsKeyEncryptedSalt + "\n" + "6563e0819237a13cfaa765016601700f02705aeebafe5dd3a55a55ac2dabf2287dbd30934b99bbed" + "\n\n";		// from salt "default_salt"
		
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
    	Path path = Path.of(settingsFilePath);
    	String settings = "";
    	
    	File tempSettingsFile = new File(settingsFilePath);
    	File tempSettingsFolder = new File(tempSettingsFile.getParent());
    	if(!tempSettingsFolder.isDirectory()) {
    		System.out.println("!warning! settingsfolder ("+tempSettingsFolder.getAbsolutePath()+") doesn't exist");
    	}
    	try {
    		settings = new String(Files.readString(path));	
    		//System.out.println("settings:");
        	//System.out.println(settings);
    	}
    	catch(Exception e) {
    		if(createDefaultFileIfNonExistent) {
        		System.out.println("creating settings file from default values");
        		System.out.println("terminating app for rerun (reload)");
        		
        		try {
        		    Files.write(Paths.get(settingsFilePath), getDefaultSettingsFileText().getBytes(StandardCharsets.UTF_8));
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

    public static boolean getBooleanSetting(String settingKey, boolean defaultValue) {
    	if(!refreshSettingsStringList(false)) {
    		return defaultValue;
    	}

    	String newValue = null;
    	for(int i = 0; i<settingsList.length; i++) {
    		if(settingsList[i].contentEquals(settingKey)) {
    			newValue = settingsList[i+1];
    			//System.out.println("loading: "+ settingKey+": " + newValue);
    		}
    	}
		Boolean retValue = null;
		String[] trueValues = {"yes","Yes","YES","1","True","TRUE","true","ja","JA","Ja","wahr","Wahr","WAHR"};
		String[] falseValues = {"no","No","NO","0","False","FALSE","false","nein","NEIN","Nein","falsch","Falsch","FALSCH"};
		for(String trueValue : trueValues) {
			if(newValue.contains(trueValue)) {
				retValue = true;
			}
		}
		for(String falseValue : falseValues) {
			if(newValue.contains(falseValue)) {
				retValue = false;
			}
		}
		if (retValue == null) {
			StringBuilder sb = new StringBuilder();
			sb.append("ERROR: trying to find boolean value for \""+settingKey+"\" but neither in [");
			for (int i = 0; i < trueValues.length; i++) {
				sb.append(trueValues[i]);
				if(i!=trueValues.length-1) {
					sb.append(", ");
				}
			}
			sb.append("] nor in [");
			for (int i = 0; i < trueValues.length; i++) {
				sb.append(falseValues[i]);
				if(i!=falseValues.length-1) {
					sb.append(", ");
				}
			}
			sb.append("]");
			System.out.println(sb.toString());
			retValue = defaultUseEncryption;
		}
		//System.out.println("loading setting \""+settingKey+"\":"+ retValue);
		return retValue;
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
    		//System.out.println("loading setting \""+settingKey+"\":"+ newValue);
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
		//System.out.println("loading setting \""+settingKey+"\":"+ newValue);
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

    static void saveBooleanSetting(String settingKey, boolean newValue) {
    	for(int i = 0; i<settingsList.length; i++) {
    		if(settingsList[i].contentEquals(settingKey)) {
    			String newValueString = "";
    			if(newValue) {
    				newValueString = "true";
    			}
    			else {
    				newValueString = "false";
    			}
    			settingsList[i+1] = newValueString;
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
		    Files.write(Paths.get(settingsFilePath), sb.toString().getBytes(StandardCharsets.UTF_8));
		} catch (Exception ex) {
			ex.printStackTrace();
		}    	
    }
}
