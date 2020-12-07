package com.github.wjjasd.istretch.alarm;

import io.realm.RealmList;

public interface AlarmInterface {

    public boolean setNewAlarm(int id, int hour, int minute, boolean repeat, RealmList<Integer> dayOfWeek); //최초 알람 등록

    public boolean deleteAlarm(int id); // 등록된 알람 삭제

    public void cancelAlarm(int id); // 등록된 알람 취소

    public void activeAlarm(int id); // 취소된 알람 활성화

}
