package com.example.view.main;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.data.ScheduleItemData;
import com.example.managers.ScheduleItemManager;
import com.example.managers.SharedDataManager;
import com.example.pim.R;

import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

/**
 * This class implements how to visualize ScheduleItem.
 */
public class ScheduleItemAdapter extends TypedRecylcerAdapter<ScheduleItemAdapter.ScheduleItemHolder> {
    private ScheduleItemManager.ScheduleItemUpdateCallBack onItemUpdate;

    private Context context;

    public ScheduleItemAdapter(Context context, int viewType) {
        super(viewType);

        this.context = context;

        // set reference
        //scheduleItemManager = DataManager.Inst().getScheduleDataManager();

        // set listener
        onItemUpdate = new ScheduleItemManager.ScheduleItemUpdateCallBack() {
            @Override
            public void onUpdate(ScheduleItemData data, ScheduleItemManager.ScheduleItemUpdateType type, ScheduleItemManager manager) {
                int pos = manager.getIndexof(data);
                Log.d("ScheduleItemAdapter", "Item at " + pos + " is updated");

                switch (type)
                {
                    case CHANGE:
                        notifyItemChanged(pos);
                        break;
                    case ADD:
                        notifyItemInserted(pos);
                        break;
                    case REMOVED:
                        notifyItemRemoved(pos);
                        break;
                }
            }
        };

        if(context == null)
            Log.e("What", "is Wrong????");
        SharedDataManager.Inst(context).giveScheduleTaskConst(new SharedDataManager.Task<ScheduleItemManager>() {
            @Override
            public void doWith(ScheduleItemManager scheduleItemManager) {
                scheduleItemManager.addUpdateListener(onItemUpdate);
            }
        });
    }

    public void onDestroy() {
    }

    @Override
    public ScheduleItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(this.viewType != viewType)
            return null;

        return new ScheduleItemHolder(parent);
    }

    @Override
    public void onBindViewHolder(ScheduleItemHolder holder, final int position) {
        // find
        final ScheduleItemData[] data = { null };
        SharedDataManager.Inst(context).giveScheduleTaskConst(new SharedDataManager.Task<ScheduleItemManager>() {
            @Override
            public void doWith(ScheduleItemManager manager) {
                data[0] = manager.getItemData(position);
            }
        });

        // link

        // name
        if(!data[0].name.equals(""))
            holder.setName(data[0].name);
        else
            holder.setName("No Name");

        // dest
        if(data[0].loc_destination != null)
            holder.setDest("장소 : " + data[0].loc_destination.getName());
        else
            holder.setDest("Destination not allocated");

        // time
        if(data[0].time != null) {
            int day = data[0].time.get(GregorianCalendar.DAY_OF_MONTH);
            int hour = data[0].time.get(GregorianCalendar.HOUR_OF_DAY);
            int min = data[0].time.get(GregorianCalendar.MINUTE);
            holder.setTime("시각 : " + day + "일 " + hour + "시" + min + "분");
        } else {
            holder.setTime("시각 미정");
        }

        // delta time
        if(data[0].deltaTime != 0) {
            long min = TimeUnit.SECONDS.toMinutes(data[0].deltaTime);
            holder.setDeltaTime("약 " + min + "분 소요");
        }
        else
            holder.setTime("시간 미정");

        // comment
        if(!data[0].comment.equals(""))
            holder.setComment(data[0].comment);
        else
            holder.setComment("");
    }

    @Override
    public int getItemCount() {
        final int[] size = new int[1];
        SharedDataManager.Inst(context).giveScheduleTaskConst(new SharedDataManager.Task<ScheduleItemManager>() {
            @Override
            public void doWith(ScheduleItemManager manager) {
                size[0] = manager.getItemsize();
            }
        });

        return size[0];
    }

    class ScheduleItemHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView dest;
        private TextView time;
        private TextView deltatime;
        private TextView comment;

        /**
         * Create Holder for Schedule Item
         * @param group root group
         */
        public ScheduleItemHolder(ViewGroup group) {
            super(LayoutInflater.from(group.getContext()).inflate(R.layout.item_schedule, group, false));

            name = (TextView)this.itemView.findViewById(R.id.Schedule_Item_Name);
            dest = (TextView)this.itemView.findViewById(R.id.Schedule_Item_Destination);
            time = (TextView)this.itemView.findViewById(R.id.Schedule_Item_Time);
            deltatime = (TextView)this.itemView.findViewById(R.id.Schedule_Item_DeltaTime);
            comment = (TextView)this.itemView.findViewById(R.id.Schedule_Item_Comment);

            View.OnClickListener click = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int indx = getAdapterPosition();
                    Log.d("ScheduleItemHolder", "item number : " + indx + " has clicked");
                    SharedDataManager.Inst(context).giveScheduleTask(new SharedDataManager.Task<ScheduleItemManager>() {
                        @Override
                        public void doWith(ScheduleItemManager manager) {
                            ScheduleItemData data = manager.getItemData(indx);
                            manager.updateScheduleItem(data);
                        }
                    });
                }
            };

            this.itemView.setOnClickListener(click);
        }

        public void setName(String name) {
            this.name.setText(name);
        }
        public void setDest(String dest) {
            this.dest.setText(dest);
        }
        public void setTime(String time) {
            this.time.setText(time);
        }
        public void setDeltaTime(String time) { this.deltatime.setText(time); }
        public void setComment(String comment) { this.comment.setText(comment);}
    }
}
