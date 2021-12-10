package com.example.mobilepjapp.note;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.mobilepjapp.R;

import java.util.ArrayList;

/**
 * 리스트에 데이터를 넣기 위한 클래스
 **/
public class ListItemAdapter extends BaseAdapter {
    ArrayList<ListItem> items = new ArrayList<ListItem>();
    ArrayList<ListItem> displayItems = new ArrayList<ListItem>();
    Context context;

    // ArrayList 크기 리턴
    @Override
    public int getCount() {
        return items.size();
    }

    // 해당 position 에 있는 item 리턴
    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    // position 리턴
    @Override
    public long getItemId(int position) {
        return position;
    }

    // position 에 위치한 데이터를 화면에 출력하는 메소드
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        context = parent.getContext();
        ListItem listItem = items.get(position);

        // listview_custom.xml을 inflate해서 convertview를 참조
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_custom, parent, false);
        }

        // 화면에 보여질 데이터를 참조
        TextView speakerText = convertView.findViewById(R.id.tv_speaker);
        TextView sentenceText = convertView.findViewById(R.id.tv_sentence);

        // 데이터 set
        speakerText.setText(listItem.getSpeaker());
        sentenceText.setText(listItem.getSentence());

        // 각 항목들의 Touch 이벤트 비활성화
        convertView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        return convertView;
    }

    // list에 데이터를 넣는 메소드
    public void addItem(ListItem item) {
        items.add(item);
    }
}
