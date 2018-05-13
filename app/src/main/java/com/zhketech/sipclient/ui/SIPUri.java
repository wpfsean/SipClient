package com.zhketech.sipclient.ui;

import org.sipdroid.sipua.SipdroidEngine;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.util.Log;
import android.view.Window;

import com.zhketech.sipclient.R;

public class SIPUri extends Activity {

	void call(String target) {
		if (!Receiver.engine(this).call(target,true)) {
			new AlertDialog.Builder(this)
			.setMessage(R.string.notfast)
			.setTitle(R.string.app_name)
			.setIcon(R.drawable.icon22)
			.setCancelable(true)
			.setOnCancelListener(new OnCancelListener() {
				public void onCancel(DialogInterface dialog) {
					finish();
				}
			})
			.show();
		} else
			finish();
	}
	
	static int item;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Boolean ask;
		
		super.onCreate(savedInstanceState);
    	if (Receiver.mContext == null) Receiver.mContext = this;

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		Sipdroid.on(this,true);
		Uri uri = getIntent().getData();
		String target;
		if (uri.getHost() != null && uri.getHost().equals(ContactsContract.AUTHORITY)) {
			target = Caller.getNumber(this,uri,Phone.CONTACT_ID);
			ask = true;
		} else {
			ask = PreferenceManager.getDefaultSharedPreferences(this).getString(Settings.PREF_PREF, Settings.DEFAULT_PREF).equals(Settings.VAL_PREF_ASK);
			if (uri.getScheme().equals("sip") || uri.getScheme().equals("sipdroid")) {
				target = uri.getSchemeSpecificPart();
			} else {
				if (uri.getAuthority().equals("aim") ||
						uri.getAuthority().equals("yahoo") ||
						uri.getAuthority().equals("icq") ||
						uri.getAuthority().equals("gtalk") ||
						uri.getAuthority().equals("msn"))
					target = uri.getLastPathSegment().replaceAll("@","_at_") + "@" + uri.getAuthority() + ".gtalk2voip.com";
				else if (uri.getAuthority().equals("skype"))
					target = uri.getLastPathSegment() + "@" + uri.getAuthority();
				else
					target = uri.getLastPathSegment();
			}
		}
		if (!Sipdroid.release) Log.v("SIPUri", "sip uri: " + target);
		if (!target.contains("@") && ask) {
			final String items[] = target.split("&");
			item = 0;
			AlertDialog.Builder alert = new AlertDialog.Builder(this)
				.setIcon(R.drawable.icon22)
				.setTitle(R.string.menu_call)
				.setOnCancelListener(new OnCancelListener() {
					public void onCancel(DialogInterface dialog) {
						finish();
					}
				}).setNegativeButton(R.string.pstn_name,new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                	PSTN.callPSTN("sip:"+items[item]);
                	finish();
                }
				}).setSingleChoiceItems(items,0,new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                	item = whichButton;
                }
				});

			for (int p = 0; p < SipdroidEngine.LINES; p++)
				if (Receiver.isFast(p) || (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(Settings.PREF_CALLBACK, Settings.DEFAULT_CALLBACK) &&
						PreferenceManager.getDefaultSharedPreferences(this).getString(Settings.PREF_POSURL, Settings.DEFAULT_POSURL).length() > 0)) {
					alert.setPositiveButton(R.string.app_name,new DialogInterface.OnClickListener() {
		                public void onClick(DialogInterface dialog, int whichButton) {
		            			call(items[item]);
		                }
		            });
					break;
				}
            alert.show();
		} else
			call(target); 
	}
	
	    @Override
	    public void onPause() {
	        super.onPause();
	        finish();
	    }
	 
}
