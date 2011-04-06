package fp.common.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.Socket;

import fp.common.models.XmlSerializer;

public abstract class Connection {
	public final static int serverPort = 6789;
	public final static String EOL = "\n";			// End of line
	public final static String EOT = "<<<END>>>";	// End of transmission
	protected Socket socket;
	protected BufferedReader in;
	protected Writer out;
	
	/**
	 * Sets up a reader and a writer for the socket.
	 * @param socket
	 * @throws IOException
	 */
	public Connection(Socket socket) throws IOException {
		this.socket = socket;
		InputStream is = socket.getInputStream();
		OutputStream os = socket.getOutputStream();
		
		// Set up Reader and writers
		InputStreamReader isr = new InputStreamReader(is);
		in = new BufferedReader(isr);
		out = new OutputStreamWriter(os);
	}
	
	/**
	 * @param hostname
	 * @throws IOException
	 */
	public Connection(InetAddress hostname) throws IOException {
		this(new Socket(hostname, serverPort));
	}
	
	/**
	 * Writes a line of text through to the socket (not used)
	 * @param line
	 * @throws IOException
	 */
	protected void writeLn(String line) throws IOException {
		out.write(line+EOL);
		out.flush();
	}
	
	/**
	 * Sends a NetworkObject through the socket.
	 * @param o
	 * @throws IOException
	 */
	protected void send(NetworkObject o) throws IOException {
		System.out.println("#NET: sending: "+o.getCommand());
		String xml = XmlSerializer.getInstance().serialize(o);
		out.write(xml+EOL);
		writeLn(EOT);
		out.flush();
	}
	
	/**
	 * Waits for a complete stream of xml.
	 * Converts it to a NetworkObject, and returns it
	 * @return the NetworkObject retrieved
	 * @throws IOException
	 */
	protected NetworkObject receive() throws IOException {
		String line, xml = "";
		while((line = in.readLine()) != null) {
			if(line.equals(EOT)) break;
			xml += line;
		}
		NetworkObject o = null;
		if(xml.length() > 0) {
			o = (NetworkObject) XmlSerializer.getInstance().unSerialize(xml);
			System.out.println("#NET: received: "+o.getCommand());
		}
		return o;
	}
	
	/**
	 * closes the socket
	 * @throws IOException
	 */
	public void close() throws IOException {
		socket.close();
	}
}
