package com.e.ft_hangout;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.e.ft_hangout.models.Contact;
import com.e.ft_hangout.models.DBLocalAccess;
import com.e.ft_hangout.tools.Useful;

import java.util.List;


public class ContactFileActivity extends AppCompatActivity {

    private static final int PERMISSION_CALL_PHONE = 23;
    public enum Focus {
        NONE, EMAIL_FOCUS, PHONE_FOCUS, ADDRESS_FOCUS
    }
    public static ContactFileActivity.Focus focus = ContactFileActivity.focus.NONE;

    Activity activity;
    String phone;
    String name;
    String firstname;
    String email;
    String address;
    String pics;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_file);

        name = getIntent().getStringExtra("NAME");
        id = getIntent().getStringExtra("ID");
        firstname = getIntent().getStringExtra("FIRSTNAME");
        final String identity = firstname + " " + name;
        phone = getIntent().getStringExtra("PHONE");
        email = getIntent().getStringExtra("EMAIL");
        pics = getIntent().getStringExtra("PICS");
        address = getIntent().getStringExtra("ADDRESS");
        activity = this;

        ActionBar actionBar;
        actionBar = getSupportActionBar();
        MainActivity.setPref(identity, this, actionBar);


        TextView prenom = findViewById(R.id.prenom);
        prenom.setText(identity);

        checkEmptyField(email, phone, firstname, address);

        TextView phone_number = findViewById(R.id.phone_number);
        phone_number.setText(phone);

        TextView email_address = findViewById(R.id.email_addresse);
        email_address.setText(email);

        TextView address_address =findViewById(R.id.address_address);
        address_address.setText(address);

        ImageView image = findViewById(R.id.profil_pics);
        Useful.fillImageView(pics, image);

        TextView modify = findViewById(R.id.modify_button);
        modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent modifyContact = new Intent(getApplicationContext(), AddContactForm.class);
                startIntent(modifyContact);
            }
        });

        Button message = findViewById(R.id.message);
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent contactFile = new Intent(getApplicationContext(), SmsActivity.class);
                startIntent(contactFile);
            }
        });

        Button appel = findViewById(R.id.appel);
        appel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestCallPermission();
            }
            });

        Button emailButton = findViewById(R.id.email);
        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
                startActivity(Intent.createChooser(intent, "Send Email :"));
            }
        });
        Button mapButton = findViewById(R.id.address_button);
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + address);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });
        TextView delete = findViewById(R.id.delete_contact);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBLocalAccess db = new DBLocalAccess(activity);
                int nb = db.deleteContact("id", id);
                if (nb > 0) {
                    Intent backToMain = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(backToMain);
                    finish();
                }
                else
                    Toast.makeText(getApplicationContext(),
                            String.valueOf(R.string.error_cancel_contact),
                            Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void requestCallPermission() {
        // check permission is given
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // request permission (see result in onRequestPermissionsResult() method)
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.CALL_PHONE},
                    PERMISSION_CALL_PHONE);
        } else {
            // permission already granted run sms send
            call(phone);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_CALL_PHONE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    call(phone);
                } else {
                    Toast.makeText(this,
                            String.valueOf(R.string.call_permission_toast),
                            Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    protected void call(String phone){
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        String num = "tel:" + phone;
        callIntent.setData(Uri.parse(num));
        startActivity(callIntent);
    }
    //check empty fields to know which button enable. If the contact is "Moi" all buttons are disable.
    private void checkEmptyField(String email, String phone, String firstname, String address){

        if (firstname.compareTo(String.valueOf(R.string.my_contact)) == 0){
            Button emailButton = findViewById(R.id.email);
            emailButton.setVisibility(View.GONE);
            Button phoneButton = findViewById(R.id.appel);
            phoneButton.setVisibility(View.GONE);
            Button messageButton = findViewById(R.id.message);
            messageButton.setVisibility(View.GONE);
            TextView delete = findViewById(R.id.delete_contact);
            delete.setVisibility(View.GONE);
        }
        if (email == null || email.isEmpty()){
            Button emailButton = findViewById(R.id.email);
            emailButton.setEnabled(false);
            Drawable[] drawables = emailButton.getCompoundDrawables();
            drawables[1].setTint(getColor(R.color.grey));
            emailButton.setTextColor(getColor(R.color.grey));
            TextView emailTitle = findViewById(R.id.email_title);
            emailTitle.setTextColor(getColor(R.color.grey));
            ImageView add_email = findViewById(R.id.add_email);
            add_email.setVisibility(View.VISIBLE);
            add_email.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent modifyContact = new Intent(getApplicationContext(), AddContactForm.class);
                    focus = focus.EMAIL_FOCUS;
                    startIntent(modifyContact);
                }
            });
        }
        if (address == null || address.isEmpty()){
            Button addressButton = findViewById(R.id.address_button);
            addressButton.setEnabled(false);
            Drawable[] drawables = addressButton.getCompoundDrawables();
            drawables[1].setTint(getColor(R.color.grey));
            addressButton.setTextColor(getColor(R.color.grey));
            TextView addressTitle = findViewById(R.id.address_title);
            addressTitle.setTextColor(getColor(R.color.grey));
            ImageView add_address = findViewById(R.id.add_address);
            add_address.setVisibility(View.VISIBLE);
            add_address.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent modifyContact = new Intent(getApplicationContext(), AddContactForm.class);
                    focus = focus.ADDRESS_FOCUS;
                    startIntent(modifyContact);
                }
            });
        }
        if (phone == null || phone.isEmpty()){
            Button phoneButton = findViewById(R.id.appel);
            Drawable[] drawables = phoneButton.getCompoundDrawables();
            drawables[1].setTint(getColor(R.color.grey));
            phoneButton.setTextColor(getColor(R.color.grey));
            phoneButton.setEnabled(false);
            Button messageButton = findViewById(R.id.message);
            drawables = messageButton.getCompoundDrawables();
            drawables[1].setTint(getColor(R.color.grey));
            messageButton.setTextColor(getColor(R.color.grey));
            TextView phoneTitle = findViewById(R.id.phone_title);
            phoneTitle.setTextColor(getColor(R.color.grey));
            ImageView add_number = findViewById(R.id.add_number);
            add_number.setVisibility(View.VISIBLE);
            add_number.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent modifyContact = new Intent(getApplicationContext(), AddContactForm.class);
                    focus = focus.PHONE_FOCUS;
                    startIntent(modifyContact);
                }
            });

        }
    }

    public void startIntent(Intent intent){
        intent.putExtra("NAME", name);
        intent.putExtra("ID", id);
        intent.putExtra("FIRSTNAME", firstname);
        intent.putExtra("PICS", pics);
        intent.putExtra("PHONE", phone);
        intent.putExtra("EMAIL", email);
        intent.putExtra("ADDRESS", address);
        startActivity(intent);
    }

}