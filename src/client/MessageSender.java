/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import Interface.GossipInterface;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 *
 * @author rafael
 */
public class MessageSender {

    private int status;
    private String nodes; //new nodes to be added
    
    public int getStatus(){
        return status;
    }

    public String getNodes(){
        return nodes;
    }
    
    public MessageSender(String target, String message) throws IOException {
        
        status = 0;
        Socket socket = null;
        PrintWriter out = null;
        BufferedReader in = null;
        String inputLine;
 
        try {
            socket = new Socket(target, GossipInterface.PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            out.println(message);
            out.flush();
            
            inputLine = in.readLine();
            if(inputLine != null){
                if (inputLine.equals("ok")){
                    status = 1;
                }
                else if (inputLine.equals("known")){
                    status = 2;
                }
                else if (inputLine.equals("wantNodes | ok")){
                    status = 3;
                    out.println("wantNodes");
                }
                else if (inputLine.equals("wantNodes | known")){
                    status = 4;
                    out.println("wantNodes");
                }
            }
            
            if (status == 3 || status == 4){
                inputLine = in.readLine();
                if(inputLine != null){
                    nodes = inputLine;
                }
            }

        } catch (UnknownHostException e) {
            System.err.println("Error: MessageSenderThread: Don't know about host: " + target + ":" + GossipInterface.PORT + "\n" + e.getMessage());
        } 
 
        try {
            out.close();
            in.close();
            socket.close();
        } catch (IOException ex) {
            System.err.println("Error: MessageSenderThread: " + ex.getMessage());
        }
        
    }
}
