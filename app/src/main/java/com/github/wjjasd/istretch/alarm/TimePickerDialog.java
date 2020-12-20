package com.github.wjjasd.istretch.alarm;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.wjjasd.istretch.R;


import java.util.List;

import ca.antonious.materialdaypicker.MaterialDayPicker;
import io.realm.Realm;
import io.realm.RealmList;

public class TimePickerDialog{

    private Context mContext;

    private int mId;
    private int mHour;
    private int mMinute;
    private boolean mRepeat;
    private RealmList<Integer> mDayOfWeek;

    private SharedPreferences sharedPreferences;

    private Button cancelBtn, saveBtn;

    private TextView repeat_tv;
    private Dialog dlg;
    private onDlgResult mDlgResult;

    MaterialDayPicker materialDayPicker;

    Realm mRealm;

    private int idCounter = -1;

    public TimePickerDialog(Context context) {
        mContext = context;
        dlg = new Dialog(mContext);
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(R.layout.time_picker_dialog);
        sharedPreferences = mContext.getSharedPreferences("stretchPref", Context.MODE_PRIVATE);
        setView(); //요일선택에 대한 동작
        setOnClick(); //저장, 취소버튼
    }

    public void show(){
        dlg.show();
    }

    private void setOnClick() {
        //취소
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
            }
        });
        //저장
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePicker timePicker = dlg.findViewById(R.id.timepicker_dialog);
                // 시, 분, 요일, id, 반복여부 얻어옴
                mHour = timePicker.getHour();
                mMinute = timePicker.getMinute();
                if (mDayOfWeek != null) {
                    mRepeat = true;
                } else {
                    mRepeat = false;
                    mDayOfWeek = null;
                }
                //알람 등록위한 아이디값 생성
                mId = generateAlmId();

                Realm.init(mContext);
                mRealm = Realm.getDefaultInstance();
                //알람 컨트롤러 호출
                AlarmController alarmController = new AlarmController(mContext);
                boolean setResult = alarmController.setNewAlarm(mId, mHour, mMinute, mRepeat, mDayOfWeek);
                if (setResult) {
                    Toast.makeText(mContext, "알람이 등록되었습니다", Toast.LENGTH_SHORT).show();
                    //타임픽커 결과 홈으로 전달
                    mDlgResult.dlgFinishSave(true, mId);
                    sharedPreferences.edit().putBoolean("checkAlarmFirst", false).apply();
                } else {
                    Toast.makeText(mContext, "실패", Toast.LENGTH_SHORT).show();
                }
                dlg.dismiss();
            }
        });
    }

    private int generateAlmId() {
        int id;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        boolean checkAlarmFirst = sharedPreferences.getBoolean("checkAlarmFirst", true);
        if (checkAlarmFirst) {
            idCounter += 1;
            id = idCounter;
            editor.putBoolean("checkAlarmFirst", false).apply();
        } else {
            idCounter = sharedPreferences.getInt("lastCount", 0) + 1;
            id = idCounter;
        }
        editor.putInt("lastCount", id);
        editor.apply();
        return id;
    }

    private void setView() {
        repeat_tv = dlg.findViewById(R.id.repeatTv);
        saveBtn = dlg.findViewById(R.id.saveBtn_timepicker);
        cancelBtn = dlg.findViewById(R.id.cancelBtn_timepicker);
        materialDayPicker = dlg.findViewById(R.id.dayPicker);
        materialDayPicker.setDaySelectionChangedListener(new MaterialDayPicker.DaySelectionChangedListener() {
            @Override
            public void onDaySelectionChanged(List<MaterialDayPicker.Weekday> selectedDays) {
                //선택한 요일 텍스트로 전환해서 뷰로 뿌려줌
                DayOfWeekConverter converter = new DayOfWeekConverter(selectedDays);
                List<String> stringList_converted = converter.convert_to_string();
                StringBuilder st = new StringBuilder();
                if (stringList_converted.size() > 1) {
                    for (int i = 0; i < stringList_converted.size(); i++) {

                        if (i == stringList_converted.size() - 1) {
                            st.append(stringList_converted.get(i).substring(0, 3));
                        } else {
                            st.append(stringList_converted.get(i).substring(0, 3)).append(", ");
                        }
                    }
                    repeat_tv.setText(st.toString());
                } else if (stringList_converted.size() == 1) {
                    repeat_tv.setText(stringList_converted.get(0));
                } else {
                    repeat_tv.setText("");
                }
                //전역변수 리스트에 저장
                mDayOfWeek = converter.convert_to_int();
                Log.d("요일선택",mDayOfWeek.toString());
            }
        });
    }


    public void dlgResult(onDlgResult result) {
        mDlgResult = result;
    }

    public interface onDlgResult {
        void dlgFinishSave(boolean result, int id);
    }


}
