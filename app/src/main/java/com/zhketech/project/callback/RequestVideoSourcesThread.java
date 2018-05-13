package com.zhketech.project.callback;

import android.util.Log;


import com.zhketech.project.bean.VideoBen;
import com.zhketech.project.global.Constant;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 *  登录后使用回调的方法获取 视频资源的list列表
 *
 *
 * Created by Root on 2018/4/5.
 */

public class RequestVideoSourcesThread extends Thread {
    List<VideoBen> videoSourceInfoList;//所有返回视频源列表集合
    Socket socket = null;
    private InputStream is = null;//读取输入流
    private ByteArrayOutputStream bos = null;

    GetDataListener listener;

    public RequestVideoSourcesThread(GetDataListener listener) {
        this.listener = listener;
    }

    @Override
    public void run() {
        try {
            byte[] bys = new byte[140];
            String fl = "ZKTH";//中科腾海
            byte[] zk = fl.getBytes();
            Log.d("views", Arrays.toString(zk));
            for (int i = 0; i < zk.length; i++) {
                bys[i] = zk[i];
            }
            bys[4] = 1;
            bys[5] = 0;
            bys[6] = 0;
            bys[7] = 0;
            String name = "admin" + "/" + "pass";
            byte[] na = name.getBytes("GB2312");
            for (int i = 0; i < na.length; i++) {
                bys[i + 8] = na[i];
            }
            socket = new Socket(Constant.IP_SERVER, 2010);
            OutputStream os = socket.getOutputStream();
            os.write(bys);
            os.flush();
            is = socket.getInputStream();
            bos = new ByteArrayOutputStream();
//                    BufferedInputStream bis = new BufferedInputStream(is);
            //先读取前8个字节 计算数据总长度
            byte[] headers = new byte[8];
            int read = is.read(headers);
            //1:解析数据头
            byte[] flag = new byte[4];
            for (int i = 0; i < 4; i++) {
                flag[i] = headers[i];
            }
//                  Log.d("views","ttttttt"+currentIndex);
            //2:解析视频源数量 Int32
            byte[] videos = new byte[4];
            for (int i = 0; i < 4; i++) {
                videos[i] = headers[i + 4];
            }
            int videoCount = videos[0];

            int alldata = 424 * videoCount;//计算总数据长度
            byte[] buffer = new byte[1024];
            int nIdx = 0;
            int nTotalLen = alldata;
            int nReadLen = 0;
            //记录当前读取进度,与总数据长度进行比较循环读取
            while (nIdx < nTotalLen) {
                nReadLen = is.read(buffer);
                bos.write(buffer, 0, nReadLen);
                if (nReadLen > 0) {
                    nIdx = nIdx + nReadLen;
                } else {
                    break;
                }
            }
            byte[] result = bos.toByteArray();
            System.out.println(new String(result, 0, result.length));
            //对返回的登录数据进行解析
            int currentIndex = 0;//记录当前解析字节数组进度
            List<byte[]> videoList = new ArrayList<>();
            for (int i = 0; i < videoCount; i++) {
                byte[] oneVideo = new byte[424];
                System.arraycopy(result, currentIndex, oneVideo, 0, 424);
                currentIndex += 424;
//                        Log.d("views", "onevideo: " + new String(oneVideo, "gb2312"));
                videoList.add(oneVideo);
            }
            //所有解析的视频集合
            videoSourceInfoList = new ArrayList<>();
            //解析每个详细视频的各项信息
            for (byte[] vInfo : videoList
                    ) {
                VideoBen info = new VideoBen();
                //解析数据头标识,4字节
                byte[] videoFlag = new byte[4];
                System.arraycopy(vInfo, 0, videoFlag, 0, 4);
                info.setFlage(new String(videoFlag, "GB2312"));
                //解析唯一识别编号48字节
                byte[] id = new byte[48];
                System.arraycopy(vInfo, 4, id, 0, 48);
                info.setId(new String(id, "GB2312").trim());

                //解析视频源名称
                byte[] videoName = new byte[128];
                System.arraycopy(vInfo, 52, videoName, 0, 128);
                info.setName(new String(videoName, "GB2312").trim());
                //解析设备类型,默认为ONVIF
                byte[] deviceType = new byte[16];
                System.arraycopy(vInfo, 180, deviceType, 0, 16);
                info.setDevicetype(new String(deviceType, "GB2312").trim());

                //解析视频源设备ip地址
                byte[] ipAddress = new byte[32];
                System.arraycopy(vInfo, 196, ipAddress, 0, 32);
                info.setIp(new String(ipAddress, "GB2312").trim());
                //解析视频源设备端口
                byte[] port = new byte[4];
                System.arraycopy(vInfo, 228, port, 0, 4);
                info.setPort(port[0] + "");

                //解析视频源通道编号
                byte[] channel = new byte[128];
                System.arraycopy(vInfo, 232, channel, 0, 128);
                info.setChannel(new String(channel, "GB2312").trim());

                //解析用户名
                byte[] userName = new byte[32];
                System.arraycopy(vInfo, 360, userName, 0, 32);
                info.setUsername(new String(userName, "GB2312").trim());

                //解析口令
                byte[] passWord = new byte[32];
                System.arraycopy(vInfo, 392, passWord, 0, 32);
                info.setPassword(new String(passWord, "GB2312").trim());

                videoSourceInfoList.add(info);
            }

            if (videoSourceInfoList.size()>0)
                if (listener != null) {
                    listener.getResult(videoSourceInfoList);
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public interface GetDataListener {
        void getResult(List<VideoBen> devices);
    }

}
