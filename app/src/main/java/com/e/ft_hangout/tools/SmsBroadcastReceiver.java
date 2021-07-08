package com.e.ft_hangout.tools;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.e.ft_hangout.MainActivity;
import com.e.ft_hangout.SmsActivity;
import com.e.ft_hangout.models.Contact;
import com.e.ft_hangout.models.DBLocalAccess;
import com.e.ft_hangout.models.Fox;
import com.e.ft_hangout.models.SMS;

public class SmsBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = SmsBroadcastReceiver.class.getSimpleName();
    public static final String pdu_type = "pdus";


    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            // Get the SMS message.
            Bundle bundle = intent.getExtras();
            SmsMessage[] msgs;
            String strMessage = "";
            String format = bundle.getString("format");
            // Retrieve the SMS message received.
            Object[] pdus = (Object[]) bundle.get(pdu_type);
            if (pdus != null) {
                // Check the Android version.
                boolean isVersionM = (Build.VERSION.SDK_INT >=
                        Build.VERSION_CODES.M);
                // Fill the msgs array.
                msgs = new SmsMessage[pdus.length];
                for (int i = 0; i < msgs.length; i++) {
                    // Check Android version and use appropriate createFromPdu.
                    if (isVersionM) {
                        // If Android version M or newer:
                        msgs[i] =
                                SmsMessage.createFromPdu((byte[]) pdus[i], format);
                    } else {
                        // If Android version L or older:
                        msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    }
                    // Build the message to show.
                    strMessage += "SMS from " + msgs[i].getOriginatingAddress();
                    strMessage += " :" + msgs[i].getMessageBody() + "\n";
                    // Log and display the SMS message.
                    Log.d(TAG, "onReceive: " + strMessage);
                    DBLocalAccess db = new DBLocalAccess(context);
                    Fox fox = db.getOrCreateContact("phoneNumber", msgs[i].getOriginatingAddress());
                    Contact contact = fox.getContact();
                    String id = contact.getId();
                    if (!fox.getKnown())
                        MainActivity.refreshList(contact);
                    SMS sms = new SMS(msgs[i].getMessageBody(), String.valueOf(System.currentTimeMillis()), "0", id, "1");
                    db = new DBLocalAccess(context);
                    db.AddSms(sms);
                    SmsActivity.refreshSMSList(sms);
                    Toast.makeText(context, strMessage, Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
