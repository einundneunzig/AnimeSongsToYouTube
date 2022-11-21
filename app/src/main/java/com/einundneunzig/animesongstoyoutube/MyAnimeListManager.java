package com.einundneunzig.animesongstoyoutube;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

    public static List<Integer> getRelatedAnime(String animeId)throws IOException {
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("X-MAL-CLIENT-ID", "3f5dca7ffc3b2dbae618687b2778a04c");
        con.getInputStream();
        BufferedReader response = new BufferedReader(new InputStreamReader(con.getInputStream()));

        String line = response.readLine();
        System.out.println(line);
        return null;
    }
    public static HashMap<String, String> getThemes(String animeId) throws IOException {

        HashMap<String, String> themes = new HashMap<>();
        themes.putAll(getOpeningThemes(animeId));
        themes.putAll(getEndingThemes(animeId));

        return themes;
    }

    public static Map<String, String> getEndingThemes(String animeId) {
    }

    public static Map<String, String> getOpeningThemes(String animeId) throws IOException {


        HashMap<String, String> openingThemes = new HashMap<>();
        URL url = new URL("https://api.myanimelist.net/v2/anime/" + animeId + "?fields=opening_themes{text}");

        String response = getAPIResponse(url);

        int anfang = line.indexOf("\"text\":");
        int ende = 0;

        ArrayList<String> themes = new ArrayList<>();

        while (anfang != -1) {
            anfang += 8;                //remove "text":"
            anfang = line.indexOf("\"", anfang) + 1;

            ende = line.indexOf("}", anfang) - 1;
            themes.add(line.substring(anfang, ende));
            anfang = line.indexOf("\"text\":", ende);
        }

        for (String s : themes) {
            int i = s.indexOf("(");
            if(!(i<s.indexOf("\""))){
                i = s.indexOf("\"");
            }
            int end = s.indexOf("(", i + 1);
            if (end != -1) end--;
            else end = s.length();
            openingThemes.put(s.substring(0, i - 1).replaceAll("\\\\u....", ""), s.substring(s.indexOf("\"") + 5, end).replaceAll("\\\\u....", ""));
        }

        return openingThemes;
    }
}
