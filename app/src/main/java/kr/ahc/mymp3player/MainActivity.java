package kr.ahc.mymp3player;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;

import android.app.ActionBar;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private static final String MEDIA_PATH = Environment
            .getExternalStorageDirectory().getAbsolutePath().concat("/Music/");

    private ArrayList<SongItem> mAudioList;
    private MediaPlayer mPlayer;

    private boolean is_autoplay = false;
    private int currentPosition = 0;

    OnItemClickListener itemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1,
                                int position, long arg3) {

            Intent intent = new Intent(MainActivity.this,
                    MusicPlayActivity.class);
            intent.putExtra("song", mAudioList.get(position).getFilepath());
            intent.putExtra("song_title", mAudioList.get(position).getTitle());

            startActivity(intent);

        }
    };

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Audio player");

        //final ActionBar bar = getActionBar();

        ListView listview = (ListView) findViewById(R.id.listView);

        listview.setOnItemClickListener(itemClickListener);

        mAudioList = retrieveAudioFiles();
        MediaListAdapter adapter = new MediaListAdapter(this,
                R.layout.activity_main, mAudioList);
        listview.setAdapter(adapter);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        stopAudio();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case R.id.action_item1:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void playAudio(String filepath) throws IllegalArgumentException,
            IllegalStateException, IOException {
        mPlayer = new MediaPlayer();
        mPlayer.setDataSource(filepath);
        mPlayer.prepare();
        mPlayer.start();

        mPlayer.setOnCompletionListener(new OnCompletionListener() {

            public void onCompletion(MediaPlayer arg0) {
                if(is_autoplay)
                    nextSong();
            }

        });
    }

    private void stopAudio() {
        if (mPlayer != null) {
            mPlayer.reset();
            mPlayer.release();
            mPlayer = null;
        }
    }

    public void onClickPause(View v) {
        if (mPlayer != null) {
            if (mPlayer.isPlaying()) {
                mPlayer.pause();
                Toast.makeText(this, "Pause", Toast.LENGTH_SHORT).show();
            } else {
                mPlayer.start();
                Toast.makeText(this, "Resume", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onClickStop(View v) {
        stopAudio();
        Toast.makeText(this, "Stop", Toast.LENGTH_SHORT).show();
    }

    private void nextSong() {
        if (++currentPosition >= mAudioList.size()) {
            currentPosition = 0;
        } else {
            try {
                playAudio(MEDIA_PATH + mAudioList.get(currentPosition));
            } catch (IllegalArgumentException | IllegalStateException
                    | IOException e) {
                e.printStackTrace();
            }
        }
    }

    void playMusicItem(int position) {
        stopAudio();
        String filepath = mAudioList.get(position).getFilepath();

        try {
            playAudio(filepath);
        } catch (IllegalArgumentException | IllegalStateException
                | IOException e) {
            e.printStackTrace();
        }

    }


  /*  public ArrayList<FileItem> updateSongList() {
        ArrayList<FileItem> items = new ArrayList<FileItem>();
        File home = new File(MEDIA_PATH);
        File[] fileLists = home.listFiles(new Mp3Filter());
        for (int i = 0; i < fileLists.length; i++) {
            items.add(new FileItem(String.valueOf(i), fileLists[i].getName()));
        }
        return items;
    } */


    public class SongItem {
        private String name;
        private String title;
        private String filepath;

        public SongItem(String name, String title, String filepath) {
            this.name = name;
            this.title = title;
            this.filepath = filepath;
        }

        public String getDisplayName() {
            return name;
        }

        public String getFilepath() {
            return filepath;
        }

        public String getTitle() {
            return title;
        }
    }


    private ArrayList<SongItem> retrieveAudioFiles() {
        ArrayList<SongItem> items = new ArrayList<SongItem>();

        String[] projection = { MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST};

        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        Cursor cursor = getContentResolver().query( musicUri,
                projection,
                null,
                null,
                null);

        int indexName = cursor
                .getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME);
        int indexData = cursor
                .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        int indexTitle = cursor
                .getColumnIndex(MediaStore.Audio.Media.TITLE);

        if (cursor.moveToFirst()) {
            do {
                items.add(new SongItem(cursor.getString(indexName),
                        cursor.getString(indexTitle),
                        cursor.getString(indexData)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return items;
    }

    class Mp3Filter implements FilenameFilter { // 내가 원하는 파일 확장자만 검색
        public boolean accept(File dir, String name) {
            return (name.endsWith(".mp3"));
        }
    }



    public class MediaListAdapter extends ArrayAdapter<SongItem> {
        private LayoutInflater mInflater;
        private int layout;
        private ArrayList<SongItem> data;

        public MediaListAdapter(Context context, int layout, ArrayList<SongItem> data) {
            super(context, layout, data);
            mInflater = LayoutInflater.from(context);
            this.layout = layout;
            this.data = data;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View item = null;
            if(convertView == null){
                LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
                item = inflater.inflate(R.layout.activity_listview, null);
            } else {
                item = convertView;
            }
            ImageView img = (ImageView)item.findViewById(R.id.imageView);
            TextView vName = (TextView)item.findViewById(R.id.textView);
            TextView vTime = (TextView)item.findViewById(R.id.textView2);
            TextView vDesc = (TextView)item.findViewById(R.id.textView3);

            SongItem song = getItem(position);

//            img.setImageResource( song.);

            vTime.setText( song.getFilepath());
            vName.setText( song.getTitle());
            vDesc.setText( song.getDisplayName());
            return item;
        }
    }

}
