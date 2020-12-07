package com.github.wjjasd.istretch.alarm;


import java.util.ArrayList;
import java.util.List;

import ca.antonious.materialdaypicker.MaterialDayPicker;
import io.realm.RealmList;


public class DayOfWeekConverter {

    private List<MaterialDayPicker.Weekday> mSelectedDays_in;
    private RealmList<Integer> mSelectedDays_int = new RealmList<Integer>();
    private List<String> mSelectedDays_string = new ArrayList<>();

    public DayOfWeekConverter(List<MaterialDayPicker.Weekday> selectedDays) {
        mSelectedDays_in = selectedDays;
    }

    public RealmList<Integer> convert_to_int() {


        for (int i = 0; i < mSelectedDays_in.size(); i++) {
            switch (mSelectedDays_in.get(i)) {
                case SUNDAY:
                    mSelectedDays_int.add(1);
                    break;
                case MONDAY:
                    mSelectedDays_int.add(2);
                    break;
                case TUESDAY:
                    mSelectedDays_int.add(3);
                    break;
                case WEDNESDAY:
                    mSelectedDays_int.add(4);
                    break;
                case THURSDAY:
                    mSelectedDays_int.add(5);
                    break;
                case FRIDAY:
                    mSelectedDays_int.add(6);
                    break;
                case SATURDAY:
                    mSelectedDays_int.add(7);
                    break;
            }
        }
        return mSelectedDays_int;
    }

    public List<String> convert_to_string() {

        for (int i = 0; i < mSelectedDays_in.size(); i++) {
            switch (mSelectedDays_in.get(i)) {
                case SUNDAY:
                    mSelectedDays_string.add("SUNDAY");
                    break;
                case MONDAY:
                    mSelectedDays_string.add("MONDAY");
                    break;
                case TUESDAY:
                    mSelectedDays_string.add("TUESDAY");
                    break;
                case WEDNESDAY:
                    mSelectedDays_string.add("WEDNESDAY");
                    break;
                case THURSDAY:
                    mSelectedDays_string.add("THURSDAY");
                    break;
                case FRIDAY:
                    mSelectedDays_string.add("FRIDAY");
                    break;
                case SATURDAY:
                    mSelectedDays_string.add("SATURDAY");
                    break;
            }
        }
        return mSelectedDays_string;
    }
}
