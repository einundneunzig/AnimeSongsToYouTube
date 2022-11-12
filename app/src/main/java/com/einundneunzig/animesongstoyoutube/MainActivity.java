package com.einundneunzig.animesongstoyoutube;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeScopes;
import com.google.api.services.youtube.model.Playlist;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistListResponse;
import com.google.api.services.youtube.model.PlaylistSnippet;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private GoogleSignInClient mGoogleSignInClient;
    private final int RC_SIGN_IN = 1001;
    private GoogleSignInAccount account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestProfile()
                .requestScopes(new Scope(YouTubeScopes.YOUTUBE_FORCE_SSL))
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setOnClickListener(this);

        account = GoogleSignIn.getLastSignedInAccount(this);

        updateUI(account);

        findViewById(R.id.buttonSignOut).setOnClickListener(this);
    }

    private void updateUI(GoogleSignInAccount account) {
        if(account!=null){
            ((TextView)findViewById(R.id.textViewLogIn)).setText("Logged in as: " + account.getDisplayName());
            findViewById(R.id.buttonSignOut).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_in_button).setVisibility(View.INVISIBLE);
            ImageView profilePicture = findViewById(R.id.profilePicture);
            if(account.getPhotoUrl()!=null) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            Bitmap bitmap = (BitmapFactory.decodeStream(new URL(account.getPhotoUrl().toString()).openStream()));
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    profilePicture.setImageBitmap(bitmap);
                                }
                            });
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        }else{
            ((TextView)findViewById(R.id.textViewLogIn)).setText("Not logged in");
            findViewById(R.id.buttonSignOut).setVisibility(View.INVISIBLE);
            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            ImageView profilePicture = findViewById(R.id.profilePicture);
            profilePicture.setImageDrawable(Drawable.createFromPath("@android:mipmap/sym_def_app_icon"));
        }

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.sign_in_button:
                signIn();
                break;
            case R.id.buttonSignOut:
                signOut();
                break;
            default:
                break;
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut(){
        mGoogleSignInClient.signOut();
        account = null;
        Toast.makeText(getApplicationContext(), "Erfolgreich ausgeloggt", Toast.LENGTH_LONG).show();
        updateUI(account);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void callYoutubeAPI() {
        Playlist p = new Playlist();
        PlaylistSnippet pSnippet = new PlaylistSnippet();
        GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(this, Arrays.asList(YouTubeScopes.YOUTUBE_FORCE_SSL));
        credential.setSelectedAccount(account.getAccount());

        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        YouTube mService = new com.google.api.services.youtube.YouTube.Builder(
                transport, jsonFactory, credential)
                .setApplicationName("YouTube Data API Android Quickstart")
                .build();
        TextView textViewResult = findViewById(R.id.textViewResult);
        textViewResult.setText("Playlist:");
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            PlaylistListResponse response = mService.playlists().list("snippet").setMine(true).execute();

                            for(Playlist p: response.getItems()){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        textViewResult.setText(textViewResult.getText()+ "\n" + p.getSnippet().getTitle());
                                    }
                                });
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();



    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.
            updateUI(account);
            callYoutubeAPI();

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("999", "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }


}