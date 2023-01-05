package com.einundneunzig.animesongstoyoutube.myanimelist;

import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
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

    public static Set<Theme> getThemes(RelatedAnime[] relatedAnimeArray, RelationType type) {
        Set<Theme> sequelThemes = new HashSet<>();

        for(RelatedAnime relatedAnime: relatedAnimeArray){
            Log.d("relatedAnime", relatedAnime.getNode().getTitle());

            if(relatedAnime.getRelationType().equalsIgnoreCase(type.name())){
                relatedAnime.getNode().loadDetails();
                Theme[] opThemes = relatedAnime.getNode().getOpeningThemes();
                if(opThemes!=null){
                    sequelThemes.addAll(Arrays.asList(opThemes));
                }
                Theme[] edThemes = relatedAnime.getNode().getEndingThemes();
                if(edThemes!=null){
                    sequelThemes.addAll(Arrays.asList(edThemes));
                }

                sequelThemes.addAll(getThemes(relatedAnime.getNode().getRelatedAnime(), type));
            }
        }

        return sequelThemes;
    }
}
