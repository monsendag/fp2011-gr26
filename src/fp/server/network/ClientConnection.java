package fp.server.network;

import java.io.* ;
import java.net.* ;

public class ClientConnection implements Runnable {
	// define the EOL character
	final static String EOL = "\n";
	Socket socket;

	// Constructor
	public ClientConnection(Socket socket) throws Exception {
		this.socket = socket;
	}

	public void run() {
		try { 
			processRequest();
		} catch (Exception e) {
			System.err.println("Could not process request");
		}
	}
	
	public void parseHeader(String headerLine) {
		
	}

	private void processRequest() throws Exception {
		// get the IO streams
		InputStream is = socket.getInputStream();
		OutputStream os = socket.getOutputStream();

		// Set up input stream readers
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader in = new BufferedReader(isr);
		OutputStreamWriter out = new OutputStreamWriter(os);
		
		String line, xml = "";
		while((line = in.readLine()) != null) {
			xml += line;
			out.write("You sent me: "+xml);
			out.flush();
		}
		
		out.write("Hello. This is server!");
		
		out.flush();
		os.flush();
		
	}
	
	private static void sendBytes(FileInputStream fis, OutputStream os) throws Exception {
	   // Construct a 1K buffer to hold bytes on their way to the socket.
	   byte[] buffer = new byte[1024];
	   int bytes = 0;

	   // Copy requested file into the socket's output stream.
	   while((bytes = fis.read(buffer)) != -1 ) {
	      os.write(buffer, 0, bytes);
	   }
	}
}