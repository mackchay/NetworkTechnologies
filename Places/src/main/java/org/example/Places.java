package org.example;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import kotlin.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Places {

    private static final int MAX_PLACES_COUNT = 12;

    private final HashMap<String, String> apiKeys;

    private Map<Integer, Pair<Double, Double>> coords = new HashMap<>();

    public Places(HashMap<String, String> apiKeys) {
        this.apiKeys = apiKeys;
    }

    public String getWeather(Integer placeID) throws IOException {

        String reqStr = "https://api.openweathermap.org/data/2.5/weather?lat=" + coords.get(placeID).getSecond()
                + "&lon=" + coords.get(placeID).getFirst() + "&appid=" + apiKeys.get("weather");
        JsonObject jsonObject = PlacesUtils.getJson(reqStr);
        String temp = ((JsonObject) jsonObject.get("main")).get("temp").getAsString();
        String feelsLike = ((JsonObject) jsonObject.get("main")).get("feels_like").getAsString();
        String maxTemp = ((JsonObject) jsonObject.get("main")).get("temp_max").getAsString();
        String minTemp = ((JsonObject) jsonObject.get("main")).get("temp_min").getAsString();
        String speed = ((JsonObject) jsonObject.get("wind")).get("speed").getAsString();
        String pressure = ((JsonObject) jsonObject.get("main")).get("pressure").getAsString();

        return "Temp now: " + temp + "*C\n" +
                "Feels like: " + feelsLike + "*C\n" +
                "Max Temp: " + maxTemp + "*C\n" +
                "Min Temp: " + minTemp + "*C\n" +
                "Wind speed: " + speed + "m/s\n" +
                "Pressure: " + Double.parseDouble(pressure) * 0.7 + " mmHg";
    }


    public ArrayList<String> placesInArea(Integer placeID) throws IOException {
        ArrayList<String> placesAndDescription = new ArrayList<>();
        String api_key = apiKeys.get("placesArea");
        String url = " http://api.opentripmap.com/0.1/en/places/bbox?lon_min=" + coords.get(placeID).getFirst() +
                "&lat_min=" + coords.get(placeID).getSecond() + "&lon_max=" + (coords.get(placeID).getFirst()
                + 0.01) +
                "&lat_max=" + (coords.get(placeID).getSecond() + 0.01) + "&kinds=interesting_places&format=geojson&apikey="
                + api_key;

        JsonObject jsonObject = PlacesUtils.getJson(url);
        JsonArray jsonArray = jsonObject.getAsJsonArray("features");
        int places = Math.min(jsonArray.size(), MAX_PLACES_COUNT);
        for (int i = 0; i < places; i++) {
            JsonObject elementObj = (JsonObject) jsonArray.get(i);
            String name = ((JsonObject) elementObj.get("properties")).get("name").getAsString();
            String xid = ((JsonObject) elementObj.get("properties")).get("xid").getAsString();
            String description = placeDescription(xid);
            if (name.equals("") || description.equals("")) {
                continue;
            }
            placesAndDescription.add("Name: " + name + "\n" +
                    "Description: " + description + "\n" +
                    "---------------------------------------------------------------------------" + "\n"
            );
        }
        return placesAndDescription;
    }

    public String placeDescription(String xid) throws IOException {
        String api_key = apiKeys.get("placesArea");
        String url = "http://api.opentripmap.com/0.1/en/places/xid/" + xid + "?apikey=" + api_key;
        JsonObject jsonObject = PlacesUtils.getJson(url);
        String description;
        try {
            description = ((JsonObject) jsonObject.get("wikipedia_extracts")).get("text").toString();

        } catch (NullPointerException e) {
            description = "";
        }
        return description;
    }

    public ArrayList<String> getPlaces(String place) throws IOException {
        ArrayList<String> places = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("https://graphhopper.com/api/1/geocode?q=");
        stringBuilder.append(place);
        stringBuilder.append("&locale=en&limit=100&key=").append(apiKeys.get("location"));

        JsonObject jsonObject = PlacesUtils.getJson(stringBuilder.toString());
        JsonArray jsonArray = jsonObject.getAsJsonArray("hits");
        int count = 1;
        for (JsonElement element : jsonArray) {
            JsonObject elementObj = (JsonObject) element;
            places.add(count + ")" + " " + "Country: " + elementObj.get("country")
                    + ", City: " + elementObj.get("city")
                    + ", Name: " + elementObj.get("name")
                    + ", Osm value: " + elementObj.get("osm_value"));
            coords.put(count, new Pair<>(((JsonObject) elementObj.get("point")).get("lng").getAsDouble(),
                    ((JsonObject) elementObj.get("point")).get("lat").getAsDouble()));
            count += 1;
        }
        return places;
    }
}
