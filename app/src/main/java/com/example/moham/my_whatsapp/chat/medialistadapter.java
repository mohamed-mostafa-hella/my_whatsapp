package com.example.moham.my_whatsapp.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.moham.my_whatsapp.R;

import java.util.ArrayList;

public class medialistadapter extends RecyclerView.Adapter<medialistadapter.myviewholder1> {

    private ArrayList<String> list;
    private Context context;
    public medialistadapter(Context context,ArrayList<String> list) {
        this.list=list;
        this.context=context;
    }

    @NonNull
    @Override
    public myviewholder1 onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.imgviewlist,null,false);
        return new myviewholder1(v);
    }

    @Override
    public void onBindViewHolder(@NonNull myviewholder1 myviewholder1, int i) {
        Glide.with(context).load(list.get(i)).into(myviewholder1.img);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class myviewholder1 extends RecyclerView.ViewHolder{
        public ImageView img;
        public LinearLayout frinndtochatwith;
        public myviewholder1(@NonNull View itemView) {
            super(itemView);
            img=itemView.findViewById(R.id.imgviewlist);
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    list.remove(getPosition());
                    notifyDataSetChanged();
                    return false;
                }
            });
        }
    }
}
