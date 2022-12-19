package com.einundneunzig.animesongstoyoutube;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.gms.auth.api.signin.GoogleSignIn;

public class SettingsActivity extends AppCompatActivity implements Preference.OnPreferenceClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.settings_container, new SettingsFragment())
                .commit();

    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        System.out.println("TEst");
        switch(preference.getKey()){
            case "log_in":
                System.out.println("TEst2");
                if(preference.getTitle().equals("Anmelden")){
                    preference.setTitle(getString(R.string.logged_in_title) + " " +  "Test");
                    preference.setSummary(getString(R.string.logged_in_summary));
                }else{
                    preference.setTitle(getString(R.string.log_in));
                    preference.setSummary("");
                }
                break;
            default:
                break;
        }
        return false;
    }
}
