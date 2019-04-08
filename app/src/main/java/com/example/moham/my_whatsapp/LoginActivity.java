package com.example.moham.my_whatsapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moham.my_whatsapp.Utils.CountryToPhonePrefix;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private String McodeVer;
    private TextView MphoneNumber,Mcode,timer;
    private Button Bsend;
    private Spinner spinner;
    private String phonenumber;
    private CountDownTimer cTimer ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);


        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Log In");
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        MphoneNumber=findViewById(R.id.phonenumber);
        Mcode=findViewById(R.id.code);
        Bsend=findViewById(R.id.verfy);
        spinner = findViewById(R.id.spinner);
        timer = findViewById(R.id.timer);

        ArrayAdapter<String> staticAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,getResources().getStringArray(R.array.countryCodes));
        staticAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(staticAdapter);


        userIsLogedIn();

        cTimer=null;

        Bsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(McodeVer != null ){
                    signinwithcode();
                }else {
                    phonenumber= CountryToPhonePrefix.getPhone(spinner.getSelectedItem().toString())+MphoneNumber.getText().toString();
                    startPhoneNumberVerification();
                }
            }
        });

        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signinwhithphonecredential( phoneAuthCredential );
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(LoginActivity.this,"error : "+e.getMessage(),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);

                McodeVer=s;
                Bsend.setText("verfiy");
            }
        };

    }

    private void userIsLogedIn() {
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        boolean frist=getshar();
        if(user != null && !frist){
            startActivity(new Intent(this,MainHomeActivity.class));
            finish();
            return;
        }else if( user!=null && frist ){
            nowuserIsLogedIn();
        }
    }

    private void nowuserIsLogedIn() {
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            Intent intent = new Intent(this,InformationActivity.class);
            intent.putExtra("phonenumber",phonenumber);
            setshard();
            startActivity(intent);
            finish();
            return;
        }
    }

    private boolean getshar() {
        SharedPreferences sharedPreferences=getSharedPreferences("fristuse",MODE_PRIVATE);
        return sharedPreferences.getBoolean("frist",true);
    }

    private void setshard() {
        SharedPreferences sharedPreferences=getSharedPreferences("fristuse",MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putBoolean("frist",true);
        editor.apply();
    }

    private void signinwithcode(){
        PhoneAuthCredential credential=PhoneAuthProvider.getCredential(McodeVer,Mcode.getText().toString());
        signinwhithphonecredential(credential);
    }
    private void signinwhithphonecredential(PhoneAuthCredential phoneAuthCredential) {
        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    final FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
                    if(user != null ){
                        nowuserIsLogedIn();
                    }
                }else{
                    Toast.makeText(LoginActivity.this,"error : "+task.getException(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void startPhoneNumberVerification() {
        Toast.makeText(this,phonenumber,Toast.LENGTH_SHORT).show();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phonenumber,
                60,
                TimeUnit.SECONDS,
                this,
                callbacks
        );
    }


    //start timer function
    void startTimer() {
        cTimer = new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                    timer.setText(""+millisUntilFinished/1000);
            }
            public void onFinish() {
                timer.setVisibility(View.GONE);
            }
        };
        cTimer.start();
    }


    //cancel timer
    void cancelTimer() {
        if(cTimer!=null)
            cTimer.cancel();
    }

}
