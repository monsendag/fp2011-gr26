package fp.common.network;

import java.io.IOException;
import java.net.InetAddress;

import fp.common.models.Employee;
import fp.common.models.XmlSerializer;


public class ClientConnection extends Connection {
	public ClientConnection() throws IOException {
		super(InetAddress.getLocalHost());
		
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
