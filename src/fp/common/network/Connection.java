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
	public static int serverPort = 6789;
	final static String EOL = "\n";
	final static String EOF = "<<<END>>>";
	protected Socket socket;

	protected BufferedReader in;
	protected Writer out;
	
	public Connection(Socket socket) throws IOException {
		this.socket = socket;
		InputStream is = socket.getInputStream();
		OutputStream os = socket.getOutputStream();
		
		// Set up input stream readers
		InputStreamReader isr = new InputStreamReader(is);
		in = new BufferedReader(isr);
		out = new OutputStreamWriter(os);
	}
	
	public Connection(InetAddress hostname) throws IOException {
		this(new Socket(hostname, serverPort));
	}
	
	protected void writeLn(String line) throws IOException {
		out.write(line+EOL);
		out.flush();
	}
	
	protected void send(NetworkObject o) throws IOException {
		System.out.println("#NET: sending command: "+o.getCommand());
		String xml = XmlSerializer.getInstance().serialize(o);
		out.write(xml+EOL);
		writeLn(EOF);
		out.flush();
	}
	
	protected NetworkObject retrieve() throws IOException {
		String line, xml = "";
		while((line = in.readLine()) != null) {
			if(line.equals(EOF)) break;
			xml += line;
		}
		NetworkObject o = null;
		if(xml.length() > 0) {
			o = (NetworkObject) XmlSerializer.getInstance().unSerialize(xml);
			System.out.println("#NET: retrieved command: "+o.getCommand());
		}
		return o;
	}
	
	public void close() throws IOException {
		socket.close();
	}
}
