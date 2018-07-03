package com.example.visas.genielogiciel2.Model.Principal;

import android.widget.ArrayAdapter;

import java.util.ArrayList;



public class Groupe {

    private int id;
    private String groupInitials, groupName;
    private int groupSize;
    private ArrayList<Contact> contacts;

    public Groupe() {
    }

    public Groupe(int id,String groupInitials, String groupName,ArrayList<Contact> contacts) {
        this.id=id;
        this.groupInitials = groupInitials;
        this.groupName = groupName;
        this.contacts = contacts;
    }

    public Groupe(String groupInitials, String groupName, int groupSize) {
        this.groupInitials = groupInitials;
        this.groupName = groupName;
        this.groupSize = groupSize;
    }

    public Groupe(int id, String groupName, ArrayList<Contact> contacts) {
        this.id = id;
        this.groupInitials=groupName.substring(0,3);
        this.groupName = groupName;
        this.contacts = contacts;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(ArrayList<Contact> contacts) {
        this.contacts = contacts;
    }

    public String getGroupInitials() {
        return groupInitials;
    }

    public void setGroupInitials(String groupInitials) {
        this.groupInitials = groupInitials;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getGroupSize() {
        return groupSize;
    }

    public void setGroupSize(int groupSize) {
        this.groupSize = groupSize;
    }

    public String groupSizeToString(){

        return groupSize+" membres";
    }

}
