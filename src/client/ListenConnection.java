/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import Interface.GossipInterface;
import java.io.IOException;
import java.net.ServerSocket;
import protocol.Gossip;

/**
 *
 * @author rafael
 */
public class ListenConnection extends Thread{
    
    ServerSocket serverSocket = null;
    Gossip protocol;
    
    public ListenConnection(Gossip protocol){
        this.protocol = protocol;

        try {
            serverSocket = new ServerSocket(GossipInterface.PORT);
            System.out.println("Control: I'm listening at port " + GossipInterface.PORT);
        } catch (IOException e) {
            System.err.println("Error: ListenConnection: Could not listen on port: " + GossipInterface.PORT);
            System.exit(-1);
        }
    }
    
    
    
    @Override
    public void run(){
        boolean listening = true;
        
        while (listening){ //listening
            try {
                new ListenConnectionThread(serverSocket.accept(), protocol).start();
            } catch (IOException ex) {
                System.err.println("Error: ListenConnection: " + ex.getMessage());    
            }
        }
        
        try {
            serverSocket.close();
        } catch (IOException ex) {
            System.err.println("Error: ListenConnection: Could not listen on port: " + GossipInterface.PORT + "\n" + ex.getMessage());
        }
    }
    
}
