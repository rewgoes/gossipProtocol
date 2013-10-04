/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.net.SocketException;
import java.sql.Timestamp;
import java.util.Date;
import javax.swing.JTextArea;

import message.MessagesControl;
import protocol.Gossip;

/**
 *
 * @author rafael
 */
public class Client {
    
    private NeighborsControl neighbors;
    private MessagesControl messages;
    private Gossip protocol;
    private BroadcastThread broadcast;
    private String myAddress;
    
    // Client constructor
    public Client(JTextArea s1, JTextArea s2) throws InterruptedException, SocketException{
        neighbors =  new NeighborsControl(s2);
        messages = new MessagesControl(s1);
        
        //Initializate protocol
        if (neighbors != null && messages != null){
            protocol = new Gossip(neighbors, messages);

            //Create and start threads
            broadcast = new BroadcastThread();
            broadcast.start();
            
            new ListenBroadcastThread(neighbors).start();

            if (protocol != null){
                new ListenConnection(protocol).start();
            }
        }
        
        while (broadcast.getMyAddress() == null){}
        myAddress = broadcast.getMyAddress();
    }
    
    public void sendMessage(String message){
        Date date= new java.util.Date();
        Timestamp time = new Timestamp(date.getTime());
        messages.add(myAddress + time + "=" + message);
    }
    
    public void hang(){
        broadcast.hang();
    }
    
    
}
