package com.example.moham.my_whatsapp;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class offline_mode extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

    }
}
