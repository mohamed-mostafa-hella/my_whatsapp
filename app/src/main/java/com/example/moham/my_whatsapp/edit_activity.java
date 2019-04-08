package com.example.moham.my_whatsapp;

import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.moham.my_whatsapp.Fragment.AllfriendsFragment;
import com.example.moham.my_whatsapp.Fragment.choosestatsFragment;
import com.example.moham.my_whatsapp.Fragment.edittextFragment;
import com.example.moham.my_whatsapp.User.recycleradapter;
import com.example.moham.my_whatsapp.User.userdata;
import com.example.moham.my_whatsapp.Utils.CountryToPhonePrefix;
import com.example.moham.my_whatsapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class edit_activity extends AppCompatActivity implements choosestatsFragment.choosestatsinterface {


    private String type,data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        setContentView(R.layout.activity_userlist);

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Edit");


        type=getIntent().getExtras().getString("type");
        data=getIntent().getExtras().getString("data");

        if (type.equals("name")) {
            getSupportFragmentManager().beginTransaction().add(R.id.edit_fragment, edittextFragment.newInstance(type, data), "all_fragment")
                    .commit();
        }else{
            getSupportFragmentManager().beginTransaction().add(R.id.edit_fragment, choosestatsFragment.newInstance(data), "all_fragment")
                    .commit();
        }

    }

    private void stats(String stat){
        FirebaseDatabase.getInstance().getReference("user").child(FirebaseAuth.getInstance().getUid())
                .child("onoff").setValue(stat);
    }

    @Override
    protected void onResume() {
        super.onResume();
        stats("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        stats("offline");
    }


    @Override
    public void editstats(String data) {
        this.data=data;
        getSupportFragmentManager().beginTransaction().replace(R.id.edit_fragment,edittextFragment.newInstance(type,data),"all_fragment")
                .addToBackStack(null)
                .commit();
    }
}
