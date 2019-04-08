package com.example.moham.my_whatsapp.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.example.moham.my_whatsapp.R;
import com.example.moham.my_whatsapp.chat.chatadapter;
import com.example.moham.my_whatsapp.chat.frindopjecthaschatwith;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class chatFragment extends Fragment {

    private RecyclerView MuserList;
    private RecyclerView.LayoutManager mlayout;
    private chatadapter madapter;
    private ProgressBar loodchat;
    private ArrayList<frindopjecthaschatwith> list;
    private String userid;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_blank, container, false);

        list= new ArrayList<>();
        userid=FirebaseAuth.getInstance().getUid();
        MuserList=v.findViewById(R.id.chatlist);
        loodchat=v.findViewById(R.id.lood_chat);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        intializethelistview();
        getchat();
    }

    private void getchat(){

        DatabaseReference db=FirebaseDatabase.getInstance().getReference("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("chat");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String>users=new ArrayList<>();
                if(dataSnapshot.exists()){
                    for(DataSnapshot child : dataSnapshot.getChildren()){
                        boolean ok=true;
                        for(String op:users){
                            if(op.equals(child.getKey().replace(FirebaseAuth.getInstance().getUid(),"")))
                            {
                                ok=false;
                                break;
                            }
                        }
                        if(ok)
                        {
                            users.add(child.getKey());
                        }
                    }
                    getfrinddata(users);
                }else{
                    getfrinddata(users);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    private void getfrinddata (final ArrayList<String> users){

        DatabaseReference dp= FirebaseDatabase.getInstance().getReference("user");

        dp.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    list.clear();
                    for(String opp:users){
                        String op=opp.replace(userid,"");
                        String name="",imageUri="default",stats="",phonenumber="",onoff="ofline",notificationKey="";

                        if(dataSnapshot.child(op).child("name").getValue() != null)
                            name=dataSnapshot.child(op).child("name").getValue().toString();

                        if(dataSnapshot.child(op).child("imageuri").getValue() != null)
                            imageUri=dataSnapshot.child(op).child("imageuri").getValue().toString();

                        if(dataSnapshot.child(op).child("stats").getValue() != null)
                            stats=dataSnapshot.child(op).child("stats").getValue().toString();

                        if(dataSnapshot.child(op).child("phone").getValue() != null)
                            phonenumber=dataSnapshot.child(op).child("phone").getValue().toString();

                        if(dataSnapshot.child(op).child("onoff").getValue() != null)
                            onoff=dataSnapshot.child(op).child("onoff").getValue().toString();

                        if(dataSnapshot.child(op).child("notificationkey").getValue() != null)
                            notificationKey=dataSnapshot.child(op).child("notificationkey").getValue().toString();

                        frindopjecthaschatwith child=new frindopjecthaschatwith(opp,name,stats,imageUri,phonenumber,onoff,notificationKey);

                        list.add(child);
                        madapter.notifyDataSetChanged();


                    }

                    loodchat.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    private void intializethelistview() {
        MuserList.setNestedScrollingEnabled(false);
        MuserList.setHasFixedSize(false);

        mlayout=new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayout.VERTICAL,false);
        MuserList.setLayoutManager(mlayout);

        madapter=new chatadapter(list,getActivity().getApplicationContext());

        MuserList.setAdapter(madapter);
    }

}
