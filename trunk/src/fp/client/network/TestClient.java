package fp.client.network;

import java.io.*;
import java.net.*;
public class TestClient {
    public static void main(String[] args) {

        Socket socket = null;  
        DataOutputStream os = null;
        DataInputStream is = null;
      
        try {
            socket = new Socket("localhost", 3210);
            os = new DataOutputStream(socket.getOutputStream());
            is = new DataInputStream(socket.getInputStream());
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: hostname");
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to: hostname");
        }
        
	    if (socket != null && os != null && is != null) {
	        System.out.println("lol");
	    }           
    }
}
