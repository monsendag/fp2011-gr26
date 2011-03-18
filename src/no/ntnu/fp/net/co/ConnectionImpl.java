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

import no.ntnu.fp.net.admin.Log;
import no.ntnu.fp.net.cl.ClException;
import no.ntnu.fp.net.cl.ClSocket;
import no.ntnu.fp.net.cl.KtnDatagram;
import no.ntnu.fp.net.cl.KtnDatagram.Flag;

/**
 * Implementation of the Connection-interface. <br>
 * <br>
 * This class implements the behaviour in the methods specified in the interface
 * {@link Connection} over the unreliable, connectionless network realised in
 * {@link ClSocket}. The base class, {@link AbstractConnection} implements some
 * of the functionality, leaving message passing and error handling to this
 * implementation.
 * 
 * @author Sebjørn Birkeland and Stein Jakob Nordbø
 * @see no.ntnu.fp.net.co.Connection
 * @see no.ntnu.fp.net.cl.ClSocket
 */
public class ConnectionImpl extends AbstractConnection {

    /** Keeps track of the used ports for each server port. */
    private static Map<Integer, Boolean> usedPorts = Collections.synchronizedMap(new HashMap<Integer, Boolean>());

    
    private static int getRandomPort() {
    	int random = (int)Math.random()*10000 + 60000;
    	return !usedPorts.containsKey(random) ? random : getRandomPort();
    }
    /**
     * Initialise initial sequence number and setup state machine.
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
        catch (UnknownHostException e) {
            return "127.0.0.1";
        }
    }

    /**
     * Establish a connection to a remote location.
     * 
     * @param remoteAddress
     *            - the remote IP-address to connect to
     * @param remotePort
     *            - the remote portnumber to connect to
     * @throws IOException
     *             If there's an I/O error.
     * @throws java.net.SocketTimeoutException
     *             If timeout expires before connection is completed.
     * @see Connection#connect(InetAddress, int)
     */
    public void connect(InetAddress remoteAddress, int remotePort) throws IOException, SocketTimeoutException {	
    	this.remoteAddress = remoteAddress.getHostAddress();
    	this.remotePort = remotePort;
    	KtnDatagram synAck, Ack, synPacket = constructInternalPacket(Flag.SYN);
    	try {
			simplySendPacket(synPacket);
			state = State.SYN_SENT;
			synAck = receiveAck();
			remotePort = synAck.getSrc_port();
			sendAck(synAck, false);
			state = State.ESTABLISHED;
		} catch (ClException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    /**
     * Listen for, and accept, incoming connections.
     * 
     * @return A new ConnectionImpl-object representing the new connection.
     * @see Connection#accept()
     */
    public Connection accept() throws IOException, SocketTimeoutException {
		state = State.LISTEN;
		KtnDatagram syn = null;
		
		while(syn == null) {
			syn = receivePacket(true);
		}
		if(syn.getFlag() != Flag.SYN) return accept();
		
		state = State.SYN_RCVD;
		remoteAddress = syn.getSrc_addr();
		remotePort = syn.getSrc_port();
		
		int newPort = getRandomPort();
		
		syn.setDest_port(newPort);
		
		sendAck(syn, true);
		
		receiveAck();
		
		state = State.ESTABLISHED;
		
		Connection newConn = new ConnectionImpl(newPort);
		
		
		return this; //new ConnectionImpl(newPort);
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
    	sendDataPacketWithRetransmit(dataPacket);
    }

    /**
     * Wait for incoming data.
     * 
     * @return The received data's payload as a String.
     * @see Connection#receive()
     * @see AbstractConnection#receivePacket(boolean)
     * @see AbstractConnection#sendAck(KtnDatagram, boolean)
     */
    public String receive() throws ConnectException, IOException {
    	KtnDatagram packet = receivePacket(false);
    	sendAck(packet, false);
    	return packet.getPayload().toString();
    }

    /**
     * Close the connection.
     * 
     * @see Connection#close()
     */
    public void close() throws IOException {
    	
    //	KtnDatagram ack1, ack2, fin2, fin1 = constructInternalPacket(Flag.FIN);
    //	sendDataPacketWithRetransmit(fin1);
    //	ack1 = receiveAck();
    	
    //	fin2 = receivePacket(true);
    //	sendAck(fin2, false);
    	
    	switch(state) {
    	case ESTABLISHED: break;
    	case FIN_WAIT_1: break;
    	case FIN_WAIT_2: break;
    	case CLOSE_WAIT: break;
    	case LAST_ACK: break;
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
    	return true;
    }
}
