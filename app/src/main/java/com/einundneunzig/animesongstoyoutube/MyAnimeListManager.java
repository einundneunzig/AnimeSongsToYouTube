package com.einundneunzig.animesongstoyoutube;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class MyAnimeListManager {


    private static String getAPIResponse(URL url) throws IOException {

        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("X-MAL-CLIENT-ID", "3f5dca7ffc3b2dbae618687b2778a04c");
        con.getInputStream();
        BufferedReader response = new BufferedReader(new InputStreamReader(con.getInputStream()));

        return response.readLine();
    }

    public static List<Integer> getRelatedAnime(int animeId)throws IOException {
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("X-MAL-CLIENT-ID", "3f5dca7ffc3b2dbae618687b2778a04c");
        con.getInputStream();
        BufferedReader response = new BufferedReader(new InputStreamReader(con.getInputStream()));

        String line = response.readLine();
        System.out.println(line);
        return null;
    }
    public static HashMap<String, String> getThemes(int animeId) throws IOException {

        HashMap<String, String> themes = new HashMap<>();
        themes.putAll(getOpeningThemes(animeId));
        themes.putAll(getEndingThemes(animeId));

        return themes;
    }

    public static Map<String, String> getEndingThemes(int animeId) throws IOException{

        URL url = new URL("https://api.myanimelist.net/v2/anime/" + animeId + "?fields=ending_themes{text}");

        String response = getAPIResponse(url);

        HashMap<String, String> endingThemes = filterThemesFromResponse(response);

        return endingThemes;
    }

    public static Map<String, String> getOpeningThemes(int animeId) throws IOException {

        URL url = new URL("https://api.myanimelist.net/v2/anime/" + animeId + "?fields=opening_themes{text}");

        String response = getAPIResponse(url);

        HashMap<String, String> openingThemes = filterThemesFromResponse(response);

        return openingThemes;
    }

    private static HashMap<String, String> filterThemesFromResponse(String response) {

        HashMap<String, String> themes = new HashMap<>();

        int anfang = response.indexOf("\"text\":");
        int ende = 0;

        ArrayList<String> themesText = new ArrayList<>();
        while (anfang != -1) {
            anfang += 8;                //remove "text":"
            anfang = response.indexOf("\"", anfang) + 1;

            ende = response.indexOf("}", anfang) - 1;
            themesText.add(response.substring(anfang, ende));
            anfang = response.indexOf("\"text\":", ende);
        }

        for (String s : themesText) {
            int i = s.indexOf("(");
            if(!(i<s.indexOf("\""))){
                i = s.indexOf("\"");
            }
            int end = s.indexOf("(", i + 1);
            if (end != -1) end--;
            else end = s.length();
            themes.put(s.substring(0, i - 1).replaceAll("\\\\u....", ""), s.substring(s.indexOf("\"") + 5, end).replaceAll("\\\\u....", ""));
        }

        return themes;
    }
}
