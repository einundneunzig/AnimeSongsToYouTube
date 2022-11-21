package com.einundneunzig.animesongstoyoutube;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

        List<Integer> relatedAnime = getRelatedAnimeRec(animeId, new ArrayList<>());
        return relatedAnime;
    }

    private static List<Integer> getRelatedAnimeRec(int animeId, List<Integer> relatedAnime)throws IOException{

        int size = relatedAnime.size();

        URL url = new URL("https://api.myanimelist.net/v2/anime/" + animeId + "?fields=related_anime");
        String response = getAPIResponse(url);

        int position = response.indexOf("\"id\":", 6);  //Skip first id (Id of current anime)

        while(position!=-1){
            int id2 = Integer.parseInt(response.substring(position+5, response.indexOf(",", position)));
            if(!relatedAnime.contains(id2)){
                relatedAnime.add(id2);
            }
            position = response.indexOf("\"id\":", position+1);
        }

        for (int i = size; i<relatedAnime.size(); i++) {
            relatedAnime.addAll(getRelatedAnimeRec(relatedAnime.get(i), relatedAnime));
        }

        return relatedAnime;
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
