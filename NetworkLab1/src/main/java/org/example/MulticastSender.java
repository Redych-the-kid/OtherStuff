package org.example;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;

public class MulticastSender implements Runnable {
    private final String group;

    public MulticastSender(String group){
        this.group = group;
    }
    @Override
    public void run() {
        MulticastSocket ms = null;
        try {
            ms = new MulticastSocket();
            String message = "Fun things are fun!";
            DatagramPacket dp = new DatagramPacket(message.getBytes(StandardCharsets.UTF_8), message.length(), InetAddress.getByName(group), 5000);
            while (true){
                Thread.sleep(3000);
                ms.send(dp);
            }
        } catch (Exception e) {
            System.out.println("Error at the sender side!");
            e.printStackTrace();
        } finally {
            assert ms != null;
            ms.close();
        }
    }
}
