package com.murilofb.wppclone.models;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.murilofb.wppclone.helpers.FirebaseH;

import java.io.Serializable;
import java.util.List;

public class GroupModel implements Serializable {
    private String groupName;
    private String groupId;
    private String iconUrl;
    private List<UserModel> participants;

    public GroupModel() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("groups");
        this.groupId = reference.push().getKey();
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public List<UserModel> getParticipants() {
        return participants;
    }

    public void setParticipants(List<UserModel> participants) {
        this.participants = participants;
    }
}
