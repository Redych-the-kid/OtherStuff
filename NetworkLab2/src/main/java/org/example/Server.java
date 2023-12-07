package org.example;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Clock;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {
    private static int port;
    private final Clock clock = Clock.systemUTC();
    private static final CopyOnWriteArrayList<Thread> handles = new CopyOnWriteArrayList<>();

    private static void print_help(){
        System.out.println("Usage: Server.java + {port_number}");
    }

    public static void main(String[] args){
        if(args.length < 1){
            print_help();
            return;
        }
        port = Integer.parseInt(args[0]);
        Thread listener = new Thread(Server::handle_connections);
        Thread killer = new Thread(Server::close_connections);
        listener.start();
        killer.start();
        try {
            listener.join();
        } catch (InterruptedException e){
            throw new RuntimeException();
        }
        killer.interrupt();
    }

    private static void close_connections() {
        handles.removeIf(s -> !s.isAlive());
    }

    private static void handle_connections(){
        try (ServerSocket socket = new ServerSocket(port)){
            System.out.println("Server started!");
            while (true){
                Socket client = socket.accept();
                System.out.println("New connection appeared!");
                Thread handle = new Thread(new ClientHandle(client));
                handle.start();
                handles.add(handle);
            }
        } catch (IOException e){
            System.err.println(e.getMessage());
        }
    }
}
