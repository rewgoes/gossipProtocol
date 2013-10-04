/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package protocol;

import Interface.GossipInterface;
import client.MessageSender;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import message.Message;
import message.MessagesControl;
import client.NeighborsControl;
import message.MessageStatus;

/**
 *
 * @author rafael
 */
public class Gossip {
     
    private NeighborsControl neighbors;
    private MessagesControl messages;
    
    public Gossip(NeighborsControl neighbors, MessagesControl messages) {
        this.neighbors = neighbors;
        this.messages = messages;
        
        new GossipThread(this).start();
    }
    
    //verify if message is already known
    public String processInput(String address, String theInput) {
        String returner = "";
        if (!neighbors.contains(address)){
            System.out.println("Control: Unknown node: " + address + " sent me a message: " + theInput);
            synchronized (this){
               neighbors.add(address); //try to add the address in the targetsList
           }
           returner = "wantNodes | ";
        }
        if (messages.contains(theInput)){
            System.out.println("Control: KNOWN Message received from: " + address + " message: " + theInput);
            return returner + "known";
        }
        else {
            messages.add(theInput);
            System.out.println("Control: UNKNOWN Message received from: " + address + " message: " + theInput);
            return returner + "ok";
        }
    }
    
    public void sendMessage(Message message){
        System.out.println("Control: Looking for someone to gossip");
        int qtyNeighbors = neighbors.size();
        if (qtyNeighbors > 0){
            Random randomGenerator = new Random();
            int targetIndex = randomGenerator.nextInt(qtyNeighbors);
            
            String target = neighbors.get(targetIndex);
            
            MessageSender sender;
            try {
                // String "\*/" divide the message in Id and Body
                sender = new MessageSender(target, message.getId() + "=" + message.getBody());
                
                while(sender.getStatus() == 0){}
                System.out.println("Control: Sending " + message.getBody() + "to " + target);
                // status = 1 => 'Ok'
                if (sender.getStatus() == 2 || sender.getStatus() == 4){ //Message is known
                    System.out.println("Control: " + target + " already knew my message: " + message.getBody());
                    message.setRemoved();
                    messages.showMessages();
                }
                if (sender.getStatus() == 3 || sender.getStatus() == 4){ //Node offered a list of neighbors
                    while (sender.getNodes() == null){}
                    System.out.println("Control: Received more nodes from " + target);
                    for(String node : sender.getNodes().split("-")){
                        if (!neighbors.contains(node)){
                            synchronized (this){
                               neighbors.add(node); //try to add the address in the targetsList
                           }
                        }
                    }
                }
            } catch (IOException ex) {
                neighbors.remove(target);
                System.err.println("Error: MessageSenderThread: Couldn't get I/O for the connection to: " + target + ":" + GossipInterface.PORT + "\n" + ex.getMessage());
            }
            
        }
    }
    
    public Message getOneMessage(){
        List<Integer> indexes = new ArrayList<Integer>();
        int qtyMesssages = 0;
        for(int index = 0; index < messages.size(); index++){
            if(messages.get(index).getStatus() == MessageStatus.INFECTIVE){
                indexes.add(index);
                qtyMesssages++;
            }
        }
        if (qtyMesssages > 0){
            Random randomGenerator = new Random();
            int targetIndex = randomGenerator.nextInt(qtyMesssages);
            System.out.println("Control: Want to gossip: " + messages.get(indexes.get(targetIndex)).getBody());
            return messages.get(indexes.get(targetIndex));
        }
        return null;
    }
    
    public String getNodes(){
        return neighbors.getNodes();
    }
}
