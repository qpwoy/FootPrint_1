package com.ljh2017.footprint;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by alfo06-15 on 2017-06-13.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    ArrayList<Memo> memos;
    Context context;

    public RecyclerAdapter(ArrayList<Memo> memos, Context context) {
        this.memos = memos;
        this.context = context;
    }

    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.recycler_item,parent,false);
        ViewHolder holder = new ViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerAdapter.ViewHolder holder, int position) {
//        ddd
        holder.textName.setText(memos.get(position).name);
        Glide.with(context).load(memos.get(position).imgpic).into(holder.imgPic);
        holder.textMemo.setText(memos.get(position).memo);
        holder.textAddr.setText(memos.get(position).addr);
        holder.textTag.setText(memos.get(position).tag);

    }

    @Override
    public int getItemCount() {
        return memos.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        //CircleImageView imgIcon;
        TextView textName;
        ImageView imgPic;
        TextView textMemo;
        TextView textAddr;
        TextView textTag;

        public ViewHolder(View itemView) {
            super(itemView);

            //imgIcon = (CircleImageView) itemView.findViewById(R.id.item_iv);
            textName = (TextView) itemView.findViewById(R.id.item_tv);
            imgPic = (ImageView) itemView.findViewById(R.id.item_pic);
            textMemo = (TextView) itemView.findViewById(R.id.item_memo);
            textAddr = (TextView) itemView.findViewById(R.id.item_addr);
            textTag = (TextView) itemView.findViewById(R.id.item_tag);

        }
    }
}
