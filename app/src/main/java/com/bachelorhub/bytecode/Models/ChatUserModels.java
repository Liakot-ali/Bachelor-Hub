package com.bachelorhub.bytecode.Models;

public class ChatUserModels {
    String userid;

    public ChatUserModels(String userid) {
        this.userid = userid;
    }

    public ChatUserModels(){

    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}
