package com.zhketech.sipclient.ui;

import org.sipdroid.sipua.UserAgent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zhketech.sipclient.R;

public class Sipdroid extends Activity {

	public static final boolean release = true;
	public static final boolean market = false;
	public static final int FIRST_MENU_ID = Menu.FIRST;
	public static final int CONFIGURE_MENU_ITEM = FIRST_MENU_ID + 1;
	public static final int ABOUT_MENU_ITEM = FIRST_MENU_ID + 2;
	public static final int EXIT_MENU_ITEM = FIRST_MENU_ID + 3;

	private static AlertDialog m_AlertDlg;
	// AutoCompleteTextView sip_uri_box, sip_uri_box2;

	Button register, makecall;

	boolean needRestartEngine = false;
	PhoneStatus mPhoneStatus = null;

	private TextView tv;

	@Override
	public void onStart() {
		super.onStart();
		Receiver.engine(this).registerMore();

	}

	@Override
	protected void onDestroy() {
		if (needRestartEngine) {
			Receiver.engine(this).halt();
			Receiver.engine(this).UpdateLines();
			Receiver.engine(this).StartEngine();
		}
		this.unregisterReceiver(mPhoneStatus);
		super.onDestroy();
	}

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.sipdroid);
		Receiver.mContext = this;
		Sipdroid.on(this, true);
		register = (Button) this.findViewById(R.id.register);
		makecall = (Button) this.findViewById(R.id.makecall);
		tv = (TextView) this.findViewById(R.id.tv);

		mPhoneStatus = new PhoneStatus();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("call_status");
		this.registerReceiver(mPhoneStatus, intentFilter);

		makecall.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				new Thread(new Runnable() {
					@Override
					public void run() {
						// Receiver.engine(this).call(target, true)

						Receiver.engine(Sipdroid.this).call("2@192.168.0.16",
								true);

					}
				}).start();
			}
		});
		register.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

//
//				AlertDialog.Builder builder = new AlertDialog.Builder(Sipdroid.this);
//				final EditText editText = new EditText(Sipdroid.this);
//
//				builder.setTitle("注册").setView(editText).setPositiveButton("sure", new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//
//						String url = editText.getText().toString().trim();
//
//
//					}
//				}).create().show();

				new Thread(new Runnable() {
					@Override
					public void run() {

						PreferenceManager
								.getDefaultSharedPreferences(Receiver.mContext)
								.edit()
								.putString(Settings.PREF_USERNAME, "5")
								.putString(Settings.PREF_PASSWORD, "5")
								.putString(Settings.PREF_SERVER, "192.168.0.16")
								.putString(Settings.PREF_DOMAIN, "")
								.putString(Settings.PREF_PORT, "5060")
								.putString(Settings.PREF_PROTOCOL, "UDP")
								.putBoolean(Settings.PREF_CALLBACK, true)
								.putString(Settings.PREF_POSURL, "")
								.putBoolean(Settings.PREF_ON, true)
								.putBoolean(Settings.PREF_3G, true)
								.putBoolean(Settings.PREF_WLAN, true)
								.putBoolean(Settings.PREF_EDGE, true)
								.putString(Settings.PREF_FROMUSER, "").commit();

						// ע���˺�
						Receiver.engine(Sipdroid.this).register();
						Receiver.engine(Sipdroid.this).halt();
						Receiver.engine(Sipdroid.this).StartEngine();

						needRestartEngine = true;

					}
				}).start();

			}
		});

		on(this, true);

		final Context mContext = this;

		Button settingsButton = (Button) findViewById(R.id.settings_button);
		settingsButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Intent myIntent = new Intent(mContext,
						com.zhketech.sipclient.ui.Settings.class);
				startActivity(myIntent);
			}
		});

	}



	public static boolean on(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context)
				.getBoolean(Settings.PREF_ON, Settings.DEFAULT_ON);
	}

	public static void on(Context context, boolean on) {
		Editor edit = PreferenceManager.getDefaultSharedPreferences(context)
				.edit();
		edit.putBoolean(Settings.PREF_ON, on);
		edit.commit();
		if (on)
			Receiver.engine(context).isRegistered();
	}

	@Override
	public void onResume() {
		super.onResume();

		switch (Receiver.call_state) {
			case UserAgent.UA_STATE_INCOMING_CALL:
				showToast("UA_STATE_INCOMING_CALL");

				break;

			case UserAgent.UA_STATE_IDLE:
				showToast("UA_STATE_IDLE");

				break;

			case UserAgent.UA_STATE_OUTGOING_CALL:
				showToast("UA_STATE_OUTGOING_CALL");

				break;

			case UserAgent.UA_STATE_INCALL:
				showToast("UA_STATE_INCALL");

				break;
			case UserAgent.UA_STATE_HOLD:
				showToast("UA_STATE_HOLD");

				break;

			default:
				break;
		}

	}

	public void showToast(String str){

		Toast.makeText(Sipdroid.this, str, 0).show();
	}

	public void receivecall(View view){

		new Thread(new Runnable() {

			@Override
			public void run() {

				Receiver.engine(Receiver.mContext).answercall();

			}
		}).start();

	}

	public void hangup(View view){

		new Thread(new Runnable() {

			@Override
			public void run() {
				Receiver.engine(Receiver.mContext).rejectcall();
			}
		}).start();
	}




	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);

		MenuItem m = menu.add(0, ABOUT_MENU_ITEM, 0, R.string.menu_about);
		m.setIcon(android.R.drawable.ic_menu_info_details);
		m = menu.add(0, EXIT_MENU_ITEM, 0, R.string.menu_exit);
		m.setIcon(android.R.drawable.ic_menu_close_clear_cancel);
		m = menu.add(0, CONFIGURE_MENU_ITEM, 0, R.string.menu_settings);
		m.setIcon(android.R.drawable.ic_menu_preferences);

		return result;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean result = super.onOptionsItemSelected(item);
		Intent intent = null;

		switch (item.getItemId()) {
			case ABOUT_MENU_ITEM:
				break;

			case EXIT_MENU_ITEM:
				on(this, false);
				Receiver.pos(true);
				Receiver.engine(this).halt();
				Receiver.mSipdroidEngine = null;
				Receiver.reRegister(0);
				stopService(new Intent(this, RegisterService.class));
				finish();
				break;

			case CONFIGURE_MENU_ITEM: {
				try {
					intent = new Intent(this,
							com.zhketech.sipclient.ui.Settings.class);
					startActivity(intent);
				} catch (ActivityNotFoundException e) {
				}
			}
			break;
		}

		return result;
	}


	class PhoneStatus extends BroadcastReceiver{

		@Override
		public void onReceive(Context arg0, Intent arg1) {

			String status = arg1.getStringExtra("msg");
			if(!TextUtils.isEmpty(status)&&status!=null){

				tv.setText("msg："+status);




			}


		}}

}
