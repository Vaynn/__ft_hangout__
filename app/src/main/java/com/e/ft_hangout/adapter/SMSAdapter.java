package com.e.ft_hangout.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.e.ft_hangout.R;
import com.e.ft_hangout.models.Contact;
import com.e.ft_hangout.models.SMS;
import com.e.ft_hangout.tools.Useful;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SMSAdapter extends BaseAdapter {

    Context context;
    List<SMS> SMSList;
    LayoutInflater inflater;

    public SMSAdapter(Context context, List<SMS> SMSList){
        this.context = context;
        this.SMSList = SMSList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return this.SMSList.size();
    }

    @Override
    public SMS getItem(int i) {
        return SMSList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.adapter_sms, null);

        SMS currentSMS = getItem(i);
        String message = currentSMS.getMessage();
        String receive = currentSMS.getReceive();
        String read = currentSMS.getRead();
        String author = currentSMS.getAuthor_id();
        RelativeLayout bubble = view.findViewById(R.id.send_bubble);
        RelativeLayout bubbleHide = view.findViewById(R.id.receive_bubble);
        if (author.compareTo("1") == 0){
            bubbleHide.setVisibility(View.GONE);
            TextView messageB = view.findViewById(R.id.send_message);
            messageB.setText(message);
            TextView receiveB = view.findViewById(R.id.send_date);
            receiveB.setText(Useful.convertDate(receive));
        }
        else {
            bubble.setVisibility(View.GONE);
            TextView messageB = view.findViewById(R.id.receive_message);
            messageB.setText(message);
            TextView receiveB = view.findViewById(R.id.receive_date);
            receiveB.setText(Useful.convertDate(receive));
        }
        return view;
    }
}
