package com.zkketech.project.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.telephony.TelephonyManager;

import java.util.Calendar;

/**
 * Created by Root on 2018/4/20.
 */

public class PhoneUtils {
    Context context ;

    public PhoneUtils(Context context){
        this.context = context;
        throw  new UnsupportedOperationException("cannot be instantiated");
    }

    public static long dateStamp() {
        long time = System.currentTimeMillis()/1000;
        return time;
    }


    @SuppressLint("MissingPermission")
    public static String getPhoneInfo(Context context, int type)  {
        try{
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        switch (type) {
            case 1:// 设备唯一标识
                return telephonyManager.getDeviceId();
            case 2:// 系统版本号
                return android.os.Build.VERSION.RELEASE;
            case 3:// 设备型号
                return android.os.Build.MODEL;
            case 4:// 应用程序版本号
                return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            default:
                return "";
        }
        }catch (Exception e){
            return "error"+e.getMessage();
        }
    }

    public static String getTimeType(){
        long time= Calendar.getInstance().getTimeInMillis();
        String  str=String.valueOf(time);
        return str;
    }

}
