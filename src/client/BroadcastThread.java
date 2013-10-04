/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.io.BufferedReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import Interface.GossipInterface;

/**
 *
 * @author rafael
 */
public class BroadcastThread extends Thread{
    
    private int broadcastPort = GossipInterface.PORT; //Port used to receive broadcast messages
    private DatagramSocket socket = null;
    private BufferedReader in = null;
    private List<InetAddress> broadcastList; //List of possible broadcast addresses
    private byte[] message = "hi".getBytes(); //Message used to broadcast
    private DatagramPacket packet; //Packet to send in broadcast
    private int WAIT_TIME = 1000;
    private final int MAX_WAIT_TIME = 60000;
    private boolean suspend;
    private String myAddress;
    
    public void hang(){
        suspend = !suspend;
        if (suspend){
            System.out.println("Control: Not broadcasting");
        } else {
            System.out.println("Control: Broadcasting");
        }
        
    }
    
    public String getMyAddress(){
        return myAddress;
    }
    
    public BroadcastThread() throws InterruptedException{
        //Verity possible intefaces that are not loopback
        suspend = true;
        
        broadcastList = new ArrayList<InetAddress>();
        
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                if (networkInterface.isLoopback())
                    continue;    // Don't want to broadcast to the loopback interface
                // If not loopback, get its broadcast address
                for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                    InetAddress broadcast = interfaceAddress.getBroadcast();
                    if (broadcast == null)
                        continue;
                    else{
                        myAddress = interfaceAddress.getAddress().getHostAddress();
                        broadcastList.add(broadcast);
                        System.out.println("Control: Broadcast address: " + broadcast);
                    }
                }
            }
        } catch (Exception ex) {
            System.err.println("Error: BroadcastThread: " + ex.getMessage());
        }
    }

    @Override
    public void run() {
        while(true){
            try {
                sleep(1);
            } catch (InterruptedException ex) {
                System.err.println("Error: BroadcastThread: " + ex.getMessage());
            }
            while (!suspend) {
                try {
                   for (InetAddress broadcastAddress : broadcastList) {
                      socket = new DatagramSocket();
                      socket.setBroadcast(true);
                      packet = new DatagramPacket(message, message.length, broadcastAddress, broadcastPort);
                      socket.send(packet); //send broadcast packet

                      socket.close();
                   }

                   if (WAIT_TIME < MAX_WAIT_TIME) {
                       WAIT_TIME += 1000;
                   }

                   sleep(WAIT_TIME);
                } catch (Exception ex) {
                    System.err.println("Error: BroadcastThread: " + ex.getMessage());            
                }
            }
        }
    }
    
}
