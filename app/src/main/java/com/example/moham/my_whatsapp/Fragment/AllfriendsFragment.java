package com.example.moham.my_whatsapp.Fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.example.moham.my_whatsapp.R;
import com.example.moham.my_whatsapp.User.recycleradapter;
import com.example.moham.my_whatsapp.User.userdata;
import com.example.moham.my_whatsapp.Utils.CountryToPhonePrefix;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AllfriendsFragment extends Fragment {

    private RecyclerView MuserList;
    private RecyclerView.LayoutManager mlayout;
    private recycleradapter madapter;
    private ArrayList<userdata> list;
    private ArrayList<userdata> comenlist;
    private ProgressBar lood_chat;

    private EditText user_search;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.allfriendsfragment, container, false);
        list=new ArrayList<>();
        comenlist=new ArrayList<>();
        user_search=v.findViewById(R.id.user_search);
        MuserList=v.findViewById(R.id.user_list);
        lood_chat=v.findViewById(R.id.lood_chat);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        intializethelistview();
        user_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String val=user_search.getText().toString().trim();
                if(val.equals("") || val.equals(null)){
                    list.clear();
                    madapter.notifyDataSetChanged();
                    getphonenumbers();
                }else {
                    searchuser(val);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }


    private void searchuser(String s) {
        Query query=FirebaseDatabase.getInstance().getReference("user").orderByChild("search")
                .startAt(s)
                .endAt(s+"\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                lood_chat.setVisibility(View.VISIBLE);
                list.clear();
                for (DataSnapshot childSnapshot:dataSnapshot.getChildren()){

                    String  phone = "",
                            name = "",stats="",imageuri="default",onoff="offline",notificationkey="";

                    if(childSnapshot.child("name").getValue() != null)
                        name=childSnapshot.child("name").getValue().toString();
                    if(childSnapshot.child("phone").getValue() != null)
                        phone=childSnapshot.child("phone").getValue().toString();
                    if(childSnapshot.child("stats").getValue() != null)
                        stats=childSnapshot.child("stats").getValue().toString();
                    if(childSnapshot.child("imageuri").getValue() != null)
                        imageuri=childSnapshot.child("imageuri").getValue().toString();
                    if(childSnapshot.child("onoff").getValue() != null)
                        onoff=childSnapshot.child("onoff").getValue().toString();
                    if(childSnapshot.child("notificationkey").getValue() != null)
                        notificationkey=childSnapshot.child("notificationkey").getValue().toString();

                    list.add(new userdata(childSnapshot.getKey(),name, phone, stats, imageuri,onoff,notificationkey));
                    madapter.notifyDataSetChanged();
                }
                lood_chat.setVisibility(View.GONE);
                madapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }




    private void getphonenumbers() {
        String iso=getiso();
        Cursor cursor= getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null);
        while (cursor.moveToNext()){
            String number=cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            number.replace(" ","");
            number.replace("_","");
            number.replace("(","");
            number.replace(")","");

            if (!String.valueOf(number.charAt(0)).equals("+"))
                number=iso+number;
            usertheapp(number);
        }
    }

    private void usertheapp(String number){

        DatabaseReference mUserDB = FirebaseDatabase.getInstance().getReference().child("userphonenumber").child(number);
        mUserDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    usertheappdata(dataSnapshot.getValue().toString());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }


    private void usertheappdata(String key) {
        DatabaseReference mUserDB = FirebaseDatabase.getInstance().getReference().child("user").child(key);
        mUserDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String  phone = "",
                            name = "",stats="",imageuri="default",onoff="offline",notificationkey="";

                    if(dataSnapshot.child("name").getValue() != null)
                        name=dataSnapshot.child("name").getValue().toString();
                    if(dataSnapshot.child("phone").getValue() != null)
                        phone=dataSnapshot.child("phone").getValue().toString();
                    if(dataSnapshot.child("stats").getValue() != null)
                        stats=dataSnapshot.child("stats").getValue().toString();
                    if(dataSnapshot.child("imageuri").getValue() != null)
                        imageuri=dataSnapshot.child("imageuri").getValue().toString();
                    if(dataSnapshot.child("onoff").getValue() != null)
                        onoff=dataSnapshot.child("onoff").getValue().toString();
                    if(dataSnapshot.child("notificationkey").getValue() != null)
                        notificationkey=dataSnapshot.child("notificationkey").getValue().toString();


                    list.add(new userdata(dataSnapshot.getKey(),name, phone, stats, imageuri,onoff,notificationkey));
                    madapter.notifyDataSetChanged();

                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    private String getiso(){
        String iso=null;
        TelephonyManager telephonyManager = (TelephonyManager) getActivity().getApplicationContext().getSystemService(getActivity().getApplicationContext().TELEPHONY_SERVICE);
        if(telephonyManager.getNetworkCountryIso() != null ){
            if(!telephonyManager.getNetworkCountryIso().toString().equals("") ){
                iso=telephonyManager.getNetworkCountryIso().toString();
            }else{
                iso="EG";
            }
        }
        return CountryToPhonePrefix.getPhone("EG");
    }




























    private void intializethelistview() {
        MuserList.setNestedScrollingEnabled(false);
        MuserList.setHasFixedSize(false);

        mlayout=new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayout.VERTICAL,false);
        MuserList.setLayoutManager(mlayout);

        madapter=new recycleradapter(list,getContext());

        MuserList.setAdapter(madapter);
    }
}
