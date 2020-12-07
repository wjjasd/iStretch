package com.github.wjjasd.istretch.alarm;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.wjjasd.istretch.R;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.viewHolder> {

    private List<AlarmVO> alarmList;
    private int mAlarmId;
    private int mHour;
    private int mMinute;
    private boolean mRepeat;
    private RealmList<Integer> mDayOfWeek;
    static Context mContext;


    private static OnClickListener mOnDelete;
    private static CompoundButton.OnCheckedChangeListener mOnCheck;
    private static OnClickListener mOnclick;

    public AlarmAdapter(Context context, List<AlarmVO> dataSet,
                        OnClickListener onClick, OnClickListener onDelete, CompoundButton.OnCheckedChangeListener onCheck) {
        alarmList = dataSet;
        mOnDelete = onDelete;
        mOnCheck = onCheck;
        mOnclick = onClick;
        mContext = context;
    }

    public static class viewHolder extends RecyclerView.ViewHolder {

        private TextView am_pmTv;
        private TextView timeTv;
        private TextView dayOfWeekTv;
        private ImageButton deleteBtn;
        private Switch alarmSw;
        private View rootView;


        public viewHolder(@NonNull View itemView) {
            super(itemView);

            am_pmTv = itemView.findViewById(R.id.ampm_row);
            timeTv = itemView.findViewById(R.id.time_row);
            dayOfWeekTv = itemView.findViewById(R.id.day_of_week_row);
            deleteBtn = itemView.findViewById(R.id.deleteBtn_row);
            alarmSw = itemView.findViewById(R.id.switch_row);
            rootView = itemView;

            rootView.setOnClickListener(mOnclick);
            deleteBtn.setOnClickListener(mOnDelete);
            alarmSw.setOnCheckedChangeListener(mOnCheck);


        }
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_alarm, parent, false);

        viewHolder vh = new viewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

        AlarmVO alarm = alarmList.get(position);
        mDayOfWeek = alarm.getDay_of_week();
        mHour = alarm.getHour();
        mMinute = alarm.getMinute();
        mAlarmId = alarm.getId();
        mRepeat = alarm.getRepeat();

        //정수형으로된 요일 문자열로 변환 후 텍스트뷰로 뿌려준다
        if (mDayOfWeek != null) {
            List<String> week = weekConverter(mDayOfWeek);
            if (week.size() == 1) {
                holder.dayOfWeekTv.setText(week.get(0));
            } else if (week.size() > 1) {
                StringBuilder st = new StringBuilder();
                st.append(week.get(0));
                for (int i = 1; i < week.size(); i++) {
                    st.append(" ").append(week.get(i));
                }
                holder.dayOfWeekTv.setText(st);
            } else {
                holder.dayOfWeekTv.setText("no repeat");
            }
        } else {
            holder.dayOfWeekTv.setText("no repeat");
        }


        //시간 계산해서 am_pm 설정
        String am_pm;
        if (mHour > 12) {
            am_pm = "pm";
            if (mHour == 24) {
                mHour = 12;
                am_pm = "am";
            } else {
                mHour -= 12;
            }

        } else {
            if (mHour == 12) {
                am_pm = "pm";
            } else if (mHour == 0) {
                mHour = 12;
                am_pm = "am";
            } else {
                am_pm = "am";
            }
        }

        holder.am_pmTv.setText(am_pm);
        String mMinute_str = "";
        if (mMinute < 10) {
            mMinute_str = "0" + mMinute;
        } else {
            mMinute_str = String.valueOf(mMinute);
        }

        String time = mHour + " : " + mMinute_str;
        holder.timeTv.setText(time);

        holder.rootView.setTag(R.id.TAG_ALARM_POSITION, position);
        holder.rootView.setTag(R.id.TAG_ALARM_ID, mAlarmId);

        holder.deleteBtn.setTag(R.id.TAG_ALARM_POSITION, position);
        holder.deleteBtn.setTag(R.id.TAG_ALARM_ID, mAlarmId);

        holder.alarmSw.setTag(R.id.TAG_ALARM_POSITION, position);
        holder.alarmSw.setTag(R.id.TAG_ALARM_ID, mAlarmId);

        SharedPreferences sharedPreferences = mContext.getSharedPreferences("stretchPref", Context.MODE_PRIVATE);
        //울린 알람이면 false, 최초 만들어질때 알람 켜지도록 디폴트값 true
        boolean switchToggle = sharedPreferences.getBoolean("switch" + holder.alarmSw.getTag(R.id.TAG_ALARM_ID), true);
        holder.alarmSw.setChecked(switchToggle);

    }

    @Override
    public int getItemCount() {
        return alarmList == null ? 0 : alarmList.size();
    }

    public AlarmVO getAlarm(int position) {
        return alarmList != null ? alarmList.get(position) : null;
    }


    private List<String> weekConverter(RealmList<Integer> weekInt) {

        List<String> weekString = new ArrayList<>();

        for (int i = 0; i < weekInt.size(); i++) {
            switch (weekInt.get(i)) {
                case 1:
                    weekString.add("Sun");
                    break;
                case 2:
                    weekString.add("Mon");
                    break;
                case 3:
                    weekString.add("Tue");
                    break;
                case 4:
                    weekString.add("Wed");
                    break;
                case 5:
                    weekString.add("Thu");
                    break;
                case 6:
                    weekString.add("Fri");
                    break;
                case 7:
                    weekString.add("Sat");
                    break;
            }
        }

        return weekString;
    }


}
