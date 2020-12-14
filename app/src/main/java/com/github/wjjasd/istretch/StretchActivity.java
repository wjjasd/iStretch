package com.github.wjjasd.istretch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

public class StretchActivity extends AppCompatActivity implements View.OnClickListener {

    private InterstitialAd mInterstitialAd; //구글광고객체
    private ImageView gifImg; //스트레칭 자세 이미지
    private ProgressBar progressBar, timeCounter; //각 스트레칭 순번, 초 단위 카운터
    private TextView progressTxt, title, explanation;
    private static int progress; // 스트레칭 순번
    private  CountDownDialog countDownDialog;
    private ImageButton pauseBtn, nextBtn, preBtn;
    private static boolean flag = false;
    private CountDownTimer cdt;
    private TextView countTxt;
    private int second; //일시정시 버튼 초 저장을 위한 변수

    private final int[] IMGS = {R.drawable.a,R.drawable.b,R.drawable.c,R.drawable.d,R.drawable.e,R.drawable.f
                                ,R.drawable.g,R.drawable.h,R.drawable.i,R.drawable.j,R.drawable.k,R.drawable.l
                                ,R.drawable.m,R.drawable.n,R.drawable.o,R.drawable.p,R.drawable.q,R.drawable.r
                                ,R.drawable.s,R.drawable.t,R.drawable.u,R.drawable.v,R.drawable.w};

    private final int[] GOALS = {10,10,10,10,10,10,10,10,10,10,10,10,10,10,15,15,10,10,10,10,10,15,15};

