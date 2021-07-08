package com.e.ft_hangout.tools;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;

import com.e.ft_hangout.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Useful {

    //check if a string only contains digit
    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            int d = Integer.parseInt(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    //return extension of a file
    public static String getFileExt(Uri contentUri, Context context) {
        ContentResolver c = context.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(c.getType(contentUri));
    }

    //set imageView depend of the data in database
    public static void fillImageView(String pics, ImageView image){
        if (Useful.isNumeric(pics))
            image.setImageResource(Integer.parseInt(pics));
        else if (pics.compareTo("pics") == 0)
            image.setImageResource(R.drawable.pics);
        else
            image.setImageURI(Uri.parse(pics));
    }

    //convert date
    public static String convertDate(String date){
        long theDate = Long.valueOf(date);
        DateFormat simple = new SimpleDateFormat("dd/MM/yy à HH:mm", Locale.FRANCE);
        Date result = new Date(theDate);
        String sResult = String.valueOf(simple.format(result));
        return sResult;
    }

}
