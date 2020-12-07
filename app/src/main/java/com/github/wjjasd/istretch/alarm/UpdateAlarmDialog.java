package com.github.wjjasd.istretch.alarm;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.github.wjjasd.istretch.R;

public class UpdateAlarmDialog extends DialogFragment {

    private Context mContext;
    private Dialog dlg;

    public UpdateAlarmDialog(Context mContext) {
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        dlg = new Dialog(mContext);
        dlg.setContentView(R.layout.time_picker_dialog);

        setView();
        setOnClick();

        return dlg;
    }

    private void setView() {

    }

    private void setOnClick() {

    }


}
