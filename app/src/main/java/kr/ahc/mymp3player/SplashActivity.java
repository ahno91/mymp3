package kr.ahc.mymp3player;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

/**
 * Created by daeguunivmac15 on 2015. 7. 9..
 */
public class SplashActivity extends Activity {

    private ImageView mImageView;
    boolean mcheck1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionbar = getActionBar();
        actionbar.hide();

        setContentView(R.layout.activity_splash);

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch(InterruptedException e) {
                    e.printStackTrace();
                }

                Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(mainIntent);
                SplashActivity.this.finish();
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();

        mImageView = (ImageView)findViewById(R.id.splashimage);
    }

    private void retrivePreferences() {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        mcheck1 = prefs.getBoolean("checkbox", false);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();

        //AnimationDrawable animationDrawable = (AnimationDrawable) mImageView.getDrawable();
        //animationDrawable.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
