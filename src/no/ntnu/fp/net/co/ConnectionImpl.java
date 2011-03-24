/*
 * Created on Oct 27, 2004
 */
package no.ntnu.fp.net.co;

import java.io.EOFException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.derby.tools.sysinfo;

import no.ntnu.fp.net.admin.Log;
import no.ntnu.fp.net.cl.ClException;
import no.ntnu.fp.net.cl.ClSocket;
import no.ntnu.fp.net.cl.KtnDatagram;
import no.ntnu.fp.net.cl.KtnDatagram.Flag;

/**
 * Implementation of the Connection-interface. <br>
 * <br>
 * This class implements the behavior in the methods specified in the interface
 * {@link Connection} over the unreliable, connectionless network realized in
 * {@link ClSocket}. The base class, {@link AbstractConnection} implements some
 * of the functionality, leaving message passing and error handling to this
 * implementation.
 * 
 * @author Dag Einar Monsen
 * @see no.ntnu.fp.net.co.Connection
 * @see no.ntnu.fp.net.cl.ClSocket
 */
public class ConnectionImpl extends AbstractConnection {
    /** Keeps track of the used ports for each server port. */
    private static Map<Integer, Boolean> usedPorts = Collections.synchronizedMap(new HashMap<Integer, Boolean>());

    
    /**
     * @return int A random port. Is checked against a list of used ports
     * The port Is stored in usedPorts by the abstract class
     */
    private static int getRandomPort() {
    	// assign a port between 60 000 and 65 000
    	int random = 60000 + (int)(Math.random() * 5000);
    	return !usedPorts.containsKey(random) ? random : getRandomPort();
    }
    /**
     * Initialize initial sequence number and setup state machine.
     * 
     * @param myPort the local port to associate with this connection
     */
    public ConnectionImpl(int myPort) {
    	super();
        this.myPort = myPort;
        myAddress = getIPv4Address();
    }

    private String getIPv4Address() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        }
        catch(UnknownHostException e) {
            return "127.0.0.1";
        }
    }

    /**
     * Establish a connection to a remote location.
     * 
     * @param remoteAddress - The remote IP-address to connect to
     * @param remotePort - The remote portnumber to connect to
     * @throws IOException If there's an I/O error.
     * @throws java.net.SocketTimeoutException If timeout expires before connection is completed.
     * @see Connection#connect(InetAddress, int)
     */
    public void connect(InetAddress remoteAddress, int remotePort) throws IOException, SocketTimeoutException {	    	
    	this.remoteAddress = remoteAddress.getHostAddress();
    	this.remotePort = remotePort;
    	
    	KtnDatagram synAck, synPacket = constructInternalPacket(Flag.SYN);
    	
    	try {
			simplySendPacket(synPacket);  // send SYN packet
			state = State.SYN_SENT; // set internal state
			synAck = receiveAck();  // receive SYNACK
			this.remotePort = synAck.getSrc_port(); // store new remotePort internally
			System.out.println(this.remotePort);
			sendAck(synAck, false); // ACKnowledge SYNACK
			state = State.ESTABLISHED; // set internal state to established
		}
    	catch(ClException e) {
			System.out.println("[Connection] Could not connect to remote server.");
		}
    }

    /**
     * Listen for and accept incoming connections.
     * 
     * @return A new ConnectionImpl-object representing the new connection.
     * @see Connection#accept()
     */
    public Connection accept() throws IOException, SocketTimeoutException {
		
    	state = State.LISTEN;
		KtnDatagram syn = null;
		
		// listen for a packet until a SYN is received
		do syn = receivePacket(true);
		while(syn == null || syn.getFlag() != Flag.SYN);
		
		
		
		// create a new connection, return it and continue listening.
		ConnectionImpl newConn = new ConnectionImpl(getRandomPort());
        /* Setting up a new connection */
		newConn.setRemoteAddress(syn.getSrc_addr());
		
		newConn.setRemotePort(syn.getSrc_port());
		newConn.setLastValidPacketReceived(syn);
		newConn.setState(State.SYN_RCVD);
		this.lastValidPacketReceived = syn;
          
		// set internal state and store remote address
		remoteAddress = syn.getSrc_addr();
		remotePort = syn.getSrc_port();
		
		int oldPort = myPort;
		// create a random port for the server and store it locally
		myPort = getRandomPort();
		
		syn.setDest_port(myPort);
		// send ACK
		sendAck(syn, true);
		// receive ACK
		receiveAck();
		// set internal state
		state = State.ESTABLISHED;
		
		return newConn;
    }

    /**
     * Send a message from the application.
     * 
     * @param msg the String to be sent.
     * @throws ConnectException If no connection exists.
     * @throws IOException If no ACK was received.
     * @see AbstractConnection#sendDataPacketWithRetransmit(KtnDatagram)
     * @see no.ntnu.fp.net.co.Connection#send(String)
     */
    public void send(String msg) throws ConnectException, IOException {
        KtnDatagram dataPacket = constructDataPacket(msg);
    	KtnDatagram ack = sendDataPacketWithRetransmit(dataPacket);
    }

    /**
     * Wait for incoming data.
     * 
     * @return The received data's payload as a String.
     * @throws IOException 
     * @throws EOFException 
     * @see Connection#receive()
     * @see AbstractConnection#receivePacket(boolean)
     * @see AbstractConnection#sendAck(KtnDatagram, boolean)
     */
    public String receive() throws IOException {
    	KtnDatagram packet;
		try {
			packet = receivePacket(false);
	    	
			//System.out.println(packet.getPayload().toString());
	    	// send ACK for payload packet
	    	sendAck(packet, false);
	    	// return the packets content
	    	return packet.getPayload().toString();
		} catch (EOFException e) {
			// received FIN
			state = State.CLOSE_WAIT;
			close();
		}
		return null;
    }

    /**
     * Close the connection.
     * 
     * @see Connection#close()
     */
    public void close() throws IOException {
    	
    	KtnDatagram ack1, ack2, fin2, fin1 = constructInternalPacket(Flag.FIN);
    //	sendDataPacketWithRetransmit(fin1);
    //	ack1 = receiveAck();
    	
    //	fin2 = receivePacket(true);
    //	sendAck(fin2, false);
    	
    	switch(state) {
			case ESTABLISHED: { // 
				
			} break;
	    	case FIN_WAIT_1:  { // client
				// receive ack
				
			} break;
	    	case FIN_WAIT_2:  { // client
				// receive fin
				
			} break;
	    	case CLOSE_WAIT:  { // server
				
				
			} break;
	    	case LAST_ACK: { // server
				
				
			} break;
    	}
    	
    }

    
    /**
     * Test a packet for transmission errors. This function should only called
     * with data or ACK packets in the ESTABLISHED state.
     * 
     * @param packet - Packet to test.
     * @return true if packet is free of errors, false otherwise. 
	*/
    protected boolean isValid(KtnDatagram packet) {
		// checksum must be valid
    	if(packet.calculateChecksum() == packet.getChecksum()) {
			// if there is a previous packet, validate sequence number
    		if(lastValidPacketReceived != null && packet.getSeq_nr() != lastValidPacketReceived.getSeq_nr() + 1) {
				return false;
			}
			return true;
       }
       return false;
    }

}
