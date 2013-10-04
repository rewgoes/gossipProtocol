/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import Interface.GossipInterface;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 *
 * @author rafael
 */
public class ListenBroadcastThread extends Thread{
    
    private int broadcastPort = GossipInterface.PORT; 
    private DatagramSocket socket;
    private DatagramPacket packet;
    private String messageReceived;
    private String senderAddress;
    private byte[] packetBytes;
    private NeighborsControl nodes; //List of neighbors
    private List<String> myAddress; //List of neighbors
    
    public ListenBroadcastThread(NeighborsControl node) throws InterruptedException, SocketException{
        nodes = node;
        myAddress = new ArrayList<String>();
        
        for (Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces(); ifaces.hasMoreElements(); )        {
            NetworkInterface iface = ifaces.nextElement();
            System.out.println("Control: Interface: " + iface.getName() + ":");
            for (Enumeration<InetAddress> addresses = iface.getInetAddresses(); addresses.hasMoreElements(); ) {
                InetAddress address = addresses.nextElement();
                nodes.addMyaddress(address.getHostAddress());
                System.out.println("Control:  My Address: " + address.getHostAddress());
                myAddress.add(address.getHostAddress());
            }
        }
        
        try {
           socket = new DatagramSocket(broadcastPort);
           packetBytes = new byte["hi".length()];
           packet = new DatagramPacket(packetBytes, packetBytes.length);
        } catch (Exception ex) {
           System.err.println("Error: ListenBroadcastThread " + ex.getMessage());         
        }
    }
    
    @Override
    public void run(){
        while (true) {
            try {
               //keep waiting ultil receive a packet
               socket.receive(packet);
               messageReceived = new String(packet.getData());

               //verify it is valid
               if (messageReceived.equals("hi")) {
                  senderAddress = packet.getAddress().getHostAddress();
                  if ((!nodes.contains(senderAddress)) &&  (!myAddress.contains(senderAddress))){
                       synchronized (this){
                           nodes.add(senderAddress); //try to add the address in the targetsList
                           
                       }
                       System.out.println("Control: Connection requested by: " + senderAddress);
                  }
               }
            } catch (Exception ex) {
               System.err.println("Error: ListenBroadcastThread: " + ex.getMessage());            
            }
        }
    }
}
