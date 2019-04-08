package com.example.moham.my_whatsapp.User;

public class userdata {
    private String uid,username,userphonenumber,userstatas,userimageuri,onoff,notificationkey;

    public userdata(String uid,String username, String userphonenumber ,String userstatas ,String userimageuri,String onoff,String notificationkey) {
        this.uid=uid;
        this.username = username;
        this.userphonenumber = userphonenumber;
        this.userstatas = userstatas;
        this.userimageuri = userimageuri;
        this.onoff = onoff;
        this.notificationkey = notificationkey;
    }

    public String getUid() {
        return uid;
    }

    public String getUsername() {
        return username;
    }

    public String getUserphonenumber() {
        return userphonenumber;
    }

    public String getUserstatas() {
        return userstatas;
    }

    public String getUserimageuri() {
        return userimageuri;
    }

    public String getOnoff() {
        return onoff;
    }

    public String getNotificationkey() {
        return notificationkey;
    }
}
