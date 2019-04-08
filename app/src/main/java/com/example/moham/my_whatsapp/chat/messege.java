package com.example.moham.my_whatsapp.chat;

import java.util.ArrayList;

public class messege {
    private String message,senderid,messageid;
    private ArrayList<String> mediaurllist;
    private int isseen , progres;
    private Boolean upload_state;

    public messege(String messageid,String message, String senderid,ArrayList<String> mediaurllist,int isseen , Boolean upload_state) {
        this.message = message;
        this.senderid = senderid;
        this.messageid = messageid;
        this.mediaurllist = mediaurllist;
        this.isseen = isseen;
        this.upload_state=upload_state;
        this.progres=0;
    }

    public void setProgres(int progres) {
        this.progres = progres;
    }

    public int getProgres() {
        return progres;
    }

    public void setUpload_state(Boolean upload_state) {
        this.upload_state = upload_state;
    }

    public Boolean getUpload_state() {
        return upload_state;
    }

    public String getMessage() {
        return message;
    }

    public String getSenderid() {
        return senderid;
    }

    public String getMessageid() {
        return messageid;
    }

    public ArrayList<String> getMediaurllist() {
        return mediaurllist;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setSenderid(String senderid) {
        this.senderid = senderid;
    }

    public void setMessageid(String messageid) {
        this.messageid = messageid;
    }

    public void resetarr(ArrayList<String> mediaurllist) {
        this.mediaurllist .clear();
        for ( String child : mediaurllist){
            this.mediaurllist.add(child);
        }
    }

    public void cleararr() {
        this.mediaurllist.clear();
    }


    public void addelemtolist (String elem) {
        this.mediaurllist .add(elem);
    }




    public int isIsseen() {
        return isseen;
    }

    public void setIsseen(int isseen) {
        this.isseen = isseen;
    }
}
