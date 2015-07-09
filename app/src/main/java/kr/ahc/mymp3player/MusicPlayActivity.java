package kr.ahc.mymp3player;


import java.io.IOException;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.TextView;

public class MusicPlayActivity extends Activity {
    private static final String MEDIA_PATH = Environment
            .getExternalStorageDirectory().getAbsolutePath().concat("/Music/");

    private int currentPosition = 0;
    private MediaPlayer mp = new MediaPlayer();
    private final static String TAG = "MusicPlayer";

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        String song = getIntent().getStringExtra("song");
        String song_title = getIntent().getStringExtra("song_title");
//        String song_name = getIntent().getStringExtra("song_name");
        Log.d(TAG, "Song ===> " + song);
        playSong(song);

// name = (TextView)findViewById(R.id.textView2);
        TextView title = (TextView)findViewById(R.id.textView);
        title.setText(song_title);
//        name.setText(song_name);
        //title.setText(song.substring( song.lastIndexOf("/") ));

        //
//        final ActionBar bar = getActionBar();
//        if(bar != null) {
//            bar.setDisplayHomeAsUpEnabled(true);
//            // bar.setNavigationMode(ActionBar.DISPLAY_HOME_AS_UP);
//        }

    }

    @Override
    public void onDestroy() {
        stopAudio();
        super.onDestroy();
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {

        final int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                break;
        }

        return super.onMenuItemSelected(featureId, item);
    }

    private void playSong(String songPath) {
        try {

            mp.reset();
            mp.setDataSource(songPath);
            mp.prepare();
            mp.start();

            // Setup listener so next song starts automatically
            mp.setOnCompletionListener(new OnCompletionListener() {

                public void onCompletion(MediaPlayer arg0) {
                    // nextSong();
                }

            });

        } catch (IOException e) {
            Log.v(getString(R.string.app_name), e.getMessage());
        }
    }

    //
    // private void nextSong() {
    // if (++currentPosition >= songs.size()) {
    // // Last song, just reset currentPosition
    // currentPosition = 0;
    // } else {
    // // Play next song
    // playSong(MEDIA_PATH + songs.get(currentPosition));
    // }
    // }

    protected void stopAudio() {
        if (mp != null) {
            mp.stop();
            mp.release();
        }
    }
}
