package com.e.ft_hangout.tools;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.e.ft_hangout.models.DBLocalAccess;

import java.io.IOException;
import java.util.ArrayList;

public class MySQLiteOpenHelper extends SQLiteOpenHelper {

    //propriétés
    private String create="create table contact (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "name TEXT," +
            "firstname TEXT," +
            "phoneNumber TEXT NOT NULL," +
            "email TEXT," +
            "pics TEXT DEFAULT 'pics'," +
            "phoneRing TEXT)";

    private String createSms ="create table sms (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "message TEXT," +
            "receive TEXT," +
            "read INTEGER," +
            "smsauthor TEXT," +
            "destinataire_id TEXT," +
            "FOREIGN KEY (smsauthor) REFERENCES contact (id) ON DELETE CASCADE," +
            "FOREIGN KEY (destinataire_id) REFERENCES contact (id) ON DELETE CASCADE)";

    /**
     * constructeur
     * @param context
     * @param name
     * @param factory
     * @param version
     */
    public MySQLiteOpenHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    /**
     * Si changement de DB
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(create);
        } catch (SQLiteException e){
            try {
                throw new IOException(e);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        try {
            db.execSQL(createSms);
        } catch (SQLiteException e){
            try {
                throw new IOException(e);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Si changement de version
     * @param db
     * @param i ancienne version
     * @param i1 nouvelle version
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS contact");
        db.execSQL("DROP TABLE IF EXISTS emoji");
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        db.execSQL("PRAGMA foreign_keys = ON;");
    }
}
