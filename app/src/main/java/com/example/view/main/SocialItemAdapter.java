package com.example.view.main;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.data.SocialItemData;
import com.example.pim.R;


public class SocialItemAdapter extends TypedRecylcerAdapter<SocialItemAdapter.SocialItemHolder>{

    public SocialItemAdapter(int viewType) {
        super(viewType);
    }

    @Override
    public SocialItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(this.viewType != viewType)
            return null;

        return new SocialItemHolder(parent);
    }

    @Override
    public void onBindViewHolder(SocialItemHolder holder, int position) {
        //SocialItemData data = DataManager.Inst().getSocialDataList().get(position);

        holder.setName("dummy");
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class SocialItemHolder extends RecyclerView.ViewHolder {
        private TextView name;

        /**
         * Create Holder for Social Item
         * @param group
         */
        public SocialItemHolder(ViewGroup group) {
            super(LayoutInflater.from(group.getContext()).inflate(R.layout.item_social, group, false));

            name = (TextView)this.itemView.findViewById(R.id.textView_Social);
        }

        public void setName(String name) {
            this.name.setText(name);
        }
        // TODO: Implement Holder for Social Item
    }

}
