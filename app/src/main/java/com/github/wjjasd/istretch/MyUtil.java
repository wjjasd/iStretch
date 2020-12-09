package com.github.wjjasd.istretch;

import android.content.Context;
import android.widget.Toast;

public class MyUtil {
    public static void print(String str, Context context){
        Toast.makeText(context,str,Toast.LENGTH_SHORT).show();
    }
}
