package org.example;

import org.json.JSONArray;
import org.json.JSONObject;

public class PlacesParser {
    private final JSONObject json;

    public PlacesParser(JSONObject json){
        this.json = json;
    }

    public String get_id(int num){
        JSONArray arr = (JSONArray) json.get("features");
        JSONObject feature = (JSONObject) arr.get(num);
        JSONObject properties = (JSONObject) feature.get("properties");
        return properties.getString("xid");
    }
}
