package com.e.ft_hangout;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.e.ft_hangout.adapter.ContactAdapter;
import com.e.ft_hangout.models.Contact;
import com.e.ft_hangout.models.DBLocalAccess;
import com.e.ft_hangout.tools.Useful;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private ImageButton addContact;
    private int MY_PERMISSIONS_REQUEST_SMS_RECEIVE = 10;
    public static ContactAdapter contactArrayAdapter;
    public static List<Contact> contacts_list;
    public enum LastScreen {
        NONE, MAIN_SCREEN
    }
    public static LastScreen lastScreen = LastScreen.NONE;
    public static String lastBack;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        Window window = getWindow();
        SharedPreferences sharedpreferences;
        sharedpreferences = getSharedPreferences("SETTINGS", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        switch (item.getItemId()) {
            case R.id.autumn:
                ColorDrawable drawable = new ColorDrawable(Color.parseColor("#F44336"));
                actionBar.setBackgroundDrawable(drawable);
                window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.autumn));
                window.setNavigationBarColor(ContextCompat.getColor(getApplicationContext(), R.color.autumn));
                editor.putString("secondaryColor", "#F44336");
                editor.putInt("primaryColor", R.color.autumn);
                editor.commit();
                return true;
            case R.id.winter:
                drawable = new ColorDrawable(Color.parseColor("#E8EAF6"));
                actionBar.setBackgroundDrawable(drawable);
                window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.winter));
                window.setNavigationBarColor(ContextCompat.getColor(getApplicationContext(), R.color.winter));
                editor.putString("secondaryColor", "#E8EAF6");
                editor.putInt("primaryColor", R.color.winter);
                editor.commit();
                return true;
            case R.id.summer:
                drawable = new ColorDrawable(Color.parseColor("#FFEE58"));
                actionBar.setBackgroundDrawable(drawable);
                window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.summer));
                window.setNavigationBarColor(ContextCompat.getColor(getApplicationContext(), R.color.summer));
                editor.putString("secondaryColor", "#FFEE58");
                editor.putInt("primaryColor", R.color.summer);
                editor.commit();
                return true;
            case R.id.spring:
                drawable = new ColorDrawable(Color.parseColor("#CCFF90"));
                actionBar.setBackgroundDrawable(drawable);
                window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.spring));
                window.setNavigationBarColor(ContextCompat.getColor(getApplicationContext(), R.color.spring));
                editor.putString("secondaryColor", "#CCFF90");
                editor.putInt("primaryColor", R.color.spring);
                editor.commit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            setPref(getResources().getString(R.string.main_title), this, actionBar);
        }
        if (!isOwnContact(this)){
            createOwnContact(this);
        }
        ActivityCompat.requestPermissions(this,new String[]{ Manifest.permission.RECEIVE_SMS},
                MY_PERMISSIONS_REQUEST_SMS_RECEIVE);
        //addContact button link to ContactFormActivity
        this.addContact = findViewById(R.id.add_button);
        addContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent contactForm = new Intent(getApplicationContext(), AddContactForm.class);
                startActivity(contactForm);

            }
        });
        // contact List
        contacts_list = new ArrayList<Contact>();
        DBLocalAccess db = new DBLocalAccess(this);
        contacts_list = db.getAllContacts();
        Collections.sort(contacts_list, Comparator.comparing(Contact::getFirstname));

        //get list view
        ListView contactListView = findViewById(R.id.contact_list_view);
        contactArrayAdapter = new ContactAdapter(this, contacts_list);
        contactListView.setAdapter(contactArrayAdapter);
        contactListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent contactFile = new Intent(getApplicationContext(), ContactFileActivity.class);
                Contact currentContact = (Contact) parent.getAdapter().getItem(position);
                contactFile.putExtra("NAME", currentContact.getName());
                contactFile.putExtra("ID", currentContact.getId());
                contactFile.putExtra("FIRSTNAME", currentContact.getFirstname());
                contactFile.putExtra("PICS", currentContact.getPics());
                contactFile.putExtra("PHONE", currentContact.getPhoneNumber());
                contactFile.putExtra("EMAIL", currentContact.getEmail());
                contactFile.putExtra("ADDRESS", currentContact.getPhoneRing());
                startActivity(contactFile);

            }

        });

    }


    public static void setPref(String title, Activity activity, ActionBar actionBar) {

        actionBar.setTitle(title);
        Window window = activity.getWindow();
        SharedPreferences sharedPref = activity.getSharedPreferences("SETTINGS", MODE_PRIVATE);
        String secondaryColor = sharedPref.getString("secondaryColor", "default");
        int primaryColor = sharedPref.getInt("primaryColor", 0);
        if (secondaryColor != "default" && primaryColor != 0) {
            ColorDrawable drawable = new ColorDrawable(Color.parseColor(secondaryColor));
            actionBar.setBackgroundDrawable(drawable);
            window.setStatusBarColor(ContextCompat.getColor(activity, primaryColor));
            window.setNavigationBarColor(ContextCompat.getColor(activity, primaryColor));
        }
    }

    private static  Boolean isOwnContact(Context context){
        SharedPreferences shared = context.getSharedPreferences("SETTINGS", MODE_PRIVATE);
        return shared.getBoolean("OWN_CONTACT", false);
    }

    private static void createOwnContact(Context context){
        DBLocalAccess db = new DBLocalAccess(context);
        Contact own = new Contact("", "", "Moi", "0607080910",
                "", "pics", "");
        db.AddContact(own);
        SharedPreferences sharedpreferences;
        sharedpreferences = context.getSharedPreferences("SETTINGS", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean("OWN_CONTACT", true);
        editor.commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[]
            permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_SMS_RECEIVE) {
            Log.d("TAG", "My permission request sms received successfully");
        }
    }

    public static void refreshList(Contact contact){
        contacts_list.add(contact);
        Collections.sort(contacts_list, Comparator.comparing(Contact::getFirstname));
        contactArrayAdapter.notifyDataSetChanged();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (lastScreen == LastScreen.MAIN_SCREEN) {
            SharedPreferences sharedpreferences = getSharedPreferences("LASTBACK", MODE_PRIVATE);
            lastBack = sharedpreferences.getString("lastback", "Oupsi!");
            Toast.makeText(getApplicationContext(), lastBack, Toast.LENGTH_LONG).show();
            lastScreen = LastScreen.NONE;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences sharedpreferences = getSharedPreferences("LASTBACK", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("lastback", Useful.convertDate(String.valueOf(System.currentTimeMillis())));
        editor.commit();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        lastScreen = LastScreen.MAIN_SCREEN;
    }
}

