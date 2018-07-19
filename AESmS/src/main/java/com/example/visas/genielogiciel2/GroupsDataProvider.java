package com.example.visas.genielogiciel2;

/**
 * Created by visas on 5/16/18.
 */

public class GroupsDataProvider {

    private String groupInitials, groupName;
    private int groupSize;

    public GroupsDataProvider(String groupInitials, String groupName, int groupSize) {
        this.groupInitials = groupInitials;
        this.groupName = groupName;
        this.groupSize = groupSize;
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
