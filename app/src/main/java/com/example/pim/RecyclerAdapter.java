package com.example.pim;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by HotHaeYoung on 2016-05-06.
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    public static final int SCHEDULE = 0;
    public static final int SOCIAL = 1;

    int viewType;
    List items;

    public RecyclerAdapter(int viewType, List items) {
        this.viewType = viewType;
        this.items = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;

        switch (viewType) {
            case SCHEDULE:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_schedule, parent, false);
                return new ScheduleHolder(v);
            case SOCIAL:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_social, parent, false);
                return new SocialHolder(v);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        switch (viewHolder.getItemViewType()) {
            case SCHEDULE:
                ScheduleHolder scheduleHolder = (ScheduleHolder) viewHolder;
                Schedule_item schedule_item = (Schedule_item) items.get(position);
                scheduleHolder.textView.setText(schedule_item.getText());
                break;
            case SOCIAL:
                SocialHolder socialHolder = (SocialHolder) viewHolder;
                Social_item social_item = (Social_item) items.get(position);
                socialHolder.textView.setText(social_item.getText());
                break;
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return this.viewType;
    }

    // ViewHolder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View v) {
            super(v);
        }
    }

    public class ScheduleHolder extends ViewHolder {
        TextView textView;

        public ScheduleHolder(View v) {
            super(v);
            this.textView = (TextView)v.findViewById(R.id.textView_Schedule);
        }
    }

    public class SocialHolder extends ViewHolder {
        TextView textView;

        public SocialHolder(View v) {
            super(v);
            this.textView = (TextView)v.findViewById(R.id.textView_Social);
        }
    }
}
