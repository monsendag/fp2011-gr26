package fp.server.network;

import java.net.*;
import java.io.*;

public class TestServer {

  static final int LISTENING_PORT = 3210;

  public static void main(String[] args) {

    ServerSocket listener; // Listens for connection requests.
    Socket connection; // A socket for communicating with
                       // a client.

    /* Listen for connection requests from clients. For
       each connection, create a separate Thread of type
       ConnectionHandler to process it. The ConnectionHandler
       class is defined below. The server runs until the
       program is terminated, for example by a CONTROL-C. */

    try {
      listener = new ServerSocket(LISTENING_PORT);
      System.out.println("Listening on port " + LISTENING_PORT);
      while (true) {
        connection = listener.accept();
        new ConnectionHandler(connection);
      }
    }
    catch (Exception e) {
      System.out.println("Server shut down unexpectedly.");
      System.out.println("Error: " + e);
      return;
    }

} // end main()

static class ConnectionHandler extends Thread {
   // An object of this class is a thread that will
   // process the connection with one client. The
   // thread starts itself in the constructor.

  Socket connection; // A connection to the client.
  DataInputStream incoming; // For reading data from the client.
  PrintWriter outgoing; // For transmitting data to the client.

  ConnectionHandler(Socket conn) {
     // Constructor. Record the connection and
     // the directory and start the thread running.
    connection = conn;
    start();
  }

  void sendIndex() throws Exception {

  }

  void sendFile(String fileName) throws Exception {
     // This is called by the run() command in response
     // to "get <fileName>" command. If the file doesn't
     // exist, send the message "error". Otherwise,
     // send the message "ok" followed by the contents
     // of the file.
    File file = new File(fileName);
    if ( (! file.exists()) || file.isDirectory() ) {
       // (Note: Don't try to send a directory, which
       // shouldn't be there anyway.)
      outgoing.println("error");
    }
    else {
      outgoing.println("ok");
      BufferedReader fileIn = new BufferedReader(
    		  new InputStreamReader(
              new FileInputStream(file)));
      
      String str;
      while ((str = fileIn.readLine()) != null) {
         // Read and send lines from the file until
         // an end-of-file is encountered.
        outgoing.println(str);
      }
    }
    outgoing.flush();
    outgoing.close();
    if (outgoing.checkError())
      throw new Exception("Error while transmitting data.");
  }

  public void run() {
     // This is the method that is executed by the thread.
     // It creates streams for communicating with the client,
     // reads a command from the client, and carries out that
     // command. The connection is logged to standard output.
     // An output beginning with ERROR indicates that a network
     // error occurred. A line beginning with OK means that
     // there was no network error, but does not imply that the
     // command from the client was a legal command.
    String command = "Command not read";
    try {
      incoming = new DataInputStream( connection.getInputStream() );
      outgoing = new PrintWriter( connection.getOutputStream() );
      command = incoming.readLine();
      if (command.equals("index")) {
        sendIndex();
      }
      else if (command.startsWith("get")){
        String fileName = command.substring(3).trim();
        sendFile(fileName);
      }
      else {
        outgoing.println("unknown command");
        outgoing.flush();
      }
      System.out.println("OK " + connection.getInetAddress() + " " + command);
    }
    catch (Exception e) {
      System.out.println("ERROR " + connection.getInetAddress() + " " + command + " " + e);
    }
    finally {
      try {
        connection.close();
      }
      catch (IOException e) {
      }
    }
  }

 } // end nested class ConnectionHandler

}