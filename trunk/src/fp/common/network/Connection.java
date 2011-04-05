package fp.common.network;

import java.net.Socket;

public abstract class Connection {
	public static int serverPort = 6789;
	final static String EOL = "\n";
	protected Socket socket;


}
