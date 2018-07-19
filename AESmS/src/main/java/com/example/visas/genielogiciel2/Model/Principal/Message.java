package com.example.visas.genielogiciel2.Model.Principal;

import java.util.ArrayList;

/**
 * Created by visas on 5/12/18.
 */


public class Message {

    private int id;
    private ArrayList<Groupe> groupes;
    private String messageTitle;
    private String messageInfo;
    private String messageTime;
    private String dateEnvoi;

    public ArrayList<Groupe> getGroupes() {
        return groupes;
    }

    public void setGroupes(ArrayList<Groupe> groupes) {
        this.groupes = groupes;
    }

    public String getDateEnvoi() {
        return dateEnvoi;
    }

    public void setDateEnvoi(String dateEnvoi) {
        this.dateEnvoi = dateEnvoi;
    }

    private boolean messageIsSent;

    public Message( String messageInfo, String messageTime,String messageTitle){
        this.messageTitle = messageTitle;
        this.messageInfo = messageInfo;
        this.messageTime = messageTime;
        messageIsSent = true;
    }

    public Message() {
    }

    public Message(String groupInitials, String messageTitle, String messageInfo, String messageTime) {
        this.messageTitle = messageTitle;
        this.messageInfo = messageInfo;
        this.messageTime = messageTime;
    }

    public Message(int id, String messageTitle, String messageInfo, String messageTime, String dateEnvoi, boolean messageIsSent) {
        this.id = id;
        this.messageTitle = messageTitle;
        this.messageInfo = messageInfo;
        this.messageTime = messageTime;
        this.dateEnvoi = dateEnvoi;
        this.messageIsSent = messageIsSent;
    }

    public Message(String groupInitials, String messageTitle,
                   String messageInfo, String messageTime, Boolean messageIsSent){
        this.messageTitle = messageTitle;
        this.messageInfo = messageInfo;
        this.messageTime = messageTime;
        this.messageIsSent = messageIsSent;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getMessageTitle() {
        return messageTitle;
    }

    public void setMessageTitle(String messageTitle) {
        this.messageTitle = messageTitle;
    }

    public String getMessageInfo() {
        return messageInfo;
    }

    public void setMessageInfo(String messageInfo) {
        this.messageInfo = messageInfo;
    }

    public String getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(String messageTime) {
        this.messageTime = messageTime;
    }

    public boolean isMessageIsSent() {
        return messageIsSent;
    }

    public void setMessageIsSent(boolean messageIsSent) {
        this.messageIsSent = messageIsSent;
    }
}