    private String[] NAMES;
    private String[] EXPLANATION;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stretch);


        NAMES = new String[]{
                  getString(R.string.a1) //a 1
                , getString(R.string.b2) //b 2
                , getString(R.string.c3) //c 3
                , getString(R.string.d4) //d 4
                , getString(R.string.e5) //e 5
                , getString(R.string.f6) //f 6
                , getString(R.string.g7) //g 7
                , getString(R.string.h8) //h 8
                , getString(R.string.i9) //i 9
                , getString(R.string.j10) //j 10
                , getString(R.string.k11) //k 11
                , getString(R.string.l12) //l 12
                , getString(R.string.m13) //m 13
                , getString(R.string.n14) //n 14
                , getString(R.string.o15) //o 15
                , getString(R.string.p16) //p 16
                , getString(R.string.q17) //q 17
                , getString(R.string.r18) //r 18
                , getString(R.string.s19) //s 19
                , getString(R.string.t20) //t 20
                , getString(R.string.u21) //u 21
                , getString(R.string.v22) //v 22
                , getString(R.string.w23)}; //w 23

        EXPLANATION = new String[]{
                  getString(R.string.a1e) //a 1
                , getString(R.string.b2e) //b 2
                , getString(R.string.c3e) //c 3
                , getString(R.string.d4e) //d 4
                , getString(R.string.e5e) //e 5
                , getString(R.string.f6e) //f 6
                , getString(R.string.g7e) //g 7
                , getString(R.string.h8e) //h 8
                , getString(R.string.i9e) //i 9
                , getString(R.string.j10e) //j 10
                , getString(R.string.k11e) //k 11
                , getString(R.string.l12e) //l 12
                , getString(R.string.m13e) //m 13
                , getString(R.string.n14e) //n 14
                , getString(R.string.o15e) //o 15
                , getString(R.string.p16e) //p 16
                , getString(R.string.q17e) //q 17
                , getString(R.string.r18e) //r 18
                , getString(R.string.s19e) //s 19
                , getString(R.string.t20e) //t 20
                , getString(R.string.u21e) //u 21
                , getString(R.string.v22e) //v 22
                , getString(R.string.w23e)}; //w 23

        //구글 광고
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.admob_test_unit_id));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Code to be executed when the interstitial ad is closed.
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
                finish();
            }
        });
        Log.d("AD", "isLoaded >>" + mInterstitialAd.isLoaded());

        setViews();
        startStretch(GOALS[progress]*1000, true);

    }

    private void startStretch(long millisInFuture, boolean popup){
        if(popup) popUpDialog();
        setStretchUI();
        cdt = new CountDownTimer(millisInFuture,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                float temp =  (millisUntilFinished/(float)1000);
                int tik = (int)(Math.round(temp));
                Log.d("millisUntilFinished >>", millisUntilFinished+"");
                Log.d("temp >>", temp+"");
                Log.d("tik >>", tik+"");
                countTxt.setText(String.format("%d", tik));
                timeCounter.setProgress(GOALS[progress]-tik+1);
            }
            @Override
            public void onFinish() {
                progress++;
                if(progress < NAMES.length){
                    cdt.cancel();
                    startStretch(GOALS[progress]*1000,true);
                }else{
                    if (mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                    } else {
                        Log.d("TAG", "The interstitial wasn't loaded yet.");
                        finish();
                    }
                }
            }

        };
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                    cdt.start();
                }
        }, 4300);
    }


    private void setViews() {
        progressBar = findViewById(R.id.progressBar);
        gifImg = findViewById(R.id.gifImg);
        timeCounter = findViewById(R.id.timeCounter);
        pauseBtn = findViewById(R.id.pauseBtn);
        preBtn = findViewById(R.id.preBtn);
        nextBtn = findViewById(R.id.nextBtn);
        progressTxt = findViewById(R.id.progressTxt);
        title = findViewById(R.id.stretchTitle);
        explanation = findViewById(R.id.stretchInfo);
        countTxt = findViewById(R.id.countTxt);

        pauseBtn.setOnClickListener(this);
        preBtn.setOnClickListener(this);
        nextBtn.setOnClickListener(this);

        pauseBtn.bringToFront();
        preBtn.bringToFront();
        nextBtn.bringToFront();

        progress = 0;
        setStretchUI();

    }

    private void setStretchUI() {
        progressBar.setMax(NAMES.length);
        progressBar.setProgress(progress);
        progressTxt.setText((progress+1)+"/"+NAMES.length);
        title.setText(NAMES[progress]);
        explanation.setText(EXPLANATION[progress]);
        timeCounter.setMax(GOALS[progress]);
        gifImg.setImageResource(IMGS[progress]);
        //Glide.with(StretchActivity.this).load(IMGS[progress]).into(gifImg);
    }

    public void popUpDialog() {
        countDownDialog = new CountDownDialog(StretchActivity.this);
        countDownDialog.show();
    }


    @Override
    public void onClick(View v) {

        if(v==pauseBtn){
            if (!flag){ //pause 버튼 클릭
                pauseBtn.setImageResource(R.drawable.ic_baseline_play_arrow);
                flag = true;
                cdt.cancel();
                second = Integer.parseInt(countTxt.getText().toString());
            }else{ //play버튼 클릭
                pauseBtn.setImageResource(R.drawable.ic_baseline_stop);
                flag = false;
                cdt.start();
            }
        }else if(v==preBtn){
            if(flag){
                cdt.cancel();
                progress--;
                startStretch(GOALS[progress]*1000, true);
                pauseBtn.setImageResource(R.drawable.ic_baseline_stop);
                flag = false;
            }else{
                MyUtil.print(getString(R.string.Stop_and_try),getApplicationContext());
            }


        }else if(v==nextBtn){
            if(flag){
                cdt.cancel();
                progress++;
                startStretch(GOALS[progress]*1000, true);
                pauseBtn.setImageResource(R.drawable.ic_baseline_stop);
                flag = false;
            }else{
                MyUtil.print(getString(R.string.Stop_and_try),getApplicationContext());
            }

        }
    }

    @Override
    protected void onDestroy() {
        cdt.cancel();
        if (mInterstitialAd.isLoaded() && progress > NAMES.length / 2) {
            mInterstitialAd.show();
        }
        super.onDestroy();
    }
}