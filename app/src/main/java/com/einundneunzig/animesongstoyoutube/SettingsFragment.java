package com.einundneunzig.animesongstoyoutube;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public class SettingsFragment extends PreferenceFragmentCompat{

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings_hierarchy_preferences, rootKey);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());

        if(account!=null){
            Preference log_in_preference = findPreference("log_in");
            log_in_preference.setTitle("Angemeldet als " + account.getDisplayName());
            log_in_preference.setSummary("Zum abmelden klicken.");
        }
    }
}