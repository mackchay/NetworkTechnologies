package org.example;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import kotlin.Pair;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Main {

    private static HashMap<String, String> api_keys;


    public static void main(String[] args) throws IOException {
        api_keys = new HashMap<>();
        System.out.println("Enter place: ");
        Scanner scanner = new Scanner(System.in);
        String place = scanner.nextLine();

        File file = new File(
                "api_keys"
        );

        BufferedReader br
                = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        String string_api;
        while ((string_api = br.readLine()) != null) {
            api_keys.put(string_api.split(" ")[0], string_api.split(" ")[1]);
        }
        OkHttpClient client = new OkHttpClient();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("https://graphhopper.com/api/1/geocode?q=");
        stringBuilder.append(place);
        stringBuilder.append("&locale=en&key=").append(api_keys.get("location"));
        Request request = new Request.Builder()
                .url(stringBuilder.toString())
                .get()
                .build();


        Response response = client.newCall(request).execute();
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(response.body().string(), JsonObject.class);
        JsonArray jsonArray = jsonObject.getAsJsonArray("hits");
        int count = 1;
        HashMap<Integer, Pair<Double, Double>> map = new HashMap<>();
        for (JsonElement element : jsonArray) {
            JsonObject elementObj = (JsonObject) element;
            System.out.println(count + ")" + " " + "Country: " + elementObj.get("country")
                    + ", City: " + elementObj.get("city")
                    + ", Name: " + elementObj.get("name")
                    + ", Osm value: " + elementObj.get("osm_value"));
            map.put(count, new Pair<>(((JsonObject) elementObj.get("point")).get("lng").getAsDouble(),
                    ((JsonObject) elementObj.get("point")).get("lat").getAsDouble()));
            count += 1;
        }

        System.out.println("Choose place from 1 to " + (count - 1) + ": ");
        int chosenPlace = scanner.nextInt();
        while (chosenPlace < 1 || chosenPlace > count - 1) {
            System.out.println("Error");
            chosenPlace = scanner.nextInt();
        }

        double lat = map.get(chosenPlace).getSecond();
        double lng = map.get(chosenPlace).getFirst();

        CompletableFuture<ArrayList<String>> placesAndDescriptionEvent = CompletableFuture.supplyAsync(
                () -> {
                    try {
                        return placesInArea(new Pair<>(lng, lat));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
        );
        CompletableFuture<String> weatherEvent = CompletableFuture.supplyAsync(
                () -> {
                    try {
                        return getWeather(new Pair<>(lng, lat));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
        );

        CompletableFuture<Object> combinedFuture = weatherEvent.thenCombine(placesAndDescriptionEvent,
                (weather, placesAndDescription) -> {
                    System.out.println("Weather: \n" + weather);
                    System.out.println("Places: \n" + placesAndDescription);
                    return null;
                });

        try {
            combinedFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getWeather(Pair<Double, Double> coords) throws IOException {
        OkHttpClient client = new OkHttpClient();
        String reqStr = "https://api.openweathermap.org/data/2.5/weather?lat=" + coords.getSecond() + "&lon=" + coords.getFirst() + "&appid=" + api_keys.get("weather");
        Request request = new Request.Builder()
                .url(reqStr)
                .get()
                .build();
        Response response = client.newCall(request).execute();

        Gson gson = new Gson();
        assert response.body() != null;
        JsonObject jsonObject = gson.fromJson(response.body().string(), JsonObject.class);
        String temp = ((JsonObject) jsonObject.get("main")).get("temp").getAsString();
        String feelsLike = ((JsonObject) jsonObject.get("main")).get("feels_like").getAsString();
        String maxTemp = ((JsonObject) jsonObject.get("main")).get("temp_max").getAsString();
        String minTemp = ((JsonObject) jsonObject.get("main")).get("temp_min").getAsString();
        String speed = ((JsonObject) jsonObject.get("wind")).get("speed").getAsString();

        return "Temp now: " + temp + "*C\n" +
                "Feels like: " + feelsLike + "*C\n" +
                "Max Temp: " + maxTemp + "*C\n" +
                "Min Temp: " + minTemp + "*C\n" +
                "Wind speed: " + speed + "m/s\n";
    }


    private static ArrayList<String> placesInArea(Pair<Double, Double> coords) throws IOException {
        ArrayList<String> placesAndDescription = new ArrayList<>();
        String api_key = api_keys.get("placesArea");
        OkHttpClient client = new OkHttpClient();
        String url = " http://api.opentripmap.com/0.1/en/places/bbox?lon_min=" + coords.getFirst() + "&lat_min=" + coords.getSecond() + "&lon_max=" + (coords.getFirst() + 0.01) + "&lat_max=" + (coords.getSecond() + 0.01) + "&kinds=interesting_places&format=geojson&apikey=" + api_key;
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        System.out.println(url);
        Response response = client.newCall(request).execute();
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(response.body().string(), JsonObject.class);
        JsonArray jsonArray = jsonObject.getAsJsonArray("features");
        for (JsonElement element : jsonArray) {
            JsonObject elementObj = (JsonObject) element;
            String name = ((JsonObject) elementObj.get("properties")).get("name").getAsString();
            String xid = ((JsonObject) elementObj.get("properties")).get("xid").getAsString();
            String kinds = ((JsonObject) elementObj.get("properties")).get("kinds").getAsString();
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

    private static String placeDescription(String xid) throws IOException {
        String api_key = api_keys.get("placesArea");
        OkHttpClient client = new OkHttpClient();
        String url = "http://api.opentripmap.com/0.1/en/places/xid/" + xid + "?apikey=" + api_key;
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        Response response = client.newCall(request).execute();
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(response.body().string(), JsonObject.class);
        String description;
        try {
            description = ((JsonObject) jsonObject.get("wikipedia_extracts")).get("text").toString();

        } catch (NullPointerException e) {
            description = "";
        }
        return description;
    }
}