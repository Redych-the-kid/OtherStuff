package org.example;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class Client {
    private static Socket socket;
    private static BufferedInputStream in;
    private static BufferedOutputStream out;
    private static final int header_size = 1024;

    private static void print_help(){
        System.out.println("Usage: Client.java + {absolute file_path} + {server_address} + {server_port}");
    }

    public static void main(String[] args){
        if(args.length < 3){
            print_help();
            return;
        }
        try{
            socket = new Socket(args[1], Integer.parseInt(args[2]));
            in = new BufferedInputStream(socket.getInputStream());
            out = new BufferedOutputStream(socket.getOutputStream());
            send_to_server(new File(args[0]));
        } catch (IOException e){
            System.err.println(e.getMessage());
        }
    }

    private static byte[] make_header(File file) {
        ByteBuffer header = ByteBuffer.allocate(header_size);
        header.put("Info:".getBytes(StandardCharsets.UTF_8));
        header.put(file.getName().getBytes(StandardCharsets.UTF_8));
        header.put(":".getBytes(StandardCharsets.UTF_8));
        header.put(String.valueOf(file.length()).getBytes(StandardCharsets.UTF_8));
        header.put("#".getBytes(StandardCharsets.UTF_8));
        return header.array();
    }

    private static void send_file(File file) throws IOException {
        InputStream fin = new FileInputStream(file);
        out.write(make_header(file));
        out.flush();
        long total_read_size = 0;
        int read_size;
        while (true){
            byte[] data = new byte[1024];
            if((read_size = fin.read(data)) == -1){
                break;
            }
            total_read_size += read_size;
            out.write(data, 0, read_size);
            out.flush();
        }
        out.close();
        fin.close();
        System.out.println("File size: " + total_read_size);
    }

    private static void send_to_server(File file){
        try{
            int attempts_counter = 0;
            String response = null;
            do {
                attempts_counter++;
                System.out.println("Trying to send the file");
                send_file(file);
                byte[] response_bytes = new byte[10];
                if(in.read(response_bytes) != -1){
                    response = new String(response_bytes, StandardCharsets.UTF_8);
                } else{
                    System.out.println("Failed to get any server response!");
                    return;
                }
                System.out.println(response);
            } while (response.startsWith("failed") && attempts_counter < 3);
            in.close();
            if(response.equals("failure")){
                System.out.println("Can't send file " + file.getName());
                return;
            }
            System.out.println("File has been sent successfully!");
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
