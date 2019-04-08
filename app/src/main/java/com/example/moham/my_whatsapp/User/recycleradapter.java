package com.example.moham.my_whatsapp.User;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.moham.my_whatsapp.MainHomeActivity;
import com.example.moham.my_whatsapp.R;
import com.example.moham.my_whatsapp.chatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class  recycleradapter extends RecyclerView.Adapter<recycleradapter.myviewholder> {

    private ArrayList<userdata> list;
    private Context context;
    private HashMap<String , Boolean> contacts ;

    public recycleradapter(ArrayList<userdata> list,Context context) {
        this.list=list;
        this.context=context;
        contacts = new HashMap<>();
        getcontacts();
    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.listitemview,null,false);
        RecyclerView.LayoutParams lp=new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        v.setLayoutParams(lp);
        return new myviewholder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull myviewholder myviewholder, final int i) {
        myviewholder.name.setText(list.get(i).getUsername());
        myviewholder.stats.setText(list.get(i).getUserstatas());
        myviewholder.onoff.setText(list.get(i).getOnoff());
        if (list.get(i).getUserimageuri().equals("default"))
            Glide.with(context).load(R.drawable.person).into(myviewholder.image);
        else
            Glide.with(context).load(list.get(i).getUserimageuri()).into(myviewholder.image);

        myviewholder.frind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if(!list.get(i).getUid(  ).equals(FirebaseAuth.getInstance().getUid()) ){
                    String key1=list.get(i).getUid()+FirebaseAuth.getInstance().getUid();
                    String key2=FirebaseAuth.getInstance().getUid()+list.get(i).getUid();

                    if(contacts.get(key1) != null && contacts.get(key1) ){
                        openwithkey(key1,list.get(i).getUsername(),list.get(i).getNotificationkey());
                    }else if(contacts.get(key2)  != null && contacts.get(key2) ){
                        openwithkey(key2,list.get(i).getUsername(),list.get(i).getNotificationkey());
                    }
                    else if(key1.compareTo(key2) > 0){
                        FirebaseDatabase.getInstance().getReference().child("chat").child(key1).setValue(true);
                        FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getUid()).child("chat").child(key1).setValue(true);
                        FirebaseDatabase.getInstance().getReference().child("user").child(list.get(i).getUid()).child("chat").child(key1).setValue(true);
                         openwithkey(key1,list.get(i).getUsername(),list.get(i).getNotificationkey());
                    }else {
                        FirebaseDatabase.getInstance().getReference().child("chat").child(key2).setValue(true);
                        FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getUid()).child("chat").child(key2).setValue(true);
                        FirebaseDatabase.getInstance().getReference().child("user").child(list.get(i).getUid()).child("chat").child(key2).setValue(true);
                        openwithkey(key2,list.get(i).getUsername(),list.get(i).getNotificationkey());
                    }

                }
            }
        });
    }



    @Override
    public int getItemCount() {
        return list.size();
    }

    public class myviewholder extends RecyclerView.ViewHolder{
        private TextView name,stats,onoff;
        private CircleImageView image;
        private  LinearLayout frind;
        public myviewholder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.user_name);
            stats=itemView.findViewById(R.id.user_stats);
            image=itemView.findViewById(R.id.userimage);
            frind=itemView.findViewById(R.id.frindlayout);
            onoff=itemView.findViewById(R.id.onoff);
        }
    }

    private void openwithkey(String Key,String name,String notificationkey){
        Intent intent=new Intent(context, chatActivity.class);
        Bundle bundle=new Bundle();
        bundle.putString("chatid",Key);
        bundle.putString("name",name);
        bundle.putString("notificationkey",notificationkey);
        intent.putExtras(bundle);

        context.startActivity(intent);
    }
    private void getcontacts(){
        FirebaseDatabase.getInstance().getReference("user").child( FirebaseAuth.getInstance().getUid() ).child("chat").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()){
                    contacts.put(dataSnapshot.getKey(),true);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
