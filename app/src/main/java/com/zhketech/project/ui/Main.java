package com.zhketech.project.ui;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.zhketech.project.bean.AlarmBen;
import com.zhketech.project.bean.SipBean;
import com.zhketech.project.bean.VideoBen;
import com.zhketech.project.callback.ReceiveServerMess;
import com.zhketech.project.callback.ReceiverServerAlarm;
import com.zhketech.project.callback.RequestVideoSourcesThread;
import com.zhketech.project.callback.SipRequestCallback;
import com.zhketech.project.global.Constant;
import com.zhketech.sipclient.R;
import com.zhketech.sipclient.ui.Receiver;
import com.zhketech.sipclient.ui.Sipdroid;
import com.zkketech.project.utils.ByteUtils;
import com.zkketech.project.utils.Logutils;
import com.zkketech.project.utils.SharedPreferencesUtils;
import org.sipdroid.sipua.phone.Call;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;

public class Main extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = Main.class.getName()+":";

    @BindView(R.id.main_relativeLayout)
    public RelativeLayout main_relativeLayout;

    //显示时间
    @BindView(R.id.main_incon_time)
    public TextView main_incon_time;

    //显示日期
    @BindView(R.id.main_icon_date)
    public TextView main_icon_date;

    //对讲
    @BindView(R.id.button_intercom)
    public ImageButton button_intercom;
    //电话
    @BindView(R.id.button_phone)
    public ImageButton button_phone;
    //设置
    @BindView(R.id.button_setup)
    public ImageButton button_setup;
    //视频
    @BindView(R.id.button_video)
    public ImageButton button_video;
    //报警
    @BindView(R.id.button_alarm)
    public ImageButton button_alarm;
    //申请供弹
    @BindView(R.id.button_applyforplay)
    public ImageButton button_appplyforplay;
    //震动对象
    Vibrator mVibrator = null;
    Context mContext = null;

    List<String> phoneInforList = null;//存放设置状态
    List<VideoBen> listResources = null;
    List<SipBean> sipListResources = null;
    PhoneCallStatus mPhoneCallStatus = null;



    String[] permissions = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CAMERA,
            Manifest.permission.USE_SIP,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.WRITE_CALL_LOG,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.RECORD_AUDIO
    };
    List<String> mPermissionList = new ArrayList<>();


    /**
     * 接收子线程发来的消息
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 1:
                    final String mes = (String) msg.obj;
                    Logutils.i("sms：" + mes);
                    initAlerdialog(mes, null);
                    break;
                case 2:
                    AlarmBen alarmBen = (AlarmBen) msg.obj;
                    Logutils.i("alarm:" + alarmBen.toString());
                    if (alarmBen != null) initAlerdialog("", alarmBen);
                    else Logutils.i("数据失败");
                    break;
                case 3:
                    long time = System.currentTimeMillis();
                    Date date = new Date(time);
                    SimpleDateFormat timeD = new SimpleDateFormat("HH:mm:ss");
                    main_incon_time.setText(timeD.format(date));
                    SimpleDateFormat dateD = new SimpleDateFormat("MM月dd日 EEE");
                    main_icon_date.setText(dateD.format(date));
                    break;
                case 4:
                    //获取视频资源
                    List<VideoBen> mList = (List<VideoBen>) msg.obj;
                    if (mList.size() > 0) {
                        listResources = mList;
                    }
                    Gson gson = new Gson();
                    String json = gson.toJson(listResources);
                    Logutils.i("video:" + json);
                    if (json != null && !TextUtils.isEmpty(json))
                        SharedPreferencesUtils.putObject(mContext, "result", json);
                    break;

                case 5:
                    //获取sip资源列表
                    List<SipBean> sipBeansList = (List<SipBean>) msg.obj;
                    if (sipBeansList != null && sipBeansList.size() > 0){
                        Logutils.i(TAG+""+sipBeansList.toString());
                    }


                    break;
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logutils.i(TAG+":"+"onCreate");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        hideAll();
        setContentView(R.layout.activity_main2);
        ButterKnife.bind(this);
        mContext = this;
        initView();
        initListern();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermission();
        } else {
            delayEntryPage();
        }

    }

    private void initListern() {
        mPhoneCallStatus = new PhoneCallStatus();
        IntentFilter intentFilter = new IntentFilter("com.zhketech.sipphone");
        this.registerReceiver(mPhoneCallStatus, intentFilter);
        Receiver.engine(this).registerMore();
        phoneInforList = new ArrayList<String>();
    }

    /**
     * 接收到短消息时弹出dialog
     *
     * @param ms 短消息
     */
    public void initAlerdialog(final String ms, final AlarmBen alarmBen) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                if (ms != null && !TextUtils.isEmpty(ms) && alarmBen == null)
                    builder.setTitle("短消息：").setMessage(ms).setNegativeButton("Cancal", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                else if (alarmBen != null && TextUtils.isEmpty(ms))
                    builder.setTitle("报警报文:").setMessage(alarmBen.toString()).setNegativeButton("Cancal", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                ;
                builder.create().show();
            }
        });
    }

    /**
     * 初始化控件
     */
    private void initView() {
        button_intercom.setOnClickListener(this);
        button_phone.setOnClickListener(this);
        button_setup.setOnClickListener(this);
        button_video.setOnClickListener(this);
        button_alarm.setOnClickListener(this);
        button_appplyforplay.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        hideAll();
    }

    @Override
    protected void onStart() {
        super.onStart();
        /**
         * 启动线程接收服务器发送过来的数据
         */
        ReceiveServerMess receiveServerMess = new ReceiveServerMess(new ReceiveServerMess.GetMessageListern() {
            @Override
            public void getMess(String ms) {
                Message smsMess = new Message();
                smsMess.what = 1;
                if (!TextUtils.isEmpty(ms) && !ms.equals("fail"))
                    smsMess.obj = ms;
                handler.sendMessage(smsMess);
            }
        });
        new Thread(receiveServerMess).start();

        /**
         * 回调获取服务器端发来的报警报文
         */
        ReceiverServerAlarm receiverServerAlarm = new ReceiverServerAlarm(new ReceiverServerAlarm.GetAlarmFromServerListern() {
            @Override
            public void getListern(AlarmBen alarmBen, String flage) {
                Message alarmMess = new Message();
                alarmMess.what = 2;
                if (flage.equals("success"))
                    alarmMess.obj = alarmBen;
                else
                    alarmMess.obj = null;
                handler.sendMessage(alarmMess);
            }
        });
        new Thread(receiverServerAlarm).start();

        /**
         * 获取Video资源列表
         */
        RequestVideoSourcesThread requestVideoSourcesThread = new RequestVideoSourcesThread(new RequestVideoSourcesThread.GetDataListener() {
            @Override
            public void getResult(List<VideoBen> devices) {
                Message message = new Message();
                message.what = 4;
                message.obj = devices;
                handler.sendMessage(message);
            }
        });
        new Thread(requestVideoSourcesThread).start();

        /**
         * 获取Sip资源列表
         */
        SipRequestCallback sipRequestCallback = new SipRequestCallback(new SipRequestCallback.SipListern() {
            @Override
            public void getDataListern(List<SipBean> mList) {
                Message message = new Message();
                message.what = 5;
                message.obj = mList;
                handler.sendMessage(message);
            }
        });
        new Thread(sipRequestCallback).start();

        //sstartService(new Intent(this, SendheartService.class));
    }

    /**
     * 隐藏状态栏和actionBar
     */
    protected void hideAll() {
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN | View.INVISIBLE;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        hideAll();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
      //  this.stopService(new Intent(this, SendheartService.class));
        this.unregisterReceiver(mPhoneCallStatus);
        Receiver.engine(this).halt();
        Receiver.engine(this).UpdateLines();
        Receiver.engine(this).StartEngine();
        mVibrator = null;
        phoneInforList.clear();
        phoneInforList = null;
        System.gc();
    }

    @Override
    protected void onPause() {
        super.onPause();
        hideAll();
    }

    @Override
    public void onClick(View view) {

        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.button_intercom:
                intent.setClass(mContext, Sipdroid.class);
                mContext.startActivity(intent);
                mVibrator.vibrate(200);
                break;
//            case R.id.button_phone:
//
//                mVibrator.vibrate(200);
//                break;
//            case R.id.button_setup:
//                intent.setClass(mContext, SettingActivity.class);
//                mContext.startActivity(intent);
//                mVibrator.vibrate(200);
//                break;
            case R.id.button_video:
                //视频
                intent.setClass(mContext, MultiScreenActivity.class);
                mContext.startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_in_left);
                mVibrator.vibrate(200);
                break;
