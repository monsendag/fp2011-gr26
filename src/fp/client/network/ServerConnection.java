package fp.client.network;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class ServerConnection extends Socket {
	
	public final static int port = 6789;
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		new ServerConnection();
	}
	
	public ServerConnection() throws UnknownHostException, IOException {
		super(InetAddress.getLocalHost(), port);
		
		// get the IO streams
		InputStream is = getInputStream();
		OutputStream os = getOutputStream();

		// Set up input stream readers
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader in = new BufferedReader(isr);
		OutputStreamWriter out = new OutputStreamWriter(os);
		
		out.write("hallais server. This is client. Whats up?");
		out.flush();
		
		String line, xml = "";
		
		while((line = in.readLine()) != null) {
			xml += line;
		}
		System.out.println(xml);
		
		
		// Close streams and socket.
		out.close();
		in.close();
		close();
	}
}
