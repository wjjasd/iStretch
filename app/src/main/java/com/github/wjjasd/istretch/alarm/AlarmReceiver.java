package com.github.wjjasd.istretch.alarm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.github.wjjasd.istretch.MainActivity;
import com.github.wjjasd.istretch.R;

import java.util.Calendar;
import java.util.Objects;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class AlarmReceiver extends BroadcastReceiver {

    Realm mRealm;

    @Override
    public void onReceive(Context context, Intent intent) {

        mRealm = Realm.getDefaultInstance();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notificationIntent = new Intent(context, MainActivity.class);

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingI = PendingIntent.getActivity(context, 0,
                notificationIntent, 0);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "default");

        builder.setSmallIcon(R.drawable.man_stretch); //mipmap 사용시 Oreo 이상에서 시스템 UI 에러남

        String channelName = "알람 채널";
        String description = "정해진 시간에 알람합니다.";
        int importance = NotificationManager.IMPORTANCE_HIGH; //소리와 알림메시지를 같이 보여줌

        NotificationChannel channel = new NotificationChannel("default", channelName, importance);
        channel.setDescription(description);

        if (notificationManager != null) {
            boolean matchOrNot = false;
            // 노티피케이션 채널을 시스템에 등록
            notificationManager.createNotificationChannel(channel);

            builder.setAutoCancel(true)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setWhen(System.currentTimeMillis())
                    .setTicker("{Time to do some cool move!}")
                    .setContentTitle("스트레칭 할 시간 입니다")
                    .setContentText("시작하기")
                    .setContentInfo("INFO")
                    .setContentIntent(pendingI);

            //intent 에서 알람 ID값 찾아옴
            int alarmId = intent.getIntExtra("alarmId", 0);
            //데이터베이스에서 알람 정보 받아오기
            final RealmResults<AlarmVO> results = mRealm.where(AlarmVO.class)
                    .equalTo("id", alarmId).findAll();
            if (results != null) {
                boolean repeat = Objects.requireNonNull(results.first()).getRepeat();
                if (repeat) {//반복요일 있을때 오늘 요일 확인하고 맞으면 노티
                    RealmList<Integer> day_of_week = Objects.requireNonNull(results.first()).getDay_of_week();
                    Calendar calendar = Calendar.getInstance();
                    Integer todayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                    if (day_of_week != null) {
                        for (int i = 0; i < day_of_week.size(); i++) {
                            int comp = day_of_week.get(i);
                            if (todayOfWeek.equals(comp)) {
                                matchOrNot = true;
                                break;
                            }
                        }
                    }
                    if (matchOrNot) {
                        notificationManager.notify(alarmId, builder.build());
                    }
                } else {
                    //반복요일 없으면 한번만 노티
                    notificationManager.notify(alarmId, builder.build());

                    //알람이 울린 후 UI 변경을 위해 sharedPreferences 값 변경
                    SharedPreferences sharedPreferences = context.getSharedPreferences("stretchPref", Context.MODE_PRIVATE);
                    sharedPreferences.edit().putBoolean("switch" + alarmId, false).apply();

                    Intent afterAlarmIntent = new Intent("alarm_ui_update");
                    afterAlarmIntent.putExtra("afterAlarmId", alarmId);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(afterAlarmIntent);

                }
            }


        }


    }
}

