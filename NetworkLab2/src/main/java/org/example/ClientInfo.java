package org.example;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;

public class ClientInfo {
    private String file_name;
    private long file_size;

    private final BufferedInputStream in;
    private final BufferedOutputStream out;

    public ClientInfo(BufferedInputStream in, BufferedOutputStream out) {
        this.in = in;
        this.out = out;
    }

    public String get_file_name(){
        return file_name;
    }

    public void set_file_name(String file_name){
        this.file_name = file_name;
    }

    public long get_file_size(){
        return file_size;
    }

    public void set_file_size(long file_size){
        this.file_size = file_size;
    }

    public BufferedInputStream get_in() {
        return in;
    }

    public BufferedOutputStream get_out() {
        return out;
    }
}
