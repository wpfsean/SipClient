package com.zhketech.project.callback;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

/**
 * 用广播注册监听电量变化
 *
 * Created by Root on 2018/4/7.
 */

public class BatteryReceiver  extends BroadcastReceiver {

    GetDataListener lister;

    public BatteryReceiver(GetDataListener getDataListener) {
        this.lister = getDataListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int level=intent.getIntExtra(BatteryManager.EXTRA_LEVEL,0);
        int scale=intent.getIntExtra(BatteryManager.EXTRA_SCALE,0);
        int levelPercent = (int)(((float)level / scale) * 100);
        if(lister!=null){
            lister.getResult(levelPercent);
        }
    }

    public interface GetDataListener {
        void getResult(Integer level);
    }

}
