/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package protocol;

import static java.lang.Thread.sleep;
import message.Message;

/**
 *
 * @author rafael
 */
public class GossipThread extends Thread{
    
    Gossip protocol;
    
    private final int WAIT_TIME_NO_MESSAGE = 10000;
    private final int WAIT_TIME_HAS_MESSAGE = 1000;
    
    public GossipThread(Gossip protocol){
        this.protocol = protocol;
    }
    
    @Override
    public void run(){
        while (true) {
            try {
                Message message = protocol.getOneMessage();

                if (message != null){
                    protocol.sendMessage(message);
                    sleep(WAIT_TIME_HAS_MESSAGE);
                }
                else{
                    sleep(WAIT_TIME_NO_MESSAGE);
                }
            } catch (Exception ex) {
                System.err.println("Error: GossipThread: " + ex.getMessage());            
            }
        }
    }
    
}
