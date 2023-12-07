package org.example;

import org.json.JSONObject;

public class PlaceinfoParser {
    private final JSONObject json;

    public PlaceinfoParser(JSONObject json){
        this.json = json;
    }

    public void print_info(){
        System.out.println("Тип: " + json.getString("kinds"));
        if(json.has("wikipedia")){
            System.out.println("Страница на вики: " + json.getString("wikipedia"));
        }
        if(json.has("info")){
            System.out.print("Информация: ");
            JSONObject descr = (JSONObject) json.get("info");
            System.out.print(descr.getString("descr"));
        }
    }
}
