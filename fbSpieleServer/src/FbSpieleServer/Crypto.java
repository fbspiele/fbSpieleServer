package FbSpieleServer;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class Crypto {
	// main source for this class: https://mkyong.com/java/java-aes-encryption-and-decryption/
	
	// salt: to derive a different key from password, does not need to be secret (https://stackoverflow.com/a/1905405)
	// iv: to derive a different message from key, does not need to be secret and can be attached to beginning of message (https://stackoverflow.com/a/1905405)
	// cypher: verschlüsselung(-stechnik)
	
	// overview: 
	//		get key from password+salt
	//		encrpytion:
	//			encrpyt message with iv and key -> prepend iv
	//		decrpytion:
	//			get iv from message, decrypt with password and iv
	
	// encrypted password in einstellungen file
	// das encrypted password wird mit dem passwordDecryptionPassword decrypted und ergibt dass passwort für die encryption
	

    private static final String ENCRYPT_ALGO = "AES/GCM/NoPadding";
    private static final int TAG_LENGTH_BIT = 128;
    private static final int IV_LENGTH_BYTE = 12;
    private static final int AES_KEY_BIT = 256;

    // charset decoding has to one to one! (https://stackoverflow.com/questions/1536054/how-to-convert-byte-array-to-string-and-vice-versa?noredirect=1&lq=1#comment60319138_1536123)
    private static final Charset charSet = StandardCharsets.ISO_8859_1;
    
    SecretKey secretKey;
    
    
   
    
    public Crypto(String password, String salt) {
    	if (!(password != null)) {
    		System.out.println("ERROR password null");    		
    	}
    	if (!(salt != null)) {
    		System.out.println("ERROR salt null");
    	}

		this.secretKey = getAESKeyFromPassword(password.toCharArray(), salt.getBytes());
    }
	
	// get a new initial vector
    private static byte[] getRandomIV(int numBytes) {
        byte[] nonce = new byte[numBytes];
        new SecureRandom().nextBytes(nonce);
        return nonce;
    }
    

    // get key from password and salt
    private SecretKey getAESKeyFromPassword(char[] password, byte[] salt) {

        SecretKeyFactory factory = null;
        KeySpec spec = new PBEKeySpec(password, salt, 65536, AES_KEY_BIT);
        SecretKey secretKey = null;
        // iterationCount = 65536
        // keyLength = AES_KEY_BIT
		try {
			factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
			secretKey = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return secretKey;
    }
    

    // get hexadecimal representation of bytes
    public String hex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
    
    
	// encrypt the message (without prepending iv, needs to be added)
    private byte[] encryptWithoutIV(byte[] plainText, SecretKey secretKey, byte[] iv) {
        Cipher cipher;
        byte[] encryptedText = null;
		try {
			cipher = Cipher.getInstance(ENCRYPT_ALGO);
	        cipher.init(Cipher.ENCRYPT_MODE, secretKey, new GCMParameterSpec(TAG_LENGTH_BIT, iv));
	        encryptedText = cipher.doFinal(plainText);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return encryptedText;
    }

    // add iv + encrpyted message, convert to String
    public byte[] encryptToBytes(String plainText) {
    	byte[] iv = getRandomIV(IV_LENGTH_BYTE);
        byte[] cipherText = null;
		try {
			cipherText = encryptWithoutIV(plainText.getBytes(), secretKey, iv);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        byte[] cipherTextWithIv = ByteBuffer.allocate(iv.length + cipherText.length)
                .put(iv)
                .put(cipherText)
                .array();
        return cipherTextWithIv;
    }
    
    // get iv and decrpyt message
    public String decryptFromBytes(byte[] cipherTextByte) {
        ByteBuffer bb = ByteBuffer.wrap(cipherTextByte);

        byte[] iv = new byte[IV_LENGTH_BYTE];
        bb.get(iv);
        //bb.get(iv, 0, iv.length);

        byte[] cipherTextMessage = new byte[bb.remaining()];
        bb.get(cipherTextMessage);
        return decryptWithoutIV(cipherTextMessage, iv);
    }


    
    // decrypt using iv and password
    private String decryptWithoutIV(byte[] cText, byte[] iv) {
        Cipher cipher;
        byte[] plainText = null;
		try {
			cipher = Cipher.getInstance(ENCRYPT_ALGO);
			cipher.init(Cipher.DECRYPT_MODE, secretKey, new GCMParameterSpec(TAG_LENGTH_BIT, iv));
			plainText = cipher.doFinal(cText);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
        return new String(plainText);
    }
    
    
    public String encryptBase64(String message) {
    	return new String(encryptToBytes(message),charSet);    	
    }

    public String decryptBase64(String cypher) {
    	return decryptFromBytes(cypher.getBytes(charSet));
    }
    
    
    
    
    public String encryptHex(String message) {
    	return bytesToHex(encryptToBytes(message));
    }   
    
    public String decryptHex(String cypher) {
    	return decryptFromBytes(hexStringToByteArray(cypher));
    }
    
    // https://stackoverflow.com/a/140861
    /* s must be an even-length string. */
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                                 + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
    
    
    // https://stackoverflow.com/a/9855338
    private static final byte[] HEX_ARRAY = "0123456789abcdef".getBytes(StandardCharsets.US_ASCII);
    public static String bytesToHex(byte[] bytes) {
        byte[] hexChars = new byte[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars, StandardCharsets.UTF_8);
    }
    	
    	
    	

    
	
	
}