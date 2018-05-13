package com.zhketech.project.callback;

import android.text.TextUtils;


import com.zkketech.project.utils.Logutils;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * Created by Root on 2018/4/19.
 */

public class ReceiveServerMess implements Runnable {

    GetMessageListern listern;

    public ReceiveServerMess(GetMessageListern listern) {
        this.listern = listern;
    }

    @Override
    public void run() {
        DatagramSocket ds = null;
        try {
            while (true) {
                ds = new DatagramSocket(2000);
                byte bytes[] = new byte[1024];
                DatagramPacket dp = new DatagramPacket(bytes, bytes.length);
                ds.receive(dp);
                String result = new String(dp.getData(), "gb2312") + "\n" + dp.getAddress().getHostAddress() + "\n" + dp.getPort();
                ds.close();
                if(!TextUtils.isEmpty(result)){
                    if(listern!=null){
                        listern.getMess(result);
                    }
                }
            }
        } catch (Exception e) {
            Logutils.e("sms异常："+e.getMessage());
        }
    }

    public interface GetMessageListern {
        void getMess(String ms);
    }
}
