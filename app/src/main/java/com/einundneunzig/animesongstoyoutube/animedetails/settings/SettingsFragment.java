package com.einundneunzig.animesongstoyoutube.animedetails.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.einundneunzig.animesongstoyoutube.R;
import com.einundneunzig.animesongstoyoutube.youtube.YoutubeManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.api.client.googleapis.GoogleUtils;
import com.google.api.services.youtube.YouTubeScopes;
import com.google.api.services.youtube.model.Playlist;

import java.io.IOException;
import java.net.URL;


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

    private GoogleSignInClient mGoogleSignInClient;

    private static final int RC_SIGN_IN = 9001;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

        setPreferencesFromResource(R.xml.settings_hierarchy_preferences, rootKey);

        initGoogle();
        updateUI(GoogleSignIn.getLastSignedInAccount(getActivity().getApplicationContext()));

        findPreference(spinOffsPreferenceKey).setOnPreferenceChangeListener(this);
        findPreference(othersPreferenceKey).setOnPreferenceChangeListener(this);
        findPreference(allPreferenceKey).setOnPreferenceChangeListener(this);

        Preference log_in_preference = findPreference(logInPreferenceKey);
        log_in_preference.setOnPreferenceClickListener(this);

        findPreference(addToPlaylistPreferenceKey).setOnPreferenceClickListener(this);

        getSavedPlaylists();
    }

    @Override
    public void onResume() {
        super.onResume();
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener((v, keyCode, event) -> {
            if(event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                getActivity().finish();
                return true;
            }
            return false;
        });
    }

    private void initGoogle() {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestProfile()
                .requestScopes(new Scope(YouTubeScopes.YOUTUBE_FORCE_SSL))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);
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
                    signIn();
                }else{
                    signOut();
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

        getActivity().runOnUiThread(()->{
            settingsCategory.addPreference(playlistPreference);
        });

    }


    private void signIn() {

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN, null);
    }

    private void signOut(){
        mGoogleSignInClient.signOut();
        Toast.makeText(getContext(), "Erfolgreich ausgeloggt", Toast.LENGTH_LONG).show();
        updateUI(null);
    }

    private void updateUI(GoogleSignInAccount account) {
        Preference preference = findPreference(logInPreferenceKey);

        if(preference == null){
            Log.w("998", logInPreferenceKey + " does not exist. Therefore can not update log in information");
            return;
        }

        if(account!=null){
            YoutubeManager.setAccount(account, getContext());
            new Thread(()->{
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(new URL(account.getPhotoUrl().toString()).openStream());
                    getActivity().runOnUiThread(()->{
                        preference.setIcon(new BitmapDrawable(getResources(), bitmap));
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

            preference.setTitle(getString(R.string.logged_in_title) + " " +  account.getDisplayName());
            preference.setSummary(getString(R.string.logged_in_summary));
        }else{
            YoutubeManager.removeAccount();
            preference.setIcon(null);
            preference.setTitle(getString(R.string.log_in));
            preference.setSummary("");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }


    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.
            updateUI(account);

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("999", "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

}