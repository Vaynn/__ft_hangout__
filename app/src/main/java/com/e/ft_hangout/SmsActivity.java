package com.e.ft_hangout;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.e.ft_hangout.adapter.ContactAdapter;
import com.e.ft_hangout.adapter.SMSAdapter;
import com.e.ft_hangout.models.Contact;
import com.e.ft_hangout.models.DBLocalAccess;
import com.e.ft_hangout.models.SMS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SmsActivity  extends AppCompatActivity {

    private static final int PERMISSION_SEND_SMS = 123;
    public static SMSAdapter SMSArrayAdapter;
    public static List<SMS> sms_arraylist;
    public static ListView smsListView;
    static boolean active = false;
    ImageButton sendSMSBtn;
    EditText smsMessageET;
    Activity activity;
    String name;
    String firstname;
    String phone;
    String email;
    String pics;
    String identity;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        MainActivity.setPref(getResources().getString(R.string.send_message_bar), this, actionBar);


        name = getIntent().getStringExtra("NAME");
        id = getIntent().getStringExtra("ID");
        firstname = getIntent().getStringExtra("FIRSTNAME");
        identity = firstname + " " + name;
        phone = getIntent().getStringExtra("PHONE");
        email = getIntent().getStringExtra("EMAIL");
        pics = getIntent().getStringExtra("PICS");
        activity = this;

        sendSMSBtn = findViewById(R.id.buttonSend);
        smsMessageET = findViewById(R.id.editMessage);
        sendSMSBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                requestSmsPermission();
            }
        });

        sms_arraylist = new ArrayList<SMS>();
        DBLocalAccess db = new DBLocalAccess(this);
        sms_arraylist = db.getConversation("1", id);
        Collections.sort(sms_arraylist, Comparator.comparing(SMS::getReceive));

        //get list view
        smsListView = findViewById(R.id.sms_list);
        SMSArrayAdapter = new SMSAdapter(this, sms_arraylist);
        smsListView.setAdapter(SMSArrayAdapter);
        smsListView.setSelection(smsListView.getAdapter().getCount()-1);
    }

    @Override
    public void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        active = false;
    }

    private void requestSmsPermission() {
        // check permission is given
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            // request permission (see result in onRequestPermissionsResult() method)
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.SEND_SMS},
                    PERMISSION_SEND_SMS);
        } else {
            // permission already granted run sms send
            sendSMS();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_SEND_SMS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    sendSMS();
                } else {
                    Toast.makeText(this,
                            getResources().getString(R.string.sms_permission_toast),
                            Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    protected void sendSMS() {
        String smsMessage = smsMessageET.getText().toString();
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phone, null, smsMessage, null, null);
            DBLocalAccess db = new DBLocalAccess(activity);
            SMS send = new SMS(smsMessage, String.valueOf(System.currentTimeMillis()), "0", "1", id);
            db.AddSms(send);
            refreshSMSList(send);
            smsMessageET.setText("");
            Toast.makeText(getApplicationContext(), "SMS sent.",
                    Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),
                    String.valueOf(R.string.sms_error_sending),
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    public static void refreshSMSList(SMS sms){
        if (active) {
            sms_arraylist.add(sms);
            Collections.sort(sms_arraylist, Comparator.comparing(SMS::getReceive));
            SMSArrayAdapter.notifyDataSetChanged();
            smsListView.setSelection(smsListView.getAdapter().getCount() - 1);
        }
    }
}