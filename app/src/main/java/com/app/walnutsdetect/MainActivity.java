package com.app.walnutsdetect;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import photo.OpencvPhotoActivity;


public class MainActivity extends AppCompatActivity {
    //public GameView gameView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.firstac);

       /* gameView = new GameView(this);

        FrameLayout.LayoutParams params=new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        params.topMargin = 0;
        params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;



        addContentView(gameView,params);*/

      startActivity(new Intent(this, OpencvPhotoActivity.class));
        finish();
    }
}
