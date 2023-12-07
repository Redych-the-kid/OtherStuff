package org.example;

import java.util.ArrayList;

public class TableChecker implements Runnable {
    private final ArrayList<String> current_alive;
    private final ArrayList<String> alive = new ArrayList<>();
    public TableChecker(ArrayList<String> current_alive){
        this.current_alive = current_alive;
    }

    @Override
    public void run() {
        while (true){
            try {
                Thread.sleep(3500);
            } catch (InterruptedException e) {
                System.out.println("ME!NEED!SLEEP!");
                e.printStackTrace();
            }
            if(!alive.equals(current_alive)){
                alive.clear();
                for(int i = 0;i < current_alive.size();++i){
                    alive.add(i, current_alive.get(i));
                }
                System.out.println(alive);
            }
            current_alive.clear();
        }
    }
}
