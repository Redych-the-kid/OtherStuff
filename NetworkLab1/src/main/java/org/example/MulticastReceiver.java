package org.example;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

public class MulticastReceiver implements Runnable{
    private final String group;

    public MulticastReceiver(String group){
        this.group = group;
    }

    @Override
    public void run() {
        MulticastSocket ms = null;
        SocketAddress address = null;
        ArrayList<String> current_alive = new ArrayList<>();
        Thread checker = new Thread(new TableChecker(current_alive));
        checker.start();
        try {
            address = new InetSocketAddress(group, 5000);
            ms = new MulticastSocket(5000);
            ms.joinGroup(address, NetworkInterface.getByInetAddress(InetAddress.getByName(group)));
            while (true) {
                byte[] buf = new byte[1024];
                DatagramPacket dp = new DatagramPacket(buf, 1024);
                ms.receive(dp);
                String str = dp.getSocketAddress().toString();
                String check_message = new String(dp.getData(), 0, dp.getLength());
                if(!current_alive.contains(str) && check_message.equals("Fun things are fun!")){
                    current_alive.add(str);
                }
            }
        } catch (Exception e) {
            System.out.println("Error at the receiver side of the app!");
            e.printStackTrace();
        } finally {
            assert ms != null;
            try {
                ms.leaveGroup(address, NetworkInterface.getByInetAddress(InetAddress.getByName(group)));
            } catch (IOException e) {
                System.out.println("Error while leaving the group!");
                e.printStackTrace();
            }
            ms.close();
        }
    }
}
