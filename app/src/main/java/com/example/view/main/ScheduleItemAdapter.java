package com.example.view.main;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.data.ScheduleItemData;
import com.example.managers.DataManager;
import com.example.pim.R;

public class ScheduleItemAdapter extends TypedRecylcerAdapter<ScheduleItemAdapter.ScheduleItemHolder> {

    public ScheduleItemAdapter(int viewType) {
        super(viewType);
    }

    @Override
    public ScheduleItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(this.viewType != viewType)
            return null;

        return new ScheduleItemHolder(parent);
    }

    @Override
    public void onBindViewHolder(ScheduleItemHolder holder, int position) {
        // find
        ScheduleItemData data = DataManager.Inst().getScheduleDataList().get(position);

        // link
        // TODO: implement how objects will be view
        // ex )
        holder.setName(data.getName());
        if(data.loc_destination != null)
            holder.setDest(data.loc_destination.getMajorName());
        else
            holder.setDest("Destination not allocated");
        holder.setTime("Dummy Time");
    }

    @Override
    public int getItemCount() {
        return DataManager.Inst().getScheduleDataList().size();
    }

    class ScheduleItemHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView dest;
        private TextView time;

        /**
         * Create Holder for Schedule Item
         * @param group root group
         */
        public ScheduleItemHolder(ViewGroup group) {
            super(LayoutInflater.from(group.getContext()).inflate(R.layout.item_schedule, group, false));

            name = (TextView)this.itemView.findViewById(R.id.Schedule_Item_Name);
            dest = (TextView)this.itemView.findViewById(R.id.Schedule_Item_Destination);
            time = (TextView)this.itemView.findViewById(R.id.Schedule_Item_DeltaTime);
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

    }
}
