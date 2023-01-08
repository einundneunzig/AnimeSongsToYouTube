package com.einundneunzig.animesongstoyoutube.animedetails.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.einundneunzig.animesongstoyoutube.R;
import com.einundneunzig.animesongstoyoutube.animedetails.settings.SettingsFragment;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.settings_container, new SettingsFragment(), "SettingsFragment")
                .addToBackStack("SettingsFragment")
                .commit();

    }
}
