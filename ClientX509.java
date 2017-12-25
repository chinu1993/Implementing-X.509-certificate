import java.io.*;
//import java.math.BigInteger;
import java.net.*;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.Scanner;

import javax.crypto.*;
import javax.swing.JOptionPane;

public class ClientX509 {

	public static void main(String[] args) throws Exception 
	{
		String host = "LOCALHOST";
		int port = 7999;
		Socket s = new Socket(host, port);
	    
		// instantiating the streams for sending and receiving the objects through the socket
		ObjectOutputStream cipherOutStream = new ObjectOutputStream(s.getOutputStream());
        InputStream inStreamCert = new FileInputStream("server.cer");
        
        //using the input stream to receive the certificate data
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509Certificate certificate = (X509Certificate)cf.generateCertificate(inStreamCert);
        Date date = certificate.getNotAfter(); 
        inStreamCert.close();
        
        //checking the validity of the certificates
        if(date.after(new Date())) 
        	JOptionPane.showMessageDialog(null, "This certificate is still valid. \n Valid from: "
        			+ certificate.getNotBefore() + " to " + certificate.getNotAfter());
        else  
        	JOptionPane.showMessageDialog(null, "This certificate has expired");
        
        try
        {
    	   certificate.checkValidity();
    	   JOptionPane.showMessageDialog(null, "This certificate is valid");
        } catch (Exception e){
    	   System.out.println(e);   
        }
        
        //printing the certificate description
        System.out.println(certificate.toString());
        JOptionPane.showMessageDialog(null, "Certificate Description Available in Console Area..."); 
        
        String message = JOptionPane.showInputDialog("please enter a message");
        
        //Getting the public Key from the certificate 
        RSAPublicKey publicKeyServer = (RSAPublicKey) certificate.getPublicKey();
       
        //encrypting the message with the public key of the certificate
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKeyServer);
        byte[] cipherText = cipher.doFinal(message.getBytes());
    
        //sending the encrypted message to the server 
        cipherOutStream.writeObject(cipherText);
		cipherOutStream.flush();
		cipherOutStream.close();
	    s.close();
	}
}