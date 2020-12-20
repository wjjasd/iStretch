package com.github.wjjasd.istretch.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import java.util.Calendar;
import java.util.Objects;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class AlarmController implements AlarmInterface {

    private Context context;
    private Realm mRealm;
    private AlarmManager mAlarmManager;
    final boolean[] newAlarmResult = {false, false}; //1데이터 저장 결과 , 2알람등록결과

    public AlarmController(Context context) {
        mRealm = Realm.getDefaultInstance();
        this.context = context;
    }

    @Override
    public boolean setNewAlarm(final int id, final int hour, final int minute, final boolean repeat, final RealmList<Integer> dayOfWeek) {
        // 알람 최초 등록 때는 테이블에 먼저 저장해줌
        // 1. vo 클래스를 통해 데이터 저장 -> 결과값 반환

        if (mRealm != null) {
            // 넘어온 알람 데이터 저장
            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    AlarmVO alarmVo = mRealm.createObject(AlarmVO.class, id);
                    alarmVo.setHour(hour);
                    alarmVo.setMinute(minute);
                    alarmVo.setRepeat(repeat);
                    alarmVo.setDay_of_week(dayOfWeek);
                    newAlarmResult[0] = true;
                }
            });
        }
        // 2. alarm 리시버에 정보 전달해서 알람 등록 -> 결과값 반환
        newAlarmResult[1] = setAlarm(id, hour, minute, repeat);

        return newAlarmResult[0] & newAlarmResult[1];
    }

    public boolean setAlarm(int id, int hour, int minute, boolean repeat) {

        boolean setResult = false;

        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);

            // 이미 지난 시간을 지정했다면 다음날 같은 시간으로 설정
            if (calendar.before(Calendar.getInstance())) {
                calendar.add(Calendar.DATE, 1);
            }

            mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent alarmIntent = new Intent(context, AlarmReceiver.class);

            alarmIntent.putExtra("alarmId", id);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            if (repeat) {
                mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
            } else {
                mAlarmManager.set(AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(), pendingIntent);
            }
            setResult = true;
        } catch (Exception e) {
            Log.d("setAlarm", e.toString());
        }
        PackageManager pm = context.getPackageManager();

        ComponentName receiver = new ComponentName(context, DeviceBootReceiver.class);

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

        return setResult; // 데이터 저장 결과반환
    }

    @Override
    public boolean deleteAlarm(int id) {
        final boolean[] deleteResult = {false, false}; //1. db 조작결과   2. 알람취소결과

        // 1. DB 에서 데이터 삭제 -> 결과리턴
        final RealmResults<AlarmVO> results = mRealm.where(AlarmVO.class)
                .equalTo("id", id).findAll();
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                results.deleteAllFromRealm();
                deleteResult[0] = true;
            }
        });

        // 2. 알람등록 되있는거 찾아서 취소  -> 결과리턴
        try {
            mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, AlarmReceiver.class);
            PendingIntent pIntent = PendingIntent.getBroadcast(context, id, intent, 0);
            if (pIntent == null) {
                deleteResult[1] = false;
            } else {
                mAlarmManager.cancel(pIntent);
                pIntent.cancel();
                deleteResult[1] = true;
            }
        } catch (Exception e) {
            Log.d("alarmCancel", e.toString());
        }
        return deleteResult[0] & deleteResult[1]; // 1&&2 반환
    }

    //알람 비활성화
    @Override
    public void cancelAlarm(int id) {
        try {
            mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, AlarmReceiver.class);
            PendingIntent pIntent = PendingIntent.getBroadcast(context, id, intent, 0);
            if (pIntent != null) {
                mAlarmManager.cancel(pIntent);
                pIntent.cancel();
            }
        } catch (Exception e) {
            Log.e("alarmCancel", e.toString());
        }
    }

    //알람 활성화
    @Override
    public void activeAlarm(int id) {
        try {
            final RealmResults<AlarmVO> results = mRealm.where(AlarmVO.class)
                    .equalTo("id", id).findAll();
            int hour = Objects.requireNonNull(results.first()).getHour();
            int minute = Objects.requireNonNull(results.first()).getMinute();
            boolean repeat = Objects.requireNonNull(results.first()).getRepeat();
            setAlarm(id, hour, minute, repeat);
        } catch (Exception e) {
            Log.d("activeAlarm", e.toString());
        }
    }

}
