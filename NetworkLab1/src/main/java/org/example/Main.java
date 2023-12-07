package org.example;

public class Main {
    public static void main(String[] args) {
        if(args.length != 1){
            System.out.println("Too many or too few args!");
            return;
        }
        String group = args[0];
        Thread client = new Thread(new MulticastSender(group));
        client.start();
        Thread server = new Thread(new MulticastReceiver(group));
        server.start();
    }
}