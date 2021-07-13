package com.bozhon.sdk.next.one;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * FileName: MainAdapter
 * Author: zhikai.jin
 * Date: 2021/6/11 15:54
 * Description:
 */
public abstract class MainAdapter extends RecyclerView.Adapter<MainAdapter.ItemHolder> {

    private List<String> mDataList = new ArrayList<>();


    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_main_item, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MainAdapter.ItemHolder holder, int position) {
        holder.textView.setText(mDataList.get(position));
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    class ItemHolder extends RecyclerView.ViewHolder {
        private TextView textView;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.item_name);
        }
    }

    public void setmData(List<String> dataList) {
        mDataList.clear();
        this.mDataList.addAll(dataList);
        notifyDataSetChanged();
    }

    public abstract void onItemClick(int position);
}
