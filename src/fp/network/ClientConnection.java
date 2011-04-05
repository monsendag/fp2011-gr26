package fp.network;

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

import no.ntnu.fp.model.Employee;
import no.ntnu.fp.model.XmlSerializer;

public class ClientConnection extends Connection {
	
	protected InetAddress hostname;
	
	public ClientConnection() throws IOException {
		hostname = InetAddress.getLocalHost();
		socket = new Socket(hostname, serverPort);
		// get the IO streams
		InputStream is = socket.getInputStream();
		OutputStream os = socket.getOutputStream();

		// Set up input stream readers
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader in = new BufferedReader(isr);
		OutputStreamWriter out = new OutputStreamWriter(os);

		
		Employee arne = new Employee("Arne bjarne", "arne@bjarne.no", "shubidubidu");
		String xml = XmlSerializer.getInstance().serialize(arne);
		out.write(xml+EOL);
		out.flush();
		
		String line;
		xml = "";
		while((line = in.readLine()) != null) {
			xml += line;
			if(line.equals("goodnight")) break;
		}
		System.out.println(xml);
		// Close streams and socket.
		out.close();
		in.close();
	}
}
