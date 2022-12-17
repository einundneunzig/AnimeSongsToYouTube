package com.einundneunzig.animesongstoyoutube;

import android.util.Log;

import androidx.annotation.NonNull;

import com.einundneunzig.animesongstoyoutube.myanimelist.AnimeList;
import com.einundneunzig.animesongstoyoutube.myanimelist.Node;
import com.einundneunzig.animesongstoyoutube.myanimelist.RelatedAnime;
import com.einundneunzig.animesongstoyoutube.myanimelist.Theme;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class MyAnimeListManager {


    public static String getAPIResponse(URL url) throws IOException {

        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("X-MAL-CLIENT-ID", "3f5dca7ffc3b2dbae618687b2778a04c");
        con.getInputStream();
        BufferedReader response = new BufferedReader(new InputStreamReader(con.getInputStream()));

        return response.readLine().replace("\\/", "/");
    }

    public static List<Integer> getRelatedAnime(int animeId)throws IOException {

        List<Integer> relatedAnime = new ArrayList<>();
        relatedAnime.add(animeId);
        getRelatedAnimeRec(animeId, relatedAnime);
        return relatedAnime;
    }

    private static void getRelatedAnimeRec(int animeId, List<Integer> relatedAnime)throws IOException{

        int size = relatedAnime.size();
        System.out.println(size);

        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.findAndRegisterModules();
        URL url = new URL("https://api.myanimelist.net/v2/anime/" + animeId + "?fields=related_anime");
        mapper.readValue(url, RelatedAnime.class);

        String response = getAPIResponse(url);

        int position = response.indexOf("\"id\":", 6);  //Skip first id (Id of current anime)

        while(position!=-1){
            int id = Integer.parseInt(response.substring(position+5, response.indexOf(",", position)));
            if(!relatedAnime.contains(id)){
                relatedAnime.add(id);
            }
            position = response.indexOf("\"id\":", position+1);
        }

        for (int i = size; i<relatedAnime.size(); i++) {
            System.out.println(i);
            getRelatedAnimeRec(relatedAnime.get(i), relatedAnime);
        }

    }

    public static HashMap<String, String> getThemes(int animeId) throws IOException {

        HashMap<String, String> themes = new HashMap<>();
        themes.putAll(getOpeningThemes(animeId));
        themes.putAll(getEndingThemes(animeId));

        return themes;
    }

    public static Map<String, String> getEndingThemes(int animeId) throws IOException{

        URL url = new URL("https://api.myanimelist.net/v2/anime/" + animeId + "?fields=ending_themes");

        String response = getAPIResponse(url);

        HashMap<String, String> endingThemes = filterThemesFromResponse(response);

        return endingThemes;
    }

    public static Map<String, String> getOpeningThemes(int animeId) throws IOException {

        URL url = new URL("https://api.myanimelist.net/v2/anime/" + animeId + "?fields=opening_themes");

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
            if((!(i<s.indexOf("\""))) || i==-1){
                i = s.indexOf("\"");
            }
            int end = s.indexOf("(", i + 1);
            if (end != -1) end--;
            else end = s.length();
            themes.put(s.substring(0, i - 1).replaceAll("\\\\u....", ""), s.substring(s.indexOf("\"") + 5, end).replaceAll("\\\\u....", ""));
        }

        return themes;
    }

    public static Node getAnimeDetails(int animeId) {

        final Node[] node = {null};

        Thread t = new Thread(() -> {

            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            mapper.findAndRegisterModules();

            try {
                URL url = new URL("https://api.myanimelist.net/v2/anime/" + animeId + "?fields=related_anime,opening_themes,ending_themes");
                node[0] = mapper.readValue(MyAnimeListManager.getAPIResponse(url), Node.class);

            } catch (IOException e) { e.printStackTrace(); }
        });

        t.start();

        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return node[0];
    }

    public static Set<Theme> getSequelThemes(RelatedAnime[] relatedAnimeArray) {
        Set<Theme> sequelThemes = new HashSet<>();

        for(RelatedAnime relatedAnime: relatedAnimeArray){
            Log.d("relatedAnime", relatedAnime.getNode().getTitle());

            if(relatedAnime.getRelationType().equalsIgnoreCase("sequel")){
                relatedAnime.getNode().loadDetails();
                Theme[] opThemes = relatedAnime.getNode().getOpeningThemes();
                if(opThemes!=null){
                    sequelThemes.addAll(Arrays.asList(opThemes));
                }
                Theme[] edThemes = relatedAnime.getNode().getEndingThemes();
                if(edThemes!=null){
                    sequelThemes.addAll(Arrays.asList(edThemes));
                }

                sequelThemes.addAll(getSequelThemes(relatedAnime.getNode().getRelatedAnime()));
            }
        }

        return sequelThemes;
    }
}
