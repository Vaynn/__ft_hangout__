package com.e.ft_hangout.adapter;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.e.ft_hangout.ChoosePicsActivity;
import com.e.ft_hangout.R;
import com.e.ft_hangout.models.Contact;
import com.e.ft_hangout.tools.Useful;


import java.util.List;

public class ContactAdapter extends BaseAdapter {

     Context context;
     List<Contact> contactList;
     LayoutInflater inflater;

    public ContactAdapter(Context context, List<Contact> contactList){
        this.context = context;
        this.contactList = contactList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return this.contactList.size();
    }

    @Override
    public Contact getItem(int i) {
        return contactList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.adapter_contact, null);

        Contact currentContact = getItem(i);
        String name = currentContact.getName();
        String firstname = currentContact.getFirstname();
        String pics = currentContact.getPics();

        TextView firstnameText = view.findViewById(R.id.contact_firstname);
        firstnameText.setText(firstname);

        TextView nameText = view.findViewById(R.id.contact_name);
        nameText.setText(name);

        ImageView image = view.findViewById(R.id.pics);
        Useful.fillImageView(pics, image);
        return view;
    }
}
