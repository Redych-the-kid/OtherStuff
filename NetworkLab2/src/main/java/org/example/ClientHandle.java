package org.example;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.locks.ReentrantLock;

public class ClientHandle implements Runnable {
    static ClientInfo info;
    private static final int BUFSIZ = 1024;
    static ReentrantLock locker = new ReentrantLock();
    private final Clock clock = Clock.systemUTC();
    private static long file_size = 0;
    private static long last_size = 0;
    private static int time = 0;

    public ClientHandle(Socket client) throws IOException {
        info = new ClientInfo(new BufferedInputStream(client.getInputStream()), new BufferedOutputStream(client.getOutputStream()));
    }

    @Override
    public void run() {
        Thread speedometer = new Thread(ClientHandle::print_speed);
        System.out.println("Speedometer started!");
        Thread reader = new Thread(ClientHandle::client_reader);
        System.out.println("Reader started!");
        Instant begin = clock.instant();
        reader.start();
        speedometer.start();
        try {
            reader.join();
            System.out.println("Connection closing!");
        } catch (InterruptedException e) {
            throw new RuntimeException();
        } finally {
            speedometer.interrupt();
            if (time < 1) {
                System.out.println("Average speed: " + (file_size / Duration.between(begin, clock.instant()).toMillis()) / 100);
            }
            file_size = 0;
            last_size = 0;
            time = 0;
        }
    }

    private static void print_speed() {
        while (true) {
            try {
                Thread.sleep(3000);
                time++;
                locker.lock();
                System.out.println("Average speed: " + file_size / (time * 3L) + " bytes per second");
                locker.unlock();
                if (Thread.interrupted()) {
                    return;
                }
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    private static void client_reader() {
        FileOutputStream writer = null;
        Instant begin = null;
        byte[] input = new byte[BUFSIZ];
        try {
            System.out.println("Waiting for data");
            String header;
            int read_counter;
            read_counter = info.get_in().read(input);
            if (read_counter == -1) {
                throw new IOException();
            }
            int header_size = 0;
            header = new String(input, 0, read_counter, StandardCharsets.UTF_8);
            while (header_size < BUFSIZ) {
                if (header.charAt(header_size) == '#') {
                    break;
                }
                header_size++;
            }
            header = header.substring(0, header_size);
            if (header.startsWith("Info:")) {
                String[] message = header.split(":");
                System.out.println("Header get!");
                info.set_file_name(message[1]);
                info.set_file_size(Long.parseLong(message[2]));
                writer = open_file();
            } else {
                throw new IOException("Header Error");
            }
            do {
                System.out.println("Reading data: " + (float) file_size / info.get_file_size()  * 100 + "%");
                read_counter = info.get_in().read(input);
                if (read_counter == -1) {
                    break;
                }
                locker.lock();
                file_size += read_counter;
                locker.unlock();
                if (writer != null) {
                    writer.write(input, 0, read_counter);
                    writer.flush();
                } else {
                    System.out.println("Formatting error in client message");
                    return;
                }
                Thread.sleep(5);
            } while (true);
            writer.close();
            if (file_size != info.get_file_size()) {
                System.out.println("Error!File sizes does not match!");
                client_write("success".getBytes(StandardCharsets.UTF_8));
                return;
            }
            client_write("failure".getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            client_write("failure".getBytes(StandardCharsets.UTF_8));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                info.get_in().close();
                info.get_out().close();
            } catch (IOException ignored) {}
        }
    }

    private static void client_write(byte[] bytes) {
        try {
            info.get_out().write(bytes);
            info.get_out().flush();
        } catch (IOException e){
            System.err.println("Failed to write back to client!");
        }
    }

    private static FileOutputStream open_file() {
        FileOutputStream writer;
        int i = 0;
        while (true){
            try {
                File file = new File("input" + i + info.get_file_name());
                if(!file.createNewFile()){
                    i++;
                    continue;
                }
                writer = new FileOutputStream(file, false);
                break;
            } catch (IOException ignored){
                i++;
            }
        }
        return writer;
    }
}
