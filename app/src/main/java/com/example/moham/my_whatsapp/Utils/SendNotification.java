package com.example.moham.my_whatsapp.Utils;

import android.content.Context;
import android.icu.util.JapaneseCalendar;
import android.widget.Toast;

import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

public class SendNotification {



    public SendNotification(String message, String heading, String notificationKey,Context context) {

        try {
            JSONObject notificationcontent=new JSONObject(
                    "{'contents':{'en':'"+message+"'},"+
                    "'include_player_ids' :['"+notificationKey+"'],"+
                    "'headding':{'en': '"+heading+"'}}");

            OneSignal.postNotification(notificationcontent,null);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(context,"faild",Toast.LENGTH_LONG).show();
        }
    }
}
