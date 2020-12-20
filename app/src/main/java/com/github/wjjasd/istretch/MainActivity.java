package com.github.wjjasd.istretch;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Toast;

import com.github.wjjasd.istretch.alarm.TimePickerDialog;
import com.github.wjjasd.istretch.alarm.AlarmAdapter;
import com.github.wjjasd.istretch.alarm.AlarmController;
import com.github.wjjasd.istretch.alarm.AlarmVO;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    private ImageButton addBtn, startBtn;
    private Realm mRealm;
    private TimePickerDialog timePickerDialog;
    private Context mContext;
    private SharedPreferences sharedPreferences;
    private RecyclerView recyclerView;
    static private RecyclerView.Adapter mAdapter;
    boolean checkAlarmFirst;
    private InterstitialAd mInterstitialAd;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            int afterAlarmId = intent.getIntExtra("afterAlarmId", 0);
            //스위치 꺼줌
            sharedPreferences.edit().putBoolean("switch" + afterAlarmId, false).apply();
            recyclerView.removeAllViews();
            setAlarmList();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//---------------------------------------ad test -------------------------------------------------
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

//        mInterstitialAd = new InterstitialAd(this);
//        mInterstitialAd.setAdUnitId(getString(R.string.admob_test_unit_id));
//        mInterstitialAd.loadAd(new AdRequest.Builder().build());
//        Button btn = findViewById(R.id.adBtn);
//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d("AD", "isLoaded >>" + mInterstitialAd.isLoaded());
//                if (mInterstitialAd.isLoaded()) {
//                    mInterstitialAd.show();
//                } else {
//                    Log.d("TAG", "The interstitial wasn't loaded yet.");
//                }
//            }
//        });
//---------------------------------------ad test -------------------------------------------------
        mContext = getApplicationContext();
        mRealm = Realm.getDefaultInstance();
        sharedPreferences = mContext.getSharedPreferences("stretchPref", Context.MODE_PRIVATE);
        checkAlarmFirst = sharedPreferences.getBoolean("checkAlarmFirst", true);

        LocalBroadcastManager.getInstance(mContext).registerReceiver(broadcastReceiver,
                new IntentFilter("alarm_ui_update"));

        recyclerView = findViewById(R.id.alarm_recycler);

        setAlarmList();

        addBtn = findViewById(R.id.addAlarmBtn); //알람추가버튼
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialog = new TimePickerDialog(MainActivity.this);
                timePickerDialog.show();
                //결과확인
                timePickerDialog.dlgResult(new TimePickerDialog.onDlgResult() {
                    @Override
                    public void dlgFinishSave(boolean result, int id) {
                        checkAlarmFirst = sharedPreferences.getBoolean("checkAlarmFirst", true);
                        recyclerView.removeAllViews();
                        setAlarmList(); //Recycler view 다시셋팅
                    }
                });
            }
        });

        startBtn = findViewById(R.id.startBtn);
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,StretchActivity.class);;
                startActivity(intent);
            }
        });



    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(broadcastReceiver);

    }

    public void setAlarmList() {

        if (!checkAlarmFirst) {

            RealmResults<AlarmVO> results = mRealm.where(AlarmVO.class).findAll();
            final AlarmController alarmController = new AlarmController(mContext);

            if (results != null || results.size() != 0) {

                int alarmId = 0;
                int hour = 0;
                int minute = 0;
                boolean repeat = false;
                RealmList<Integer> dayOfWeek = null;

                List<AlarmVO> alarmDataSet = new ArrayList<>();

                for (int i = 0; i < results.size(); i++) {
                    AlarmVO alarmData = new AlarmVO();

                    AlarmVO alarm = results.get(i);
                    alarmId = Objects.requireNonNull(alarm).getId();
                    hour = alarm.getHour();
                    minute = alarm.getMinute();
                    repeat = alarm.getRepeat();
                    dayOfWeek = alarm.getDay_of_week();

                    alarmData.setId(alarmId);
                    alarmData.setHour(hour);
                    alarmData.setMinute(minute);
                    alarmData.setRepeat(repeat);
                    alarmData.setDay_of_week(dayOfWeek);
                    alarmDataSet.add(alarmData);
                }

                final AlarmController controller = new AlarmController(mContext);

                mAdapter = new AlarmAdapter(mContext, alarmDataSet, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //rootView
                        //데이터 삭제, 알람채널 등록 취소하고 다시 타임피커 띄워줌
                        //Toast.makeText(mContext, "root view clicked", Toast.LENGTH_SHORT).show();
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //delete image button
                        // 1. 데이터 삭제, 알람채널 등록취소 >> deleteAlarm();
                        Object obj = v.getTag(R.id.TAG_ALARM_ID);
                        if (obj != null) {
                            int id = (int) obj;
                            boolean result = false;
                            result = controller.deleteAlarm(id);
                            if (result) {
                                // 2. 리스트 갱신
                                recyclerView.removeAllViews();
                                setAlarmList();
                            } else {
                                Toast.makeText(mContext, "삭제오류", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }, new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        // alarm switch
                        //데이터 유지, 알람채널 취소, 활성화
                        Object obj = buttonView.getTag(R.id.TAG_ALARM_ID);
                        if (obj != null) {
                            int id = (int) obj;
                            if (isChecked) {
                                //스위치 ON 알람채널 다시 등록
                                sharedPreferences.edit().putBoolean("switch" + id, true).apply();
                                alarmController.activeAlarm(id);
                            } else {
                                //스위치 OFF 알람채널 삭제
                                sharedPreferences.edit().putBoolean("switch" + id, false).apply();
                                alarmController.cancelAlarm(id);
                            }


                        }
//                        Toast.makeText(mContext, String.valueOf(isChecked), Toast.LENGTH_SHORT).show();
                    }
                });

                recyclerView.setAdapter(mAdapter);

            }

        }

    }
}