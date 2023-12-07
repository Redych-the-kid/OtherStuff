package org.example;

import org.json.JSONArray;
import org.json.JSONObject;

public class LocationParser {
    private final JSONObject json;

    public LocationParser(JSONObject json){
        this.json = json;
    }

    public double[] get_point(int num){
        JSONArray arr = (JSONArray) json.get("hits");
        JSONObject pos = (JSONObject) arr.get(num);
        JSONObject coords = (JSONObject) pos.get("point");
        double[] res = {0, 0};
        res[0] = coords.getDouble("lng");
        res[1] = coords.getDouble("lat");
        return res;
    }
}
