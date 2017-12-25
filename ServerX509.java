import java.io.*;
import java.net.*;
import java.security.*;
import javax.crypto.*;
import javax.swing.JOptionPane;

public class ServerX509 {

	public static void main(String[] args) throws Exception 
	{   
		
		int port = 7999;
		ServerSocket server = new ServerSocket(port);
		Socket s = server.accept();
		
		ObjectInputStream cipherClientStream = new ObjectInputStream(s.getInputStream());
		
		//Alias name and password defined when creating the certficate using keytool
		String aliasName="tagatsi";
        char[] pswd="tagatsi".toCharArray();
		
        //getting the server private key by reading the keystore file
        KeyStore ks = KeyStore.getInstance("jks");
        ks.load(new FileInputStream("keystore.jks"), pswd);
        PrivateKey serverPrivateKey = (PrivateKey)ks.getKey(aliasName, pswd);
       
        // getting the cipher text from the client and decripting using the server private key
        Cipher cipher = Cipher.getInstance("RSA");
        byte[] cipherTextClient = (byte[]) cipherClientStream.readObject(); // reading the ciphertext sent by the client
        
        //initiating the decryption with the server private key
        cipher.init(Cipher.DECRYPT_MODE, serverPrivateKey); 
		byte[] decryptedMessage = cipher.doFinal(cipherTextClient);
		
		//Showing the decrypted message
		JOptionPane.showMessageDialog(null, "The Message is: " + new String(decryptedMessage));
		
		server.close();
	}

}