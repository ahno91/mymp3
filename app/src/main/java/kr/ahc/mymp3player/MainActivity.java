package kr.ahc.mymp3player;

import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {


    private static final String MEDIA_PATH = Environment
            .getExternalStorageDirectory().getAbsolutePath().concat("/Music/");

    private ArrayList<SongItem> mAudioList;
    private MediaPlayer mPlayer;

    private boolean is_autoplay = false;
    private int currentPosition = 0;

    private String test = "";
    private String test2 = "";

    /*AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(MainActivity.this, MusicPlayActivity.class);
            intent.putExtra("song", mAudioList.get(position).getFilepath());
            intent.putExtra("song_title", mAudioList.get(position).getTitle());

            startActivity(intent);
        }
    }; */

    AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(MainActivity.this, MusicPlayActivity.class);
            intent.putExtra("song", mAudioList.get(position).getFilepath());
            intent.putExtra("song_title", mAudioList.get(position).getTitle());

            startActivity(intent);
        }
    }

    class SongItem {
        //int image;
        String name;
        String title;
        String data;


        public SongItem(String x1, String x2, String x3) {
            //this.image = img;
            this.name = x1;
            this.title = x2;
            this.data = x3;

        }
    }

    public ArrayList<SongItem> retrieveData() {
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

        int indexName = cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME);
        int indexData = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
        int indexTitle = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);

        if(cursor.moveToFirst()) {
            do {
                items.add(new SongItem(cursor.getString(indexName),
                        cursor.getString(indexTitle),
                        cursor.getString(indexData)));
            } while (cursor.moveToNext());

        }
        cursor.close();
        return items;
    }

    class Mp3Filter implements FilenameFilter {
        public boolean accept(File dir, String name) {
            return (name.endsWith(".mp3"));
        }
    }

    class MyAdapter extends ArrayAdapter<SongItem> {

        public MyAdapter(ArrayList<SongItem> objects) {
            super(getBaseContext(), 0, objects);
        }

        @Override
        public View getView(int position, View converView, ViewGroup parent) {

            View item = null;
            if(converView == null) {
                LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
                item = inflater.inflate(R.layout.mlistview, null);
            } else {
                item = converView;
            }

            ImageView img = (ImageView) item.findViewById(R.id.imageView);
            TextView vName = (TextView) item.findViewById(R.id.textView);
            TextView vTitle = (TextView) item.findViewById(R.id.textView2);
            TextView vData = (TextView) item.findViewById(R.id.textView3);

            SongItem song = getItem(position);

            //img.setImageResource(song.img);
            vName.setText(song.name);
            vTitle.setText(song.title);
            vData.setText(song.data);

            return item;
        }
    }

    private void playAudio(String filepath) throws IllegalArgumentException,
            IllegalStateException, IOException {
        mPlayer = new MediaPlayer();
        mPlayer.setDataSource(filepath);
        mPlayer.prepare();
        mPlayer.start();

        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if(is_autoplay)
                    nextSong();
            }
        });
    }

    private void stopAudio() {
        if(mPlayer != null) {
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
        if(++currentPosition >= mAudioList.size()) {
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

    public ArrayList<FileItem> updateSongList() {
        ArrayList<FileItem> items = new ArrayList<FileItem>();
        File home = new File(MEDIA_PATH);
        File[] fileLists = home.listFiles(new Mp3Filter());
        for(int i=0;i<fileLists.length;i++) {
            items.add(new FileItem(String.valueOf(i), fileLists[i].getName()));
        }
        return items;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Audio player");

        ListView listView = (ListView)findViewById(R.id.listView);

        listView.setOnClickListener(itemClickListener);

        mAudioList = retrieveData();

        //ArrayList<Champ> data = retrieveData();

        MyAdapter adapter = new MyAdapter(this, R.layout.activity_main, mAudioList);

        listView.setAdapter(adapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // stopAudio();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }





}
