package com.einundneunzig.animesongstoyoutube;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.api.services.youtube.model.Playlist;


public class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {

    public final static String logInPreferenceKey = "log_in";
    public final static String spinOffsPreferenceKey = "spin_offs";
    public final static String allPreferenceKey = "all";
    public final static String othersPreferenceKey = "others";
    public final static String playlistNamePreferenceKey = "add_to_playlist_name";
    public final static String playlistIdPreferenceKey = "add_to_playlist_id";
    public final static String addToPlaylistPreferenceKey = "add_to_playlist";

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
                            .replace(((ViewGroup)getView().getParent()).getId(), new AddToPlaylistFragment(), "calledFromSettingsFragment")
                            .addToBackStack(null)
                            .commit();
                    return true;
                }
            default:
                break;
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
                /*
            case playlistNamePreferenceKey:
                EditTextPreference playlistIdPreference = ((EditTextPreference)findPreference(playlistIdPreferenceKey));

                    YoutubeManager.setAccount(account, getContext());
                    Thread t = new Thread(()->{
                        p[0] = YoutubeManager.findPlaylistByName((String) newValue);
                        String playlistId = "";
                        if(p[0] !=null){
                            playlistId = p[0].getId();
                        }else{
                            System.out.println("p is null");
                            //Toast.makeText(getContext(), getString(R.string.playlist_name_invalid), Toast.LENGTH_LONG).show();
                            //return false;
                        }
                        String finalPlaylistId = playlistId;
                        this.getActivity().runOnUiThread(()->{
                            playlistIdPreference.setText(finalPlaylistId);
                        });
                    });
                    t.start();

                    return true;


            case playlistIdPreferenceKey:

                EditTextPreference playlistNamePreference = ((EditTextPreference)findPreference(playlistNamePreferenceKey));
                if(playlistNamePreference.getText()== "") {
                    String playlistName = "";

                    p[0] = YoutubeManager.findPlaylistById((String) newValue);
                    if (p[0] != null) {
                        playlistName = p[0].getSnippet().getTitle();
                    } else {
                        Toast.makeText(getContext(), getString(R.string.playlist_id_invalid), Toast.LENGTH_LONG).show();
                        return false;
                    }

                    ((EditTextPreference) findPreference(playlistNamePreferenceKey)).setText(playlistName);
                    return true;
                }else{
                    return false;
                }
                */
            default:
                return true;
        }

    }






}