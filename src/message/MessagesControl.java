/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package message;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JTextArea;

/**
 *
 * @author rafael
 */
public class MessagesControl {

    private JTextArea saida1;
    private List<Message> messages;
    
    public MessagesControl(JTextArea s1) {
        saida1 = s1;
        messages = new ArrayList<Message>();
    }
    
    public void add(String message){
        String messageId = message.split("=")[0];
        String messageBody = message.split("=")[1];
        synchronized (this) {
            messages.add(new Message(messageId, messageBody));
            showMessages();
        }
    }
    
    public int size(){
        synchronized (this) {
            return messages.size();
        }
    }
    
    public Message get(int index){
        synchronized (this) {
            return messages.get(index);
        }
    }
    
    public boolean contains(String iMessage){
        String iMessageId = iMessage.split("=")[0];
        for(Message message : messages){
            synchronized (this) {
            if (message.getId().equals(iMessageId))
                return true;
            }
        }
        return false;
    }
    
    public void showMessages(){
        synchronized (this) {
            saida1.setText(null);
            for (Message message : messages) {
                saida1.append("Mensagem: "+ message.getBody() + ", Status: " + message.getStatus() + "\n");
            }
        }
    }
}
