package com.zhketech.project.callback;

import com.zhketech.project.bean.AlarmBen;
import com.zhketech.project.bean.VideoBen;
import com.zkketech.project.utils.ByteUtils;
import com.zkketech.project.utils.Logutils;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 回调接收服务器的报警报文
 *
 * Created by Root on 2018/4/20.
 */

public class ReceiverServerAlarm extends Thread {

    GetAlarmFromServerListern listern;

    public ReceiverServerAlarm(GetAlarmFromServerListern listern) {
        this.listern = listern;
    }

    @Override
    public void run() {
        super.run();


        try {
            ServerSocket serverSocket = new ServerSocket(2000, 3);
            InputStream in = null;
            while (true) {
                Socket socket = null;
                try {
                    socket = serverSocket.accept();
                    Logutils.i("来报警了:" + socket.getInetAddress() + ":" + socket.getPort());
                    in = socket.getInputStream();
                    //falge 4
                    byte[] header = new byte[524];
                    int read = in.read(header);
                    byte[] flage = new byte[4];
                    for (int i = 0; i < 4; i++) {
                        flage[i] = header[i];
                    }
                    String falge1 = new String(flage, "gb2312");
                    //sender 32
                    byte[] sender = new byte[32];
                    for (int i = 0; i < 32; i++) {
                        sender[i] = header[i + 4];
                    }
                    String sender1 = new String(sender, "gb2312");

                    //videoource

                    //videoFlage 4
                    byte[] videoFlage = new byte[4];
                    for (int i = 0; i < 4; i++) {
                        videoFlage[i] = header[i + 36];
                    }
                    String videoFlage1 = new String(videoFlage, "gb2312");

                    //videoId 48
                    byte[] videoId = new byte[48];
                    for (int i = 0; i < 48; i++) {
                        videoId[i] = header[i + 40];
                    }
                    String videoId1 = new String(videoId, "gb2312");

                    //name 128
                    byte[] videoName = new byte[128];
                    for (int i = 0; i < 128; i++) {
                        videoName[i] = header[i + 88];
                    }
                    String videoName1 = new String(videoName, "gb2312");

                    //DeviceType  16
                    byte[] videoDeviceType = new byte[16];
                    for (int i = 0; i < 16; i++) {
                        videoDeviceType[i] = header[i + 216];
                    }
                    String deviceType1 = new String(videoDeviceType, "gb2312");

                    //videoIPAddress  32
                    byte[] videoIPAddress = new byte[32];
                    for (int i = 0; i < 16; i++) {
                        videoIPAddress[i] = header[i + 232];
                    }
                    String videoIp1 = new String(videoIPAddress, "gb2312");

                    //port 4
                    int sentryId = ByteUtils.bytesToInt(header, 264);
                    System.out.println(sentryId);

                    //Channel  128
                    byte[] videoChannel = new byte[128];
                    for (int i = 0; i < 16; i++) {
                        videoChannel[i] = header[i + 268];
                    }
                    String channel1 = new String(videoChannel, "gb2312");

                    //Username  32
                    byte[] videoUsername = new byte[32];
                    for (int i = 0; i < 32; i++) {
                        videoUsername[i] = header[i + 396];
                    }
                    String username1 = new String(videoUsername, "gb2312");

                    //Password  32
                    byte[] videoPassword = new byte[32];
                    for (int i = 0; i < 32; i++) {
                        videoPassword[i] = header[i + 428];
                    }
                    String videoPass1 = new String(videoPassword, "gb2312");

                    //alarmType 32
                    byte[] videoAlarmType = new byte[32];
                    for (int i = 0; i < 32; i++) {
                        videoAlarmType[i] = header[i + 460];
                    }
                    String alarmType1 = new String(videoAlarmType, "gb2312");

                    VideoBen videoBen = new VideoBen(videoFlage1,videoId1,videoName1,deviceType1,videoIp1,sentryId+"",channel1,username1,videoPass1);
                    AlarmBen alarmBen = new AlarmBen(sender1,videoBen,alarmType1,"");

                    if(listern != null){
                        listern.getListern(alarmBen,"success");
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    listern.getListern(null,"fail");
                } finally {
                    in.close();
                    if (socket != null) {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (Exception e) {
            Logutils.e("接收报警socket异常:"+e.getMessage());
        }
    }


    public interface GetAlarmFromServerListern {
        public void getListern(AlarmBen alarmBen, String flage);
    }
}
