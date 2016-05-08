package com.example.view.main;

import android.support.v7.widget.RecyclerView;

import java.util.List;

/**
 * Created by Hoon on 5/8/2016.
 */
public abstract class TypedRecylcerAdapter<T extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<T>{
    protected final int viewType;

    public TypedRecylcerAdapter(int viewType){
        this.viewType = viewType;
    }

    @Override
    public int getItemViewType(int position) {
        return this.viewType;
    }
}
