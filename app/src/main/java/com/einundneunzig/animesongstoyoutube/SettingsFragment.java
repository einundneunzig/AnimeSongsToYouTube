package com.einundneunzig.animesongstoyoutube;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.api.services.youtube.model.Playlist;

import java.util.Arrays;
import java.util.HashSet;
import java.util.UUID;


public class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {

    public final static String logInPreferenceKey = "log_in";
    public final static String spinOffsPreferenceKey = "spin_offs";
    public final static String allPreferenceKey = "all";
    public final static String othersPreferenceKey = "others";
    public final static String addToPlaylistPreferenceKey = "add_to_playlist";
    public final static String addedPlaylistKey = "addedPlaylist";
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
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {

        System.out.println("Test " + preference.getKey());

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
        if(preference.getKey().contains(addedPlaylistKey)){

            new MaterialAlertDialogBuilder(getContext(), com.google.android.material.R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog_Centered)
                    .setTitle(getString(R.string.info))
                    .setMessage(getString(R.string.removePlaylist))
                    .setNegativeButton(getString(R.string.no), (dialog, which) -> {
                    })
                    .setPositiveButton(getString(R.string.yes), (dialog, which)->{
                        getActivity().runOnUiThread(()->{
                            ((PreferenceCategory)findPreference(youtubeSettingsCategoryKey)).removePreference(preference);
                        });
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
        playlistPreference.setKey(addedPlaylistKey);
        playlistPreference.setOnPreferenceClickListener(this);

        PreferenceCategory settingsCategory = findPreference(youtubeSettingsCategoryKey);


        getActivity().runOnUiThread(()->{
            settingsCategory.addPreference(playlistPreference);
        });


    }

}