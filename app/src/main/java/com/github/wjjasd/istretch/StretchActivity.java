package com.github.wjjasd.istretch;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;

public class StretchActivity extends AppCompatActivity {

    private ImageView gifImg;
    private ProgressBar progressBar, timeCounter;
    private int progress;
    private  CountDownDialog countDownDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stretch);

        progressBar = findViewById(R.id.progressBar);
        progress = 1;
        progressBar.setProgress(progress);

        gifImg = findViewById(R.id.gifImg);
        Glide.with(StretchActivity.this).load(R.drawable.aa).into(gifImg);

        timeCounter = findViewById(R.id.timeCounter);

        countDownDialog = new CountDownDialog(StretchActivity.this);
        countDownDialog.show();

    }
}