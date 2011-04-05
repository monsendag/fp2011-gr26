package fp.network;

import java.io.* ;
import java.net.* ;

import no.ntnu.fp.model.Employee;
import no.ntnu.fp.model.XmlSerializer;

public class ServerConnection extends Connection implements Runnable {

	// Constructor
	public ServerConnection(Socket socket) throws Exception {
		this.socket = socket;
	}

	public void run() {
		try {
			processRequest();
		} catch (Exception e) {
			System.err.println("Could not process request ("+e.getMessage()+")");
		}
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
			if(line.equals("EN")) break;
			xml += line;
		}
		
		Employee arne = (Employee) XmlSerializer.getInstance().unSerialize(xml);
		
		System.out.println("Got employee: "+arne.getName());
		
		out.write("Hello. This is server!"+EOL);
		out.write("goodnight"+EOL);
		out.flush();
	}
	
	public String toString() {
		return socket.getRemoteSocketAddress().toString();
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