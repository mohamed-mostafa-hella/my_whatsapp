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
import com.example.moham.my_whatsapp.chatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class chatadapter extends RecyclerView.Adapter<chatadapter.myviewholder> {

    private ArrayList<frindopjecthaschatwith> list;
    private Context context;
    public chatadapter(ArrayList<frindopjecthaschatwith> list,Context context) {
        this.list=list;
        this.context=context;
    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chatitem,null,false);
        RecyclerView.LayoutParams lp=new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        v.setLayoutParams(lp);
        return new myviewholder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull myviewholder myviewholder, final int i) {

        myviewholder.name.setText(list.get(i).getFrindname());
        myviewholder.onoff.setText(list.get(i).getOnoff());

        setlastmessandCount(list.get(i).getId(),myviewholder.stats,myviewholder.count);


        if (list.get(i).getFrindimguri().equals("default"))
            Glide.with(context).load(R.drawable.person).into(myviewholder.image);
        else
            Glide.with(context).load(list.get(i).getFrindimguri()).into(myviewholder.image);

        myviewholder.frinndtochatwith.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(v.getContext(),chatActivity.class);
                Bundle bundle=new Bundle();

                bundle.putString("chatid",list.get(i).getId());
                bundle.putString("notificationkey",list.get(i).getNotificationKey());

                bundle.putString("name",list.get(i).getFrindname());
                intent.putExtras(bundle);
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class myviewholder extends RecyclerView.ViewHolder{
        private TextView name,stats,onoff,count;
        private CircleImageView image;
        public LinearLayout frinndtochatwith;
        public myviewholder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.Frind_name);
            stats=itemView.findViewById(R.id.Friend_statas);
            image=itemView.findViewById(R.id.Frindimage);
            frinndtochatwith=itemView.findViewById(R.id.frindlayout);
            onoff=itemView.findViewById(R.id.onoff);
            count=itemView.findViewById(R.id.count);

        }
    }

    void setlastmessandCount(String opp , final TextView messview , final TextView countview){
        DatabaseReference dp= FirebaseDatabase.getInstance().getReference("chat").child(opp).child("lastmess");

        dp.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    String creator=null,mess=null,count=null;
                    if(dataSnapshot.child("creator").getValue() != null){
                        creator=dataSnapshot.child("creator").getValue().toString();
                    }
                    if(dataSnapshot.child("count") .getValue()!= null){
                        count=dataSnapshot.child("count").getValue().toString();
                    }
                    if(dataSnapshot.child("text").getValue() != null) {
                        mess=dataSnapshot.child("text").getValue().toString();
                    }



                    if( creator != null && creator.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                        messview.setText("u send a message");
                        countview.setVisibility(View.GONE);
                    }else{
                        if(count!=null && Integer.parseInt(count.trim()) > 0){
                            countview.setText(count);
                            countview.setVisibility(View.VISIBLE);
                        }else{
                            countview.setVisibility(View.GONE);
                        }
                        if(mess!=null){
                            messview.setText(mess);
                        }else{
                            messview.setText("media.....");
                        }
                    }


                }else{
                    messview.setText("start message.......");
                    countview.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
