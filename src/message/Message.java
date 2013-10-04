/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package message;

/**
 *
 * @author rafael
 */
public class Message {
    private String id;
    private MessageStatus status;
    private String body;
    
    public Message(String address, String message){
        id = address;
        body = message;
        status = MessageStatus.INFECTIVE;
        System.out.println("Control: New message: ID " + id + " BODY: " + body);
    }
    
    public boolean equals(String id){
        return this.id.equals(id);
    }
    
    public void setRemoved(){
        status = MessageStatus.REMOVED;
    }
    
    public String getId(){
        return id;
    }
    
    public MessageStatus getStatus(){
        return status;
    }
    
    public String getBody() {
        return body;
    }
}
