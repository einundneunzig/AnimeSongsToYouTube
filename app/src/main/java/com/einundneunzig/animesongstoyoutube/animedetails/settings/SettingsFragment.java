package com.einundneunzig.animesongstoyoutube.animedetails.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.einundneunzig.animesongstoyoutube.R;
import com.einundneunzig.animesongstoyoutube.youtube.YoutubeManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.api.services.youtube.model.Playlist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {

    private static final String title = "title";
    private static final String summary = "summary";
    private static final String playlistKey = "playlist_key";

    public final static String logInPreferenceKey = "log_in";
    public final static String spinOffsPreferenceKey = "spin_offs";
    public final static String allPreferenceKey = "all";
    public final static String othersPreferenceKey = "others";
    public final static String addToPlaylistPreferenceKey = "add_to_playlist";
    public final static String youtubeSettingsCategoryKey = "youtube_settings";

    GoogleSignInAccount account;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

        setPreferencesFromResource(R.xml.settings_hierarchy_preferences, rootKey);
        account = GoogleSignIn.getLastSignedInAccount(getActivity().getApplicationContext());


        findPreference(spinOffsPreferenceKey).setOnPreferenceChangeListener(this);
        findPreference(othersPreferenceKey).setOnPreferenceChangeListener(this);
        findPreference(allPreferenceKey).setOnPreferenceChangeListener(this);

        Preference log_in_preference = findPreference(logInPreferenceKey);
        log_in_preference.setOnPreferenceClickListener(this);

        if(account!=null){
            log_in_preference.setTitle(getString(R.string.logged_in_title) + " " +account.getDisplayName());
            log_in_preference.setSummary(getString(R.string.logged_in_summary));
        }

        findPreference(addToPlaylistPreferenceKey).setOnPreferenceClickListener(this);

        getSavedPlaylists();

    }

    private void getSavedPlaylists() {

        SharedPreferences sharedPreferences = getPreferenceManager().getSharedPreferences();

        PreferenceCategory category = findPreference(youtubeSettingsCategoryKey);
        int i = 0;

        String playlistTitle = sharedPreferences.getString(playlistKey + i + title, null);

        while (playlistTitle != null) {
            Preference playlistPreference = new Preference(getContext());
            playlistPreference.setTitle(playlistTitle);
            playlistPreference.setSummary(sharedPreferences.getString(playlistKey + i + summary, "Fehler"));
            playlistPreference.setOnPreferenceClickListener(this);
            playlistPreference.setKey(playlistKey + i);
            category.addPreference(playlistPreference);

            playlistTitle = sharedPreferences.getString(playlistKey + ++i + title, null);
        }
    }

    private void removePlaylistPreference(Preference preference) {

        int index = Integer.parseInt(preference.getKey().substring(playlistKey.length()));
        getActivity().runOnUiThread(()->{
            ((PreferenceCategory)findPreference(youtubeSettingsCategoryKey)).removePreference(preference);
        });
        SharedPreferences.Editor editor = getPreferenceManager().getSharedPreferences().edit();
        editor.remove(preference.getKey() + title);
        editor.remove(preference.getKey() + summary);


        //update all indexes from playlist-preferences with higher index to index-1
        Preference preferenceToRename = findPreference(playlistKey + (index + 1));

        while(preferenceToRename!=null){
            editor.remove(preferenceToRename.getKey()+title);
            editor.remove(preferenceToRename.getKey()+summary);
            preferenceToRename.setKey(playlistKey + index);
            editor.putString(preferenceToRename.getKey()+title, (String) preferenceToRename.getTitle());
            editor.putString(preferenceToRename.getKey()+summary, (String) preferenceToRename.getSummary());

            preferenceToRename = findPreference(playlistKey + (++index + 1));
        }

        editor.apply();

    }

    @Override
    public void onPause() {
        super.onPause();

        SharedPreferences.Editor editor = getPreferenceManager().getSharedPreferences().edit();

        Preference addToPlaylistPreference = findPreference(addToPlaylistPreferenceKey);
        PreferenceCategory category = findPreference(youtubeSettingsCategoryKey);

        int index = category.getPreferenceCount() - 1;
        Preference currentPreference = category.getPreference(index);

        while (!currentPreference.equals(addToPlaylistPreference)) {

            editor.putString(currentPreference.getKey() + title, (String) currentPreference.getTitle());
            editor.putString(currentPreference.getKey() + summary, (String) currentPreference.getSummary());

            currentPreference = category.getPreference(--index);
        }

        editor.apply();
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {

        switch(preference.getKey()){
            case logInPreferenceKey:
                if(preference.getTitle().equals("Anmelden")){
                    preference.setTitle(getString(R.string.logged_in_title) + " " +  "Test");
                    preference.setSummary(getString(R.string.logged_in_summary));
                }else{
                    preference.setTitle(getString(R.string.log_in));
                    preference.setSummary("");
                }
                return true;
            case addToPlaylistPreferenceKey:
                if(YoutubeManager.getAccount()!=null){
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(((ViewGroup)getView().getParent()).getId(), new AddToPlaylistFragment())
                            .addToBackStack(null)
                            .commit();
                    return true;
                }

            default:
                break;
        }
        if(preference.getKey().contains(playlistKey)){

            new MaterialAlertDialogBuilder(getContext(), com.google.android.material.R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog_Centered)
                    .setTitle(getString(R.string.info))
                    .setMessage(getString(R.string.removePlaylistInfo))
                    .setNegativeButton(getString(R.string.no), (dialog, which) -> {
                    })
                    .setPositiveButton(getString(R.string.yes), (dialog, which)->{
                        removePlaylistPreference(preference);
                    })
                    .show();
            return true;
        }

        return false;
    }


    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        final Playlist[] p = new Playlist[1];

        switch (preference.getKey()){
            case othersPreferenceKey:
            case allPreferenceKey:
            case spinOffsPreferenceKey:
                if((boolean)newValue){
                    new MaterialAlertDialogBuilder(getContext(), com.google.android.material.R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog_Centered)
                            .setTitle(getString(R.string.warning))
                            .setMessage(getString(R.string.crossover_warning))
                            .setNegativeButton(getString(R.string.abort), (dialog, which) -> {
                            })
                            .setPositiveButton(getString(R.string.activate_nonetheless), (dialog, which)->{
                                ((SwitchPreferenceCompat)preference).setChecked(true);
                            })
                            .show();

                    return false;
                }
            default:
                return true;
        }

    }

    public void addPlaylistToPreference(Playlist playlist){
        Preference playlistPreference = new Preference(getContext());
        String title = playlist.getSnippet().getTitle();
        String summary = playlist.getId();
        playlistPreference.setTitle(title);
        playlistPreference.setSummary(summary);

        PreferenceCategory settingsCategory = findPreference(youtubeSettingsCategoryKey);
        Preference addToPlaylistPreference = findPreference(addToPlaylistPreferenceKey);

        int index = settingsCategory.getPreferenceCount()-1;
        while(!settingsCategory.getPreference(index).equals(addToPlaylistPreference)){
            index--;
        }
        int playlistIndex = settingsCategory.getPreferenceCount()-index-1;      //Anzahl der Kinder - Position der AddToPlaylist Preference -1 = Anzahl der Playlists -> Index der neuen Playlist

        playlistPreference.setKey(playlistKey + playlistIndex);
        playlistPreference.setOnPreferenceClickListener(this);

        System.out.println("PlaylistIndex " + playlistIndex);
        getActivity().runOnUiThread(()->{
            settingsCategory.addPreference(playlistPreference);
        });


    }

}