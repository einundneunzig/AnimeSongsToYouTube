package com.einundneunzig.animesongstoyoutube;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.api.services.youtube.model.Playlist;

import java.io.IOException;
import java.net.URL;
import java.util.List;


public class AddToPlaylistFragment extends Fragment implements TextView.OnEditorActionListener, View.OnClickListener{

    private LinearLayout search_playlist_results;

    public AddToPlaylistFragment(){
        super(R.layout.add_to_playlist_layout);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EditText textField = getActivity().findViewById(R.id.search_playlist);
        textField.setOnEditorActionListener(this);
        search_playlist_results = getActivity().findViewById(R.id.search_playlist_results);
    }

    private void searchPlaylists(String search) {

        new Thread(()->{
            updatePlaylistResults(YoutubeManager.findPlaylistsByName(search));
        }).start();

    }

    private void updatePlaylistResults(List<Playlist> playlistList) {
        getActivity().runOnUiThread(()->{
            search_playlist_results.removeAllViews();
        });

        //Creating Horizontal LinearLayout for each Playlist, giving it a Tag with it's Id and adding it to search_playlist_results
        for(Playlist p: playlistList){
            LinearLayout nodeLayout = new LinearLayout(getActivity().getApplicationContext());
            nodeLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT));
            nodeLayout.setOrientation(LinearLayout.HORIZONTAL);

            ImageView image = new ImageView(getContext());
            image.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT, 3));
            image.setImageBitmap(getBitmap(p.getSnippet().getThumbnails().getMedium().getUrl()));

            TextView text = new TextView(getContext());
            text.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT, 1));
            text.setText(p.getSnippet().getTitle());

            nodeLayout.addView(image);
            nodeLayout.addView(text);

            nodeLayout.setTag(p.getId());
            nodeLayout.setOnClickListener(this);
            getActivity().runOnUiThread(()->{
                search_playlist_results.addView(nodeLayout);
            });
        }
    }

    private Bitmap getBitmap(String pictureUrl){
        final Bitmap[] bitmap = new Bitmap[1];
        Thread t = new Thread(() -> {
            try {
                bitmap[0] = BitmapFactory.decodeStream(new URL(pictureUrl).openStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return bitmap[0];
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if(actionId == EditorInfo.IME_ACTION_SEARCH){
            searchPlaylists(v.getText().toString());
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        if(view instanceof LinearLayout){
            LinearLayout playlistLayout = (LinearLayout) view;
            if(((LinearLayout) view).getOrientation() == LinearLayout.HORIZONTAL){
                String playlistId = (String) playlistLayout.getTag();
                getActivity().getSupportFragmentManager().popBackStack();
            }
        }
    }
}
