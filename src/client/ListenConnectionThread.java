/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import protocol.Gossip;

/**
 *
 * @author rafael
 */
public class ListenConnectionThread extends Thread{
    
    Socket socket = null;
    boolean listening = true;
    Gossip protocol;
    
    public ListenConnectionThread(Socket socket, Gossip protocol){
        this.protocol = protocol;
        this.socket = socket;
    }
    
    @Override
    public void run(){
        try {
            if (!socket.isClosed()){
                
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                
                String inputLine, outputLine;
                
                inputLine = in.readLine();
                outputLine = protocol.processInput(socket.getInetAddress().toString().split("/")[1],  inputLine);
                
                out.println(outputLine);
                
                inputLine = in.readLine();
                if (inputLine != null){
                    if (inputLine.equals("wantNodes")){
                        out.write(protocol.getNodes() + "\n");
                        out.flush();
                    }
                }
                    
                out.close();
                in.close();
                socket.close();
            }
        } catch (IOException e) {
            System.err.println("Error: ListenConnectionThread: " + e.getMessage());
        }
        
    }
}
