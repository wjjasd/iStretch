package com.github.wjjasd.istretch.alarm;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class AlarmVO extends RealmObject {

    @PrimaryKey
    private int id;
    private int hour;
    private int minute;
    private boolean repeat;
    private RealmList<Integer> day_of_week;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }

    public boolean getRepeat() {
        return repeat;
    }

    public RealmList<Integer> getDay_of_week() {
        return day_of_week;
    }

    public void setDay_of_week(RealmList<Integer> day_of_week) {
        this.day_of_week = day_of_week;
    }
}
