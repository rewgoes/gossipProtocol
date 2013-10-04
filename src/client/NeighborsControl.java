/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JTextArea;

/**
 *
 * @author rafael
 */
public class NeighborsControl {
    
    private JTextArea saida2;
    private List<String> neighbors;
    private List<String> myAddresses;
    
    public NeighborsControl(JTextArea s2) {
        saida2 = s2;
        neighbors = new ArrayList<String>();
        myAddresses = new ArrayList<String>();
    }
    
    public void add(String message){
        synchronized (this) {
            neighbors.add(message);
            showNeighbors();
        }
    }
    
    public void remove(String message){
        synchronized (this) {
            neighbors.remove(message);
            showNeighbors();
        }
    }
    
    public int size(){
        synchronized (this) {
            return neighbors.size();
        }
    }
    
    public String get(int index){
        synchronized (this) {
            return neighbors.get(index);
        }
    }
    
    public boolean contains(String address){
        synchronized (this) {
            return neighbors.contains(address) || myAddresses.contains(address);
        }
    }
    
    public void showNeighbors(){
        synchronized (this) {
            saida2.setText(null);
            for (String neighbor : neighbors) {
                saida2.append(neighbor.toString() + "\n");
            }
        }
    }

    public String getNodes() {
        synchronized (this) {
            String returner = new String();
            for (String neighbor : neighbors) {
                returner = returner + neighbor;
                returner += "-";
            }
            return returner;
        }
    }

    void addMyaddress(String myAdress) {
        myAddresses.add(myAdress);
    }
    
}
