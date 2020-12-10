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

    private final int IMGS[] = {R.drawable.a,R.drawable.b,R.drawable.c,R.drawable.d,R.drawable.e
                                ,R.drawable.f,R.drawable.g,R.drawable.h,R.drawable.i,R.drawable.j
                                ,R.drawable.k,R.drawable.l,R.drawable.m,R.drawable.n,R.drawable.o,R.drawable.p
                                ,R.drawable.q,R.drawable.r,R.drawable.s,R.drawable.t,R.drawable.u,R.drawable.v,R.drawable.w};

    private final int GOALS[] = {3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3};

    private final String NAMES[] = {
             "좌우앞뒤 목 늘이기(좌)" //a 1
            ,"좌우앞뒤 목 늘이기(우)" //b 2
            ,"좌우앞뒤 목 늘이기(뒤)" //c 3
            ,"좌우앞뒤 목 늘이기(앞)" //d 4
            ,"손뻗어 당기기 (좌)" //e 5
            ,"손뻗어 당기기 (우)" //f 6
            ,"팔 옆으로 당기기 (좌)" //g 7
            ,"팔 옆으로 당기기 (우)" //h 8
            ,"머리뒤로 팔 당기기 (좌)" //i 9
            ,"머리뒤로 팔 당기기 (우)" //j 10
            ,"두 팔 뒤로 당기기 " //k 11
            ,"두 팔 피며 상체 굽히기" //l 12
            ,"팔올려 옆으로 몸통 굽히기 (좌)" //m 13
            ,"팔올려 옆으로 몸통 굽히기 (우)" //n 14
            ,"한쪽 다리 접고 몸 숙이기 (좌)" //o 15
            ,"한쪽 다리 접고 몸 숙이기 (우)" //p 16
            ,"한쪽 다리 접고 상체 회전 (좌)" //q 17
            ,"한쪽 다리 접고 상체 회전 (우)" //r 18
            ,"한쪽 다리 뒤로 굽혀 당기기 (좌)" //s 19
            ,"한쪽 다리 뒤로 굽혀 당기기 (우)" //t 20
            ,"서서 상체 접기" //u 21
            ,"런지 자세로 종아리 늘이기 (좌)" //v 22
            ,"런지 자세로 종아리 늘이기 (우)"}; //w 23

    private final String EXPLANATION[] = {
             "왼손으로 머리를 잡고 천천히 당겨 \n 목 오른쪽 근육을 스트레칭 해줍니다" //a 1
            ,"오른손으로 머리를 잡고 천천히 당겨 \n 목 왼쪽 근육을 스트레칭 해줍니다" //b 2
            ,"어깨를 고정하고 머리를 \n 위로 들어 줍니다" //c 3
            ,"어깨를 고정하고 머리를 \n 앞으로 숙여 줍니다" //d 4
            ,"왼손을 앞으로 쭉 뻗은 상태에서 \n 오른손으로 왼손끝을 몸쪽으로 당겨줍니다" //e 5
            ,"오른손은 앞으로 쭉 뻗은 상태에서 \n 왼손으로 오른손 끝을 몸쪽으로 당겨줍니다" //f 6
            ,"왼쪽 팔을 오른쪽으로 당기고 왼쪽 어깨를 \n 스트레칭 해주며 자세를 유지합니다" //g 7
            ,"오른쪽 팔을 왼쪽으로 당기고 오른쪽 어깨를 \n 스트레칭 해주며 자세를 유지합니다 " //h 8
            ,"왼쪽 팔을 머리뒤쪽에서 반대편으로 넘기며 \n 다른 한 손으로 천천히 당겨줍니다" //i 9
            ,"오른쪽 팔을 머리뒤쪽에서 반대편으로 넘기며 \n 다른 한 손으로 천천히 당겨줍니다" //j 10
            ,"두 팔을 뒤로 모아 마주잡고 \n 어깨를 펴며 쭉 뻗어 줍니다" //k 11
            ,"양발을 나란히 놓고 선후 두손을 모아줍니다 \n 허리를 구부리며 등과 팔을 쭉 펴세요" //l 12
            ,"두손을 깍지껴 위로 들어 올리고 \n 왼쪽으로 기울여 줍니다" //m 13
            ,"두손을 깍지껴 위로 들어 올리고 \n 오른쪽으로 기울여 줍니다" //n 14
            ,"의자에 앉아 왼쪽 다리을 반대편 \n 무릎에 올린 후 몸을 앞으로 지그시 눌러줍니다" //o 15
            ,"의자에 앉아 오른쪽 다리을 \n 반대편 무릎에 올린 후 몸을 앞으로 \n 지그시 눌러줍니다" //p 16
            ,"의자에 앉아 왼쪽 다리을 반대편 \n 무릎에 올린 후 상체를 왼쪽으로 틀어줍니다" //q 17
            ,"의자에 앉아 왼쪽 다리을 반대편 \n 무릎에 올린 후 상체를 오른쪽으로 틀어줍니다" //r 18
            ,"바로선 자세에서 왼쪽다리를 들어올려 \n 왼쪽손으로 잡고 자세를 유지합니다" //s 19
            ,"바로선 자세에서 오른쪽다리 들어올려 \n 오른손으로 잡고 자세를 유지합니다" //t 20
            ,"양발을 어깨 넓이로 벌려 선 상태에서 \n 몸을 앞으로 서서히 숙여 등과 허벅지 \n 근육을 늘려줍니다" //u 21
            ,"오른쪽 발을 앞으로 내딛고 뒤쪽 종아리를 늘려줍니다" //v 22
            ,"왼쪽 발을 앞으로 내딛고 뒤쪽 종아리를 늘려줍니다"}; //w 23
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stretch);
        
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
        //if(popup) popUpDialog();
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
        }, 1000); //4500
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
                pauseBtn.setImageResource(R.drawable.ic_baseline_pause);
                flag = false;
                cdt.start();
            }
        }else if(v==preBtn){
            if(flag){
                cdt.cancel();
                progress--;
                startStretch(GOALS[progress]*1000, true);
                pauseBtn.setImageResource(R.drawable.ic_baseline_pause);
                flag = false;
            }else{
                MyUtil.print("정지후 시도하세요",getApplicationContext());
            }


        }else if(v==nextBtn){
            if(flag){
                cdt.cancel();
                progress++;
                startStretch(GOALS[progress]*1000, true);
                pauseBtn.setImageResource(R.drawable.ic_baseline_pause);
                flag = false;
            }else{
                MyUtil.print("정지후 시도하세요",getApplicationContext());
            }

        }
    }

    @Override
    protected void onDestroy() {
        cdt.cancel();
        super.onDestroy();
    }
}