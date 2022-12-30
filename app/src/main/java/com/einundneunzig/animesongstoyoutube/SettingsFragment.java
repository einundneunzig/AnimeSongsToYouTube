package com.einundneunzig.animesongstoyoutube;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

public class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {

    public final static String logInPreferenceKey = "log_in";
    public final static String spinOffsPreferenceKey = "spin_offs";
    public final static String allPreferenceKey = "all";
    public final static String othersPreferenceKey = "others";

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings_hierarchy_preferences, rootKey);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());

        Preference log_in_preference = findPreference(logInPreferenceKey);
        log_in_preference.setOnPreferenceClickListener(this);

        findPreference(spinOffsPreferenceKey).setOnPreferenceChangeListener(this);
        findPreference(othersPreferenceKey).setOnPreferenceChangeListener(this);
        findPreference(allPreferenceKey).setOnPreferenceChangeListener(this);

        if(account!=null){
            log_in_preference.setTitle("Angemeldet als " + account.getDisplayName());
            log_in_preference.setSummary("Zum abmelden klicken.");
        }
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
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
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

                }
            default:
                break;
        }

        return false;
    }






}