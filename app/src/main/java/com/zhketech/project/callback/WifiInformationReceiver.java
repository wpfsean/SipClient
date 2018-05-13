package com.zhketech.project.callback;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

/**
 * Created by Root on 2018/4/7.
 */

public class WifiInformationReceiver extends BroadcastReceiver {

    GetDataListern listern;
    public  WifiInformationReceiver(GetDataListern listern){this.listern= listern;}
    @Override
    public void onReceive(Context context, Intent intent) {

     WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

     WifiInfo wifiInfo = wifi.getConnectionInfo();
     if(wifiInfo.getSSID()!=null){
        int num = wifiInfo.getRssi();
        if (listern!=null){
            listern.getDataResult(num);
        }
     }
    }

    public interface  GetDataListern{
        void getDataResult(Integer num);
    };
}
