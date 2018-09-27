package com.example.lqs2.courseapp.entity;

import java.util.List;

public class FriMsgSimple {

    private String userId;
    private String userProfilePic;
    private List<String> recentChatList;


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserProfilePic() {
        return userProfilePic;
    }

    public void setUserProfilePic(String userProfilePic) {
        this.userProfilePic = userProfilePic;
    }

    public List<String> getRecentChatList() {
        return recentChatList;
    }

    public void setRecentChatList(List<String> recentChatList) {
        this.recentChatList = recentChatList;
    }
}
