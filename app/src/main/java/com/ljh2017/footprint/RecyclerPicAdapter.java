package com.ljh2017.footprint;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by alfo06-15 on 2017-06-13.
 */

public class RecyclerPicAdapter extends RecyclerView.Adapter<RecyclerPicAdapter.ViewHolder> {

    ArrayList<Memo> pics;
    Context context;

    public RecyclerPicAdapter(ArrayList<Memo> pics, Context context) {
        this.pics = pics;
        this.context = context;
    }

    @Override
    public RecyclerPicAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.recycler_pic_item,parent,false);
        ViewHolder holder = new ViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerPicAdapter.ViewHolder holder, int position) {
//        ddd
        Glide.with(context).load(pics.get(position).imgpic).into(holder.imgPic);
    }

    @Override
    public int getItemCount() {
        return pics.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        //CircleImageView imgIcon;
        //TextView textName;
        ImageView imgPic;

        public ViewHolder(View itemView) {
            super(itemView);

            imgPic = (ImageView) itemView.findViewById(R.id.img_grid);
        }
    }
}
