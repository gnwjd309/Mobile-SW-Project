package com.example.calendar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ScheduleListAdapter extends BaseAdapter {
    private ArrayList<ScheduleDTO> schedules;
    private Map<String, Integer> WeatherIamgeMap;
    private LayoutInflater layoutInflater;

    // 생성자 생성
    public ScheduleListAdapter(ArrayList<ScheduleDTO> schedules) {
        this.schedules = schedules;
    }

    @Override
    public int getCount() {
        return schedules.size();
    }

    @Override
    public Object getItem(int position) {
        return schedules.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder holder;

        if(view == null) {
            holder = new ViewHolder();
            view = layoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_main, viewGroup, false);

            TextView schedulelsit_tv = (TextView) view.findViewById(R.id.schedulelist_tv);

            holder.schedulelist_tv = schedulelsit_tv;
        }

        else {
            holder = (ViewHolder) view.getTag();
        }

        ScheduleDTO scheduleDTO = schedules.get(position);
        holder.schedulelist_tv.setText(scheduleDTO.getScheduleID());

        return view;
    }

    static class ViewHolder {
        TextView schedulelist_tv;
    }

}
