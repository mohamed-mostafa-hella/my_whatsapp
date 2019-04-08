package com.example.moham.my_whatsapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.moham.my_whatsapp.Fragment.AllfriendsFragment;
import com.example.moham.my_whatsapp.Fragment.chatFragment;
import com.example.moham.my_whatsapp.Fragment.brofileFragment;
import com.example.moham.my_whatsapp.User.userdata;
import com.example.moham.my_whatsapp.User.viewbageradabter;
import com.example.moham.my_whatsapp.Utils.CountryToPhonePrefix;
import com.example.moham.my_whatsapp.Utils.SendNotification;
import com.example.moham.my_whatsapp.chat.frindopjecthaschatwith;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OneSignal;

import java.util.ArrayList;
import java.util.HashMap;

public class MainHomeActivity extends AppCompatActivity {

    private ArrayList<userdata> list;
    private AllfriendsFragment allfriendsFragment;

    private FirebaseUser user;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        setContentView(R.layout.activity_main_home);



        Fresco.initialize(this);

        user=FirebaseAuth.getInstance().getCurrentUser();
        reference=FirebaseDatabase.getInstance().getReference("user").child(user.getUid());

        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .init();
        OneSignal.setSubscription(true);
        OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
            @Override
            public void idsAvailable(String userId, String registrationId) {
                FirebaseDatabase.getInstance().getReference("user").child(user.getUid()).child("notificationkey").setValue(userId);
               // new SendNotification("mes1","head1",userId);

            }
        });


///////////////////////////////////  ActionBar   ///////////////////////////////////////////////////////

        //final CircleImageView profile_image=findViewById(R.id.Profile_image);
       // final TextView user_name=findViewById(R.id.user_name);

        user=FirebaseAuth.getInstance().getCurrentUser();
        reference=FirebaseDatabase.getInstance().getReference("user").child(user.getUid());




        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Chat App");

////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////


        TabLayout tabLayout=findViewById(R.id.tablayout);
        ViewPager viewPager=findViewById(R.id.viewbager);

        viewbageradabter adabter=new viewbageradabter(getSupportFragmentManager());

        adabter.add(new chatFragment() , "chats");

        allfriendsFragment=new AllfriendsFragment();

        adabter.add(allfriendsFragment, "Users");

        brofileFragment fragment=new brofileFragment();
        fragment.setcontext(getApplicationContext());

        adabter.add(fragment,"brofile");

        viewPager.setAdapter(adabter);
        tabLayout.setupWithViewPager(viewPager);

        getpermition();

    }


    private void getpermition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},1);
            }else{
               // getphonenumbers();
            }
        }else{
           // getphonenumbers();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout:
                OneSignal.setSubscription(false);
                FirebaseAuth.getInstance().signOut();
                Intent intent=new Intent( getApplicationContext() , LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                return true;
        }
        return false;
    }

    private void stats(String stat){
        reference.child("onoff").setValue(stat);
    }

    @Override
    protected void onStart() {
        super.onStart();
        stats("online");
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
    protected void onDestroy() {
        super.onDestroy();
        stats("offline");
    }

    ////////////////////////////////////////////////////

//    private void getphonenumbers() {
//        String iso=getiso();
//        Cursor cursor= getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null);
//        while (cursor.moveToNext()){
//            String number=cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//
//            number.replace(" ","");
//            number.replace("_","");
//            number.replace("(","");
//            number.replace(")","");
//
//            if (!String.valueOf(number.charAt(0)).equals("+"))
//                number=iso+number;
//            usertheapp(number);
//        }
//        usertheapp("112211");
//
//    }
//
//    private void usertheapp(String number){
//
//        DatabaseReference mUserDB = FirebaseDatabase.getInstance().getReference().child("userphonenumber").child(number);
//        mUserDB.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if(dataSnapshot.exists()){
//                    usertheappdata(dataSnapshot.getValue().toString());
//                }
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) { }
//        });
//    }
//
//
//    private void usertheappdata(String key) {
//        if(key.equals("end")){
//            allfriendsFragment.addelemtocomenlist(new userdata("end","", "", "", "","",""));
//        }
//        DatabaseReference mUserDB = FirebaseDatabase.getInstance().getReference().child("user").child(key);
//        mUserDB.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if(dataSnapshot.exists()){
//                    String  phone = "",
//                            name = "",stats="",imageuri="default",onoff="offline",notificationkey="";
//
//                    if(dataSnapshot.child("name").getValue() != null)
//                        name=dataSnapshot.child("name").getValue().toString();
//                    if(dataSnapshot.child("phone").getValue() != null)
//                        phone=dataSnapshot.child("phone").getValue().toString();
//                    if(dataSnapshot.child("stats").getValue() != null)
//                        stats=dataSnapshot.child("stats").getValue().toString();
//                    if(dataSnapshot.child("imageuri").getValue() != null)
//                        imageuri=dataSnapshot.child("imageuri").getValue().toString();
//                    if(dataSnapshot.child("onoff").getValue() != null)
//                        onoff=dataSnapshot.child("onoff").getValue().toString();
//                    if(dataSnapshot.child("notificationkey").getValue() != null)
//                        notificationkey=dataSnapshot.child("notificationkey").getValue().toString();
//
//
//                    allfriendsFragment.addelemtocomenlist(new userdata(dataSnapshot.getKey(),name, phone, stats, imageuri,onoff,notificationkey));
//                }
//
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) { }
//        });
//    }
//
//    private String getiso(){
//        String iso=null;
//        TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(getApplicationContext().TELEPHONY_SERVICE);
//        if(telephonyManager.getNetworkCountryIso() != null ){
//            if(!telephonyManager.getNetworkCountryIso().toString().equals("") ){
//                iso=telephonyManager.getNetworkCountryIso().toString();
//            }else{
//                iso="EG";
//            }
//        }
//        return CountryToPhonePrefix.getPhone("EG");
//    }

}
