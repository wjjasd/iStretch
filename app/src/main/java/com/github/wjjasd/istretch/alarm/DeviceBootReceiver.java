package com.github.wjjasd.istretch.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Objects;

import io.realm.Realm;
import io.realm.RealmResults;

public class DeviceBootReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        if (Objects.equals(intent.getAction(), "android.intent.action.BOOT_COMPLETED")) {

            Realm realm = Realm.getDefaultInstance();
            RealmResults<AlarmVO> results = realm.where(AlarmVO.class).findAll();

            if (results != null || results.size() != 0) {

                int alarmId;
                int hour;
                int minute;
                boolean repeat;

                for (int i = 0; i < results.size(); i++) {

                    AlarmVO alarm = results.get(i);
                    alarmId = Objects.requireNonNull(alarm).getId();
                    hour = alarm.getHour();
                    minute = alarm.getMinute();
                    repeat = alarm.getRepeat();

                    AlarmController alarmController = new AlarmController(context);
                    alarmController.setAlarm(alarmId, hour, minute, repeat);
                }
            }

        }
    }
}
