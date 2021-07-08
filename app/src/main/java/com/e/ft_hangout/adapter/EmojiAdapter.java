package com.e.ft_hangout.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.e.ft_hangout.R;
import com.e.ft_hangout.models.Contact;

public class EmojiAdapter extends BaseAdapter {

    private int[] emoji;
    private LayoutInflater inflater;
    private Context context;

    public EmojiAdapter(Context context, int[] emoji){
        this.context = context;
        this.emoji = emoji;
        this.inflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return this.emoji.length;
    }

    @Override
    public Object getItem(int position) {
        return this.emoji[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.adapter_emoji, null);
        int emo = emoji[position];
        ImageView imageView = convertView.findViewById(R.id.current_emoji);
        imageView.setTag(emo);
        imageView.setImageResource(emo);
        imageView.setBackground(context.getDrawable(R.drawable.layout_rounded_bg));
        imageView.setPadding(5, 5, 5, 5);

        return convertView;
    }
}
