package org.example;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Scanner;

public class ResponceUtils {
    public static String parse_weather(String body) {
        JSONObject json = new JSONObject(body);
        WeatherParser parser = new WeatherParser(json);
        parser.print_weather();
        return null;
    }

    public static String parse_city(String body){
        JSONObject json = new JSONObject(body);
        JSONArray arr = (JSONArray) json.get("hits");
        System.out.println("Выберите место:");
        Scanner scanner = new Scanner(System.in);
        for(int i = 0; i < arr.length(); ++i){
            JSONObject pos = (JSONObject) arr.get(i);
            if(json.has("city")){
                System.out.printf("%d.Имя: %s; Город: %s; Страна: %s%n", i,pos.get("name"), pos.get("city"), pos.get("country"));
            }
            else{
                System.out.printf("%d.Имя: %s; Страна: %s%n", i, pos.get("name"), pos.get("country"));
            }
        }
        System.out.print("Ваш выбор:");
        int num = Integer.parseInt(scanner.nextLine());
        LocationParser parser = new LocationParser(json);
        return Arrays.toString(parser.get_point(num));
    }
    public static String parse_places(String body){
        JSONObject json = new JSONObject(body);
        System.out.println("Выберите интересное для вас место: ");
        JSONArray arr = (JSONArray) json.get("features");
        Scanner scanner = new Scanner(System.in);
        for(int i = 0; i < arr.length(); ++i){
            JSONObject pos = (JSONObject) arr.get(i);
            JSONObject props = (JSONObject) pos.get("properties");
            String name = props.getString("name");
            if(name.length() != 0){
                System.out.println(i + ".Имя: " + name);
            }
        }
        System.out.print("Ваш выбор: ");
        int num = Integer.parseInt(scanner.nextLine());
        PlacesParser parser = new PlacesParser(json);
        return parser.get_id(num);
    }

    public static String parse_info(String body){
        JSONObject json = new JSONObject(body);
        PlaceinfoParser parser = new PlaceinfoParser(json);
        parser.print_info();
        return null;
    }
}
