package com.zhketech.project.callback;

import com.zhketech.project.global.Constant;
import com.zkketech.project.utils.ByteUtils;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;

/**
 * Created by Root on 2018/4/20.
 */

public class SendHearToServer  extends  Thread {

    List<String> mList;
    byte[] timeByte;
    public SendHearToServer(List<String> mList,byte[] timeByte){
        this.mList = mList;
        this.timeByte = timeByte;
    }

    @Override
    public void run() {
        super.run();


        byte[] requestBytes = new byte[96];
        byte[] flag = "ZDHB".getBytes();// 数据标识头字节
        byte[] id = new byte[48];
        byte[] idKey = mList.get(0).getBytes();//手机唯一 的标识字节
        System.arraycopy(flag,0,requestBytes,0,4);
        System.arraycopy(idKey,0,id,0,idKey.length);
        System.arraycopy(id,0,requestBytes,4,48);

        byte[] stamp = new byte[8];
        byte[] timeT =timeByte;
        System.arraycopy(timeT,0,stamp,0,timeT.length);
        System.arraycopy(stamp,0,requestBytes,52,stamp.length);


        // 纬度
        double lat = 89.362;
        byte[] latB = ByteUtils.getBytes(lat);

        System.arraycopy(latB, 0, requestBytes, 64, 8);
        // 经度
        double lon = 136.301;
        byte[] lonB = ByteUtils.getBytes(lon);
        // System.out.println("经度:" + Arrays.toString(lonB));
        // System.out.println(getDouble(lonB));
        System.arraycopy(lonB, 0, requestBytes, 72, 8);

        //设备状态
        //剩余电量
        byte[] power = new byte[1];
        power[0] = 55;
        System.arraycopy(power, 0, requestBytes, 80, 1);
        //内存使用
        byte[] mem = new byte[1];
        mem[0] = 60;
        System.arraycopy(mem, 0, requestBytes, 81, 1);
        //cpu使用
        byte[] cpu = new byte[1];
        cpu[0] = 40;
        System.arraycopy(cpu, 0, requestBytes, 82, 1);
        //信号强度
        byte[] signal = new byte[1];
        signal[0] = 88;
        System.arraycopy(signal, 0, requestBytes, 83, 1);
        //弹箱连接状态
        byte[] bluetooth = new byte[1];
        bluetooth[0] = 0;
        System.arraycopy(bluetooth, 0, requestBytes, 84, 1);
        //保留
        byte[] save = new byte[11];
        System.arraycopy(save, 0, requestBytes, 85, 11);

        //把第56到59位的四个字节反转
         byte[] temp = new byte[4];
        System.arraycopy(requestBytes, 56, temp, 0, 4);
        byte[] reversalByte = new byte[4];
        reversalByte[0] = temp[3];
        reversalByte[1] = temp[2];
        reversalByte[2] = temp[1];
        reversalByte[3] = temp[0];
        System.arraycopy(reversalByte, 0, requestBytes, 56, 4);

        //建立UDP请求
        DatagramSocket socketUdp = null;
        try {
            socketUdp = new DatagramSocket(2020);
            DatagramPacket datagramPacket = new DatagramPacket(requestBytes, requestBytes.length, InetAddress.getByName(Constant.IP_SERVER), 2020);
            socketUdp.send(datagramPacket);
            socketUdp.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
