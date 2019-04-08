package com.example.moham.my_whatsapp;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.AnyRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class InformationActivity extends AppCompatActivity {

    private EditText name,stats;
    private ImageButton ok,skip;
    private String phonenumber;
    private ImageView personalimage;
    private String Imageuri=null;
    private ProgressBar lood;

    private FirebaseUser user;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        setContentView(R.layout.activity_information);

        user=FirebaseAuth.getInstance().getCurrentUser();
        reference=FirebaseDatabase.getInstance().getReference("user").child(user.getUid());

        name=findViewById(R.id.name);
        stats=findViewById(R.id.satas);
        ok=findViewById(R.id.ok);
        skip=findViewById(R.id.skip);
        lood=findViewById(R.id.lood);
        personalimage=findViewById(R.id.personimage);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String username = "",imageuri="default",statsnow="";
                if(dataSnapshot.exists()){
                    if (dataSnapshot.child("name").getValue()!=null)
                        username=dataSnapshot.child("name").getValue().toString();
                    if (dataSnapshot.child("imageuri").getValue()!=null)
                        imageuri=dataSnapshot.child("imageuri").getValue().toString();
                    if (dataSnapshot.child("stats").getValue()!=null)
                        statsnow=dataSnapshot.child("stats").getValue().toString();

                    if(name.getText().toString().equals(""))
                        name.setText(username);
                    if(stats.getText().toString().equals(""))
                        stats.setText(statsnow);

                    if(!imageuri.equals("default"))
                        Glide.with(InformationActivity.this).load(imageuri).into(personalimage);

                    skip.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Information");



        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userIsLogedIn();
            }
        });

        phonenumber=getIntent().getStringExtra("phonenumber");
        Imageuri="default";

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(name.getText().toString() != null && stats.getText().toString() != null){
                    name.setFocusable(false);
                    name.setFocusableInTouchMode(true);
                    stats.setFocusable(false);
                    stats.setFocusableInTouchMode(true);
                    ok.setFocusable(false);
                    ok.setFocusableInTouchMode(true);
                    lood.setVisibility(View.VISIBLE);
                    save();
                }
            }
        });
        personalimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getimgs();
            }
        });
    }
    private void save(){
        final DatabaseReference mUserDB = FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getUid());
        mUserDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final Map<String,Object> usermap=new HashMap<>();
                usermap.put("name",name.getText().toString());
                usermap.put("phone",phonenumber);
                usermap.put("stats",stats.getText().toString());
                usermap.put("onoff","ofline");
                usermap.put("search",name.getText().toString().toLowerCase());

                ///image
                final StorageReference filepath=FirebaseStorage.getInstance().getReference().child("usersimage").child(FirebaseAuth.getInstance().getUid());

                if(!Imageuri.equals("default"))
                {
                    UploadTask uploadTask=filepath.putFile(Uri.parse(Imageuri));
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    usermap.put("imageuri",uri.toString());
                                    mUserDB.updateChildren(usermap);
                                    FirebaseDatabase.getInstance().getReference().child("userphonenumber").child(phonenumber).setValue(FirebaseAuth.getInstance().getUid());
                                    userIsLogedIn();
                                }
                            });
                        }
                    });

                }else{
                    usermap.put("imageuri",Imageuri);
                    mUserDB.updateChildren(usermap);
                    FirebaseDatabase.getInstance().getReference().child("userphonenumber").child(phonenumber).setValue(FirebaseAuth.getInstance().getUid());
                    userIsLogedIn();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    private void userIsLogedIn() {
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            clearshar();
            startActivity(new Intent(this, MainHomeActivity.class));
            finish();
            return;
        }
    }
    private void clearshar() {
        SharedPreferences sharedPreferences=getSharedPreferences("fristuse",MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putBoolean("frist",false);
        editor.apply();
    }
    private void getimgs() {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,false);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"select Picture(s)"),1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK){
            Imageuri=data.getData().toString();
            Glide.with(this).load(Imageuri).into(personalimage);
        }
    }
}
