package com.zhketech.sipclient.ui;
import java.io.IOException;

import android.annotation.TargetApi;
import android.hardware.Camera;
import android.os.Build;

@TargetApi(Build.VERSION_CODES.FROYO) 
public class VideoCameraNew2 {
	static void reconnect(Camera c) {
		try {
			c.reconnect();
		} catch (IOException e) {
			if (!Sipdroid.release) e.printStackTrace();
		}
	}
}
