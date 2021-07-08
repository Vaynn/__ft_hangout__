package com.e.ft_hangout.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.Telephony;

import com.e.ft_hangout.tools.MySQLiteOpenHelper;

import java.util.ArrayList;

public class DBLocalAccess {

    private String DBName = "ft_hangout.sqlite";
    private Integer DBVersion = 1;
    private MySQLiteOpenHelper DBAcccess;
    private SQLiteDatabase db;
    private String CONTACT_DB_NAME = "contact";

    public DBLocalAccess(Context context){
        DBAcccess = new MySQLiteOpenHelper(context, DBName,null, DBVersion);
    }

    public void AddContact(Contact contact){
        db = DBAcccess.getWritableDatabase();
        String req = "INSERT INTO contact (name, firstname, phoneNumber, email, pics, phoneRing) " +
                "values (\"" + contact.getName() + "\", \"" + contact.getFirstname() + "\", \"" +
                contact.getPhoneNumber() + "\", \"" + contact.getEmail() + "\", \"" + contact.getPics() +
                "\" ,\"" + contact.getPhoneRing() + "\")";
        db.execSQL(req);
        if (db.isOpen()) {
            db.close();
        }
    }

    public Contact getOneContact(String row, String arg) {
        Contact contact;
        db = DBAcccess.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT id, name, firstname, phoneNumber, email, pics, " +
                "phoneRing FROM contact WHERE " + row + " = ?", new String[]{arg});
        if (res.getCount() == 1) {
            res.moveToFirst();
            String id = res.getString(res.getColumnIndex("id"));
            String name = res.getString(res.getColumnIndex("name"));
            String firstName = res.getString(res.getColumnIndex("firstname"));
            String phoneNumber = res.getString(res.getColumnIndex("phoneNumber"));
            String email = res.getString(res.getColumnIndex("email"));
            String pics = res.getString(res.getColumnIndex("pics"));
            String phoneRing = res.getString(res.getColumnIndex("phoneRing"));
            contact = new Contact(id, name, firstName, phoneNumber, email, pics, phoneRing);
        }
        else {
            contact = null;
        }
        if (db.isOpen())
            db.close();
        return contact;
    }

    //check if contact exist, if not create it, return the contact + boolean true/false if it was existing
    public Fox getOrCreateContact(String row, String arg){
        Fox fox;
        Contact contact;
        contact = getOneContact(row, arg);
        if (contact != null){
            fox = new Fox(contact, true);
        }
        else {
            contact = new Contact("","", arg, arg, "", "pics", "");
            try
            {
                AddContact(contact);
            }
            finally
            {
                contact = getOneContact("phoneNumber", arg);
                fox = new Fox(contact, false);
            }
        }
        return fox;
    }

    //check if more than one contact column exist with two common points
    public int getContact2(String row1, String row2, String args1, String args2){
        db = DBAcccess.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT id, name, firstname, phoneNumber, email, pics, " +
                "phoneRing FROM contact WHERE " + row1  + " = ? AND " + row2 + " = ?", new String[]{args1, args2});
        int nb = res.getCount();
        if (db.isOpen()){
        db.close();
        }
        return nb;
    }

    public ArrayList getAllContacts(){
        db = DBAcccess.getReadableDatabase();
        ArrayList<Contact> contacts_list = new ArrayList<Contact>();
        Cursor res = db.rawQuery("SELECT * FROM contact", null);
        res.moveToFirst();
        while(!res.isAfterLast()) {
            String id = res.getString(res.getColumnIndex("id"));
         String name = res.getString(res.getColumnIndex("name"));
         String firstName = res.getString(res.getColumnIndex("firstname"));
         String phoneNumber = res.getString(res.getColumnIndex("phoneNumber"));
         String email = res.getString(res.getColumnIndex("email"));
         String pics = res.getString(res.getColumnIndex("pics"));
         String phoneRing = res.getString(res.getColumnIndex("phoneRing"));
         contacts_list.add(new Contact(id, name, firstName, phoneNumber, email, pics, phoneRing));
         res.moveToNext();
        }
        if (db.isOpen()) {
            db.close();
        }
        return contacts_list;
    }

    public int updateContact(Contact contact){
        db = DBAcccess.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", contact.getName());
        cv.put("firstname", contact.getFirstname());
        cv.put("phoneNumber", contact.getPhoneNumber());
        cv.put("email", contact.getEmail());
        cv.put("pics", contact.getPics());
        cv.put("phoneRing", contact.getPhoneRing());
        String Column = "id";
        String rowId = contact.getId();
        int num = db.update(CONTACT_DB_NAME, cv, Column + "= ?", new String[] {rowId});
        if (db.isOpen()) {
            db.close();
        }
        return num;
    }
    public int deleteContact(String row, String id){
        db = DBAcccess.getWritableDatabase();
        String where = row + " = ?";
        int num = db.delete(CONTACT_DB_NAME, where, new String[]{id});
        if (db.isOpen()) {
            db.close();
        }
        return num;
    }

    public void AddSms(SMS sms){
        db = DBAcccess.getWritableDatabase();
        String req = "INSERT INTO sms (message, receive, read, smsauthor, destinataire_id) " +
                "values (\"" +sms.getMessage() + "\", \"" + sms.getReceive() + "\", \"" +
                sms.getRead() + "\", \"" + sms.getAuthor_id()  + "\", \"" + sms.getDestinataire_id() +
                "\")";
        db.execSQL(req);
        if (db.isOpen()) {
            db.close();
        }
    }

    public ArrayList<SMS> getConversation(String author, String destinataire){
        db = DBAcccess.getReadableDatabase();
        ArrayList<SMS> sms_list = new ArrayList<SMS>();
        Cursor res = db.rawQuery("SELECT id, message, receive, read, smsauthor, destinataire_id " +
                "FROM sms WHERE smsauthor = " + " ? AND destinataire_id = ? " +
                "UNION " +
                "SELECT id, message, receive, read, smsauthor, destinataire_id " +
                "FROM sms WHERE smsauthor = " + " ? AND destinataire_id = ? "
                ,new String[]{author, destinataire, destinataire, author});
        res.moveToFirst();
        while (!res.isAfterLast()){
            String id = res.getString(res.getColumnIndex("id"));
            String message = res.getString(res.getColumnIndex("message"));
            String receive = res.getString(res.getColumnIndex("receive"));
            String read = res.getString(res.getColumnIndex("read"));
            String smsauthor = res.getString(res.getColumnIndex("smsauthor"));
            String destinataire_id = res.getString(res.getColumnIndex("destinataire_id"));
            sms_list.add(new SMS(message, receive, read, smsauthor, destinataire_id));
            res.moveToNext();
        }
        if (db.isOpen())
            db.close();
        return sms_list;
    }
}
