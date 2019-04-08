package com.example.moham.my_whatsapp.chat;

public class frindopjecthaschatwith {
    private String id,frindname,frindstatas,frindimguri,phonenumber,onoff,notificationKey;

    public frindopjecthaschatwith(String id, String frindname, String frindstatas, String frindimguri,String phonenumber,String onoff,String notificationKey) {
        this.id = id;
        this.frindname = frindname;
        this.frindstatas = frindstatas;
        this.frindimguri = frindimguri;
        this.phonenumber = phonenumber;
        this.onoff = onoff;
        this.notificationKey = notificationKey;
    }

    public String getId() {
        return id;
    }

    public String getFrindname() {
        return frindname;
    }

    public String getFrindstatas() {
        return frindstatas;
    }

    public String getFrindimguri() {
        return frindimguri;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public String getOnoff() {
        return onoff;
    }

    public String getNotificationKey() {
        return notificationKey;
    }
}
