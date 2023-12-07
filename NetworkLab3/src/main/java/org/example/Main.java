package org.example;

import java.io.IOException;
import java.net.http.HttpClient;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Main {
    public static void main(String[] args) throws ExecutionException, InterruptedException, IOException {
        HttpClient client = HttpClient.newHttpClient();
        Scanner scanner = new Scanner(System.in);
        System.out.print("Введите город:");
        String city = scanner.nextLine();
        System.out.println("Пожалуйста подождите...");
        String sc_request = "https://graphhopper.com/api/1/geocode?q=%s&locale=en&key=c95bf823-52d0-49d3-82a2-0cfa6059747d".formatted(city);
        APIRequest c_request = new APIRequest(sc_request, client);
        CompletableFuture<String> c_responce = c_request.request(ResponceUtils::parse_city);
        String arr = c_responce.get().replaceAll("\\[", "").replaceAll("]","");
        String[] parsed = arr.split(",");
        double lat = Double.parseDouble(parsed[1]);
        double lon = Double.parseDouble(parsed[0]);
        System.out.print("Введите радиус:");
        int radius = Integer.parseInt(scanner.nextLine());
        System.out.println("Пожалуйста подождите...");
        String sw_request = "http://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&appid=54af45efc419b07de500a5523ca322ca&units=metric&lang=ru".formatted(lat, lon);
        APIRequest w_request = new APIRequest(sw_request, client);
        CompletableFuture<String> w_responce = w_request.request(ResponceUtils::parse_weather);
        String sp_request = "https://api.opentripmap.com/0.1/ru/places/radius?radius=%d&lon=%s&lat=%s&apikey=5ae2e3f221c38a28845f05b6a7361170b85c2ad4e2687342b3950e49".formatted(radius, lon, lat);
        APIRequest p_request = new APIRequest(sp_request, client);
        CompletableFuture<String> p_responce = p_request.request(ResponceUtils::parse_places);
        w_responce.get();
        String id = p_responce.get();
        String si_request = "https://api.opentripmap.com/0.1/ru/places/xid/%s?apikey=5ae2e3f221c38a28845f05b6a7361170b85c2ad4e2687342b3950e49".formatted(id);
        APIRequest i_request = new APIRequest(si_request, client);
        CompletableFuture<String> i_responce = i_request.request(ResponceUtils::parse_info);
        i_responce.get();
    }


}