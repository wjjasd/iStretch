package com.github.wjjasd.istretch;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.Window;
import android.widget.TextView;

public class CountDownDialog {

    private Context mContext;
    private Dialog dlg;
    private TextView textView;
    private CountDownTimer countdowntimer;
    private boolean isVisible;

    public boolean getVisibility(){
        return isVisible;
    }

    public CountDownDialog(Context context) {
        mContext = context;
        isVisible = false;
        dlg = new Dialog(mContext);
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dlg.setContentView(R.layout.countdown_dialog);
        textView = dlg.findViewById(R.id.countdownDlg_txt);
        countDown();
        dlg.setCancelable(false);
        dlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                isVisible = false;
            }
        });

    }

    private void countDown() {
        countdowntimer = new CountDownTimerClass(3000, 1000);
        countdowntimer.start();
    }

    public void show(){
        dlg.show();
        isVisible = true;
    }

    public class CountDownTimerClass extends CountDownTimer {

        public CountDownTimerClass(long millisInFuture, long countDownInterval) {

            super(millisInFuture, countDownInterval);

        }

        @Override
        public void onTick(long millisUntilFinished) {

            int progress = (int) (millisUntilFinished/1000);

            textView.setText(Integer.toString(progress));

        }

        @Override
        public void onFinish() {

            textView.setText("START");
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    dlg.dismiss();
                }
            }, 1000);

        }
    }

}