//            case R.id.button_alarm:
//                //报警
//                intent.setClass(mContext, AlarmActivity.class);
//                mContext.startActivity(intent);
//                mVibrator.vibrate(200);
//                break;
            case R.id.button_applyforplay:
                //申请供弹
                mVibrator.vibrate(200);
                requestBombosMethod();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, "申请已发出!", Toast.LENGTH_SHORT).show();
                    }
                }, 2 * 1000);

                break;
        }
    }

    /**
     * 申请开箱
     */
    private void requestBombosMethod() {

        new Thread(new Runnable() {
            @Override
            public void run() {

                Socket socket = null;
                byte[] request = new byte[20];
                //数据头
                String flage = "ReqB";
                byte[] flage1 = flage.getBytes();
                System.arraycopy(flage1, 0, request, 0, flage1.length);
                //版本号
                request[4] = 0;
                request[5] = 0;
                request[6] = 0;
                request[7] = 1;

                //请求的action
                request[8] = 0;
                request[9] = 0;
                request[10] = 0;
                request[11] = 0;
                System.out.println(Arrays.toString(request));

                //ulReserved1
                int ulReserved1 = (int) (Math.random() * (9999 - 1000 + 1)) + 1000;
                System.out.println(ulReserved1);
                byte[] flage2 = ByteUtils.toByteArray(ulReserved1);
                System.out.println(Arrays.toString(flage2));
                System.arraycopy(flage2, 0, request, 12, flage2.length);

                //ulReserved2
                int ulReserved2 = (int) (Math.random() * (9999 - 1000 + 1)) + 1000;
                System.out.println(ulReserved2);
                byte[] flage3 = ByteUtils.toByteArray(ulReserved2);
                System.out.println(Arrays.toString(flage3));
                System.arraycopy(flage3, 0, request, 16, flage3.length);
                //打印请求数据主体
                System.out.println(Arrays.toString(request));
                try {
                    socket = new Socket(Constant.IP_SERVER, 20000);
                    OutputStream os = socket.getOutputStream();
                    os.write(request);
                    os.flush();
                } catch (Exception e) {

                } finally {
                    if (socket != null) {
                        try {
                            socket.close();
                            socket = null;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();

    }

    /**
     * 显示时间的线程
     */
    class TimeThread extends Thread {
        @Override
        public void run() {
            super.run();
            do {
                try {
                    Thread.sleep(1000);
                    Message msg = new Message();
                    msg.what = 3;
                    handler.sendMessage(msg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (true);
        }
    }



    /**
     * 来电状态处理
     */
    class PhoneCallStatus extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            String status = intent.getStringExtra("msg");
            String caller = intent.getStringExtra("caller");
            if (status != null && !TextUtils.isEmpty(status)) {
                switch (status) {
                    case "UA_STATE_INCOMING_CALL":

                        initDialog(caller);
                        break;
                    case "UA_STATE_INCALL":


                        break;
                    case "UA_STATE_IDLE":
                        if (Receiver.ccCall != null) {
                            Receiver.stopRingtone();
                            Receiver.ccCall.setState(Call.State.DISCONNECTED);
                        }

                        break;
                }


            }
        }
    }

    /**
     * 显示个提示框
     * @param caller
     */
    private synchronized void initDialog(String caller) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("来电话").setMessage(caller).setPositiveButton("Answer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                Intent intent1 = new Intent();
                intent1.putExtra("flage", "flage");
                intent1.setClass(mContext, Sipdroid.class);
                mContext.startActivity(intent1);
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Receiver.engine(mContext).rejectcall();
                        Receiver.ccCall.setState(Call.State.DISCONNECTED);
                    }
                }).start();
            }
        }).create().show();
    }


    /**
     * 权限申请
     */
    private void checkPermission() {
        mPermissionList.clear();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(mContext, permission) != PackageManager.PERMISSION_GRANTED) {
                mPermissionList.add(permission);
            }
        } /** * 判断存储委授予权限的集合是否为空 */
        if (!mPermissionList.isEmpty()) {
            String[] permissions = mPermissionList.toArray(new String[mPermissionList.size()]);//将List转为数组
            ActivityCompat.requestPermissions(Main.this, permissions, 1);
        } else {
            //未授予的权限为空，表示都授予了 // 后续操作...
            delayEntryPage();
        }

    }


    boolean mShowRequestPermission = true;//用户是否禁止权限

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        //判断是否勾选禁止后不再询问
                        boolean showRequestPermission = ActivityCompat.shouldShowRequestPermissionRationale(Main.this, permissions[i]);
                        if (showRequestPermission) {//
                            checkPermission();//重新申请权限
                            return;
                        } else {
                            mShowRequestPermission = false;//已经禁止
                            String permisson = permissions[i];
                            Log.i("TAG", "permisson:"+permisson);
                        }
                    }
                }
                delayEntryPage();
                break;
            default:
                break;
        }
    }

    //加载 数据
    private void delayEntryPage() {
        Log.i("TAG", "do something");
        TimeThread timeThread = new TimeThread();
        new Thread(timeThread).start();
        listResources = new ArrayList<>();
        sipListResources = new ArrayList<>();
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }
}
